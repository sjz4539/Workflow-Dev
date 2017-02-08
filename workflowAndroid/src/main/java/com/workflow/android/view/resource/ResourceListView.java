package com.workflow.android.view.resource;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.android.view.task.TabbedTaskView;
import com.workflow.core.controller.library.ResourceList;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.library.IResourceListView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ResourceListView extends LinearLayout implements IResourceListView{

	private ResourceList resourceList;
	private ResourceListAdapter listAdapter;
	
	private TabbedTaskView parentView;
	
	private ListView resourceListView;
	private Button addFileButton, addWebsiteButton;
	
	public ResourceListView(Context context, AttributeSet attributes){
		super(context, attributes);
	}
	
	public void setModel(ResourceList rl){
		resourceList = rl;
		
		resourceListView = (ListView)findViewById(R.id.resource_list_view);
		addFileButton = (Button)findViewById(R.id.add_file_button);
		addWebsiteButton = (Button)findViewById(R.id.add_website_button);
		
		listAdapter = new ResourceListAdapter(WorkflowGUI.getContext(), resourceList);
		resourceListView.setAdapter(listAdapter);
		updateUI();
	}
	
	public void updateUI(){
		if(listAdapter != null){
			listAdapter.notifyDataSetChanged();
		}
	}

	public void updateStatus(boolean error) {
		parentView.updateResourceListStatus(error);
	}
	
	public void setParent(TabbedTaskView ttv){
		parentView = ttv;
	}
	
	private static class ResourceListAdapter extends ArrayAdapter<Resource>{
		
		public ResourceListAdapter(Context context, ResourceList resources){
			super(context, android.R.layout.simple_list_item_1, resources.getModel());
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			
			if(getItem(position).storedAsFile()){
				
				FileResourceView view;
				
				if(convertView != null && convertView instanceof FileResourceView){
					view = (FileResourceView)convertView;
					//decouple it from whatever it belonged to
					if(view.getResource() != null){
						view.getResource().setView(null);
					}
					//set and couple the new model
					view.setResource(getItem(position));
					view.getResource().setView(view);
				}else{
					view = (FileResourceView)(getItem(position).getView());
				}
				
				view.updateUI();
				return view;
				
			}else{
				
				ResourceView view;
				
				if(convertView != null && convertView instanceof ResourceView){
					view = (ResourceView)convertView;
					//decouple it from whatever it belonged to
					if(view.getResource() != null){
						view.getResource().setView(null);
					}
					//set and couple the new model
					view.setResource(getItem(position));
					view.getResource().setView(view);
				}else{
					view = (ResourceView)(getItem(position).getView());
				}
				
				view.updateUI();
				return view;
			}
			
		}
		
	}
	
}
