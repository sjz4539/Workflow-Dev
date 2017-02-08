package com.workflow.core.model.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTask.Action;
import com.workflow.core.model.resource.task.FileResource;

/**
 * Handles requests for actions that may rely on the transfer
 * of resources, as well as the initiation and monitoring of
 * any such transfers related to the requested action.
 * 
 * @author Steven Zuchowski
 *
 */
public abstract class ResourceTaskService{

	//track both queued and running tasks so we can obey down/upload limits
	//note that multipart tasks (those with child tasks) remain queued until empty
	//only tasks with running threads ever get listed as running
	private static ArrayList<ResourceTask> queue = new ArrayList<ResourceTask>(), running = new ArrayList<ResourceTask>();
	
	/**
	 * Loads a file resource's contents from storage.
	 * 
	 * @param resource A FileResource object to load.
	 * @param success A SimpleHandler to be run if the operation succeeds.
	 * @param failed A SimpleHandler to be run if the operation fails.
	 */
	public static void loadResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		if(resource.getParentLibrary().getType() == Library.Type.LOCAL){
			File file = new File(resource.getAbsolutePath());
			if(file.exists() && file.isFile()){
				success.handle();
			}else{
				failed.handle();
			}
		}else{
			ResourceTask rt = new ResourceTask(resource, Action.GET, success, failed);
			queue.add(rt);
			startNextTask();
		}
	}
	
	/**
	 * Loads multiple file resources' contents from storage.
	 * 
	 * @param resource A list of FileResource objects to load.
	 * @param success A SimpleHandler to be run if all operations succeed.
	 * @param failed A SimpleHandler to be run if any operation fails.
	 */
	public static void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ArrayList<ResourceTask> subtasks = new ArrayList<ResourceTask>();
		for(FileResource resource : resources){
			subtasks.add(new ResourceTask(resource, Action.GET, null, null));
		}
		ResourceTask rt = new ResourceTask(subtasks, success, failed);
		queue.add(rt);
		startNextTask();
	}
	
	/**
	 * Saves a file resource's contents to storage.
	 * 
	 * @param resource A FileResource object to save.
	 * @param success A SimpleHandler to be run if the operation succeeds.
	 * @param failed A SimpleHandler to be run if the operation fails.
	 */
	public static void saveResource(FileResource resource, SimpleHandler success,  SimpleHandler failed){
		if(resource.getParentLibrary().getType() == Library.Type.LOCAL){
			success.handle();
		}else{
			ResourceTask rt = new ResourceTask(resource, Action.SEND, success, failed);
			queue.add(rt);
			startNextTask();
		}
	}
	
	/**
	 * Saves multiple file resources' contents to storage.
	 * 
	 * @param resource A list of FileResource objects to save.
	 * @param success A SimpleHandler to be run if all operations succeed.
	 * @param failed A SimpleHandler to be run if any operation fails.
	 */
	public static void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ArrayList<ResourceTask> subtasks = new ArrayList<ResourceTask>();
		for(FileResource resource : resources){
			subtasks.add(new ResourceTask(resource, Action.SEND, null, null));
		}
		ResourceTask rt = new ResourceTask(subtasks, success, failed);
		queue.add(rt);
		startNextTask();
	}
	
	/**
	 * Called to start the next task or subtask in the queue.
	 * Does nothing if the maximum number of tasks are already running.
	 */
	private static void startNextTask(){
		int max = Integer.parseInt(Core.getCore().getProperty(Core.PROP_MAX_SIMULTANEOUS_TRANSFERS));
		if(running.size() < max && !queue.isEmpty()){
			ResourceTask next = queue.get(0);
			if(next.hasChildren()){
				next.startNext();
			}else{
				running.add(next);
				next.startNext();
			}
		}
	}
	
	/**
	 * Called when a resource task is started.
	 * @param rt The ResourceTask that was just started.
	 */
	public static void taskStarted(ResourceTask rt){
		if(!rt.hasChildren()){
			queue.remove(rt); //this won't remove parent tasks if a child was started, which is what we want
			running.add(rt); //but this -will- add the child to the running list, which is also what we want
		}
	}
	
	/**
	 * Called when a resource task is completed, regardless of its state.
	 * Attempts to start the next queue in the task if any exist.
	 * @param rt The ResourceTask that has finished.
	 */
	public static void taskFinished(ResourceTask rt){
		queue.remove(rt); //this may be a cancel request, so make sure we remove from the queue as well.
		running.remove(rt);
		startNextTask();
	}
	
}
