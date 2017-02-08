package com.workflow.core.view.library;

import com.workflow.core.model.resource.library.LibraryResource;

public interface ILibraryView{
	
	public void updateUI();
	
	public void setResource(LibraryResource lr);
	
	public void setTaskListView(ITaskListView tlv);

}
