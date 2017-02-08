package com.workflow.windows.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.RemovableLibrary;
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
import com.workflow.windows.view.account.AccountListView;
import com.workflow.windows.view.chooser.LocalChooserDialog;
import com.workflow.windows.view.chooser.LocalChooserPane;
import com.workflow.windows.view.chooser.RemoteChooserDialog;
import com.workflow.windows.view.chooser.RemoteChooserPane;
import com.workflow.windows.view.dialog.AccountDialog;
import com.workflow.windows.view.dialog.LibraryDialog;
import com.workflow.windows.view.dialog.TextInputDialog;
import com.workflow.windows.view.form.NewTaskListForm;
import com.workflow.windows.view.form.PropertiesForm;
import com.workflow.windows.view.library.FileResourceView;
import com.workflow.windows.view.library.LibraryMenu;
import com.workflow.windows.view.library.LibraryView;
import com.workflow.windows.view.library.ResourceListView;
import com.workflow.windows.view.library.ResourceView;
import com.workflow.windows.view.library.TaskListView;
import com.workflow.windows.view.library.TaskView;
import com.workflow.windows.view.oauth.OauthHandlerView;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class WorkflowGUIFactory implements IWorkflowGUIFactory{

	public void displayError(String title, String header, String message, SimpleHandler onClose){
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.ERROR);
			if(title != null){
				alert.setTitle(title);
			}
			if(header != null){
				alert.setHeaderText(header);
			}
			if(message != null){
				alert.setContentText(message);
			}
			if(onClose != null){
				alert.setOnCloseRequest(new EventHandler<DialogEvent>(){
					public void handle(DialogEvent event) {
						onClose.handle();
					}
				});
			}
			alert.show();
		});
	}
	
	public void displayError(String title, String header, String message, boolean modal){
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.ERROR);
			if(title != null){
				alert.setTitle(title);
			}
			if(header != null){
				alert.setHeaderText(header);
			}
			if(message != null){
				alert.setContentText(message);
			}
			if(modal){
				alert.showAndWait();
			}else{
				alert.show();
			}
		});
	}
	
	public void displayMessage(String title, String header, String message, boolean modal){
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION);
			if(title != null){
				alert.setTitle(title);
			}
			if(header != null){
				alert.setHeaderText(header);
			}
			if(message != null){
				alert.setContentText(message);
			}
			if(modal){
				alert.showAndWait();
			}else{
				alert.show();
			}
		});
	}
	
	public void requestConfirmation(String title, String header, String message, SimpleHandler onSuccess, SimpleHandler onFailure){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		if(title != null){
			alert.setTitle(title);
		}
		if(header != null){
			alert.setHeaderText(header);
		}
		if(message != null){
			alert.setContentText(message);
		}
		
		Optional<ButtonType> response = alert.showAndWait();
		
		if(response.isPresent() && response.get().equals(ButtonType.OK)){
			onSuccess.handle();
		}else{
			onFailure.handle();
		}
	}
	
	public void showConfigureDialog(){
		Platform.runLater(()->{
			PropertiesForm propForm = new PropertiesForm();
			Dialog<ButtonType> propDialog = new Dialog<ButtonType>();
			propDialog.getDialogPane().setContent(propForm);
			ButtonType ok = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
			ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			propDialog.getDialogPane().getButtonTypes().addAll(ok, cancel);
			
			Optional<ButtonType> confResult = propDialog.showAndWait();
			
			if(confResult.isPresent() && confResult.get().equals(ok)){
				propForm.saveProperties();
			}
		});
	}
	
	public void showNewLibraryDialog(){
		Platform.runLater(()->{
			LibraryDialog dialog = new LibraryDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				Core.getCore().addLibrary(dialog.makeLibrary(), true);
			}
		});
	}
	
	public void showImportLocalLibraryDialog(){
		Platform.runLater(()->{
			LocalChooserDialog chooser = new LocalChooserDialog("", Chooser.Mode.MODE_SINGLE_FILE, false, "Import Library", "Select a library file", Library.REMOVABLE_LIBRARY_FILE_EXTENSION);
			if( chooser.showDialogAndWait() && !chooser.getString().isEmpty()){
				RemovableLibrary newLib = Core.getCore().loadRemovableLibrary(chooser.getString());
				Core.getCore().addLibrary(newLib, true);
			}
		});
	}
	
	public void showNewAccountDialog(){
		Platform.runLater(()->{
			AccountDialog dialog = new AccountDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				Core.getCore().addAccount(dialog.makeAccount());
			}
		});
	}
	
	public void showAccountListDialog(){
		Platform.runLater(()->{
			AccountListView dialog = new AccountListView();
			dialog.showAndWait();
		});
	}
	
	public void showNewTaskListDialog() {
		Platform.runLater(()->{
			Dialog<ButtonType> dialog = new Dialog<ButtonType>();
			dialog.setTitle("New Task List");
			NewTaskListForm ntlf = new NewTaskListForm();
			dialog.getDialogPane().setContent(ntlf);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			
			Optional<ButtonType> response = dialog.showAndWait();
			if(response.isPresent() && response.get().equals(ButtonType.OK)){
				TaskList newList = ntlf.makeTaskList(Core.getCore().getCurrentLibrary());
				Core.getCore().getCurrentLibrary().addTaskList(newList);
			}
		});
	}

	public void showLocalChooserDialog(String name, Mode mode, boolean editable, String title, String message,
			String initialDirectory, String initialFilename, List<String> extensions, ArgumentHandler<String> onSuccess, SimpleHandler onCancel) {
		
		LocalChooserDialog dialog = new LocalChooserDialog(name, mode, editable, title, message, initialDirectory, initialFilename, extensions.toArray(new String[extensions.size()]));
		dialog.showDialog(new SimpleHandler(){
			public void handle() {
				onSuccess.handle(dialog.getString());
			}
		}, onCancel);
	}

	public void showRemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message,
			String initialDirectory, String initialFilename, List<String> extensions, ArgumentHandler<String> onSuccess, SimpleHandler onCancel) {
		
		RemoteChooserDialog dialog = new RemoteChooserDialog(account, name, mode, editable, title, message, initialDirectory, initialFilename, extensions.toArray(new String[extensions.size()]));
		dialog.showDialog(new SimpleHandler(){
			public void handle() {
				onSuccess.handle(dialog.getString());
			}
		}, onCancel);
		
	}

	public List<File> showNativeFileChooser(Mode mode, String title, File initialFile, String... extensions) {
		return showNativeFileChooser(mode, title, initialFile.getName(), initialFile.getParent(), extensions);
	}

	public List<File> showNativeFileChooser(Mode mode, String title, String initialDirectory, String initialFilename, String... extensions) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title != null ? title : "");
		chooser.setInitialFileName(initialFilename != null ? initialFilename : "");
		chooser.setInitialDirectory(initialDirectory != null ? new File(initialDirectory) : null);
		
		if(extensions != null){
			for(String ext : extensions){
				chooser.getExtensionFilters().add(new ExtensionFilter(ext, ext));
			}
		}
		
		switch(mode){
			case MODE_SINGLE_FILE:
				File retFile = chooser.showOpenDialog(null);
				if(retFile != null){
					ArrayList<File> retFileList = new ArrayList<File>();
					retFileList.add(retFile);
					return retFileList;
				}else{
					return null;
				}
			case MODE_MULTIPLE_FILE:
				return chooser.showOpenMultipleDialog(null);
			case MODE_SINGLE_FOLDER:
				File retDir = chooser.showOpenDialog(null);
				if(retDir != null){
					ArrayList<File> retDirList = new ArrayList<File>();
					retDirList.add(retDir);
					return retDirList;
				}else{
					return null;
				}
			case MODE_SAVE_FILE:
				File saveFile = chooser.showOpenDialog(null);
				if(saveFile != null){
					ArrayList<File> saveFileList = new ArrayList<File>();
					saveFileList.add(saveFile);
					return saveFileList;
				}else{
					return null;
				}
			default:
				return null;
		}
	}
	
