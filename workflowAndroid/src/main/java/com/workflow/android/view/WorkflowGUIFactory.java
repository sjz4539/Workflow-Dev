package com.workflow.android.view;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.workflow.android.R;
import com.workflow.android.controller.ActivityRegistry;
import com.workflow.android.view.chooser.RemoteChooserPane;
import com.workflow.android.view.chooser.RemoteChooserDialog;
import com.workflow.android.view.chooser.LocalChooserPane;
import com.workflow.android.view.chooser.LocalChooserDialog;
import com.workflow.android.view.dialog.LibraryDialog;
import com.workflow.android.view.dialog.TextInputDialog;
import com.workflow.android.view.form.*;
import com.workflow.android.view.library.LibraryListView;
import com.workflow.android.view.library.LibraryMenu;
import com.workflow.android.view.library.LibraryView;
import com.workflow.android.view.resource.FileResourceView;
import com.workflow.android.view.resource.ResourceListView;
import com.workflow.android.view.resource.ResourceView;
import com.workflow.android.view.task.TaskDetailView;
import com.workflow.android.view.task.TaskListView;
import com.workflow.android.view.task.TaskView;
import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.ResourceList;
import com.workflow.core.controller.library.Task;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.IWorkflowGUIFactory;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.ILocalChooserDialog;
import com.workflow.core.view.chooser.ILocalChooserPane;
import com.workflow.core.view.chooser.IRemoteChooserDialog;
import com.workflow.core.view.chooser.IRemoteChooserPane;
import com.workflow.core.view.chooser.Chooser.Mode;
import com.workflow.core.view.dialog.ITextInputDialog;
import com.workflow.core.view.library.IFileResourceView;
import com.workflow.core.view.library.ILibraryMenu;
import com.workflow.core.view.library.ILibraryView;
import com.workflow.core.view.library.IResourceListView;
import com.workflow.core.view.library.IResourceView;
import com.workflow.core.view.library.ITaskListView;
import com.workflow.core.view.library.ITaskView;
import com.workflow.core.view.oauth.IOauthHandlerView;

public class WorkflowGUIFactory implements IWorkflowGUIFactory{

	
//==============================================
//			 Dialog/Input Functions
//==============================================
	
	public void displayMessage(String title, String header, String message, boolean modal) {
		AlertDialog.Builder builder = new AlertDialog.Builder(WorkflowGUI.getContext());
		AlertDialog dialog = builder.setTitle(title).setMessage(message).setCancelable(false).create();
		dialog.show();
	}

	public void displayError(String title, String header, String message, boolean modal) {
		AlertDialog.Builder builder = new AlertDialog.Builder(WorkflowGUI.getContext());
		AlertDialog dialog = builder.setIcon(android.R.drawable.stat_sys_warning).setTitle(title).setMessage(message).setCancelable(false).create();
		dialog.show();
	}
	
	@Override
	public void displayError(String title, String header, String message, final SimpleHandler onClose) {
		AlertDialog.Builder builder = new AlertDialog.Builder(WorkflowGUI.getContext());
		AlertDialog dialog = builder.setIcon(android.R.drawable.stat_sys_warning)
									.setTitle(title)
									.setMessage(message)
									.setCancelable(false)
									.setPositiveButton("Ok", new OnClickListener(){
										public void onClick(DialogInterface arg0, int arg1) {
											onClose.handle();
										}
									})
									.create();
		dialog.show();
	}

	@Override
	public void requestConfirmation(String title, String header, String message, final SimpleHandler onAccept, final SimpleHandler onCancel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(WorkflowGUI.getContext());
		AlertDialog dialog = builder.setTitle(title)
									.setMessage(message)
									.setPositiveButton("Ok", new OnClickListener(){
										public void onClick(DialogInterface arg0, int arg1) {
											onAccept.handle();
										}
									})
									.setNegativeButton("Cancel", new OnClickListener(){
										public void onClick(DialogInterface arg0, int arg1) {
											onCancel.handle();
										}
									})
									.create();
		dialog.show();
	}

