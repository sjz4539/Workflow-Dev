package com.workflow.windows.controller;

import java.util.Optional;

import com.workflow.core.controller.Core;
import com.workflow.core.controller.IPlatformHelper;
import com.workflow.core.controller.IProcessMonitor;
import com.workflow.core.model.resource.ResourceTaskService;
import com.workflow.core.view.IWorkflowGUIFactory;
import com.workflow.windows.view.WorkflowGUIFactory;
import com.workflow.windows.view.dialog.UnsavedChangesDialog;

import javafx.scene.control.ButtonType;

public class PlatformHelper implements IPlatformHelper{

	private ResourceTaskService resTaskServ = null;
	
	@Override
	public String getStorageRoot() {
		return ".";
	}

	@Override
	public boolean checkUnsavedResources() {
		if(!Core.getResourceCache().checkUnsavedResources()){
			UnsavedChangesDialog dialog = new UnsavedChangesDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			return response.isPresent() && response.get().equals(ButtonType.OK);
		}
		return true;
	}

	@Override
	public boolean destroyCache() {
		if(!Core.getResourceCache().deleteAll()){
			UnsavedChangesDialog dialog = new UnsavedChangesDialog();
			Optional<ButtonType> response = dialog.showAndWait();
			return response.isPresent() && response.get().equals(ButtonType.OK);
		}
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

