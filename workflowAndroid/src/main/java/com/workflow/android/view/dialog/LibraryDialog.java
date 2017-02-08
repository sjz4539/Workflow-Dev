package com.workflow.android.view.dialog;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.android.view.form.LibraryForm;
import com.workflow.android.R;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.LocalLibrary;
import com.workflow.core.controller.library.RemoteLibrary;
import com.workflow.core.controller.library.RemovableLibrary;
import com.workflow.core.model.account.Account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class LibraryDialog extends DialogFragment{

	private LibraryForm.LocalLibraryForm localForm;
	private LibraryForm.RemovableLibraryForm removableForm;
	private LibraryForm.RemoteLibraryForm remoteForm;
	private Spinner libType;
	private ArrayAdapter<String> adapter;
	
	private LinearLayout content;
	private LocalLibrary local_library;
	private RemovableLibrary removable_library;
	private RemoteLibrary remote_library;
	
	private Library library;
	
	final String libOptDropbox = "Dropbox";
	final String libOptGoogle = "Google Drive";
	final String libOptRemovable = "Removable";
	final String libOptLocal = "Local";
	
	public LibraryDialog(){
		generateUI(WorkflowGUI.getContext());
	}
	
	public LibraryDialog(LocalLibrary l){
		local_library = l;
		library = l;
		generateUI(WorkflowGUI.getContext());
	}
	
	public LibraryDialog(RemovableLibrary l){
		removable_library = l;
		library = l;
		generateUI(WorkflowGUI.getContext());
	}
	
	public LibraryDialog(RemoteLibrary l){
		remote_library = l;
		library = l;
		generateUI(WorkflowGUI.getContext());
	}
	
	public void generateUI(Context context) {
		
		content = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_form, null);
		
		localForm = new LibraryForm.LocalLibraryForm(context, local_library);
		removableForm = new LibraryForm.RemovableLibraryForm(context, removable_library);
		remoteForm = new LibraryForm.RemoteLibraryForm(context, remote_library);
		
		content.addView(localForm);
		content.addView(removableForm);
		removableForm.setVisibility(View.GONE);
		content.addView(remoteForm);
		remoteForm.setVisibility(View.GONE);
		
		libType = (Spinner)content.findViewById(R.id.library_form_library_types);
		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new String[]{libOptDropbox, libOptGoogle, libOptRemovable, libOptLocal});
		libType.setAdapter(adapter);
		libType.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(adapter.getItem(position).compareTo(libOptDropbox) == 0){
						remoteForm.setVisibility(View.VISIBLE);
						remoteForm.setAccountType(Account.AccountType.ACCOUNT_TYPE_DROPBOX);
						removableForm.setVisibility(View.GONE);
						localForm.setVisibility(View.GONE);
						
				}else if(adapter.getItem(position).compareTo(libOptGoogle) == 0){
						remoteForm.setVisibility(View.VISIBLE);
						remoteForm.setAccountType(Account.AccountType.ACCOUNT_TYPE_GOOGLE);
						removableForm.setVisibility(View.GONE);
						
						localForm.setVisibility(View.GONE);
				}else if(adapter.getItem(position).compareTo(libOptRemovable) == 0){
						remoteForm.setVisibility(View.GONE);
						removableForm.setVisibility(View.VISIBLE);
						localForm.setVisibility(View.GONE);
						
				}else if(adapter.getItem(position).compareTo(libOptLocal) == 0){
						remoteForm.setVisibility(View.GONE);
						removableForm.setVisibility(View.GONE);
						localForm.setVisibility(View.VISIBLE);
						
				}else{
						remoteForm.setVisibility(View.GONE);
						removableForm.setVisibility(View.GONE);
						localForm.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				libType.setSelection(0);
			}
			
		});
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		return new AlertDialog.Builder(WorkflowGUI.getContext())
			.setTitle(library == null ? "Create Library" : "Edit Library")
			.setView(content)
			.setPositiveButton(library == null ? "Create" : "Save", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					if(library == null){
						Core.getCore().addLibrary(makeLibrary(), true);
					}else{
						//TODO: Save changes to given library.
					}
				}
			})
			.setNegativeButton("Cancel", null)
			.create();
		
	}
	
	public Library makeLibrary(){
		
		Library newLib = null;
		String type = (String)libType.getSelectedItem();
		
		if(type.compareTo(libOptDropbox) == 0){
			newLib = remoteForm.makeLibrary();
			
		}else if(type.compareTo(libOptGoogle) == 0){
			newLib = remoteForm.makeLibrary();
			
		}else if(type.compareTo(libOptRemovable) == 0){
			newLib = removableForm.makeLibrary();
				
		}else if(type.compareTo(libOptLocal) == 0){
			newLib = localForm.makeLibrary();
				
		}
		
		return newLib;
	}
	
}
