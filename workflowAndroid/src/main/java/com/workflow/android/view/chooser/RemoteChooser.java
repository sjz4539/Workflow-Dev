package com.workflow.android.view.chooser;

import java.util.ArrayList;
import java.util.List;

import com.workflow.android.R;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteFile;
import com.workflow.core.model.resource.remote.RemoteFolder;
import com.workflow.core.model.resource.remote.RemoteResource;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.Chooser.Mode;

import android.app.Dialog;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class RemoteChooser extends Dialog{

	private RemoteFolder root, cur;
	private Mode mode;

	private String filename, initialDirectory;
	private String[] extensions;
	
	private Button upButton, newFolderButton, okButton, cancelButton;
	private ListView itemList;
	private EditText filenameField;
	private ArrayAdapter<RemoteResource> listAdapter;
	
	public RemoteChooser(Context context, Mode selectMode, Account account){
		this(context, selectMode, "", "", null, account);
	}
	
	public RemoteChooser(Context context, Mode selectMode, String id, String fn, String[] ext, Account account){
		
		super(context);
		
		root = new RemoteFolder("", null, account);
		cur = root;
		
		mode = selectMode;
		
		initialDirectory = (id == null ? "" : id);
		filename = (fn == null ? "" : fn);
		extensions = (ext == null ? new String[]{} : ext);
		
		switch(mode){
		case MODE_SINGLE_FILE:
			setTitle("Select a file");
			break;
		case MODE_MULTIPLE_FILE:
			setTitle("Select one or more files");
			break;
		case MODE_SINGLE_FOLDER:
			setTitle("Select a folder");
			break;
		case MODE_SAVE_FILE:
			setTitle("Choose a location to save to");
			break;
		default:
			break;
		}
		
		upButton = (Button)findViewById(R.id.remote_chooser_up_button);
		newFolderButton = (Button)findViewById(R.id.remote_chooser_new_folder_button);
		okButton = (Button)findViewById(R.id.remote_chooser_ok_button);
		cancelButton = (Button)findViewById(R.id.remote_chooser_cancel_button);
		filenameField = (EditText)findViewById(R.id.remote_chooser_filename_field);
		
		if(mode == Chooser.Mode.MODE_SAVE_FILE){
			filenameField.setVisibility(View.GONE);
		}
		
		upButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if(cur != root){
					cur = cur.getParent();
					updateUI();
				}
			}
		});
		
		newFolderButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				/*
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Folder");
				dialog.setContentText("Enter a name for the new folder");
				Optional<String> name = dialog.showAndWait();
				if(name.isPresent() && name.get().length() > 0){
					if(cur.getSource().getFileOps().createFolder(cur.getSource(), cur, name.get()).getCode() == FileOpsStatus.Code.SUCCESS){
						cur.clear();
						updateUI();
					};
				}
				*/
			}
		});
		
		updateUI();
		
	}
	
	public void updateUI(){
		listAdapter.clear();
		//check the tree structure for data contained within the current active folder
		if(cur.getFolders() != null){
			for(RemoteFolder f : cur.getFolders()){
				listAdapter.add(f);
			}
		}
		if(cur.getFiles() != null && mode != Chooser.Mode.MODE_SINGLE_FOLDER){
			for(RemoteFile f : cur.getFiles()){
				if(extensions.length == 0){
					listAdapter.add(f);
				}else{
					for(String ext : extensions){
						if(f.getName().endsWith(ext)){
							listAdapter.add(f);
						}
					}
				}
			}
		}
		listAdapter.notifyDataSetChanged();
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
	
	public ArrayList<RemoteResource> getSelection(){
		ArrayList<RemoteResource> sel = new ArrayList<RemoteResource>();
		
		switch(mode){
		
			case MODE_SINGLE_FILE:
				if(itemList.getCheckedItemPosition() != ListView.INVALID_POSITION && !listAdapter.getItem(itemList.getCheckedItemPosition()).isDirectory()){
					sel.add(listAdapter.getItem(itemList.getCheckedItemPosition()));
				}
				break;
			case MODE_MULTIPLE_FILE:
				SparseBooleanArray items = itemList.getCheckedItemPositions();
				for(int i = 0; i < itemList.getCount(); i++){
					if( items.get(i) == true && !listAdapter.getItem(i).isDirectory()){
						sel.add(listAdapter.getItem(i));
					}
				}
				break;
			case MODE_SINGLE_FOLDER:
				if((itemList.getCheckedItemPosition() != ListView.INVALID_POSITION && listAdapter.getItem(itemList.getCheckedItemPosition()).isDirectory())){
					sel.add(listAdapter.getItem(itemList.getCheckedItemPosition()));
				}
				break;
			case MODE_SAVE_FILE:
				sel.add(new RemoteFile(filenameField.getText().toString(), cur, cur.getSource()));
				break;
			default:
				break;
				
		}
		
		return sel;
	}
	
}
