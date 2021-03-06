/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support;

import com.mobileobservinglog.support.SettingsContainer.SessionMode;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class CustomizeBrightness {

	private Context client;
	private Activity activity;
	private SettingsContainer settingsRef;
	
	public CustomizeBrightness(Context context, Activity activity){
		client = context;
		this.activity = activity;
		settingsRef = SettingsContainer.getSettingsContainer();
	}

	/**
	 * This method is used to set the backlight intensity -- either full for normal mode or according to the user's
	 * preference set in the settings database for night mode. It in turn calls the overriden version of the method
	 * according to the session mode
	 */
	public void setBacklight()
	{
		if (settingsRef.getSessionMode() == SessionMode.night)
        	setBacklight(false);
        else
        	setBacklight(true);
	}
	
	/**
	 * Set the backlight intensity for the current screen. This method will either set it to full for normal mode
	 * or it will get the perferred night-mode setting from the SettingsContainer, parse it out into a float and 
	 * dim the screen to the appropriate level
	 * 
	 * This method is meant to be used internally only, but it's left public for testing purposes (need to learn about 
	 * test hooks and such in Java
	 * 
	 * @param full True for full intensity (normal mode), false for dim (night mode)
	 */
	public void setBacklight(boolean full)
	{
		WindowManager.LayoutParams WMLP = activity.getWindow().getAttributes();
		
		if (full)
		{
			WMLP.screenBrightness = -1.0F;
			activity.getWindow().setAttributes(WMLP);
		}
		else
		{
			int backlightPreference = Integer.parseInt(settingsRef.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, client));
			
			//Backlight preference setting is saved as an int from 0 - 10. In the platform, backlight intensity is 
			//set with a float from 0.0F - 1.0F. So if the user has left the default setting of 1, and we divide by 10
			//then we get a backlight intensity setting of 0.1F to feed to the platform, dimming the backlight 
			//down very low.
			float intensity = (float)backlightPreference/10;
			WMLP.screenBrightness = intensity;
			activity.getWindow().setAttributes(WMLP);
		}
	}
	
	/**
	 * Set the buttons backlight level down to protect night vision.
	 * @param Float Value. -1.0f for no override, 0.0f to turn lights off 1.0f for full intensity
	 */
	public void setDimButtons(float val) {
		Log.d("JoeTest", "getWindow: " + activity.getWindow());
		WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();

		if (layoutParams.buttonBrightness != val)
	    {
	    	try {
		        Log.d("JoeDebug", "Current button Brightness is " + layoutParams.buttonBrightness);
		        Log.d("JoeDebug", "Activity Base setDimButtons. Setting to " + val);
			    //layoutParams.buttonBrightness = val;
		        new BtnBrightness(layoutParams, val);
		    } catch (Exception e) {
		        Log.d("JoeDebug", "Failed to set button brightness");
		        e.printStackTrace();
		    }
	    	activity.getWindow().setAttributes(layoutParams);
	    }
	}

	private static class BtnBrightness {
	    BtnBrightness(LayoutParams lp, float v) {
	        lp.buttonBrightness = v;
	    }
	}

}
