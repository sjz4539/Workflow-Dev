package com.workflow.core.controller.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.io.FileOps;
import com.workflow.core.controller.io.RemoteFileOps;
import com.workflow.core.controller.io.FileOpsStatus;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.library.LocalLibraryResource;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.view.chooser.Chooser;

public class LocalLibrary extends Library{

	private static final long serialVersionUID = 1L;
	
	public LocalLibrary(String n, String r, String p){
		name = n;
		root = r;
		libResource = new LocalLibraryResource(p, this);
	}
	
	public Account getAccount(){
		return null;
	}
	
	public boolean saveTasks() {
		String location = root + File.separator + libResource.getPath();
		
		java.io.File taskFile = new java.io.File(location);
		
		try {
			FileOutputStream fOut = new FileOutputStream(taskFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fOut);
			
			ArrayList<TaskList> libData = libResource.getLibraryData();
			
			if(libData != null){
				objOut.writeObject(libData);
				objOut.close();
				fOut.close();
				
				return true;
			}else{
				objOut.close();
				fOut.close();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadTasks() {
		String location = root + File.separator + libResource.getPath();

		java.io.File taskFile = new java.io.File(location);
		
		if(taskFile.exists() && taskFile.isFile()){
			try {
				FileInputStream fRead = new FileInputStream(taskFile);
				ObjectInputStream objRead = new ObjectInputStream(fRead);
				
				libResource.setLibraryData((ArrayList<TaskList>)objRead.readObject());
				
				for(TaskList tl : libResource.getLibraryData()){
					tl.setLibrary(this);
				}
				
				objRead.close();
				fRead.close();
				
				return true;
			} catch (FileNotFoundException e) { //this should never happen due to the checks above
				e.printStackTrace();
				return false;
			} catch (IOException e) { //read error
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) { //corrupted/unexpected data in serialized object
				e.printStackTrace();
				return false;
			}
		}else{
			System.out.println("Couldn't find the library file at " + location);
			return false;
		}
	}

	//save all files of all of this library's tasks to this library's storage location
	public void saveAllResources(SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//save the specified files to this library's storage location
	public void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//save a resource's data
	public void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}

	//get the actual contents of all files of all of this library's tasks from this library's storage location
	public void loadAllResources(SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	//get the actual contents of the specified files from this library's storage location
	public void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed) {
		//nothing to do here, all data is stored on the local disk
	}
	
	/*
	
	//add a resource to this library
	public void addResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		switch(addResource(resource).getCode()){
			case SUCCESS:
				onSuccess.handle();
				break;	
			case INVALID_DESTINATION_PATH:
				addResource()
			default:
				onFailure.handle();
				break;
		}
	}
	
	//add a resource to this library
	public FileOpsStatus addResource(FileResource resource) {
		//compare the resource's path against the root of this library
		//if the resource is not a child, call moveResource and then deleteResource if the move fails.
		return validatePath(resource) ? FileOpsStatus.SUCCESS() : FileOpsStatus.INVALID_DESTINATION_PATH();
	}
	
	*/
	
	public void addResource(final Task parent, final String source, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		addResource(parent, source, false, onSuccess, onFailure);
	}
	
	public void addResource(final Task parent, final String source, final boolean overwrite, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		//display a graphical interface to get a destination path for this resource
		//give the GUI a handler that will, if the user clicks ok:
		//		-call copyResource with the user's input and the resource object
		//		-if the function returns success, call onSuccess.handle()
		//		-else, call onFailure.handle()
		
		Core.getGuiFactory().showLocalChooserDialog(
			null, Chooser.Mode.MODE_SAVE_FILE, false, "Add Resource to Library...", "Please indicate where to store this resource in the chosen library.", 
			this.getRoot(), source.substring(source.lastIndexOf(File.separator) + 1), null,
			new ArgumentHandler<String>(){
				public void handle(String arg) {

					switch(FileOps.Local.copyFile(source, arg, overwrite).getCode()){
						case SUCCESS:
							if(onSuccess != null){
								onSuccess.handle();
							}
							break;
						case FILE_ALREADY_EXISTS:
							RemoteFileOps.requestOverwrite(arg, new SimpleHandler(){
								public void handle() {
									FileOps.Local.copyFile(source, arg, true);
								}
							}, null);
							break;
						case INVALID_DESTINATION_PATH:
							Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
								public void handle() {
									addResource(parent, source, onSuccess, onFailure);
								}
							});
							break;
						default:
							if(onFailure != null){
								onFailure.handle();
							}
							break;
					}
				}
			},
			null
		);
	}
	
	public void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		copyResource(resource, onSuccess, onFailure, false);
	}
	
