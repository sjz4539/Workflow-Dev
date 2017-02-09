package com.workflow.core.controller.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.SimpleHandler;
import com.workflow.core.model.account.Account;
import com.workflow.core.model.resource.remote.RemoteFolder;

public interface FileOps {

	public static abstract interface Local{
		
		public static FileOpsStatus moveFile(String source, String destination){
			return moveFile(source, destination, false);
		}
		
		public static FileOpsStatus moveFile(String source, String destination, boolean overwrite){
			File sourcePath = new File(source);
			File destPath = new File(destination);
			
			if(destPath.exists() && !overwrite){
				return FileOpsStatus.FILE_ALREADY_EXISTS();
			}else if(destPath.exists() && !destPath.delete()){
				return FileOpsStatus.LOCAL_IO_ERROR();
			}else{
				return (sourcePath.renameTo(destPath) ? FileOpsStatus.SUCCESS() : FileOpsStatus.LOCAL_IO_ERROR());
			}
		}

		public static FileOpsStatus copyFile(String source, String destination){
			return copyFile(source, destination, false);
		}
		
		public static FileOpsStatus copyFile(String source, String destination, boolean overwrite){
			File sourcePath = new File(source);
			File destPath = new File(destination);
			
			try {
				if(destPath.exists() && !overwrite){
					return FileOpsStatus.FILE_ALREADY_EXISTS();
				}else if(destPath.exists() && !destPath.delete()){
					return FileOpsStatus.LOCAL_IO_ERROR();
				}else{
						
					FileInputStream in = new FileInputStream(sourcePath);
					FileOutputStream out = new FileOutputStream(destPath);
					
					byte[] buffer = new byte[2048];
					int size;
					
					while((size = in.read(buffer)) > 0){
						out.write(buffer, 0, size);
					}
					
					in.close();
					out.close();
					
					return FileOpsStatus.SUCCESS();
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return FileOpsStatus.LOCAL_IO_ERROR();
			}
		}
		
		public static FileOpsStatus deleteFile(String source){
			if(new File(source).delete()){
				return FileOpsStatus.SUCCESS();
			} else {
				return FileOpsStatus.LOCAL_IO_ERROR();
			}
		}
	}
	
	public static abstract interface Remote{
		
		public static RemoteFileOps getFileOps(Account a){
			switch(a.getType()){
				case ACCOUNT_TYPE_DROPBOX:
					return DropboxFileOps.getFileOps();
				case ACCOUNT_TYPE_GOOGLE:
					return GoogleFileOps.getFileOps();
				default:
					return null;
			}
		}
		
		public static FileOpsStatus loadFile(Account source, String remoteSource, String localDest){
			return getFileOps(source).loadFile(source, remoteSource, localDest);
		}
		
		public static FileOpsStatus saveFile(Account source, String localSource, String remoteDest){
			return getFileOps(source).saveFile(source, localSource, remoteDest);
		}
		
		public static FileOpsStatus copyFile(Account source, String remoteSource, String remoteDest, boolean overwrite){
			return getFileOps(source).copyFile(source, remoteSource, remoteDest, overwrite);
		}
		
		public static FileOpsStatus moveFile(Account source, String remoteSource, String remoteDest, boolean overwrite){
			return getFileOps(source).moveFile(source, remoteSource, remoteDest, overwrite);
		}
		
		public static FileOpsStatus deleteFile(Account source, String remoteSource){
			return getFileOps(source).deleteFile(source, remoteSource);
		}
		
		public static FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource){
			return getFileOps(source).loadFolder(source, remoteSource);
		}
		
		public static FileOpsStatus loadFolder(Account source, RemoteFolder remoteSource, int depth){
			return getFileOps(source).loadFolder(source, remoteSource, depth);
		}
		
		public static FileOpsStatus createFolder(Account source, RemoteFolder parent, String name){
			return getFileOps(source).createFolder(source, parent, name);
		}
		
		public static FileOpsStatus copyFolder(Account source, RemoteFolder remoteSource, String remoteDest){
			return getFileOps(source).copyFolder(source, remoteSource, remoteDest);
		}
		
		public static FileOpsStatus moveFolder(Account source, RemoteFolder remoteSource, String remoteDest){
			return getFileOps(source).moveFolder(source, remoteSource, remoteDest);
		}
		
		public static FileOpsStatus deleteFolder(Account source, RemoteFolder remoteSource){
			return getFileOps(source).deleteFolder(source, remoteSource);
		}
		
		public static void requestOverwrite(String f, SimpleHandler onAccept, SimpleHandler onRefuse){
			Core.getGuiFactory().requestConfirmation("Overwrite?", null, "Are you sure you want to overwrite " + f + "?", onAccept, onRefuse);
		}
		
		public static void handleError(String t, String m){
			Core.getGuiFactory().displayError(t, null, m, true);
		}
		
		public static FileOpsStatus validateOauth(Account source){
			return getFileOps(source).validateOauth(source);
		}
		
	}
	
}
