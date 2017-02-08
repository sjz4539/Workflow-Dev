package com.workflow.core.view.account;

import com.workflow.core.model.account.Account;

public interface IAccountListView{
	
	public enum Mode{
		MODE_VIEW, MODE_SELECT
	}
	
	public void updateUI();
	
	public Account getSelection();
	
}
