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

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.view.Menu;

import com.mobileobservinglog.HomeScreen;
import com.mobileobservinglog.SettingsContainer;

public class ActivityBaseTests extends SingleLaunchActivityTestCase<HomeScreen>{

	HomeScreen mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public ActivityBaseTests(){
		super("com.mobileobservinglog", HomeScreen.class);
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
		com.mobileobservinglog.SettingsContainer.SessionMode currentMode = mSettings.getSessionMode();
		assertNotNull("Initial session mode is null", currentMode);
		
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				mAut.hookToggleMode();
			}
		});
		
		com.mobileobservinglog.SettingsContainer.SessionMode newMode = mSettings.getSessionMode();
		assertNotNull(newMode);
		assertNotSame("The session mode did not change. Original mode: " + currentMode + ". new mode: " + newMode + ". ", currentMode, newMode);
	}
}
