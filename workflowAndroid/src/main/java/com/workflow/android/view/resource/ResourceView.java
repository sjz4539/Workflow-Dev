package com.workflow.android.view.resource;

import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.library.IResourceView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResourceView extends LinearLayout implements IResourceView{

	private Resource resource;
	
	public ResourceView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public void updateUI() {
		// TODO Auto-generated method stub
		
	}

	public void setResource(Resource r) {
		resource = r;
		updateUI();
	}
	
	public Resource getResource(){
		return resource;
	}

}
