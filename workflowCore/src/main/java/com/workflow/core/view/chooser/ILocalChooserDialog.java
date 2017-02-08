package com.workflow.core.view.chooser;

import java.io.File;
import java.util.List;

import com.workflow.core.controller.SimpleHandler;

public interface ILocalChooserDialog extends Chooser{
	
	public void showDialog(SimpleHandler onAccept, SimpleHandler onCancel);
	
	public boolean showDialogAndWait();
	
	public String getString();
	
	public List<File> getSelection();
	
}
