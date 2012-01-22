package com.rowley.mobileobservinglog.tests;

import java.lang.reflect.Field;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

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
	
	//Test setDimButton
	public void testsetDimButton() throws Throwable
	{
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				mAut.setDimButtons(0.5f);
				
				Window window = mAut.getWindow();
			    LayoutParams layoutParams = window.getAttributes();
			    try {
			        Field buttonBrightness = layoutParams.getClass().getField("buttonBrightness");
			        float brightnessValue = buttonBrightness.getFloat(layoutParams);
			        assertEquals("Button brightness was not correct", 0.5f, brightnessValue);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		});
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.ActivityBase#setBacklight()}.
	 * 
	 * This test will set the night-mode backlight and then check to see that it's set right.
	 * @throws Throwable 
	 */
	public void testSetBacklight() throws Throwable
	{
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				mAut.setBacklight(true);
				
				WindowManager.LayoutParams WMLP = mAut.getWindow().getAttributes();
				Log.d("JoeTest", "Full Brightness is " + WMLP.screenBrightness);
				assertEquals("Full Brightness was not right", -1.0F, WMLP.screenBrightness);
				
				mAut.setBacklight(false);
				
				WMLP = mAut.getWindow().getAttributes();
				Log.d("JoeTest", "night mode Brightness is " + WMLP.screenBrightness);
				assertTrue("Night Mode Brightness was not less than full", WMLP.screenBrightness > 0.0F);
			}
		});
	}
}