//==============================================================
	
	//===========================
	// GUI factory functions
	//===========================

	public ILibraryView getLibraryView(Library library) {
		return new LibraryView(library.getResource());
	}

	public ILibraryMenu getLibraryMenu(Library library) {
		return new LibraryMenu(library);
	}

	public ITaskListView getTaskListView(TaskList taskList) {
		return new TaskListView(taskList);
	}

	public ITaskView getTaskView(Task task) {
		return new TaskView(task);
	}

	public IResourceView getResourceView(Resource resource) {
		return new ResourceView(resource);
	}

	public IResourceListView getResourceListView(ResourceList resourceList) {
		return new ResourceListView(resourceList);
	}

	public IFileResourceView getFileResourceView(FileResource resource) {
		return new FileResourceView(resource);
	}

	public ILocalChooserPane getLocalChooserPane(String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserPane(name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public ILocalChooserDialog getLocalChooserDialog(String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new LocalChooserDialog(name, mode, editable, title, message, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserPane getRemoteChooserPane(Account account, String name, Mode mode, boolean editable, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserPane(account, name, mode, editable, initialDirectory, initialFilename, extensions);
	}

	public IRemoteChooserDialog getRemoteChooserDialog(Account account, String name, Mode mode, boolean editable, String title, String message, String initialDirectory, String initialFilename, String... extensions) {
		return new RemoteChooserDialog(account, name, mode, editable, title, message, initialDirectory, initialFilename, extensions);
	}

	public IOauthHandlerView getOauthDialog() {
		return new OauthHandlerView();
	}
	
	public ITextInputDialog getTextInputDialog(String title, String header, String message, String value) {
		return new TextInputDialog(title, header, message, value);
	}
	
}
