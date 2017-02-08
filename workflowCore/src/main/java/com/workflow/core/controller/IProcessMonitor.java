package com.workflow.core.controller;

import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;

/**
 * Interface used by Core to create and start new processes.
 * Originally intended to monitor running processes and provide a means of closing them
 * (or at least warn a user of resources still in use in external editors).
 * 
 * @author Steven Zuchowski
 */
public interface IProcessMonitor {
	
	/**
	 * Opens a resource in the system's default program for the resource type.
	 * @param resource The Resource to be opened.
	 */
	public void open(Resource resource);
	
	/**
	 * Opens a FileResource in the system's default program for the file type of the given resource.
	 * @param resource The FileResource to be opened.
	 */
	public void open(FileResource resource);

	/*
	public void remove(Resource resource){
		processes.remove(resource);
	}
	
	public boolean close(Resource resource){
		synchronized(processes){
			if(processes.containsKey(resource)){
				Process runningProc = processes.get(resource);
				if(runningProc != null && runningProc.isAlive()){
					runningProc.destroy();
					if(runningProc.isAlive()){
						return false;
					}
				}
				processes.remove(resource);
			}
			return true; //resource has no open process, identical result to process being closed.
		}
	}
	
	public boolean close(List<Resource> resources){
		synchronized(processes){
			boolean result = true;
			for(Resource resource : resources){
				if(!close(resource)){
					result = false;
				}
			}
			return result;
		}
	}
	
	public boolean forceClose(Resource resource){
		synchronized(processes){
			if(processes.containsKey(resource)){
				Process runningProc = processes.get(resource);
				if(runningProc != null && runningProc.isAlive()){
					runningProc.destroyForcibly();
					if(runningProc.isAlive()){
						return false;
					}
				}
				processes.remove(resource);
			}
			return true; //resource has no open process, identical result to process being closed.
		}
	}
	
	public boolean forceClose(List<Resource> resources){
		synchronized(processes){
			boolean result = true;
			for(Resource resource : resources){
				if(!forceClose(resource)){
					result = false;
				}
			}
			return result;
		}
	}
	
	public boolean closeAll(){
		synchronized(processes){
			ArrayList<Resource> keys = new ArrayList<Resource>();
			
			for(Resource resource : processes.keySet()){
				keys.add(resource);
			}
			
			return close(keys);
		}
	}
	
	public boolean forceCloseAll(){
		synchronized(processes){
			ArrayList<Resource> keys = new ArrayList<Resource>();
			
			for(Resource resource : processes.keySet()){
				keys.add(resource);
			}
			
			return forceClose(keys);
		}
	}
	
	public Process getProcess(Resource resource){
		synchronized(processes){
			return processes.get(resource);
		}
	}
	
	public int getProcessCount(){
		synchronized(processes){
			return processes.size();
		}
	}
	
	protected HashMap<Resource, Process> getProcesses(){
		return processes;
	}
	
	public ArrayList<Resource> getOpenResources(){
		synchronized(processes){
			ArrayList<Resource> result = new ArrayList<Resource>();
			result.addAll(processes.keySet());
			return result;
		}
	}
	
	public void stop(){
		monitorService.cancel();
	}
	
	private class ProcessMonitorService extends Service<Boolean>{

		HashMap<Resource, Process> processes = getProcesses();
		
		protected Task<Boolean> createTask() {
			return new Task<Boolean>(){
				protected Boolean call() throws Exception {

					boolean result = false;
					
					synchronized(processes){
						
						if(processes.size() > 0){
							
							ArrayList<Resource> closed = new ArrayList<Resource>();
							
							for(Resource resource : processes.keySet()){
								if(!processes.get(resource).isAlive()){
									closed.add(resource);
								}
							}
							
							for(Resource resource : closed){
								processes.remove(resource);
							}
							
						}
						
						processes.wait(1000);
						
					}
					
					return result;
				}
			};
		}
		
	}
	*/
}
