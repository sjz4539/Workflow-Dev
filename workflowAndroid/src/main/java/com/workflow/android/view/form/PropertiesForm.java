package com.workflow.android.view.form;

import com.workflow.android.view.chooser.LocalChooserPane;
import com.workflow.core.controller.Core;
import com.workflow.core.view.chooser.Chooser;
import com.workflow.core.view.form.IPropertiesForm;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PropertiesForm extends LinearLayout implements IPropertiesForm{

	LocalChooserPane cacheRootPane;
	
	public PropertiesForm(Context context, AttributeSet attrs) {
		super(context, attrs);
		cacheRootPane = new LocalChooserPane(context, null, Chooser.Mode.MODE_SINGLE_FOLDER, false, Core.getCore().getProperty(Core.PROP_CACHE_ROOT), null);
		addView(cacheRootPane);
	}

	public void saveProperties() {
		Core.getCore().setProperty(Core.PROP_CACHE_ROOT, cacheRootPane.getString());
	}

}
