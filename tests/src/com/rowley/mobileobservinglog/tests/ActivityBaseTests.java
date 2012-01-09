package com.rowley.mobileobservinglog.tests;

import java.lang.reflect.Field;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.rowley.mobileobservinglog.ActivityBase;
import com.rowley.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends ActivityInstrumentationTestCase2<ActivityBase>{

	ActivityBase mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public ActivityBaseTests(){
		super("com.rowley.mobileobservinglog", ActivityBase.class);
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
	
	//Test setDimButton
	public void testsetDimButton(){
		mAut.setDimButtons(0.5f);
		
		Window window = mAut.getWindow();
	    LayoutParams layoutParams = window.getAttributes();
	    try {
	        Field buttonBrightness = layoutParams.getClass().getField(
	                "buttonBrightness");
	        float brightnessValue = buttonBrightness.getFloat(layoutParams);
	        assertEquals("Button brightness was not correct", 0.5f, brightnessValue);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
