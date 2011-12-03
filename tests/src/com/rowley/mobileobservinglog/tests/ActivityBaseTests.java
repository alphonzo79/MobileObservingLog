package com.rowley.mobileobservinglog.tests;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Menu;
import android.view.LayoutInflater.Factory;

import com.rowley.mobileobservinglog.ActivityBase;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends ActivityInstrumentationTestCase2<ActivityBase>{

	ActivityBase mAut = null;
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
		mAut = new ActivityBase();
		setActivity(mAut);
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	//Test toggle mode method
	public void testToggleMode(){
		com.rowley.mobileobservinglog.SettingsContainer.SessionMode currentMode = mSettings.getSessionMode();
		assertNotNull("Initial session mode is null", currentMode);
		mAut.hookToggleMode();
		
		com.rowley.mobileobservinglog.SettingsContainer.SessionMode newMode = mSettings.getSessionMode();
		assertNotNull(newMode);
		assertNotSame("The session mode did not change. Original mode: " + currentMode + ". new mode: " + newMode + ". ", currentMode, newMode);
	}
	
	//Test the set menu background method
	public void testSetMenuBackground()
	{
		//View testView = mAut.onCreateView(, context, attrs)
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
