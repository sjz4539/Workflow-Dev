package com.workflow.core.controller.oauth;

import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;

public abstract class OauthHandler{
	
	public static final String DROPBOX_APP_ID = "5fs4fyzxlhsko7r";
	public static final String DROPBOX_APP_SECRET = "dvvuuqcf64jd764";
	public static final String GOOGLE_APP_ID = "";
	public static final String GOOGLE_APP_SECRET = "";

	public abstract void getAuth(Account account, SimpleHandler onSuccess, SimpleHandler onFailure);

}
