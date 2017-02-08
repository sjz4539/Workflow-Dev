package com.workflow.android.view.resource;

import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.library.IFileResourceView;

import android.content.Context;
import android.util.AttributeSet;

public class FileResourceView extends ResourceView implements IFileResourceView{

	private FileResource resource;
	
	public FileResourceView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public void updateUI() {
		// TODO Auto-generated method stub
		
	}

	public void setResource(FileResource r) {
		resource = r;
		updateUI();
	}
	
	public Resource getResource(){
		return resource;
	}

	@Override
	public void updateStatus(String m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProgress(float p) {
		// TODO Auto-generated method stub
		
	}

}
