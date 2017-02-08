package com.workflow.windows.view.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.workflow.core.controller.Core;
import com.workflow.core.model.resource.task.FileResource;
import com.workflow.core.model.resource.task.Resource;
import com.workflow.core.view.library.IResourceView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResourceView extends BorderPane implements IResourceView{

	protected Label nameLabel;
	protected MenuButton menuButton;
	protected MenuItem openItem, copyItem, removeItem;
	protected Resource resource;
	
	protected ResourceView(){
	}
	
	public ResourceView(Resource r){
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
		
		setCenter(nameLabel);
		setRight(menuButton);
		
	}
	
	public void updateUI(){
		nameLabel.setText(resource.getName());
	}
	
	public void setResource(Resource r){
		resource = r;
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
			
		});
		items.add(copyItem);
		
		removeItem = new MenuItem("Remove");
		removeItem.setOnAction((click)->{
			resource.getParentTask().getResourceList().removeResource(resource);
		});
		items.add(removeItem);
			
		return items;
	}
	
}
