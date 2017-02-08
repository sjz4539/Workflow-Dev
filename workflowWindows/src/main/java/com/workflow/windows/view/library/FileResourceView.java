package com.workflow.windows.view.library;

import java.util.ArrayList;
import java.util.List;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.controller.library.RemoteLibrary;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.ResourceTask;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.view.library.IFileResourceView;
import com.workflow.core.view.library.IResourceView;
import com.workflow.windows.view.WorkflowGUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FileResourceView extends ResourceView implements IFileResourceView{

	private ProgressBar progBar;
	private Label statusLabel;
	private MenuItem moveItem, exportItem, saveItem, reloadItem;
	private ImageView warningIcon;
	private Tooltip tooltip;
	private BorderPane namePane, progPane;
	private VBox centerPane;
	private Button cancelButton;
	
	private ResourceTask task;
	private FileResource resource;
	
	public FileResourceView(FileResource r){
		resource = r;
		generateUI();
		updateUI();
	}
	
	protected void generateUI(){
		
		nameLabel = new Label(resource.getName());
		menuButton = new MenuButton("...");
		menuButton.getItems().addAll(getMenuItems());
		setAlignment(nameLabel, Pos.CENTER_LEFT);
		setAlignment(menuButton, Pos.CENTER_RIGHT);
		
		namePane = new BorderPane();
		namePane.setCenter(nameLabel);
		namePane.setRight(menuButton);
		
		progBar = new ProgressBar();
		
		statusLabel = new Label();
		
		StackPane stack = new StackPane();
		stack.getChildren().addAll(progBar, statusLabel);
		stack.setAlignment(Pos.CENTER);
		
		cancelButton = new Button();
		cancelButton.setGraphic(new ImageView(new Image(IResourceView.class.getResourceAsStream("/com/workflow/core/resources/block_16.png"))));
		cancelButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				task.cancel();
			}
		});
		
		setAlignment(stack, Pos.CENTER_LEFT);
		setAlignment(cancelButton, Pos.CENTER_RIGHT);
		
		progPane = new BorderPane();
		progPane.setVisible(false);
		progPane.setCenter(stack);
		progPane.setRight(cancelButton);
		
		warningIcon = new ImageView(new Image(IResourceView.class.getResourceAsStream("/com/workflow/core/resources/warning_16.png")));
		tooltip = new Tooltip();
		Tooltip.install(warningIcon, tooltip);
		
		centerPane = new VBox(2);
		centerPane.getChildren().add(namePane);
		
		namePane.prefWidthProperty().bind(centerPane.widthProperty());
		progPane.prefWidthProperty().bind(centerPane.widthProperty());
		
		setCenter(centerPane);
		
	}
	
	public void setResource(FileResource r){
		resource = r;
		updateUI();
	}
	
	public void updateUI(){
		nameLabel.setText(resource.getName());
		updateStatus(null);
	}
	
	public void updateStatus(String m){
		tooltip.setText(m != null ? m : "");
		if(m != null){
			Tooltip.install(warningIcon, tooltip);
		}else{
			Tooltip.uninstall(warningIcon, tooltip);
		}
		switch(resource.getStatus()){
			case NORMAL:
				setLeft(null);
				centerPane.getChildren().remove(progPane);
				break;
			case ERROR:
				setLeft(warningIcon);
				centerPane.getChildren().remove(progPane);
				break;
			case DOWNLOADING:
				progBar.setProgress(-1);
				if(!centerPane.getChildren().contains(progPane)){
					centerPane.getChildren().add(progPane);
				}
				break;
			case UPLOADING:
				progBar.setProgress(-1);
				if(!centerPane.getChildren().contains(progPane)){
					centerPane.getChildren().add(progPane);
				}
				break;
			case COPYING:
				progBar.setProgress(-1);
				if(!centerPane.getChildren().contains(progPane)){
					centerPane.getChildren().add(progPane);
				}
				break;
			case MOVING:
				progBar.setProgress(-1);
				if(!centerPane.getChildren().contains(progPane)){
					centerPane.getChildren().add(progPane);
				}
				break;
			case DELETING:
				progBar.setProgress(-1);
				if(!centerPane.getChildren().contains(progPane)){
					centerPane.getChildren().add(progPane);
				}
				break;
			case MISSING:
				setLeft(warningIcon);
				centerPane.getChildren().remove(progPane);
				break;
			default: 
				setLeft(null);
				centerPane.getChildren().remove(progPane);
				break;
		}
	}
	
	public void updateProgress(float p){
		if(resource.getParentLibrary().getType() == Library.Type.REMOTE && ((RemoteLibrary)resource.getParentLibrary()).getAccount().getType() != Account.AccountType.ACCOUNT_TYPE_DROPBOX){
			progBar.setProgress(p);
		}
	}
	
	public List<MenuItem> getMenuItems(){

		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
			
		openItem = new MenuItem("Open");
		openItem.setOnAction((click)->{
			resource.open();
		});
		items.add(openItem);
		
		copyItem = new MenuItem("Copy");
		copyItem.setOnAction((click)->{
			resource.getParentLibrary().copyResource(resource, null, null);
		});
		items.add(copyItem);
		
		moveItem = new MenuItem("Move");
		moveItem.setOnAction((click)->{
			resource.getParentLibrary().moveResource(resource, null, null);
		});
		items.add(moveItem);
		
		removeItem = new MenuItem("Remove");
		removeItem.setOnAction((click)->{ 
			Core.getGuiFactory().requestConfirmation(
				"Remove Resource", null, 
				"This will delete the copy of this file stored in this library! If you want to keep it, press Cancel and export a copy first. Proceed with removal?",
				new SimpleHandler(){
					public void handle() {
						if(!Core.getResourceCache().removeFile(resource)){
							Core.getGuiFactory().displayError("Delete Failed", null, "The library copy of this resource could not be deleted.", true);
						}
						resource.getParentTask().getResourceList().removeResource(resource);
					}
				}, null
			);
				
			
		});
		items.add(removeItem);
		
		exportItem = new MenuItem("Export");
		exportItem.setOnAction((click)->{
			resource.getParentLibrary().exportResource(resource, null, null);
		});
		
		if(resource.getParentLibrary().getType() == Library.Type.REMOTE){
			saveItem = new MenuItem("Upload");
			saveItem.setOnAction((click)->{
				resource.save();
			});
			items.add(saveItem);
			
			reloadItem = new MenuItem("Reload");
			reloadItem.setOnAction((click)->{
				Core.getGuiFactory().requestConfirmation(
					"Reload Resource", null, 
					"This will retrieve a fresh copy of this resource from remote storage. Any changes you haven't uploaded will be overwritten. Proceed?",
					new SimpleHandler(){
						public void handle() {
							resource.load();
						}
					}, null
				);
			});
			items.add(reloadItem);
		}
			
		return items;
	}
	
}
