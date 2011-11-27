package com.rowley.mobileobservinglog.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import com.rowley.mobileobservinglog.ActivityBase;
import com.rowley.mobileobservinglog.R;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends ActivityInstrumentationTestCase2<ActivityBase>{

	Activity mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public ActivityBaseTests(){
		super("com.rowley.mobileobservinglog", ActivityBase.class);
		//super(com.rowley.mobileobservinglog.ActivityBase.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		//setActivityInitialTouchMode(false);
		mAut = new ActivityBase();
		setActivity(mAut);
        //mAut = getActivity();
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
		
		//Create the menu to be used in the test
		//mMenu = new myMenu();
		MenuInflater inflater = mAut.getMenuInflater();
	    inflater.inflate(R.menu.global_menu, mMenu);
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	public void testOnPrepareMenu(){
		//Get the settings container
		//mSettings = SettingsContainer.getSettingsContainer();
		
		//Set night mode and check the values
		mSettings.setNightMode();
		mAut.onPrepareOptionsMenu(mMenu);
		mMenu = (Menu)mAut.findViewById(R.menu.global_menu);
		assertEquals(R.string.menu_toggle_normal_mode, mMenu.findItem(R.id.toggleMode).getTitle());
		
		//set Normal Mode and check the values
		mSettings.setNormalMode();
		mAut.onPrepareOptionsMenu(mMenu);
		mMenu = (Menu)mAut.findViewById(R.menu.global_menu);
		assertEquals(R.string.menu_toggle_night_mode, mMenu.findItem(R.id.toggleMode).getTitle());
	}
	
	public void testMenuHomePress(){
		
	}
	
	public void testMenuModePress(){
		
	}
	
	public void testMenuSettingsPress(){
		
	}
	
	public void testMenuInfoPress(){
		
	}
	
	public void testToggleMode(){
		
	}
	
	public void testUiTestExample(){
		mAut.runOnUiThread(new Runnable(){
			public void run(){
				//Put UI interactions here
			}
		});
	}
}
