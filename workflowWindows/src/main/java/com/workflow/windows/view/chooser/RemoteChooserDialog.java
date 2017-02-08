package com.workflow.windows.view.chooser;

import java.util.List;
import java.util.Optional;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteResource;
import com.workflow.core.view.chooser.IRemoteChooserDialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class RemoteChooserDialog extends Dialog<ButtonType> implements IRemoteChooserDialog{

	private RemoteChooserPane pane;
	
	public RemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message, String... extensions){
		this(account, name, mode, editable, title, message, null, null, extensions);
	}
	
	public RemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions){
		
		setTitle(title);
		pane = new RemoteChooserPane(account, name, mode, editable, initialDirectory, initialFilename, extensions);
		
		BorderPane contentPane = new BorderPane();
		contentPane.setTop(new Label(message));
		contentPane.setCenter(pane);
		
		getDialogPane().setContent(contentPane);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
	}

	public void showDialog(SimpleHandler onAccept, SimpleHandler onCancel) {
		if(showDialogAndWait()){
			if(onAccept != null){
				onAccept.handle();
			}
		}else if(onCancel != null){
			onCancel.handle();
		}
	}

	public boolean showDialogAndWait() {
		Optional<ButtonType> response = this.showAndWait();
		return response.isPresent() && response.get().equals(ButtonType.OK);
	}
	
	public String getString(){
		return pane.getString();
	}
	
	public List<RemoteResource> getSelection() {
		return pane.getSelection();
	}

}
