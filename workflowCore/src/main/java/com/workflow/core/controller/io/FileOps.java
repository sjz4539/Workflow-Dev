package com.workflow.core.controller.io;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteFile;
import com.workflow.core.model.resource.remote.RemoteFolder;

public abstract class FileOps {

	public abstract FileOpsStatus loadFile(Account source, String remoteSource, String localDest);
	
	public abstract FileOpsStatus saveFile(Account source, String localSource, String remoteDest);
	
	public abstract FileOpsStatus copyFile(Account source, String remoteSource, String remoteDest, boolean overwrite);
	
	public abstract FileOpsStatus copyFile(Account source, RemoteFile remoteSource, String remoteDest, boolean overwrite);
	
	public abstract FileOpsStatus moveFile(Account source, String remoteSource, String remoteDest, boolean overwrite);
	
	public abstract FileOpsStatus moveFile(Account source, RemoteFile remoteSource, String remoteDest, boolean overwrite);
	
	public abstract FileOpsStatus deleteFile(Account source, String remoteSource);
	
	public abstract FileOpsStatus deleteFile(Account source, RemoteFile remoteSource);
	
	public abstract FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource);
	
	public abstract FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth);
	
	public abstract FileOpsStatus createFolder(Account source, RemoteFolder parent, String name);
	
	public abstract FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest);
	
	public abstract FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest);
	
	public abstract FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource);
	
	public static void requestOverwrite(String f, SimpleHandler onAccept, SimpleHandler onRefuse){
		Core.getGuiFactory().requestConfirmation("Overwrite?", null, "Are you sure you want to overwrite " + f + "?", onAccept, onRefuse);
	}
	
	public static void handleError(String t, String m){
		Core.getGuiFactory().displayError(t, null, m, true);
	}
	
	public abstract FileOpsStatus validateOauth(Account source);
	
}
