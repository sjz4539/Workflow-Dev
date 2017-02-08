package com.workflow.core.view.dialog;

import com.workflow.core.controller.SimpleHandler;

public interface ITextInputDialog {

	public void setOnAccept(final SimpleHandler handler);
	
	public void setOnCancel(final SimpleHandler handler);
	
	public void setValue(String v);
	
	public String getValue();
	
	public void showDialog();
	
}
