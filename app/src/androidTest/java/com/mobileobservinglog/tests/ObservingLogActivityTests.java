/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.tests;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.widget.Button;

import com.mobileobservinglog.ObservingLogActivityParent;
import com.mobileobservinglog.R;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.SettingsContainer.SessionMode;

public class ObservingLogActivityTests extends SingleLaunchActivityTestCase<ObservingLogActivityParent>
{

	ObservingLogActivityParent mAut = null;
	Instrumentation mInstrumentation = null;
	SettingsContainer mSettings = null;
	
	public ObservingLogActivityTests()
	{
		super("com.mobileobservinglog", ObservingLogActivityParent.class);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		Log.d("JoeTest", "Setup");
		super.setUp();
		mAut = getActivity();
		
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		Log.d("JoeTest", "Teardown");
		mAut.finish();
		super.tearDown();
	}
	
	/**
	 * this test will press the normal mode button, wait for home screen to display and check the session mode
	 * 
	 * @throws Throwable
	 */
	public void testSelectNormalMode() throws Throwable
	{
		Log.d("JoeTest", "Starting testSelectNormalMode");
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				Log.d("JoeTest", "Inside run()");
				Button normalMode = (Button)mAut.findViewById(R.id.initialNormalButton);
				
				//Set up the activity manager
				ActivityManager am = (ActivityManager)mAut.getApplication().getSystemService(Activity.ACTIVITY_SERVICE);
	        	
	        	//Press the normal mode button
				Log.d("JoeTest", "Clicking the Normal Mode button");
				normalMode.performClick();
				
				//Check for the screen display, settingsContainer.SessionMode, and current layout
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					Log.d("JoeTest", "Caught interrupted exception");
				}
				
				List<RunningTaskInfo> tasks = am.getRunningTasks(3);
				Log.d("JoeTest", "Top task is " + tasks.get(0).topActivity.toString());
				assertEquals("Home screen was not at the top of the stack", com.mobileobservinglog.HomeScreen.class.toString(), "class " + tasks.get(0).topActivity.getClassName().toString());
				assertEquals("SessionMode did not equal Normal", SessionMode.normal, mSettings.getSessionMode());
			}
		});
	}
	
	/**
	 * this test will press the night mode button, wait for home screen to display and check the session mode
	 * 
	 * @throws Throwable
	 */
	public void testSelectNightMode() throws Throwable
	{
		Log.d("JoeTest", "Starting testSelectNightMode");
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				Log.d("JoeTest", "Inside run()");
				Button nightMode = (Button)mAut.findViewById(R.id.initialNightButton);
				
				//Set up the activity manager
				ActivityManager am = (ActivityManager)mAut.getApplication().getSystemService(Activity.ACTIVITY_SERVICE);
	        	
	        	//Press the normal mode button
				Log.d("JoeTest", "Clicking the Night Mode button");
				nightMode.performClick();
				
				//Check for the screen display, settingsContainer.SessionMode, and current layout
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					Log.d("JoeTest", "Caught interrupted exception");
				}
				
				List<RunningTaskInfo> tasks = am.getRunningTasks(3);
				Log.d("JoeTest", "Top task is " + tasks.get(0).topActivity.toString());
				assertEquals("Home screen was not at the top of the stack", com.mobileobservinglog.HomeScreen.class.toString(), "class " + tasks.get(0).topActivity.getClassName().toString());
				assertEquals("SessionMode did not equal Normal", SessionMode.night, mSettings.getSessionMode());
			}
		});
	}
}