	public void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, final boolean overwrite){
		//display a graphical interface to get a destination path for this resource
		//give the GUI a handler that will, if the user clicks ok:
		//		-call copyResource with the user's input and the resource object
		//		-if the function returns success, call onSuccess.handle()
		//		-else, call onFailure.handle()
		
		Core.getGuiFactory().showLocalChooserDialog(
			null, Chooser.Mode.MODE_SAVE_FILE, false, "Copy resource to...", null, resource.getFolder(), resource.getName(), null,
			new ArgumentHandler<String>(){
				public void handle(String arg) {
					
					switch(copyResource(resource, arg, overwrite).getCode()){
						case SUCCESS:
							resource.getParentTask().getResourceList().addResource(new FileResource(resource.getParentTask(), arg));
							if(onSuccess != null){
								onSuccess.handle();
							}
							break;
						case FILE_ALREADY_EXISTS:
							RemoteFileOps.requestOverwrite(arg, new SimpleHandler(){
								public void handle() {
									copyResource(resource, arg, true);
								}
							}, null);
							break;
						case INVALID_DESTINATION_PATH:
							Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
								public void handle() {
									copyResource(resource, onSuccess, onFailure);
								}
							});
							break;
						default:
							if(onFailure != null){
								onFailure.handle();
							}
							break;
					}
				}
			},
			null
		);
		
	}
	
	//copy the file that holds the resource's data on the local disk to the specified location
	public FileOpsStatus copyResource(FileResource resource, String destination, boolean overwrite) {
		//if the path is valid, perform the copy, else return null.
		if(validatePath(destination)){
			return FileOps.Local.copyFile(resource.getAbsolutePath(), destination, overwrite);
		}else{
			return FileOpsStatus.INVALID_DESTINATION_PATH();
		}
	}

	//move the file that holds the resource's data on the local disk to the specified location
	public void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		moveResource(resource, onSuccess, onFailure, false);
	}
	
	//move the file that holds the resource's data on the local disk to the specified location
	public void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, boolean overwrite) {
		//display a graphical interface to get a destination path for this resource
		//give the GUI a handler that will, if the user clicks ok:
		//		-call copyResource with the user's input and the resource object
		//		-if the function returns success, call onSuccess.handle()
		//		-else, call onFailure.handle()
		
		Core.getGuiFactory().showLocalChooserDialog(
			null, Chooser.Mode.MODE_SAVE_FILE, false, "Copy resource to...", null, resource.getFolder(), resource.getName(), null,
			new ArgumentHandler<String>(){
				public void handle(String arg) {
					
					switch(moveResource(resource, arg, false).getCode()){
						case SUCCESS:
							if(onSuccess != null){
								onSuccess.handle();
							}
							break;
						case FILE_ALREADY_EXISTS:
							RemoteFileOps.requestOverwrite(arg, new SimpleHandler(){
								public void handle() {
									copyResource(resource, arg, true);
								}
							}, null);
							break;
						case INVALID_DESTINATION_PATH:
							Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
								public void handle() {
									moveResource(resource, onSuccess, onFailure);
								}
							});
							break;
						default:
							if(onFailure != null){
								onFailure.handle();
							}
							break;
					}
				}
			},
			null
		);
	}
	
	//move the file that holds the resource's data on the local disk to the specified location
	public FileOpsStatus moveResource(FileResource resource, String destination, boolean overwrite) {
		//ask the user for a location to move the resource to
		//if the path is not a child of this library's root, request a new path
		if(validatePath(destination)){
			resource.setPath(destination);
			return FileOps.Local.moveFile(resource.getAbsolutePath(), destination, overwrite);
		}else{
			return FileOpsStatus.INVALID_DESTINATION_PATH();
		}
	}

	//delete the file that holds the resource's data on the local disk
	public FileOpsStatus deleteResource(FileResource resource) {
		return FileOps.Local.deleteFile(resource.getAbsolutePath());
	}

	//locate the file that holds the resource's data on the local disk
	public void locateResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		//spawn a graphical interface to request a new path for the resource
		Core.getGuiFactory().showLocalChooserDialog(
			null, Chooser.Mode.MODE_SAVE_FILE, false, "Locate " + resource.getName() + "...", null, resource.getFolder(), resource.getName(), null,
			new ArgumentHandler<String>(){
				public void handle(String arg) {
					if(validatePath(arg)){
						resource.setPath(arg);
						if(onSuccess != null){
							onSuccess.handle();
						}
					}else{
						Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
							public void handle() {
								locateResource(resource, onSuccess, onFailure);
							}
						});
					}
				}
			},
			onFailure
		);
		
	}
	
	public Type getType(){
		return Type.LOCAL;
	}
	
	public boolean isRemovable(){
		return false;
	}

}
