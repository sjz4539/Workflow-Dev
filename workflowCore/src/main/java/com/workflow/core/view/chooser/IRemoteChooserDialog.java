package com.workflow.core.view.chooser;

import java.util.List;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.resource.remote.RemoteResource;

public interface IRemoteChooserDialog extends Chooser{
	
	public void showDialog(SimpleHandler onAccept, SimpleHandler onCancel);
	
	public boolean showDialogAndWait();
	
	public String getString();
	
	public List<RemoteResource> getSelection();
	
}
