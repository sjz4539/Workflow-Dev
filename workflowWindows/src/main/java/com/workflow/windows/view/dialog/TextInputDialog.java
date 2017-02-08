package com.workflow.windows.view.dialog;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.view.dialog.ITextInputDialog;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;

public class TextInputDialog extends javafx.scene.control.TextInputDialog implements ITextInputDialog{

	SimpleHandler onAccept, onCancel;
	
	public TextInputDialog(String title, String header, String message, String value){
		
		setDialogPane(new DialogPane(){
			protected Node createButton(ButtonType buttonType){
				Node newButt = super.createButton(buttonType);
				if(buttonType == ButtonType.OK){
					newButt.setOnMouseClicked(new EventHandler<MouseEvent>(){
						public void handle(MouseEvent event) {
							if(onAccept != null){
								onAccept.handle();
							}
						}
					});
				}else if(buttonType == ButtonType.CANCEL){
					newButt.setOnMouseClicked(new EventHandler<MouseEvent>(){
						public void handle(MouseEvent event) {
							if(onCancel != null){
								onCancel.handle();
							}
						}
					});
				}
				return newButt;
			}
		});
		
		setTitle(title);
		setHeaderText(header);
		setContentText(message);
		setValue(value);
		
	}
	
	public void setOnAccept(SimpleHandler handler) {
		onAccept = handler;
	}

	public void setOnCancel(SimpleHandler handler) {
		onCancel = handler;
	}

	public void setValue(String value) {
		getEditor().setText(value);
	}

	public String getValue() {
		return getEditor().getText();
	}

	public void showDialog() {
		show();
	}

}
