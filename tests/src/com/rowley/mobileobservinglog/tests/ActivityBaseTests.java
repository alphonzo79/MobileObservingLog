package com.rowley.mobileobservinglog.tests;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.view.Menu;

import com.rowley.mobileobservinglog.HomeScreen;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends SingleLaunchActivityTestCase<HomeScreen>{

	HomeScreen mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public ActivityBaseTests(){
		super("com.rowley.mobileobservinglog", HomeScreen.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		mAut = getActivity();
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	//Test toggle mode method
	public void testToggleMode() throws Throwable{
		com.rowley.mobileobservinglog.SettingsContainer.SessionMode currentMode = mSettings.getSessionMode();
		assertNotNull("Initial session mode is null", currentMode);
		
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				mAut.hookToggleMode();
			}
		});
		
		com.rowley.mobileobservinglog.SettingsContainer.SessionMode newMode = mSettings.getSessionMode();
		assertNotNull(newMode);
		assertNotSame("The session mode did not change. Original mode: " + currentMode + ". new mode: " + newMode + ". ", currentMode, newMode);
	}
}
