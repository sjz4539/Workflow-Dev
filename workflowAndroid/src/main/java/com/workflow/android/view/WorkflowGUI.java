package com.workflow.android.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.workflow.android.R;
import com.workflow.android.controller.PlatformHelper;
import com.workflow.android.controller.ProcessMonitor;
import com.workflow.android.view.library.LibraryListView;
import com.workflow.android.view.library.LibraryView;
import com.workflow.core.controller.Core;
import com.workflow.core.view.IWorkflowGUI;
import com.workflow.core.view.library.ILibraryView;

public class WorkflowGUI extends Activity implements IWorkflowGUI{

	private static Context context;
	
	private DrawerLayout mainLayout;
	private LinearLayout navDrawer;
	private FrameLayout libraryView;
	
	private LibraryListView libraryListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		//do core init
		if(!Core.init(this, new PlatformHelper())){
		
			System.out.println("CORE INIT FAILED");
			
		}else{
		
			setContentView(R.layout.main_layout);
			
			mainLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
			navDrawer = (LinearLayout)findViewById(R.id.left_drawer);
			libraryView = (FrameLayout)findViewById(R.id.content_frame);
			
			libraryListView = (LibraryListView)(getLayoutInflater().inflate(R.layout.library_list_view, null));
			libraryListView.setModel(Core.getCore().getLibraries());
			
			navDrawer.addView(libraryListView);

		}
	}
	
	protected void onSaveInstanceState(Bundle savedInstanceState){
		
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceBundle){
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case R.id.action_settings:
				Core.getGuiFactory().showConfigureDialog();
				return true;
			case R.id.menu_item_new_library:
				Core.getGuiFactory().showNewLibraryDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	public static Context getContext(){
		return context;
	}

	@Override
	public void setLibraryView(ILibraryView lrv) {
		libraryView.removeAllViews();
		if(lrv != null){
			libraryView.addView((LibraryView)lrv);
		}
	}

	@Override
	public void updateLibraryList() {
		if(libraryListView != null){
			libraryListView.updateUI();
		}
	}

}
