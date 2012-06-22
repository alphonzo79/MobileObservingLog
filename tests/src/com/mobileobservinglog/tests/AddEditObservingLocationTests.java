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
