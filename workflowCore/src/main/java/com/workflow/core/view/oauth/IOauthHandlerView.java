package com.workflow.core.view.oauth;

import com.workflow.core.controller.SimpleHandler;

public interface IOauthHandlerView {

	public void requestOauthAuthToken(String authURI, SimpleHandler onSuccess, SimpleHandler onFailure);
	
	public String getToken();
	
}
