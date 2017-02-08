package com.workflow.core.view.library;

import com.workflow.core.controller.library.Task;
import com.workflow.core.controller.library.TaskList;

public interface ITaskListView{
	
	public void updateUI(boolean updateTaskList);
	
	public TaskList getTaskList();
	
	public void setTaskList(TaskList tl);
	
	public boolean containsTask(Task t);
	
	public void updateStatus(boolean error);
	
}
