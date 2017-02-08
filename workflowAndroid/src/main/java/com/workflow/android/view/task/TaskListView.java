package com.workflow.android.view.task;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.library.Task;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.view.library.ITaskListView;

public class TaskListView extends ListView implements ITaskListView{

	private TaskListAdapter listAdapter;
	private TaskList taskList;
	
	public TaskListView(Context context, AttributeSet attributes){
		super(context, attributes);
	}
	
	public void setTaskList(TaskList tl){
		taskList = tl;
		listAdapter = new TaskListAdapter(WorkflowGUI.getContext(), taskList.getModel());
		super.setAdapter(listAdapter);
		updateUI(true);
	}
	
	public TaskList getTaskList(){
		return taskList;
	}
	
	public void updateUI(boolean updateTaskList){
		if(listAdapter != null){
			listAdapter.clear();
			listAdapter.addAll(taskList.getModel());
		}
	}
	
	public void updateStatus(boolean updateTaskList){
		
	}
	
	@Override
	public boolean containsTask(Task t) {
		return taskList != null && taskList.getModel().contains(t);
	}
	
	private class TaskListAdapter extends ArrayAdapter<Task>{
		
		public TaskListAdapter(Context context, List<Task> objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TaskView view;
			if(convertView != null && convertView instanceof TaskView){
				//recycle this view
				view = (TaskView)convertView;
				//decouple it from whatever it belonged to
				if(view.getTask() != null){
					view.getTask().setView(null);
				}
				//set and couple the new model
				view.setTask(getItem(position));
				view.getTask().setView(view);
			}else{
				//get a new view
				view = (TaskView)(getItem(position).getView());
			}
			view.updateUI();
			return view;
		}
		
	}

}
