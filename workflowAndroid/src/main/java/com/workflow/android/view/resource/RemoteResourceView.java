package com.workflow.android.view.resource;

import com.workflow.android.R;
import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.Core;
import com.workflow.core.model.resource.remote.RemoteResource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemoteResourceView extends LinearLayout{

	ImageView tag, navArrow;
	TextView nameLabel;
	
	public RemoteResourceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		tag = new ImageView(context);
		navArrow = (ImageView)findViewById(R.id.remote_resource_view_nav_arrow);
		nameLabel = (TextView)findViewById(R.id.remote_resource_view_resource_name);
	}
	
	public void setResource(RemoteResource res){
		nameLabel.setText(res.getName());
		tag.setImageDrawable( 
			res.isDirectory() ? 
			WorkflowGUI.getContext().getResources().getDrawable(R.drawable.folder_16) : 
			WorkflowGUI.getContext().getResources().getDrawable(R.drawable.document_16) 
		);
		nameLabel.setTag(tag);
		navArrow.setVisibility(res.isDirectory() ? VISIBLE : GONE);
	}

}
