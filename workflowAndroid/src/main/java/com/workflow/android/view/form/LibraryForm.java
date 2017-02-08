package com.workflow.android.view.form;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.android.view.chooser.LocalChooserPane;
import com.workflow.android.view.chooser.RemoteChooserPane;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.LocalLibrary;
import com.workflow.core.controller.library.RemoteLibrary;
import com.workflow.core.controller.library.RemovableLibrary;
import com.workflow.core.model.account.Account;
import com.workflow.core.view.chooser.Chooser;

public abstract class LibraryForm extends LinearLayout{
	
	protected LinearLayout content;
	
	public LibraryForm(Context context) {
		super(context);
	}

	public abstract Library makeLibrary();
	
	public static class LocalLibraryForm extends LibraryForm{

		private LocalLibrary library;
		
		private EditText local_libraryname;
		private LocalChooserPane local_libraryroot;
		private EditText local_librarypath;
		
		public LocalLibraryForm(Context context, LocalLibrary l) {
			super(context);
			
			library = l;
			
			content = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_form_local, null);
			
			local_libraryname = (EditText)content.findViewById(R.id.library_form_local_name);
			local_libraryroot = new LocalChooserPane(context, "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, (library != null ? library.getRoot() : null), null);
			((LinearLayout)content.findViewById(R.id.library_form_local_root_pane)).addView(local_libraryroot);
			local_librarypath = (EditText)content.findViewById(R.id.library_form_local_path);
			
			addView(content);
			
		}

		public void updateUI(){
			local_libraryname.setText(library != null ? library.getName() : "Workflow Library");
			local_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.RESOURCE_FILE_EXTENSION);
		}

		public Library makeLibrary() {
			if(library == null){
				return new LocalLibrary(
					local_libraryname.getText().toString(), 
					(local_libraryroot.getSelection().size() > 0 ? local_libraryroot.getSelection().get(0).getAbsolutePath() : ""), 
					local_librarypath.getText().toString()
				);
			}else{
				return library;
			}
		}
		
	}
	
	public static class RemovableLibraryForm extends LibraryForm{

		private EditText removable_libraryname;
		private LocalChooserPane removable_libraryroot;
		private EditText removable_librarypath;
		
		private RemovableLibrary library;
		
		public RemovableLibraryForm(Context context, RemovableLibrary l) {
			super(context);
			
			library = l;
			
			content = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_form_removable, null);
			
			removable_libraryname = (EditText)content.findViewById(R.id.library_form_removable_name);
			removable_libraryroot = new LocalChooserPane(context, "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, (library != null ? library.getRoot() : null), null);
			((LinearLayout)content.findViewById(R.id.library_form_removable_root_pane)).addView(removable_libraryroot);
			removable_librarypath = (EditText)content.findViewById(R.id.library_form_removable_path);
			
			addView(content);
		}
		
		public void updateUI(){
			removable_libraryname.setText(library != null ? library.getName() : "Workflow Library");
			removable_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
		}

		public Library makeLibrary() {
			if(library == null){
				return new RemovableLibrary(
					removable_libraryname.getText().toString(), 
					(removable_libraryroot.getSelection().size() > 0 ? removable_libraryroot.getSelection().get(0).getAbsolutePath() : ""), 
					removable_librarypath.getText().toString()
				);
			}else{
				return library;
			}
		}
		
	}
	
	public static class RemoteLibraryForm extends LibraryForm{

		private Spinner remote_accountlist;
		private Button remote_newaccountbutton;
		private EditText remote_libraryname;
		private RemoteChooserPane remote_libraryroot;
		private EditText remote_librarypath;
		
		private RemoteLibrary library;
		private ArrayAdapter<Account> accountAdapter;
		private Account.AccountType accountType;
		
		public RemoteLibraryForm(final Context context, RemoteLibrary l) {
			super(context);
			
			library = l;
			
			content = (LinearLayout)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_form_remote, null);
		
			remote_accountlist = (Spinner)content.findViewById(R.id.library_form_remote_account);
			remote_accountlist.setEnabled(library == null);
			
			accountAdapter = new ArrayAdapter<Account>(context, android.R.layout.simple_spinner_item, Core.getCore().getAccounts());
			remote_accountlist.setAdapter(accountAdapter);
			remote_accountlist.setOnItemSelectedListener(new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					remote_libraryroot.setAccount(accountAdapter.getItem(position));
				}

				public void onNothingSelected(AdapterView<?> parent) {
					remote_libraryroot.setAccount(null);
				}
			});
			
			remote_newaccountbutton = (Button)content.findViewById(R.id.library_form_remote_new_account_button);
			remote_newaccountbutton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Core.getGuiFactory().showNewAccountDialog();
					updateUI();
				}
			});
			
			remote_libraryname = (EditText)content.findViewById(R.id.library_form_remote_name);
			
			remote_libraryroot = (library != null ? 
				new RemoteChooserPane(context, library.getAccount(), "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, (library != null ? library.getRoot() : null), null) :
				new RemoteChooserPane(context, null, "Library Root:", Chooser.Mode.MODE_SINGLE_FOLDER, false, null, null)
			);
			remote_libraryroot.setEnabled(library == null);
			((LinearLayout)content.findViewById(R.id.library_form_remote_root_pane)).addView(remote_libraryroot);
			
			remote_librarypath = (EditText)content.findViewById(R.id.library_form_remote_path);
			remote_librarypath.setEnabled(library == null);
			
			addView(content);
			
		}
		
		public void updateUI(){
			if(accountType != null){
				accountAdapter.clear();
				for(Account account : Core.getCore().getAccounts()){
					if(account.getType() == accountType){
						accountAdapter.add(account);
					}
				}
			}
			if(library != null && accountAdapter.getPosition(library.getAccount()) != -1){
				remote_accountlist.setSelection(accountAdapter.getPosition(library.getAccount()));
			}
			remote_libraryname.setText(library != null ? library.getName() : "Workflow Library");
			remote_librarypath.setText(library != null ? library.getResource().getPath() : Library.DEFAULT_LIBRARY_FILENAME + Library.RESOURCE_FILE_EXTENSION);
		}

		public Library makeLibrary() {
			if(library == null){
				return new RemoteLibrary(
					remote_libraryname.getText().toString(),
					remote_libraryroot.getString(),
					remote_librarypath.getText().toString(),
					accountAdapter.getItem(remote_accountlist.getSelectedItemPosition())
				);
			}else{
				library.setName(remote_libraryname.getText().toString());
				return library;
			}
		}
		
		public void setAccountType(Account.AccountType type){
			accountType = type;
			updateUI();
		}
		
	}
}
