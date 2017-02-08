package com.workflow.core.view;

import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.ResourceList;
import com.workflow.core.controller.library.Task;
import com.workflow.core.controller.library.TaskList;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.chooser.ILocalChooserDialog;
import com.workflow.core.view.chooser.ILocalChooserPane;
import com.workflow.core.view.chooser.IRemoteChooserDialog;
import com.workflow.core.view.chooser.IRemoteChooserPane;
import com.workflow.core.view.dialog.ITextInputDialog;
import com.workflow.core.view.library.IFileResourceView;
import com.workflow.core.view.library.ILibraryMenu;
import com.workflow.core.view.library.ILibraryView;
import com.workflow.core.view.library.IResourceListView;
import com.workflow.core.view.library.IResourceView;
import com.workflow.core.view.library.ITaskListView;
import com.workflow.core.view.library.ITaskView;
import com.workflow.core.view.oauth.IOauthHandlerView;

import java.util.List;

public interface IWorkflowGUIFactory{
	
	public void displayError(String title, String header, String message, SimpleHandler onClose);
	
	public void displayError(String title, String header, String message, boolean modal);
	
	public void displayMessage(String title, String header, String message, boolean modal);
	
	public void requestConfirmation(String title, String header, String message, SimpleHandler onAccept, SimpleHandler onCancel);
	
	public void showConfigureDialog();
	
	public void showNewAccountDialog();
	
	public void showAccountListDialog();
	
	public void showNewLibraryDialog();
	
	public void showImportLocalLibraryDialog();
	
	public void showNewTaskListDialog();
	
	public void showLocalChooserDialog(
			String name, Chooser.Mode mode, boolean editable, String title,
			String message, String initialDirectory, String initialFilename,
			List<String> extensions, ArgumentHandler<String> onSuccess, SimpleHandler onCancel);

	public void showRemoteChooserDialog(
			Account account, String name, Chooser.Mode mode, boolean editable,
			String title, String message, String initialDirectory, String initialFilename,
			List<String> extensions, ArgumentHandler<String> onSuccess, SimpleHandler onCancel);

	
// Factory Functions
	
	public ILibraryView getLibraryView(Library library);
	
	public ILibraryMenu getLibraryMenu(Library library);
	
	public ITaskListView getTaskListView(TaskList taskList);
	
	public ITaskView getTaskView(Task task);
	
	public IResourceView getResourceView(Resource resource);
	
	public IResourceListView getResourceListView(ResourceList resourceList);
	
	public IFileResourceView getFileResourceView(FileResource resource);
	
	public ILocalChooserPane getLocalChooserPane(String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions);

	public IRemoteChooserPane getRemoteChooserPane(Account account, String name, Chooser.Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions);

	public IOauthHandlerView getOauthDialog();
	
	public ITextInputDialog getTextInputDialog(String title, String header, String message, String value);
}
