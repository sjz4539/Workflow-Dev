package com.workflow.android.view.chooser;

import java.io.File;
import java.util.List;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.view.chooser.ILocalChooserDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocalChooserDialog extends Dialog implements ILocalChooserDialog {

	LocalChooserPane chooserPane;
	Button okButton, cancelButton;
	
	public LocalChooserDialog(Context context, String name, Mode mode, boolean editable, String title, String message, String... extensions) {
		this(context, name, mode, editable, title, message, null, null, extensions);
	}
	
	public LocalChooserDialog(Context context, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions){
		
		super(context);
		
		LinearLayout dialogView = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.chooser_dialog, null);
		LinearLayout contentView = (LinearLayout)dialogView.findViewById(R.id.dialog_content);
		TextView dialogMessage = (TextView)dialogView.findViewById(R.id.dialog_message);
		
		okButton = (Button)dialogView.findViewById(R.id.dialog_ok_button);
		cancelButton = (Button)dialogView.findViewById(R.id.dialog_cancel_button);
		chooserPane = (LocalChooserPane)Core.getGuiFactory().getLocalChooserPane(name, mode, editable, initialDirectory, initialFilename, extensions);
		
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

	public boolean showDialogAndWait() {
		return false;
	}

	public String getString() {
		return chooserPane.getString();
	}

	public List<File> getSelection() {
		return chooserPane.getSelection();
	}

}
