package com.workflow.android.view.dialog;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.view.dialog.ITextInputDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TextInputDialog extends AlertDialog implements ITextInputDialog{

	EditText textField;
	
	public TextInputDialog(Context context) {
		super(context);
		textField = new EditText(context);
		setView(textField);
	}

	public void setOnAccept(final SimpleHandler handler){
		getButton(BUTTON_POSITIVE).setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				handler.handle();
			}
		});
	}
	
	public void setOnCancel(final SimpleHandler handler){
		getButton(BUTTON_NEGATIVE).setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				handler.handle();
			}
		});
	}
	
	@Override
	public void setValue(String value) {
		textField.setText(value);
	}

	@Override
	public String getValue() {
		return textField.getText().toString();
	}

	@Override
	public void showDialog() {
		show();
	}

}
