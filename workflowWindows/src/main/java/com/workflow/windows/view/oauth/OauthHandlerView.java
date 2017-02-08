package com.workflow.windows.view.oauth;

import java.util.Optional;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.controller.oauth.OauthHandler;
import com.workflow.core.view.oauth.IOauthHandlerView;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class OauthHandlerView extends Dialog<ButtonType> implements IOauthHandlerView{

	private TextField authTokenField;
	private WebEngine engine;
	private String authToken;
	
	public OauthHandlerView(){
		generateUI();
	}
	
	private void generateUI(){
		BorderPane contentPane = new BorderPane();
		
		WebView view = new WebView();
		engine = view.getEngine();
		
		authTokenField = new TextField();
		FlowPane bottomPane = new FlowPane();
		bottomPane.getChildren().addAll(new Label("Auth Code:"), authTokenField);
		
		contentPane.setCenter(view);
		contentPane.setBottom(bottomPane);

		getDialogPane().setContent(contentPane);
		getDialogPane().getButtonTypes().clear();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		this.setOnHiding(
			(event)->{
				authToken = authTokenField.getText();
			}
		);
	}
	
	public void requestOauthAuthToken(String authURI, SimpleHandler onSuccess, SimpleHandler onFailure) {
		engine.load(authURI);
		Optional<ButtonType> response = this.showAndWait();
		if(response.isPresent() && response.get().equals(ButtonType.OK)){
			onSuccess.handle();
		}else{
			onFailure.handle();
		}
	}

	public String getToken() {
		return authToken;
	}

}
