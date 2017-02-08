package com.workflow.android.controller;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.android.view.WorkflowGUIFactory;
import com.workflow.core.controller.IPlatformHelper;
import com.workflow.core.controller.IProcessMonitor;
import com.workflow.core.view.IWorkflowGUIFactory;

public class PlatformHelper implements IPlatformHelper{

	public String getStorageRoot(){
		return WorkflowGUI.getContext().getCacheDir().getAbsolutePath();
	}

	@Override
	public boolean checkUnsavedResources() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean destroyCache() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IWorkflowGUIFactory getGuiFactory() {
		return new WorkflowGUIFactory();
	}

	@Override
	public IProcessMonitor getProcessMonitor() {
		return new ProcessMonitor();
	}

}
