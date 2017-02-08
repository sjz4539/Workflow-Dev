package com.workflow.core.controller.oauth;

import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.view.oauth.IOauthHandlerView;

public class DropboxOauthHandler extends OauthHandler{

	public void getAuth(final Account account, final SimpleHandler onSuccess, final SimpleHandler onFailure){
		
		if(account.getType() == Account.AccountType.ACCOUNT_TYPE_DROPBOX){
		
			//give the user a chance to bail on this operation first
			Core.getGuiFactory().requestConfirmation("Authorization Required", null, "Workflow requires access to your Dropbox account. Click OK to be directed to the Dropbox website to grant this access.",
				new SimpleHandler(){
					public void handle() {

						//do whatever dropbox requires for oauth until we get a URL to direct the user to
						DbxAppInfo appInfo = new DbxAppInfo(DROPBOX_APP_ID, DROPBOX_APP_SECRET);
				        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
				        final DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
						
						//execute the common segment of the code; this directs the user to login and start an oauth request session
				        final IOauthHandlerView view = Core.getGuiFactory().getOauthDialog();
		
						//retrieve the auth token (if present) and attempt to get an access token with it
						view.requestOauthAuthToken(webAuth.start(), new SimpleHandler(){
							public void handle(){
								if(view.getToken() != null){
									try {
										DbxAuthFinish authFinish = webAuth.finish(view.getToken());
										String accessToken = authFinish.accessToken;
										account.setToken(accessToken);
										onSuccess.handle();
									} catch (DbxException e) {
										e.printStackTrace();
										onFailure.handle();
									}
								}
							}
							
						}, onFailure);
						
					}
				}, null
			);
				
		}else{
			onFailure.handle();
		}
		
	}
	
}
