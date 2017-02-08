package com.workflow.android.view.chooser;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.android.R;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteResource;
import com.workflow.core.view.chooser.IRemoteChooserDialog;

public class RemoteChooserDialog extends Dialog implements IRemoteChooserDialog {

	RemoteChooserPane chooserPane;
	Button okButton, cancelButton;
	
	public RemoteChooserDialog(Context context, Account account, String name, Mode mode, boolean editable, String title, String message, String... extensions) {
		this(context, account, name, mode, editable, title, message, null, null, extensions);
	}
	
	public RemoteChooserDialog(Context context, Account account, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions){
		
		super(context);
		
		LinearLayout dialogView = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.chooser_dialog, null);
		LinearLayout contentView = (LinearLayout)dialogView.findViewById(R.id.dialog_content);
		TextView dialogMessage = (TextView)dialogView.findViewById(R.id.dialog_message);
		
		okButton = (Button)dialogView.findViewById(R.id.dialog_ok_button);
		cancelButton = (Button)dialogView.findViewById(R.id.dialog_cancel_button);
		chooserPane = (RemoteChooserPane)Core.getGuiFactory().getRemoteChooserPane(account, name, mode, editable, initialDirectory, initialFilename, extensions);
		
		dialogMessage.setText(message);
		contentView.addView(chooserPane);
		
		setTitle(title);
		setContentView(dialogView);
		
	}

	public void showDialog(final SimpleHandler onAccept, final SimpleHandler onCancel) {
		okButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if(onAccept != null){
					onAccept.handle();
				}
				dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if(onCancel != null){
					onCancel.handle();
				}
				dismiss();
			}
		});
		this.show();
	}

	@Override
	public boolean showDialogAndWait() {
		return false;
	}

	@Override
	public String getString() {
		return chooserPane.getString();
	}

	@Override
	public List<RemoteResource> getSelection() {
		return chooserPane.getSelection();
	}

}
