package com.rowley.mobileobservinglog.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Menu;
import com.rowley.mobileobservinglog.ObservingLogActivity;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ObservingLogActivityTests extends ActivityInstrumentationTestCase2<ObservingLogActivity>{

	Activity mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public ObservingLogActivityTests(){
		super("com.rowley.mobileobservinglog", ObservingLogActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		mAut = new ObservingLogActivity();
		setActivity(mAut);
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/*All these are giving me testing trouble. They should be tested in each individual screen to allow for the UI and the OS to provide the events

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
	
	*/
	
	public void testUiTestExample(){
		mAut.runOnUiThread(new Runnable(){
			public void run(){
				//Put UI interactions here
			}
		});
	}
}
