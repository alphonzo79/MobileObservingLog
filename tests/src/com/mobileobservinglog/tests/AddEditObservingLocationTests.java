package com.mobileobservinglog.tests;

import java.util.List;

import junit.framework.Assert;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.ActivityManager.RunningTaskInfo;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.widget.Button;

import com.mobileobservinglog.AddEditObservingLocation;
import com.mobileobservinglog.R;
import com.mobileobservinglog.SettingsContainer;
import com.mobileobservinglog.SettingsContainer.SessionMode;

public class AddEditObservingLocationTests extends SingleLaunchActivityTestCase<AddEditObservingLocation>{
	AddEditObservingLocation mAut = null;
	Instrumentation mInstrumentation = null;
	
	public AddEditObservingLocationTests(){
		super("com.mobileobservinglog", AddEditObservingLocation.class);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		Log.d("JoeTest", "Setup");
		super.setUp();
		mAut = getActivity();
		
		mInstrumentation = getInstrumentation();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		Log.d("JoeTest", "Teardown");
		mAut.finish();
		super.tearDown();
	}
	
	/**
	 * Test the formatting of a longitude or lattitude value returned from the location manager
	 * 
	 * @throws Throwable
	 */
	public void testFormatCoordinate() throws Throwable
	{
		Log.d("JoeTest", "Starting testSelectNormalMode");
		String formatted = mAut.formatCoordinate("-116:35:8.16432");
		Assert.assertEquals("The method did not format the string properly", "116°35'8.16\"", formatted);
	}
}