	public void showConfigureDialog() {
		final PropertiesForm propForm = (PropertiesForm)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.configure_dialog_form, null);
		new AlertDialog.Builder(WorkflowGUI.getContext())
			.setTitle("Configure Workflow")
			.setView(propForm)
			.setPositiveButton("Save", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					 propForm.saveProperties();
				}
			})
			.setNegativeButton("Cancel", null)
			.create()
			.show();
	}

	public void showNewAccountDialog() {
		final AccountForm accForm = (AccountForm)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.account_form, null);
		new AlertDialog.Builder(WorkflowGUI.getContext())
			.setTitle("Configure/Create Account")
			.setView(accForm)
			.setPositiveButton("Save", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					 accForm.makeAccount();
				}
			})
			.setNegativeButton("Cancel", null)
			.create()
			.show();
	}

	public void showAccountListDialog() {
		// TODO Auto-generated method stub
		
	}

	public void showNewLibraryDialog() {
		new LibraryDialog().show(((WorkflowGUI)Core.getGui()).getFragmentManager(), "Library Dialog Fragment");
	}

	public void showImportLocalLibraryDialog() {
		// TODO Auto-generated method stub
		
	}

	public void showNewTaskListDialog() {
		if(Core.getCore().getCurrentLibrary() != null){
			final EditText textField = new EditText(WorkflowGUI.getContext());
			AlertDialog dialog = new AlertDialog.Builder(WorkflowGUI.getContext())
				.setTitle("New Task List...")
				.setView(textField)
				.setPositiveButton("Create", new OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						Core.getCore().getCurrentLibrary().addTaskList(new TaskList(Core.getCore().getCurrentLibrary(), textField.getText().toString()));
					}
				})
				.create();
			dialog.show();
		}
	}

	/**
	 * Displays a new activity for the user to select or specify a local file or directory path.
	 * @param name
	 * @param mode The selection mode this chooser should operate in. See Chooser.Mode.
	 * @param editable If true, the currently selected path can be edited directly, allowing custom paths.
	 * @param title The title of this activity.
	 * @param message A message to be displayed within this new activity, describing the purpose of the selected location.
	 * @param initialDirectory The directory this chooser should initialize to.
	 * @param initialFilename The filename this chooser should initialize to.
	 * @param extensions A list of valid file extensions. This will filter visible files.
     * @param onSuccess A handler to execute if the user clicks ok. The argument provided will be the selected file/directory path. May be null.
     * @param onCancel A handler to execute if the user returns without selecting a file/directory. May be null.
     */
	public void showLocalChooserDialog(String name, Mode mode, boolean editable, String title, String message,
									   String initialDirectory, String initialFilename, List<String> extensions,
									   ArgumentHandler<String> onSuccess, SimpleHandler onCancel) {

		//register handlers with catch-all return point
		ActivityRegistry.register(0, new ArgumentHandler<Intent>(){
			public void handle(Intent arg) {
				onSuccess.handle("");
			}
		}, onCancel);
		//spawn filechooser activity
	}

	/**
	 * Displays a new activity for the user to select or specify a remote file or directory path.
	 * @param account The remote storage account this chooser should use.
	 * @param name
	 * @param mode The selection mode this chooser should operate in. See Chooser.Mode.
	 * @param editable If true, the currently selected path can be edited directly, allowing custom paths.
	 * @param title The title of this activity.
	 * @param message A message to be displayed within this new activity, describing the purpose of the selected location.
	 * @param initialDirectory The directory this chooser should initialize to.
	 * @param initialFilename The filename this chooser should initialize to.
	 * @param extensions A list of valid file extensions. This will filter visible files.
	 * @param onSuccess A handler to execute if the user clicks ok. The argument provided will be the selected file/directory path. May be null.
	 * @param onCancel A handler to execute if the user returns without selecting a file/directory. May be null.
	 */
	public void showRemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message,
										String initialDirectory, String initialFilename, List<String> extensions,
										ArgumentHandler<String> onSuccess, SimpleHandler onCancel) {

		//register handlers with catch-all return point
		ActivityRegistry.register(0, new ArgumentHandler<Intent>(){
			public void handle(Intent arg) {
				onSuccess.handle("");
			}
		}, onCancel);
		//spawn filechooser activity
	}

	public boolean showUnsavedResourcesDialog() {
		return false;
		// TODO Auto-generated method stub
	}

	public boolean showCachedFilesDialog() {
		return false;
		// TODO Auto-generated method stub
	}

//==============================================
//				Factory Functions
//==============================================
	
	public ILibraryView getLibraryView(Library library) {
		return (LibraryView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_view, null);
	}

	public ILibraryMenu getLibraryMenu(Library library) {
		return (LibraryMenu)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.library_menu, null);
	}

	public ITaskListView getTaskListView(TaskList taskList) {
		return (TaskListView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.task_list_view, null);
	}

	public ITaskView getTaskView(Task task) {
		return (TaskView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.task_view, null);
	}
	
	public TaskDetailView getTaskDetailView(Task task){
		return (TaskDetailView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.task_detail_view, null);
	}

	public IResourceListView getResourceListView(ResourceList resourceList) {
		return (ResourceListView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.resource_list_view, null);
	}
	
	public IResourceView getResourceView(Resource resource) {
		return (ResourceView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.resource_view, null);
	}

	public IFileResourceView getFileResourceView(FileResource resource) {
		return (FileResourceView)((WorkflowGUI)Core.getGui()).getLayoutInflater().inflate(R.layout.file_resource_view, null);
	}

	public ILocalChooserPane getLocalChooserPane(String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserPane(WorkflowGUI.getContext(), name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public ILocalChooserDialog getLocalChooserDialog(String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserDialog(WorkflowGUI.getContext(), name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserPane getRemoteChooserPane(Account account, String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserPane(WorkflowGUI.getContext(), account, name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserDialog getRemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserDialog(WorkflowGUI.getContext(), account, name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public IOauthHandlerView getOauthDialog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ITextInputDialog getTextInputDialog(String title, String header, String message, String value) {
		
		TextInputDialog dialog = new TextInputDialog(WorkflowGUI.getContext());
		
		if(title != null){
			dialog.setTitle(title);
		}
		if(message != null){
			dialog.setMessage(message);
		}
		
		return dialog;
	}

}
