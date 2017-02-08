package com.workflow.android.view.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.Core;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteResource;
import com.workflow.core.view.chooser.IRemoteChooserPane;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemoteChooserPane extends LinearLayout implements IRemoteChooserPane {
	
	private Account account;
	
	private RemoteChooser dialog;
	private Button browseButton;
	private TextView nameLabel;
	private EditText textField;
	
	public RemoteChooserPane(Context context, Account a, String name, Mode mode, boolean editable){
		this(context, a, name, mode, editable, null, null, (String[])null);
	}
	
	public RemoteChooserPane(Context context, Account a, String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions){
		super(context);
		account = a;
		
		LinearLayout layout = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.chooser_pane_content, null);
		
		browseButton = (Button)layout.findViewById(R.id.chooser_pane_browse_button);
		
		nameLabel = (TextView)layout.findViewById(R.id.chooser_pane_name);
		nameLabel.setText(name);
		
		textField = (EditText)layout.findViewById(R.id.chooser_pane_text_field);
		textField.setFocusable(editable);
		textField.setEnabled(editable);
		textField.setCursorVisible(editable);
		textField.setText(
			initialDirectory != null && initialFilename != null ? ( 
				(
					initialDirectory.endsWith(File.separator) && !initialDirectory.equals(File.separator) ? 
					initialDirectory.substring(0, initialDirectory.length() - 1) : 
					initialDirectory
				) + (
					initialFilename.startsWith(File.separator) ?
					initialFilename.substring(1) : 
					initialFilename
				) 
			) : 
			""
		);

		browseButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				Core.getGuiFactory().displayMessage(null, null, "NYI", true);

				/*
				
				if(account != null){
				
				switch(mode){
					case MODE_SAVE_FILE:
						
						dialog = new RemoteChooser(context, mode, initialDirectory, initialFilename, extensions, account);
						if(dialog.showAndWait()){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
						
						break;
					case MODE_SINGLE_FILE:

						dialog = new RemoteChooser(context, mode, initialDirectory, initialFilename, extensions, account);
						if(dialog.showAndWait()){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
								
						break;
					case MODE_MULTIPLE_FILE:
						
						dialog = new RemoteChooser(context, mode, initialDirectory, "", extensions, account);
						if(dialog.showAndWait()){
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
						
						dialog = new RemoteChooser(context, mode, initialDirectory, "", null, account);
						if(dialog.showAndWait()){
							if(dialog.getSelection().size() > 0){
								textField.setText(dialog.getSelection().get(0).getPath());
							}
						}
						
						break;
					default:
						break;
					}
				}
				
				*/
			}
		});
		
		addView(layout);
	}
	
	@Override
	public String getString() {
		return textField.getText().toString();
	}
	
	public List<RemoteResource> getSelection(){
		return dialog != null ? dialog.getSelection() : new ArrayList<RemoteResource>();
	}
	
	public void setAccount(Account a){
		account = a;
		setEnabled(account != null);
	}

	@Override
	public void setEnabled(boolean enable) {
		if(textField != null && browseButton != null){
			textField.setFocusable(enable);
			textField.setEnabled(enable);
			textField.setCursorVisible(enable);
			browseButton.setEnabled(enable);
		}
	}

}
