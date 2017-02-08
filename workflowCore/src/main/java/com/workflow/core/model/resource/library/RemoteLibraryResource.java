package com.workflow.core.model.resource.library;

import com.workflow.core.controller.library.RemoteLibrary;

public class RemoteLibraryResource extends LibraryResource{

	private static final long serialVersionUID = 1L;

	public RemoteLibraryResource(String p, RemoteLibrary l) {
		super(p, l);
	}

	public RemoteLibraryResource copyObject() {
		return null;
	}

	public boolean storedRemotely() {
		return true;
	}
}
