package com.workflow.core.controller.io;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.account.Account.AccountType;
import com.workflow.core.model.resource.remote.RemoteFile;
import com.workflow.core.model.resource.remote.RemoteFolder;

public interface RemoteFileOps {

	public FileOpsStatus loadFile(Account source, String remoteSource, String localDest);
	
	public FileOpsStatus saveFile(Account source, String localSource, String remoteDest);
	
	public FileOpsStatus copyFile(Account source, String remoteSource, String remoteDest, boolean overwrite);
	
	public FileOpsStatus moveFile(Account source, String remoteSource, String remoteDest, boolean overwrite);
	
	public FileOpsStatus deleteFile(Account source, String remoteSource);
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource);
	
	public FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth);
	
	public FileOpsStatus createFolder(Account source, RemoteFolder parent, String name);
	
	public FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest);
	
	public FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest);
	
	public FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource);
	
	public static void requestOverwrite(String f, SimpleHandler onAccept, SimpleHandler onRefuse){
		Core.getGuiFactory().requestConfirmation("Overwrite?", null, "Are you sure you want to overwrite " + f + "?", onAccept, onRefuse);
	}
	
	public static void handleError(String t, String m){
		Core.getGuiFactory().displayError(t, null, m, true);
	}
	
	public FileOpsStatus validateOauth(Account source);
	
}
