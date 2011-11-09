package com.rowley.mobileobservinglog.tests;

import android.app.Activity;
import android.app.Instrumentation;
//import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.SingleLaunchActivityTestCase;
import android.view.Menu;
import com.rowley.mobileobservinglog.ActivityBase;
import com.rowley.mobileobservinglog.R;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends SingleLaunchActivityTestCase<ActivityBase>{

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
		mAut = getActivity();
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
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
