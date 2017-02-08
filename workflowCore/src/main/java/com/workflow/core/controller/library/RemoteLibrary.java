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
import com.workflow.core.controller.io.FileOpsStatus;
import com.workflow.core.controller.io.LocalFileOps;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTaskService;
import com.workflow.core.model.resource.library.RemoteLibraryResource;
import com.workflow.core.model.resource.remote.RemoteFolder;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.ILocalChooserDialog;
import com.workflow.core.view.chooser.IRemoteChooserDialog;

public class RemoteLibrary extends Library{

	private static final long serialVersionUID = 1L;
	
	public RemoteLibrary(String n, String r, String p, Account a){
		name = n;
		root = r;
		account = a;
		libResource = new RemoteLibraryResource(p, this);
	}

	public boolean saveTasks() {
		
		if(!Core.getResourceCache().containsFileRecord(libResource)){
			Core.getResourceCache().addFile(libResource);
		}
		
		File taskFile = new File(Core.getResourceCache().getPath(libResource));
		
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
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Core.getGuiFactory().displayError(null, null, "Library data could not be saved. The specified target location could not be found.", true);
		} catch (IOException e) {
			e.printStackTrace();
			Core.getGuiFactory().displayError(null, null, "Library data could not be saved. An error occurred during the write process.", true);
		}
		return false;
	}
	
	public boolean loadTasks() {
		
		if(Core.getResourceCache().containsFileData(libResource)){
			File taskFile = Core.getResourceCache().getFile(libResource);
			
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
					Core.getGuiFactory().displayError(null, null, "This library's data file could not be located.", true);
				} catch (IOException e) { //read error
					e.printStackTrace();
					Core.getGuiFactory().displayError(null, null, "This library's data could not be loaded. An error occurred during the read process.", true);
				} catch (ClassNotFoundException e) { //corrupted/unexpected data in serialized object
					e.printStackTrace();
					Core.getGuiFactory().displayError(null, null, "This library's data could not be loaded. It may be corrupt or damaged.", true);
				}
				return false;
			}else{
				System.out.println("Couldn't find the library file at " + taskFile.getAbsolutePath());
			}
		}
		return false;
	}
	
	public void loadAllResources(SimpleHandler success, SimpleHandler failed){
		for(TaskList tl : libResource.getLibraryData()){
			for(Task t : tl.getModel()){
				for(Resource r : t.getResourceList().getModel()){
					r.load();
				}
			}
		}
	}
	
	public void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		//Core.getResourceTaskService().loadResources(resources, success, failed);
	}
	
	public void saveAllResources(SimpleHandler success, SimpleHandler failed){
		for(TaskList tl : libResource.getLibraryData()){
			for(Task t : tl.getModel()){
				for(Resource r : t.getResourceList().getModel()){
					r.save();
				}
			}
		}
	}
	
	public void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		//Core.getResourceTaskService().saveResources(resources, success, failed);
	}
	
	public void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		if(resource.needsSave()){		
			ResourceTaskService.saveResource(resource, success, failed);
		}
	}
	
	public void addResource(final Task parent, final String source, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		addResource(parent, source, false, onSuccess, onFailure);
	}
	
	public void addResource(final Task parent, final String source, final boolean overwrite, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		//display a graphical interface to get a destination path for this resource
		//give the GUI a handler that will, if the user clicks ok:
		//		-call copyResource with the user's input and the resource object
		//		-if the function returns success, call onSuccess.handle()
		//		-else, call onFailure.handle()
		String filename = source.substring(source.lastIndexOf(File.separator) + 1);
		
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						addResource(parent, source, onSuccess, onFailure);
					}
				}, null
			);
			
		}else{
		
			Core.getGuiFactory().showRemoteChooserDialog(account, null, Chooser.Mode.MODE_SAVE_FILE, false, "Add Resource to Library...", "Specify where " + filename + " should be stored.", this.getRoot(), filename,
				new ArgumentHandler<String>(){
					public void handle(String arg) {
						//copy the file to the resource cache
						FileResource newRes = new FileResource(parent, arg);
						final String path = Core.getResourceCache().addFile(newRes);
						
						switch(LocalFileOps.copyFile(source, path, overwrite).getCode()){
							case SUCCESS:
								if(onSuccess != null){
									onSuccess.handle();
								}
								break;
							case FILE_ALREADY_EXISTS:
								FileOps.requestOverwrite(path, new SimpleHandler(){
									public void handle() {
										LocalFileOps.copyFile(source, path, true);
									}
								}, null);
								break;
							case INVALID_DESTINATION_PATH:
								Core.getResourceCache().removeFile(newRes);
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
	}

	public void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		copyResource(resource, onSuccess, onFailure, false);
	}
	
	public void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, final boolean overwrite) {
		
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						copyResource(resource, onSuccess, onFailure, overwrite);
					}
				}, null
			);
			
		}else{
		
			Core.getGuiFactory().showRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Copy Resource", "Specify where " + resource.getName() + " should be copied to.", resource.getFolder(), resource.getName(),

				new ArgumentHandler<String>(){
					public void handle(String arg) {
						
						switch(copyResource(resource, arg, overwrite).getCode()){
							case SUCCESS:
								if(onSuccess != null){
									onSuccess.handle();
								}
								break;
							case FILE_ALREADY_EXISTS:
								FileOps.requestOverwrite(arg, new SimpleHandler(){
									public void handle() {
										copyResource(resource, arg, true);
									}
								}, null);
								break;
							case INVALID_OAUTH_TOKEN:
								//This shouldn't be happening here; we already validated the token!
								//That said, we should be able to just loop through this method again and retry getting auth via the check.
								copyResource(resource, onSuccess, onFailure, overwrite);
								break;
							case INVALID_DESTINATION_PATH:
								Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
									public void handle() {
										copyResource(resource, onSuccess, onFailure, overwrite);
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
		
	}

	public FileOpsStatus copyResource(FileResource resource, String destination, boolean overwrite) {
		//if the path is valid, perform the copy, else return null.
		if(validatePath(destination)){
			return account.getFileOps().copyFile(account, resource.getAbsolutePath(), destination, overwrite);
		}else{
			return FileOpsStatus.INVALID_DESTINATION_PATH();
		}
	}
	
	public void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		moveResource(resource, onSuccess, onFailure, false);
	}

	public void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, final boolean overwrite) {
		
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						moveResource(resource, onSuccess, onFailure, overwrite);
					}
				}, null
			);
			
		}else{
		
			Core.getGuiFactory().showRemoteChooserDialog(account, "", Chooser.Mode.MODE_SAVE_FILE, true, "Move Resource", "Specify where " + resource.getName() + " should be moved to.", resource.getFolder(), resource.getName(),

				new ArgumentHandler<String>(){
					public void handle(String arg) {
						
						switch(moveResource(resource, arg, overwrite).getCode()){
							case SUCCESS:
								if(onSuccess != null){
									onSuccess.handle();
								}
								break;
							case FILE_ALREADY_EXISTS:
								FileOps.requestOverwrite(arg, new SimpleHandler(){
									public void handle() {
										moveResource(resource, arg, true);
									}
								}, null);
								break;
							case INVALID_OAUTH_TOKEN:
								//This shouldn't be happening here; we already validated the token!
								//That said, we should be able to just loop through this method again and retry getting auth via the check.
								moveResource(resource, onSuccess, onFailure, overwrite);
								break;
							case INVALID_DESTINATION_PATH:
								Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", new SimpleHandler(){
									public void handle() {
										moveResource(resource, onSuccess, onFailure, overwrite);
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
		
	}

	public FileOpsStatus moveResource(FileResource resource, String destination, boolean overwrite) {
		if(validatePath(destination)){
			return account.getFileOps().moveFile(account, resource.getAbsolutePath(), destination, overwrite);
		}else{
			return FileOpsStatus.INVALID_DESTINATION_PATH();
		}
	}

	public FileOpsStatus deleteResource(FileResource resource) {
		return account.getFileOps().deleteFile(account, resource.getAbsolutePath());
	}

	public void locateResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure) {	
		//spawn a graphical interface to request a new path for the resource
		
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						locateResource(resource, onSuccess, onFailure);
					}
				}, null
			);
			
		}else{
		
			Core.getGuiFactory().showRemoteChooserDialog(account, "", Chooser.Mode.MODE_SINGLE_FILE, false, "Missing Resource", "The resource " + resource.getName() + " cannot be located.\nPlease enter its location.", resource.getFolder(), resource.getName(),

				new ArgumentHandler<String>(){
					public void handle(String arg) {
						if(validatePath(arg)){
							resource.setPath(trimRemoteResourcePath(arg));
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

	}
	
	private String trimRemoteResourcePath(String s){
		String path = s;
		int loc = path.indexOf(root);
		path = path.substring(loc != -1 ? loc + root.length() : 0);
		return path;
	}
	
	public void loadFolder(final RemoteFolder f, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						loadFolder(f, onSuccess, onFailure);
					}
				}, null
			);
		}else{
			if(account.getFileOps().loadFolder(account, f).getCode() == FileOpsStatus.Code.SUCCESS){
				onSuccess.handle();
			}else{
				onFailure.handle();
			}
		}
	}
	
	public void createFolder(final RemoteFolder parent, final String name, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						createFolder(parent, name, onSuccess, onFailure);
					}
				}, null
			);
		}else{
			if(account.getFileOps().createFolder(account, parent, name).getCode() == FileOpsStatus.Code.SUCCESS){
				onSuccess.handle();
			}else{
				onFailure.handle();
			}
		}
	}

	public void deleteFolder(final RemoteFolder folder, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						deleteFolder(folder, onSuccess, onFailure);
					}
				}, null
			);
		}else{
			if(account.getFileOps().deleteFolder(account, folder).getCode() == FileOpsStatus.Code.SUCCESS){
				onSuccess.handle();
			}else{
				onFailure.handle();
			}
		}
	}

	public void copyFolder(final RemoteFolder source, final String target, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						copyFolder(source, target, onSuccess, onFailure);
					}
				}, null
			);
		}else{
			if(account.getFileOps().copyFolder(account, source, target).getCode() == FileOpsStatus.Code.SUCCESS){
				onSuccess.handle();
			}else{
				onFailure.handle();
			}
		}
	}

	public void moveFolder(final RemoteFolder source, final String target, final SimpleHandler onSuccess, final SimpleHandler onFailure) {
		if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.INVALID_OAUTH_TOKEN){
			account.getAuth(
				new SimpleHandler(){
					public void handle() {
						moveFolder(source, target, onSuccess, onFailure);
					}
				}, null
			);
		}else{
			if(account.getFileOps().moveFolder(account, source, target).getCode() == FileOpsStatus.Code.SUCCESS){
				onSuccess.handle();
			}else{
				onFailure.handle();
			}
		}
	}
	
	public Type getType(){
		return Type.REMOTE;
	}
	
	public boolean isRemovable(){
		return false;
	}
	
	public String getToken(){
		return account.getToken();
	}
	
	public Account getAccount(){
		return account;
	}
	
	public void setAccount(Account a){
		account = a;
	}
}
