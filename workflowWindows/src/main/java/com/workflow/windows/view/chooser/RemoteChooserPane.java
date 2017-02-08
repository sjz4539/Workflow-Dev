package com.workflow.windows.view.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.io.FileOpsStatus;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteFile;
import com.workflow.core.model.resource.remote.RemoteResource;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.IRemoteChooserPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class RemoteChooserPane extends HBox implements IRemoteChooserPane{

	private TextField textField;
	private Button button;
	private RemoteChooser dialog;
	
	private Account account;
	private Chooser.Mode mode;
	private String initialDirectory, initialFilename;
	private String[] extensions;
	
	public RemoteChooserPane(Account a, String name, Chooser.Mode mode, boolean editable){
		this(a, name, mode, editable, null, null, (String[])null);
	}
	
	public RemoteChooserPane(Account a, String name, Chooser.Mode m, boolean editable, String iDirectory, String iFilename, String... exts){

		setSpacing(2);
		setAccount(a);
		mode = m;
		initialDirectory = iDirectory;
		initialFilename = iFilename;
		extensions = exts;
		
		textField = new TextField( 
				iDirectory != null && iFilename != null ? (
				(
					iDirectory.endsWith(File.separator) || iDirectory.equals(File.separator) ? 
					iDirectory : 
					iDirectory + File.separator
				) + (
					iFilename.startsWith(File.separator) ?
					iFilename.substring(1) : 
					iFilename
				)
			) : ""
		);
		textField.setEditable(editable);
		
		button = new Button("Browse...");
		button.setOnAction((on_click)->{
				
			if(account != null){
				if(account.getFileOps().validateOauth(account).getCode() == FileOpsStatus.Code.SUCCESS){
					showChooser();
				}else{
					account.getAuth(
						new SimpleHandler(){
							public void handle() {
								showChooser();
							}
						},
						null
					);
				}
			}
		});
		
		getChildren().addAll(
				new Label(name),
				textField,
				button
		);
		
		setEnabled(account != null);
	}
	
	private void showChooser(){
		Optional<ButtonType> response;
		
		switch(mode){
			case MODE_SAVE_FILE:
				
				dialog = new RemoteChooser(mode, initialDirectory, initialFilename, extensions, account);
				response = dialog.showAndWait();
				if(response.isPresent() && response.get().equals(RemoteChooser.save)){
					if(dialog.getSelection().size() > 0){
						textField.setText(dialog.getSelection().get(0).getPath());
					}
				}
				
				break;
			case MODE_SINGLE_FILE:

				dialog = new RemoteChooser(mode, initialDirectory, initialFilename, extensions, account);
				response = dialog.showAndWait();
				if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
					if(dialog.getSelection().size() > 0){
						textField.setText(dialog.getSelection().get(0).getPath());
					}
				}
						
				break;
			case MODE_MULTIPLE_FILE:
				
				dialog = new RemoteChooser(mode, initialDirectory, "", extensions, account);
				response = dialog.showAndWait();
				if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
					String str = "", part = "";
					for(int i = 0; i < dialog.getSelection().size(); i++){
						part = dialog.getSelection().get(i).getPath();
						str += part;
						str += (i < dialog.getSelection().size() - 1 ? "," : "");
					}
					textField.setText(str);
				}
				
				break;
			case MODE_SINGLE_FOLDER:
				
				dialog = new RemoteChooser(mode, initialDirectory, "", null, account);
				response = dialog.showAndWait();
				if(response.isPresent() && response.get().equals(RemoteChooser.ok)){
					if(dialog.getSelection().size() > 0){
						textField.setText(dialog.getSelection().get(0).getPath());
					}
				}
				
				break;
			default:
				break;
		}
	}
	
	public String getString(){
		return textField.getText();
	}
	
	public List<RemoteResource> getSelection(){
		return dialog != null ? dialog.getSelection() : new ArrayList<RemoteResource>();
	}
	
	public void setAccount(Account a){
		account = a;
		setEnabled(account != null);
	}
	
	public void setEnabled(boolean enable){
		if(textField != null && button != null){
			textField.setEditable(enable);
			button.setDisable(!enable);
		}
	}

}
