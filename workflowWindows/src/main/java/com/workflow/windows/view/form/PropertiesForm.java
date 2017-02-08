package com.workflow.windows.view.form;

import com.workflow.core.controller.Core;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.form.IPropertiesForm;
import com.workflow.windows.view.chooser.LocalChooserPane;

import javafx.scene.layout.VBox;

public class PropertiesForm extends VBox implements IPropertiesForm{

	private LocalChooserPane cacheRootPane;
	
	public PropertiesForm(){
		
		String cacheRoot = Core.getCore().getProperty(Core.PROP_CACHE_ROOT);
		
		//create form objects
		cacheRootPane = new LocalChooserPane("File Cache Location:", Chooser.Mode.MODE_SINGLE_FOLDER, false, cacheRoot, null);
		
		//assemble GUI
		getChildren().add(cacheRootPane);
	}
	
	public void saveProperties(){
		Core.getCore().setProperty(Core.PROP_CACHE_ROOT, cacheRootPane.getString());
	}
	
}
