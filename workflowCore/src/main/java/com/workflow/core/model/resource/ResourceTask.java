package com.workflow.core.model.resource;

import java.util.ArrayList;
import java.util.List;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.resource.task.FileResource;

public class ResourceTask {
	
	/**
	 * Represents the action a task should undertake:
	 * GET a file's contents from storage
	 * SEND a file's contents to storage
	 */
	public enum Action{
		GET, SEND
	}
	
	/**
	 * Represents the state of a ResourceTask:
	 * IDLE, hasn't started yet
	 * RUNNING, in progress with an active thread
	 * COMPLETE, operation completed successfully
	 * FAILED, operation completed unsuccessfully
	 * CANCELLED, operation was cancelled before it finished
	 */
	public enum State{
		IDLE, RUNNING, COMPLETE, FAILED, CANCELLED
	}
	
	//basic info, parent task (if applicable) and the resource this task is operating on
	private ResourceTask parent = null;
	private FileResource resource;
	
	//subtasks, if they exist, and the count of running subtasks
	private ArrayList<ResourceTask> children;
	private int runCount = 0;
	
	//the worker thread for this task
	private ResourceRunnable runnable;
	
	//the state of this task
	private State state = State.IDLE;
	
	//completion handlers for this task
	private SimpleHandler successHandler, failureHandler;

	/**
	 * @param fr The FileResource this task should act upon.
	 * @param a The Action this task should perform.
	 */
	public ResourceTask(FileResource fr, Action a){
		this(fr, a, null, null);
	}
	
	/**
	 * @param fr The FileResource this task should act upon.
	 * @param a The Action this task should perform.
	 * @param onSuccess A hander to call if this task succeeds. May be null.
	 * @param onFailure A handler to call if this task fails. May be null.
	 */
	public ResourceTask(FileResource fr, Action a, SimpleHandler onSuccess, SimpleHandler onFailure){
		resource = fr;
		successHandler = onSuccess;
		failureHandler = onFailure;
		runnable = new ResourceRunnable(this, fr, a);
	}
	
	/**
	 * @param subtasks A list of child ResourceTasks this task should perform. 
	 * @param a The Action each child should perform.
	 */
	public ResourceTask(List<ResourceTask> subtasks){
		this(subtasks, null, null);
	}
	
	/**
	 * @param subtasks A list of child ResourceTasks this task should perform.
	 * @param onSuccess A hander to call if all child tasks succeed. May be null.
	 * @param onFailure A handler to call if any child tasks fail. May be null.
	 */
	public ResourceTask(List<ResourceTask> subtasks, SimpleHandler onSuccess, SimpleHandler onFailure){
		children = new ArrayList<ResourceTask>(subtasks);
		for(ResourceTask child : children){
			child.setParent(this);
		}
		successHandler = onSuccess;
		failureHandler = onFailure;
	}
	
	public void setParent(ResourceTask p){
		parent = p;
	}
	
	/**
	 * @return The State of this task.
	 * @see ResourceTask.State
	 */
	public State getState(){
		return state;
	}
	
	/**
	 * @return True if this task has and child tasks, false otherwise.
	 */
	public boolean hasChildren(){
		return children != null && children.size() > 0;
	}
	
	/**
	 * @return The next child task of this task that isn't running.
	 */
	public ResourceTask getNextChild(){
		if(children != null){
			for(ResourceTask child : children){
				if(child.hasChildren()){
					return child.getNextChild();
				}else if(!child.isRunning()){
					return child;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return True if this task or any subtasks are running, false otherwise.
	 */
	public boolean isRunning(){
		if(hasChildren()){
			for(ResourceTask child : children){
				if(child.isRunning()){
					//above call searches child's children as well, etc
					//if false, just move on
					return true;
				}
			}
			return false;
		}else{
			return runnable != null && runnable.isAlive();
		}
	}
	
	/**
	 * Starts the next unstarted task related to this one and notifies the service.
	 * If this task has children, the next unstarted child task will be started.
	 * If this task has no children, this task will be started.
	 */
	public void startNext(){
		if(children != null){
			if(!children.isEmpty()){
				ResourceTask next = children.get(runCount);
				runCount++;
				next.startNext();
				ResourceTaskService.taskStarted(next);
				state = State.RUNNING;
			}else{
				taskFinished();
			}
		}else{
			runnable.start();
			ResourceTaskService.taskStarted(this);
		}
	}
	
	/**
	 * Cancels this task and any subtasks. No handlers will be called.
	 */
	public void cancel(){
		
		if(hasChildren()){
			//propagate the request to all children
			for(ResourceTask child : children){
				child.cancel();
			}
			
		}else{
			//no children
			//since we can't force a call to the dropbox or google APIs to halt, we have no choice but to simply stop our thread.
			if(runnable != null && runnable.isAlive()){
				runnable.stop();
			}
		}
		
		//If we hadn't previously failed, note that we cancelled instead
		if(state != State.FAILED){
			state = State.CANCELLED;
		}
		
		taskFinished();
	}
	
	/**
	 * Called when a child task is complete.
	 * 
	 * @param child A child ResourceTask of this task. 
	 */
	public void childTaskFinished(ResourceTask child){
		if(children.contains(child)){
			
			//update our state if a child failed
			if(child.getState() == State.FAILED){
				state = State.FAILED;
			}
			
			//update our state if no child failed and all children have finished
			if(state != State.FAILED && runCount == children.size()){
				state = State.COMPLETE;
			}
			
			//if all children finished, we're done too
			if(runCount == children.size()){
				taskFinished();
			}
		}
	}

	/**
	 * Called when this task's runnable finishes.
	 * @param s A State representing whether the thread succeeded or failed.
	 */
	public void threadFinished(State s){
		state = s;
		taskFinished();
	}
	
	/**
	 * Called when this task is complete.
	 */
	public void taskFinished(){
		
		//notify the service that we're done
		ResourceTaskService.taskFinished(this);
		
		//notify any existing parent that we're done
		if(parent != null){
			parent.childTaskFinished(this);
		}
		
		//run any applicable handlers
		if(state == State.COMPLETE){
			if(successHandler != null){
				successHandler.handle();
			}
		}else if(state == State.FAILED){
			if(failureHandler != null){
				failureHandler.handle();
			}
		}
	}
	
}
