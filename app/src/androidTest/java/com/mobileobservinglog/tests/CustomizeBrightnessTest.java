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

import java.lang.reflect.Field;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mobileobservinglog.ObservingLogActivityParent;
import com.mobileobservinglog.support.CustomizeBrightness;

/**
 * @author Joe Rowley
 *
 */
public class CustomizeBrightnessTest extends SingleLaunchActivityTestCase<ObservingLogActivityParent>{
	
	//Class Under Test
	CustomizeBrightness mCut = null;
	ObservingLogActivityParent mAut = null;
	Instrumentation mInstrumentation = null;
	
	public CustomizeBrightnessTest(){
		super("com.mobileobservinglog", ObservingLogActivityParent.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mAut = getActivity();
		mCut = new CustomizeBrightness(mAut, mAut);
		mInstrumentation = getInstrumentation();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		mCut = null;
	}
	
	//Test setDimButton
	public void testsetDimButton() throws Throwable
	{
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				mCut.setDimButtons(0.5f);
				
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
	 * Test method for {@link com.com.mobileobservinglog.mainityBase#setBacklight()}.
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
				mCut.setBacklight(true);
				
				WindowManager.LayoutParams WMLP = mAut.getWindow().getAttributes();
				Log.d("JoeTest", "Full Brightness is " + WMLP.screenBrightness);
				assertEquals("Full Brightness was not right", -1.0F, WMLP.screenBrightness);
				
				mCut.setBacklight(false);
				
				WMLP = mAut.getWindow().getAttributes();
				Log.d("JoeTest", "night mode Brightness is " + WMLP.screenBrightness);
				assertTrue("Night Mode Brightness was not less than full", WMLP.screenBrightness > 0.0F);
			}
		});
	}
}
