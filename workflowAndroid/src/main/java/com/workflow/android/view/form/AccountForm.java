package com.workflow.android.view.form;

import com.workflow.android.R;
import com.workflow.core.model.account.Account;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AccountForm extends LinearLayout{

	private final String accOptDropbox = "Dropbox";
	private final String accOptGoogle = "Google Drive";
	
	private Account account;
	
	private Spinner accountType;
	private ArrayAdapter<String> adapter;
	private EditText accountName, oauthToken, refreshToken;
	
	public AccountForm(Context context, AttributeSet attrs) {
		super(context, attrs);

		accountType = (Spinner)findViewById(R.id.account_form_account_types);
		accountName = (EditText)findViewById(R.id.account_form_account_name);
		oauthToken = (EditText)findViewById(R.id.account_form_oauth_token);
		refreshToken = (EditText)findViewById(R.id.account_form_refresh_token);
		
		adapter = new ArrayAdapter<String>(context, -1, new String[]{accOptDropbox, accOptGoogle});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		accountType.setAdapter(adapter);
	}
	
	public void setAccount(Account a){
		account = a;
		updateUI();
	}
	
	public void updateUI(){
		if(account != null){
			switch(account.getType()){
				case ACCOUNT_TYPE_DROPBOX:
					accountType.setSelection(0);
					break;
				case ACCOUNT_TYPE_GOOGLE:
					accountType.setSelection(1);
					break;
				default:
					break;
			}
			accountName.setText(account.getName());
		}else{
			accountType.setSelection(0);
		}
		accountName.setHint("Account Name");
		oauthToken.setHint(account != null && account.getToken() != null ? "******" : "OAuth Access Token (Optional)");
		refreshToken.setHint(account != null && account.getRefreshToken() != null ? "******" : "OAuth Refresh Token (Optional)");
	}
	
	public Account makeAccount(){
		if(account == null){
			account = new Account(accountName.getText().toString(), accountType.getItemAtPosition(accountType.getSelectedItemPosition()).equals(accOptDropbox) ? Account.AccountType.ACCOUNT_TYPE_DROPBOX : Account.AccountType.ACCOUNT_TYPE_GOOGLE);
		}else{
			account.setName(accountName.getText().toString());
			account.setType(accountType.getItemAtPosition(accountType.getSelectedItemPosition()).equals(accOptDropbox) ? Account.AccountType.ACCOUNT_TYPE_DROPBOX : Account.AccountType.ACCOUNT_TYPE_GOOGLE);
		}
		
		if(oauthToken.getText().length() > 0){
			account.setToken(oauthToken.getText().toString());
		}
		if(refreshToken.getText().length() > 0){
			account.setRefreshToken(refreshToken.getText().toString());
		}
		
		return account;
	}
	
}
