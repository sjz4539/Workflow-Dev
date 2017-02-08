package com.workflow.core.controller.oauth;

import java.io.IOException;
import java.util.Collections;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.view.oauth.IOauthHandlerView;

public class GoogleOauthHandler extends OauthHandler{

	public void getAuth(final Account account, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		
		if(account.getType() != Account.AccountType.ACCOUNT_TYPE_GOOGLE){
				onFailure.handle();
		}else{
			
			//if a refresh token exists, try getting a new access token that way first.
			if(account.getRefreshToken() != null){
				
				try {
					
					GoogleTokenResponse authFinish = new GoogleAuthorizationCodeTokenRequest(
							new NetHttpTransport(), new JacksonFactory(), 
							GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
							account.getRefreshToken(), null)
					.setGrantType("refresh_token")
					.setScopes(Collections.singleton("https://www.googleapis.com/auth/drive"))
					.execute();
				
					account.setToken(authFinish.getAccessToken());

					onSuccess.handle();
					
				} catch (TokenResponseException tre) {
					//something went wrong, blank the refresh token and try again to get a new refresh and access token set.
					tre.printStackTrace();
					account.setRefreshToken(null);
					getAuth(account, onSuccess, onFailure);
				} catch (IOException e) {
					//something went wrong, blank the refresh token and try again to get a new refresh and access token set.
					e.printStackTrace();
					account.setRefreshToken(null);
					getAuth(account, onSuccess, onFailure);
				}
	
			}else{
			
				//we need to get a new auth token so we can get a new access/refresh token
				//give the user a chance to bail on this operation first
				Core.getGuiFactory().requestConfirmation("Authorization Required", null, "Workflow requires access to your Google Drive account. Click OK to be directed to a Google website to grant this access.",
					new SimpleHandler(){
						public void handle() {
				
							GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
									new NetHttpTransport(), new JacksonFactory(), 
									GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
									Collections.singleton("https://www.googleapis.com/auth/drive")
							).setAccessType("offline").build();
							
							//execute the common segment of the code; this directs the user to login and start an oauth request session
							final IOauthHandlerView view = Core.getGuiFactory().getOauthDialog();
							
							//retrieve the auth token (if present) and attempt to get an access token with it
							view.requestOauthAuthToken(flow.newAuthorizationUrl().setRedirectUri("urn:ietf:wg:oauth:2.0:oob").build(), new SimpleHandler(){
								public void handle() {
									
									if(view.getToken() != null){
										
										try {
											
											GoogleTokenResponse authFinish = new GoogleAuthorizationCodeTokenRequest(
													new NetHttpTransport(), new JacksonFactory(), 
													GOOGLE_APP_ID, GOOGLE_APP_SECRET, 
													view.getToken(), null)
											.setGrantType("authorization_code")
											.setScopes(Collections.singleton("https://www.googleapis.com/auth/drive"))
											.execute();
										
											account.setToken(authFinish.getAccessToken());
											account.setRefreshToken(authFinish.getRefreshToken());
			
											onSuccess.handle();
											
										} catch (TokenResponseException tre) {
											//something went wrong, blank the access token and refresh token.
											account.setToken(null);
											account.setRefreshToken(null);
											tre.printStackTrace();
											onFailure.handle();
										} catch (IOException e) {
											//something went wrong, blank the access token and refresh token.
											account.setToken(null);
											account.setRefreshToken(null);
											e.printStackTrace();
											onFailure.handle();
										}
									}else{
										onFailure.handle();
									}
									
								}
								
							}, onFailure);
							
						}
					}, null
				);
				
			}
			
		}
		
	}
	
}
