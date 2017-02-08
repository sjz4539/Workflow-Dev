package com.workflow.android.view.chooser;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.Core;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.ILocalChooserPane;

public class LocalChooserPane extends LinearLayout implements ILocalChooserPane {

	private ArrayList<File> selection = new ArrayList<File>();
	
	private Button browseButton;
	private TextView nameLabel;
	private EditText textField;
	
	public LocalChooserPane(Context context, String name, Chooser.Mode mode, boolean editable){
		this(context, name, mode, editable, null, null, (String[])null);
	}
	
	public LocalChooserPane(Context context, String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... filters){
		super(context);
		
		LinearLayout layout = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.chooser_pane_content, this);
		
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
				
				File result;
				List<File> resultList; 
				switch(mode){
					case MODE_SAVE_FILE:
						FileChooser saveFileChooser = new FileChooser();
						saveFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						saveFileChooser.setInitialFileName((initialFilename != null && initialFilename.length() > 0 ? initialFilename : ""));

						result = saveFileChooser.showSaveDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
						}
						break;
					case MODE_SINGLE_FILE:
						FileChooser singleFileChooser = new FileChooser();
						singleFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						singleFileChooser.setInitialFileName((initialFilename != null && initialFilename.length() > 0 ? initialFilename : ""));
						if(filters != null){
							ExtensionFilter[] filArr = new ExtensionFilter[filters.length];
							for(int i = 0; i < filters.length; i++){
								filArr[i] = new ExtensionFilter(filters[i], filters[i]);
							}
							singleFileChooser.getExtensionFilters().addAll(filArr);
						}
						result = singleFileChooser.showOpenDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
						}
						break;
					case MODE_MULTIPLE_FILE:
						FileChooser multipleFileChooser = new FileChooser();
						multipleFileChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						resultList = multipleFileChooser.showOpenMultipleDialog(WorkflowGUI.getStage());
						if(filters != null){
							ExtensionFilter[] filArr = new ExtensionFilter[filters.length];
							for(int i = 0; i < filters.length; i++){
								filArr[i] = new ExtensionFilter(filters[i], filters[i]);
							}
							multipleFileChooser.getExtensionFilters().addAll(filArr);
						}
						if(resultList != null){
							selection.clear();
							selection.addAll(resultList);
							
							String str = "";
							for(int i = 0; i < selection.size(); i++){
								str += selection.get(i).getAbsolutePath();
								str += (i < selection.size() - 1 ? "," : "");
							}
							textField.setText(str);
						}
						break;
					case MODE_SINGLE_FOLDER:
						DirectoryChooser singleDirectoryChooser = new DirectoryChooser();
						singleDirectoryChooser.setInitialDirectory((initialDirectory != null && initialDirectory.length() > 0 ? new File(initialDirectory) : null));
						result = singleDirectoryChooser.showDialog(WorkflowGUI.getStage());
						if(result != null){
							selection.clear();
							selection.add(result);
							textField.setText(selection.size() > 0 ? selection.get(0).getAbsolutePath() : "");
						}
						break;
					default:
						break;
				}
				
				*/
				
			}
		});
		
	}
	
	@Override
	public String getString() {
		return textField.getText().toString();
	}

	@Override
	public ArrayList<File> getSelection() {
		// TODO Auto-generated method stub
		return selection;
	}

}
