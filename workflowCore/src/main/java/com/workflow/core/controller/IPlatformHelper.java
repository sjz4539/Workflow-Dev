package com.workflow.core.controller;

import com.workflow.core.view.IWorkflowGUIFactory;

/**
 * Used by Core to perform platform-specific tasks and retrieve platform-specific '
 * implementations of core module interfaces. Must be implemented by platform-specific modules.
 * 
 * @author Steven Zuchowski
 */
public interface IPlatformHelper {
	
	/**
	 * @return Platform-specific implementation of IWorkflorGUIFactory.
	 */
	public IWorkflowGUIFactory getGuiFactory();

	/**
	 * @return Platform-specific implementation of IProcessMonitor.
	 */
	public IProcessMonitor getProcessMonitor();
	
	/**
	 * @return The path to the root storage folder for this program, as a String.
	 */
	public String getStorageRoot();
	
	/**
	 * @return True if any resources (libraries, task resources, etc) have not been uploaded 
	 * to remote storage since they were last modified locally. False otherwise.
	 */
	public boolean checkUnsavedResources();
	
	/**
	 * Cleans up the local cache, deleting all contents.
	 * @return True if the operation succeeds, false otherwise.
	 */
	public boolean destroyCache();
	
}
