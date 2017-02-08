package com.workflow.windows.view.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.library.ResourceList;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.model.resource.task.WebResource;
import com.workflow.core.view.library.IResourceListView;
import com.workflow.core.view.library.IResourceView;
import com.workflow.windows.view.WorkflowGUI;
import com.workflow.windows.view.form.LinkInputForm;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

public class ResourceListView extends TitledPane implements IResourceListView{
	
	private ResourceList resourceList;
	
	private BorderPane containerPane;
	private ScrollPane scrollPane;
	private ListView<Resource> listPane;
	private FlowPane buttonPane;
	private Button addFile, addLink;
	
	private ContextMenu context;
	
	public ResourceListView(ResourceList rl){
		resourceList = rl;
		generateUI();
		updateUI();
	}
	
	private void generateUI(){
		
		setText("Resources");
		
		containerPane = new BorderPane();
		
		listPane = new ListView<Resource>();
		listPane.setCellFactory((ListView<Resource> lv)->{
				ListCell<Resource> newCell = new ListCell<Resource>(){
					protected void updateItem(Resource resource, boolean empty){
						super.updateItem(resource, empty);
						
						if(resource == null || empty){
							setText(null);
							setGraphic(null);
						}else{
							if(resource.storedAsFile()){
								setGraphic(new FileResourceView((FileResource)resource));
							}else{
								setGraphic(new ResourceView(resource));
							}
						}
					}
				};
				return newCell;
			}
		);
		listPane.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		scrollPane = new ScrollPane();
		scrollPane.setMaxHeight(150);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(listPane);
		
		addFile = new Button("Add File");
		addLink = new Button("Add Webpage");
		
		addFile.setOnAction((on_click)->{
				//open a filechooser so the user can add a new file to this task
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Add a File");
				File selection = fileChooser.showOpenDialog(WorkflowGUI.getStage());
				
				if(selection != null){
					resourceList.addResourceFromFile(selection.getAbsolutePath());
				}
			}
		);
		
		addLink.setOnAction((on_click)->{
				Dialog<ButtonType> linkDialog = new Dialog<ButtonType>();
				linkDialog.setTitle("Add a Webpage");
				
				LinkInputForm lif = new LinkInputForm();
				ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
				ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				
				linkDialog.getDialogPane().setContent(lif);
				linkDialog.getDialogPane().getButtonTypes().addAll(ok, cancel);
				
				Optional<ButtonType> response = linkDialog.showAndWait();
				
				if(response.isPresent() && response.get().equals(ok)){
					resourceList.addResource(new WebResource(resourceList.getParentTask(), lif.getLink()));
				}
			}
		);
		
		buttonPane = new FlowPane();
		buttonPane.getChildren().addAll(addFile, addLink);
		
		containerPane.setCenter(scrollPane);
		containerPane.setBottom(buttonPane);
		
		setContent(containerPane);
		setExpanded(false);
	}
	
	public void updateUI(){
		listPane.getItems().clear();
		for(Resource r : resourceList.getModel()){
			listPane.getItems().add(r);
		}
		setText(resourceList.getModel().size() + " Resources");
	}
	
	private void showContextMenu(MouseEvent e, ListCell<Resource> cell){
		
		ArrayList<Resource> selection = new ArrayList<Resource>(listPane.getSelectionModel().getSelectedItems());
		
		if(context != null){
			context.hide();
		}
		
		context = new ContextMenu();
		
		MenuItem openItem = new MenuItem("Open");
		openItem.setOnAction((onClick)->{
				for(Resource r : selection){
					Core.getProcessMonitor().open(r);
				}
			}
		);
		MenuItem removeItem = new MenuItem("Remove");
		removeItem.setOnAction((onClick)->{
				for(Resource r : selection){
					resourceList.removeResource(r);
				}
			}
		);
		MenuItem deleteItem = new MenuItem("Delete");
		deleteItem.setOnAction((onClick)->{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirm Multiple Resource Delete");
				alert.setContentText("Permanently delete these " + selection.size() + " resources?");
				alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
				
				Optional<ButtonType> response = alert.showAndWait();
				if(response.isPresent() && response.get().equals(ButtonType.YES)){
					for(Resource r : selection){
						if(r.storedAsFile()){
							r.getParentLibrary().deleteResource((FileResource)r);
						}
					}
					for(Resource r : selection){
						resourceList.removeResource(r);
					}
				}
			}
		);
		
		context.getItems().addAll(openItem, removeItem, deleteItem);
		
		context.show(cell, e.getScreenX(), e.getScreenY());
		
	}
	
	public void updateStatus(boolean error){
		if(error){
			setGraphic(new ImageView(new Image(IResourceView.class.getResourceAsStream("../../resources/block_16.png"))));
			Tooltip.install(getGraphic(), new Tooltip("A resource in this list has a problem."));
		}else{
			setGraphic(null);
		}
	}
}
