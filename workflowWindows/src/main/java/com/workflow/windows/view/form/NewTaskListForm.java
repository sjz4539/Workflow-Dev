package com.workflow.windows.view.form;

import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.view.form.INewTaskListForm;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewTaskListForm extends VBox implements INewTaskListForm{

	private TextField nameField;
	
	public NewTaskListForm(){
		
		nameField = new TextField();
		
		getChildren().addAll(new Label("Name"), nameField);
		
	}
	
	public TaskList makeTaskList(Library l){
		
		return new TaskList(l, nameField.getText());
		
	}
	
}
