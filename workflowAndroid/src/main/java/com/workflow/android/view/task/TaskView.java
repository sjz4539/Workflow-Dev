package com.workflow.android.view.task;

import com.workflow.android.R;
import com.workflow.core.controller.library.Task;
import com.workflow.core.view.library.ITaskView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskView extends LinearLayout implements ITaskView{

	private Task task;
	
	private TextView taskName, taskDescription, taskDueDate, taskDueTime;
	private ImageView statusIcon;
	
	public TaskView(Context context, AttributeSet attributes) {
		super(context, attributes);
		taskName = (TextView)findViewById(R.id.task_name);
		taskDescription = (TextView)findViewById(R.id.task_description);
		taskDueDate = (TextView)findViewById(R.id.task_due_date);
		taskDueTime = (TextView)findViewById(R.id.task_due_time);
		statusIcon = (ImageView)findViewById(R.id.status_image);
	}

	public void updateUI(){
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
	
	public void updateStatus(boolean error){
		statusIcon.setVisibility(error ? VISIBLE : GONE);
	}
	
}
