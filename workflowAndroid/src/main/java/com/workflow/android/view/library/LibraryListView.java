package com.workflow.android.view.library;

import java.util.List;

import com.workflow.android.view.WorkflowGUI;
import com.workflow.core.controller.library.Library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LibraryListView extends ListView{

	private LibraryListAdapter listAdapter;
	
	public LibraryListView(Context context, AttributeSet attributes){
		super(context, attributes);
	}
	
	public void setModel(List<Library> libraries){
		listAdapter = new LibraryListView.LibraryListAdapter(WorkflowGUI.getContext(), libraries);
		super.setAdapter(listAdapter);
		updateUI();
	}
	
	public void updateUI(){
		if(listAdapter != null){
			listAdapter.notifyDataSetChanged();
		}
	}

	private static class LibraryListAdapter extends ArrayAdapter<Library>{

		public LibraryListAdapter(Context context, List<Library> objects){
			super(context, android.R.layout.simple_list_item_1, objects);
		}

		public View getView(int position, View convertView, ViewGroup parent){
			LibraryMenu view;
			if(convertView != null && convertView instanceof LibraryMenu){
				//recycle this view
				view = (LibraryMenu)convertView;
				//decouple it from whatever it belonged to
				if(view.getLibrary() != null){
					view.getLibrary().setMenu(null);
				}
				//set and couple the new model
				view.setLibrary(getItem(position));
				view.getLibrary().setMenu(view);
			}else{
				//get a new view
				view = (LibraryMenu)(getItem(position).getMenu());
			}
			view.updateUI();
			return view;
		}

	}
	
}
