package com.workflow.android.view.task;

import com.workflow.android.view.WorkflowGUIFactory;
import com.workflow.android.view.resource.ResourceListView;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.library.Task;

import android.R;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TabbedTaskView extends TabLayout{

	private Task task;
	
	private Tab taskTab, resourceListTab;
	private TaskDetailView taskDetailView;
	private ResourceListView resourceListView;
	private ImageView statusIcon;
	
	public TabbedTaskView(Context context, AttributeSet attributes) {
		super(context, attributes);
		taskTab = newTab();
		taskTab.setText("Details");
		resourceListTab = newTab();
		resourceListTab.setText("Resources");
		statusIcon = new ImageView(context);
		statusIcon.setImageResource(R.drawable.stat_sys_warning);
		statusIcon.setVisibility(GONE);
		resourceListTab.setTag(statusIcon);
		addTab(taskTab);
		addTab(resourceListTab);
	}
	
	public void setTask(Task t){
		task = t;
		
		if(taskDetailView == null){
			taskDetailView = (TaskDetailView)((WorkflowGUIFactory)Core.getGuiFactory()).getTaskDetailView(task);
		}else{
			taskDetailView.setTask(task);
		}
		if(resourceListView == null){
			resourceListView = (ResourceListView)Core.getGuiFactory().getResourceListView(task.getResourceList());
		}else{
			resourceListView.setModel(task.getResourceList());
		}
		
		updateUI();
	}
	
	public void updateUI(){
		taskTab.setCustomView(taskDetailView);
		resourceListTab.setCustomView(resourceListView);
	}
	
	public void updateResourceListStatus(boolean error){
		statusIcon.setVisibility(error ? VISIBLE : GONE);
	}

}
