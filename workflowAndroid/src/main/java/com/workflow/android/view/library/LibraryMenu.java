package com.workflow.android.view.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.workflow.android.R;
import com.workflow.core.controller.library.Library;
import com.workflow.core.view.library.ILibraryMenu;

public class LibraryMenu extends LinearLayout implements ILibraryMenu{

	private Library library;
	
	private TextView nameLabel;
	private ImageView statusIcon;
	
	public LibraryMenu(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		nameLabel = (TextView)findViewById(R.id.library_name);
		statusIcon = (ImageView)findViewById(R.id.status_image);
	}
	
	public Library getLibrary(){
		return library;
	}
	
	public void setLibrary(Library l){
		library = l;
		updateUI();
	}
	
	@Override
	public void updateUI() {
		nameLabel.setText(library == null ? "" : library.getName());
	}

	@Override
	public void updateStatus(String m) {
		if(m != null && m.length() > 0){
			statusIcon.setVisibility(VISIBLE);
		}else{
			statusIcon.setVisibility(GONE);
		}
	}

}
