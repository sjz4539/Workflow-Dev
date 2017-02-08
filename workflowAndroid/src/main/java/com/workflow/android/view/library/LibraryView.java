package com.workflow.android.view.library;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.model.resource.library.LibraryResource;
import com.workflow.core.view.library.ILibraryView;
import com.workflow.core.view.library.ITaskListView;

public class LibraryView extends ListView implements ILibraryView {

	private LibraryResource resource;
	private TaskListListAdapter listAdapter;
	
	public LibraryView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		
		this.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setTaskListView(((TaskList)listAdapter.getItem(position)).getView());
			}
		});
	}
	
	@Override
	public void updateUI() {
		if(listAdapter != null){
			listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setResource(LibraryResource lr) {
		resource = lr;
		listAdapter = new TaskListListAdapter(WorkflowGUI.getContext(), resource.getLibraryData());
		updateUI();
	}

	@Override
	public void setTaskListView(ITaskListView tlv) {
		System.out.println("Set task list view to " + tlv.getTaskList().getName());
	}

	private class TaskListListAdapter extends ArrayAdapter<TaskList>{

		public TaskListListAdapter(Context context, List<TaskList> objects){
			super(context, android.R.layout.simple_list_item_1, objects);
		}

		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null || !(convertView instanceof TextView)){
				convertView = new TextView(WorkflowGUI.getContext());
			}
			((TextView)convertView).setText(getItem(position).getName());
			return convertView;
		}

	}

}
