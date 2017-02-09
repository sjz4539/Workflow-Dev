package com.workflow.core.model.resource;

import java.io.File;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.io.FileOps;
import com.workflow.core.controller.io.FileOpsStatus;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTask;
import com.workflow.core.model.resource.ResourceTask.Action;
import com.workflow.core.model.resource.task.FileResource;

public class ResourceRunnable extends Thread{

	private ResourceTask parent;
	private FileResource resource;
	private Account account;
	private Action action;

	public ResourceRunnable(ResourceTask p, FileResource r, Action act){
		parent = p;
		resource = r;
		account = r.getParentLibrary().getAccount();
		action = act;
	}
	
	public void run(){
		FileOpsStatus status;
		ResourceTask.State result = ResourceTask.State.COMPLETE;
		
		switch(action){
		
			case GET:
				resource.setStatus(FileResource.Status.DOWNLOADING);
				if(!Core.getResourceCache().containsFileRecord(resource)){
					Core.getResourceCache().addFile(resource);
				}
				status = FileOps.Remote.loadFile(account, resource.getAbsolutePath(), Core.getResourceCache().getPath(resource));
				
				switch(status.getCode()){
					
					case SUCCESS:
						//update the last saved version
						File file = new File(Core.getResourceCache().getAbsolutePath(resource));
						resource.setLastSavedTime(file.lastModified());
						resource.setStatus(FileResource.Status.NORMAL);
						break;
				
					case FILE_NOT_FOUND:
						resource.setStatus(FileResource.Status.MISSING, status.getMessage());
						result = ResourceTask.State.FAILED;
						break;
						
					default:
						resource.setStatus(FileResource.Status.ERROR, status.getMessage());
						result = ResourceTask.State.FAILED;
						break;
				}
				
				break;
				
			case SEND:
				status = FileOps.Remote.saveFile(account, Core.getResourceCache().getAbsolutePath(resource), resource.getAbsolutePath());
				
				switch(status.getCode()){
					case SUCCESS:
						//update the last saved version
						File file = new File(Core.getResourceCache().getAbsolutePath(resource));
						resource.setLastSavedTime(file.lastModified());
						resource.setStatus(FileResource.Status.NORMAL);
						break;
						
					default:
						resource.setStatus(FileResource.Status.ERROR, status.getMessage());
						result = ResourceTask.State.FAILED;
						break;
				}
					
				break;
				
			default:
				result = ResourceTask.State.CANCELLED;
				break;
		}
			
		//done, notify our parent
		parent.threadFinished(result);
		
	}

}
