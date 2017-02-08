package com.workflow.windows.view;

import java.util.Optional;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.library.Library;
import com.workflow.core.view.IWorkflowGUI;
import com.workflow.core.view.library.ILibraryView;
import com.workflow.windows.controller.PlatformHelper;
import com.workflow.windows.controller.ProcessMonitor;
import com.workflow.windows.view.library.LibraryMenu;
import com.workflow.windows.view.library.LibraryView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WorkflowGUI extends Application implements IWorkflowGUI{
	
	//root pane for GUI, singleton
	private static BorderPane root;
	private static Pane emptyView;
	private static Menu libraryMenu;
	private static MenuItem newLibrary, importLibrary;
	private static Stage stage;
	
	public static void main(String[] args){
		launch(args);
	}
	
	//Primary program method
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Workflow");
		primaryStage.setOnCloseRequest((closeRequest)->{
			//program is closing, shut down all threads
			//if threads won't stop or some error occurs, prevent window close
			if(!(Core.getPlatformHelper().checkUnsavedResources() && Core.getCore().saveLibraries() && Core.getCore().saveSettings() && Core.getPlatformHelper().destroyCache())){
				
				//this REQUIRES blocking UI; can't use core functions.
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Quit With Errors?");
				alert.setContentText("One or more errors occurred while attempting to close the program. Are you sure you want to quit without resolving them?");
				Optional<ButtonType> response = alert.showAndWait();

				if(!response.isPresent() || !response.get().equals(ButtonType.OK)){
					closeRequest.consume();
				}
				
			}
		});
		
		stage = primaryStage;
		
		//initialize the root pane before doing anything.
		getRoot();

		//do core init
		if(!Core.init(this, new PlatformHelper())){
			//display some error dialog box
		}else{
			
			//Core should be good to go, set up the gui
			getRoot().setTop(getMenuBar());
			getRoot().setCenter(getEmptyView());
			
			primaryStage.setScene(new Scene(root, 500, 500));
			primaryStage.minWidthProperty().set(100);
			primaryStage.minHeightProperty().set(100);
			
			setUserAgentStylesheet(STYLESHEET_CASPIAN);
			
			primaryStage.show();
		}
	}

	public static Stage getStage(){
		return stage;
	}
	
	//Static accessor for root pane instance
	public static BorderPane getRoot(){
		if(root == null){
			root = new BorderPane();
		}
		return root;
	}
	
	public void updateLibraryList(){

		libraryMenu.getItems().clear();
		
		for(Library l : Core.getCore().getLibraries()){
			Menu nextItem = (LibraryMenu)l.getMenu();
			libraryMenu.getItems().add(nextItem);
		}
		
		if(newLibrary == null){
			newLibrary = new MenuItem("New Library");
			newLibrary.setOnAction((click)->{
				Core.getGuiFactory().showNewLibraryDialog();
			});
		}
		
		if(importLibrary == null){
			importLibrary = new MenuItem("Import Library ");
			importLibrary.setOnAction((click)->{
				Core.getGuiFactory().showImportLocalLibraryDialog();
			});
		}
		
		libraryMenu.getItems().addAll(newLibrary, importLibrary);
		
	}
	
	public void setLibraryView(ILibraryView lrv){
		Platform.runLater(()->{
			getRoot().setCenter( lrv == null ? getEmptyView() : (LibraryView)lrv);
		});
	}
	

	
//========================================================
	 
	//===========================
	//private GUI setup functions
	//===========================
	
	private static Pane getEmptyView(){
		if(emptyView == null){
			emptyView = new Pane();
		}
		return emptyView;
	}
	
	private MenuBar getMenuBar(){

		//todo, needs to provide access to core program functions
		//	-create task list
		//	-settings
		//	-exit
		
		//feels like there must be more than that; list needs to be expanded
		MenuBar menubar = new MenuBar();
		
		//Main menu - general program functions
		Menu mainMenu = new Menu("Main");
		MenuItem configureItem = new MenuItem("Configure");
		configureItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				Core.getGuiFactory().showConfigureDialog();
			}
		});
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				WorkflowGUI.getStage().fireEvent(new WindowEvent(WorkflowGUI.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		mainMenu.getItems().addAll(configureItem, exitItem);
		
		Menu accountMenu = new Menu("Accounts");
		MenuItem newAccountItem = new MenuItem("New Account");
		newAccountItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				Core.getGuiFactory().showNewAccountDialog();
			}
		});
		MenuItem accountListItem = new MenuItem("View/Edit Accounts");
		accountListItem.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				Core.getGuiFactory().showAccountListDialog();
			}
		});
		accountMenu.getItems().addAll(newAccountItem, accountListItem);
		
		//Library menu - library management functions
		libraryMenu = new Menu("Library");
		
		menubar.getMenus().addAll(mainMenu, accountMenu, libraryMenu);
		
		updateLibraryList();
		
		return menubar;
	}

}
