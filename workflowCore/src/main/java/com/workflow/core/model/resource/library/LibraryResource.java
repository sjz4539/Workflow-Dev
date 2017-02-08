package com.workflow.core.model.resource.library;

import java.util.ArrayList;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.FileResource.Status;
import com.workflow.core.view.library.ILibraryView;

public abstract class LibraryResource extends FileResource{

	private static final long serialVersionUID = 1L;
	
	protected transient ArrayList<TaskList> libraryData;
	protected transient boolean loaded;
	
	public LibraryResource(String p, Library l){
		super(l);
		path = (p.endsWith(Library.RESOURCE_FILE_EXTENSION) ? p : p + Library.RESOURCE_FILE_EXTENSION);
		loaded = false;
	}
	
	public String getName(){
		return library.getName();
	}
	
	public LibraryResource copyObject(){
		return null;
	}
	
	public void clearLibraryData(){
		libraryData = new ArrayList<TaskList>();
	}
	
	public ArrayList<TaskList> getLibraryData() {
		//if the library is new and hasn't had its model created yet, do so.
		if(loaded && libraryData == null){
			libraryData = new ArrayList<TaskList>();
		}

		return libraryData;
	}
	
	public void setLibraryData(ArrayList<TaskList> ld){
		libraryData = ld;
		loaded = true;
	}
	
	public void setStatus(Status s, String m){
		status = s;
		library.updateStatus(m);
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
}
