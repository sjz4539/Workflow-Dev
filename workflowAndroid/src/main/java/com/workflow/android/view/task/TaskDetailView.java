package com.workflow.android.view.task;

import com.workflow.android.R;
import com.workflow.core.controller.library.Task;
import com.workflow.core.view.library.ITaskView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskDetailView extends LinearLayout{

	private Task task;
	
	private LinearLayout viewPane, editPane;
	private TextView taskName, taskDescription, taskDueDate, taskDueTime;
	private EditText taskNameEdit, taskDescriptionEdit, taskDueDateEdit, taskDueTimeEdit;
	
	public TaskDetailView(Context context, AttributeSet attributes) {
		super(context, attributes);
		viewPane = (LinearLayout)findViewById(R.id.view_pane);
		editPane = (LinearLayout)findViewById(R.id.edit_pane);
		taskName = (TextView)findViewById(R.id.task_name);
		taskDescription = (TextView)findViewById(R.id.task_description);
		taskDueDate = (TextView)findViewById(R.id.task_due_date);
		taskDueTime = (TextView)findViewById(R.id.task_due_time);
		taskNameEdit = (EditText)findViewById(R.id.task_name_edit);
		taskDescriptionEdit = (EditText)findViewById(R.id.task_description_edit);
		taskDueDateEdit = (EditText)findViewById(R.id.task_due_date_edit);
		taskDueTimeEdit = (EditText)findViewById(R.id.task_due_time_edit);
	}

	public void updateUI() {
		if(task != null){
			taskName.setText(task.getName());
			taskDescription.setText(task.getDescription());
			taskDueDate.setText(task.getDate());
			taskDueTime.setText(task.getTime());
		}
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task t) {
		task = t;
		updateUI();
	}

	public void setMode(boolean edit) {
		if(!edit){
			editPane.setVisibility(GONE);
			viewPane.setVisibility(VISIBLE);
		}else{
			viewPane.setVisibility(GONE);
			editPane.setVisibility(VISIBLE);
		}
	}

	public void cancelUpdateTask() {
		taskNameEdit.setText(task.getName());
		taskDescriptionEdit.setText(task.getDescription());
		taskDueDateEdit.setText(task.getDate());
		taskDueTimeEdit.setText(task.getTime());
		setMode(false);
	}

	public void confirmUpdateTask() {
		task.setName(taskNameEdit.getText().toString());
		task.setDescription(taskDescriptionEdit.getText().toString());
		task.setTime(taskDueTimeEdit.getText().toString());
		task.setDate(taskDueDateEdit.getText().toString());
		setMode(false);
	}

}
