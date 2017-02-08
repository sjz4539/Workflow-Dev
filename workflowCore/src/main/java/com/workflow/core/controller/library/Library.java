package com.workflow.core.controller.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.workflow.core.controller.io.FileOps;
import com.workflow.core.controller.io.FileOpsStatus;
import com.workflow.core.controller.io.LocalFileOps;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTaskService;
import com.workflow.core.model.resource.library.LibraryResource;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.ILocalChooserDialog;
import com.workflow.core.view.dialog.ITextInputDialog;
import com.workflow.core.view.library.ILibraryMenu;
import com.workflow.core.view.library.ILibraryView;
import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;

/**
 * Libraries represent storage locations.
 * They handle all I/O requests for data stored in the location they represent.
 * Subclasses representing remote library types also handle all cache operations for downloaded copies of data.
 * 
 * @author Steven Zuchowski
 */
public abstract class Library implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//File extensions and descriptions
	public static final String RESOURCE_FILE_EXTENSION = ".wlr";
	public static final String RESOURCE_FILE_DESCRIPTION = "Workflow Library Resource";
	public static final String LIBRARY_FILE_EXTENSION = ".wfl";
	public static final String LIBRARY_FILE_DESCRIPTION = "Workflow Library";
	public static final String REMOVABLE_LIBRARY_FILE_EXTENSION = ".wrl";
	public static final String REMOVABLE_LIBRARY_FILE_DESCRIPTION = "Removable Workflow Library";
	public static final String DEFAULT_LIBRARY_FILENAME = "WorkflowLibrary";
	
	public static final Account LOCAL_ACCOUNT = new Account(Account.AccountType.ACCOUNT_TYPE_LOCAL);
	
	protected String name = ""; //this library's name
	protected int type = -1; //the type of this library
	protected String root = ""; //the root folder of this library
	protected LibraryResource libResource; //the resource object representing this library's data
	protected Account account; //the remote storage account this library uses
	
	protected transient ILibraryView libraryView;
	protected transient ILibraryMenu menu;
	protected transient boolean hasChanges = false; //flag indicating whether this library needs to be written to storage
	
	public enum Type{
		LOCAL, REMOTE
	}
	
	public boolean isLoaded(){
		return libResource.getLibraryData() != null;
	}
	
	public abstract boolean isRemovable();
	
	public boolean hasChanges(){
		return hasChanges;
	}
	
	public void setHasChanges(boolean changes){
		hasChanges = changes;
	}
	
	public abstract Account getAccount();
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getRoot(){
		return root;
	}
	
	public abstract Type getType();
	
	public ILibraryView getView(){
		if(libraryView == null){
			libraryView = Core.getGuiFactory().getLibraryView(this);
		}
		return libraryView;
	}
	
	public void setView(ILibraryView view){
		libraryView = view;
	}
	
	public void updateUI(){
		if(libraryView == null){
			libraryView.updateUI();
		}
	}
	
	public void clearLibraryData(){
		libResource.clearLibraryData();
	}
	
	public LibraryResource getResource(){
		return libResource;
	}
	
	public ArrayList<TaskList> getLibraryData(){
		return libResource.getLibraryData();
	}
	
	public void setLibraryData(ArrayList<TaskList> ld){
		libResource.setLibraryData(ld);
	}
	
	//add task lists to this library
	public void addTaskList(TaskList tl){
		getLibraryData().add(tl);
		updateUI();
	}
	public void addTaskLists(List<TaskList> tlv){
		getLibraryData().addAll(tlv);
		updateUI();
	}
	
	public void copyTaskList(final TaskList tl){
		
		final TaskList newList = new TaskList(tl);
		
		final ITextInputDialog dialog = Core.getGuiFactory().getTextInputDialog("Rename Task List", null, null, tl.getName());
		dialog.setOnAccept(new SimpleHandler(){
			public void handle() {
				tl.setName(dialog.getValue());
				addTaskList(newList);
			}
		});
		
	}
	
	//remove task lists  from this library
	public void removeTaskList(TaskList tl){
		//should probably confirm this first.
		getLibraryData().remove(tl);
		updateUI();
	}
	public void removeTaskLists(List<TaskList> tlv){
		//should probably confirm this first.
		getLibraryData().removeAll(tlv);
		updateUI();
	}

	//save all current task lists to a file in this library's storage location
	public abstract boolean saveTasks();
	
	//get all task lists from this library's storage location
	public abstract boolean loadTasks();
	
	//save all files of all of this library's tasks to this library's storage location
	public abstract void saveAllResources(SimpleHandler success, SimpleHandler failed);
	
	//save the specified files to this library's storage location
	public abstract void saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed);
	
	//save the specified file to this library's storage location
	public abstract void saveResource(FileResource resource, SimpleHandler success, SimpleHandler failed);
	
	//save the specified file to this library's storage location
	public void saveResource(FileResource resource){
		saveResource(resource, null, null);
	}
	
	//get the actual contents of all files of all of this library's tasks from this library's storage location
	public abstract void loadAllResources(SimpleHandler success, SimpleHandler failed);
	
	//get the actual contents of the specified files from this library's storage location
	public abstract void loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed);
	
	//get the actual contents of the specified file from this library's storage location
	public void loadResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		ResourceTaskService.loadResource(resource, success, failed);
	}
	
	//get the actual contents of the specified file from this library's storage location
	public void loadResource(FileResource resource){
		loadResource(resource, null, null);
	}
	
	//public abstract void addResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure);
	
	//public abstract FileOpsStatus addResource(FileResource resource);
	
	public abstract void addResource(final Task parent, String source, SimpleHandler onSuccess, SimpleHandler onFailure);
	
	public abstract void addResource(final Task parent, String source, boolean overwrite, SimpleHandler onSuccess, SimpleHandler onFailure);

	public abstract void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure);
	
	public abstract void copyResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, boolean overwrite);

	public abstract FileOpsStatus copyResource(FileResource resource, String destination, boolean overwrite);
	
	public abstract void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure);
	
	public abstract void moveResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure, boolean overwrite);
	
	public abstract FileOpsStatus moveResource(FileResource resource, String destination, boolean overwrite);
	
	public abstract FileOpsStatus deleteResource(FileResource resource);
	
	public abstract void locateResource(final FileResource resource, final SimpleHandler onSuccess, final SimpleHandler onFailure);
	
	public void exportResource(final FileResource r, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		exportResource(r, false, onSuccess, onFailure);
	}
	
	public void exportResource(final FileResource r, final boolean overwrite, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		if(this.getType() == Library.Type.REMOTE && !r.isLoaded()){
			ResourceTaskService.loadResource(r, ()->{ this.exportResource(r, onSuccess, onFailure); }, null);
		}else{
			Core.getGuiFactory().showLocalChooserDialog(
				null, Chooser.Mode.MODE_SAVE_FILE, false, "Export Resource...", "Select an export location for " + r.getName(), r.getFolder(), r.getName(), null,
				new ArgumentHandler<String>(){
					public void handle(String arg) {
						
						switch(exportResource(r, arg, overwrite).getCode()){
							case SUCCESS:
								if(onSuccess != null){
									onSuccess.handle();
								}
								break;
							case FILE_ALREADY_EXISTS:
								FileOps.requestOverwrite(arg, new SimpleHandler(){
									public void handle() {
										exportResource(r, arg, true);
									}
								}, null);
								break;
							case INVALID_DESTINATION_PATH:
								Core.getGuiFactory().displayError("Invalid Path", null, "The specified path is invalid.", null);
								if(onFailure != null){
									onFailure.handle();
								}
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
	
	public FileOpsStatus exportResource(FileResource r, String destination, boolean overwrite){
		if(r.isLoaded()){
			return LocalFileOps.copyFile(r.getAbsolutePath(), destination, overwrite);
		}else{
			return FileOpsStatus.RESOURCE_NOT_LOADED();
		}
	}

	protected boolean validatePath(String p){
		return p.startsWith(root);
	}
	
	protected boolean validatePath(FileResource r){
		return r.getParentLibrary().equals(this) && r.getAbsolutePath().startsWith(root);
	}
	
	public ILibraryMenu getMenu(){
		if(menu == null){
			menu = Core.getGuiFactory().getLibraryMenu(this);
		}
		return menu;
	}
	
	public void setMenu(ILibraryMenu m){
		menu = m;
	}
	
	public void updateStatus(String m){
		if(menu != null){
			menu.updateStatus(m);
		}
	}
	
}
