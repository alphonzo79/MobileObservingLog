/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.mobileobservinglog.service.ChartDownloadService;
import com.mobileobservinglog.support.database.ScheduledDownloadsDao;

public class ObservingLogActivityParent extends ActivityBase{
	
	Button btnNight;
	Button btnNormal;
	
	//Create listeners
    private final Button.OnClickListener btnNightOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeDebug", "Initial Screen Night Mode Button Clicked");
    		settingsRef.setNightMode();
        	Intent intent = new Intent(ObservingLogActivityParent.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener btnNormalOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeDebug", "Initial Screen Normal Mode Button Clicked");
    		settingsRef.setNormalMode();
    		Intent intent = new Intent(ObservingLogActivityParent.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("JoeDebug", "Initial Screen onCreate");

		checkForScheduledDownloads();
        
        //Set the session mode to night until it get changed
		settingsRef.setNightMode();
		
		//capture the current button intensity setting to be saved and restored upon pause/destroy
		Window window = getWindow();
	    LayoutParams layoutParams = window.getAttributes();
	    try {
	        float brightnessValue = layoutParams.buttonBrightness;
	        Log.d("JoeDebug", "Original button Brightness is " + brightnessValue);
	        settingsRef.setOriginalButtonBrightness(brightnessValue);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
        
        setContentView(R.layout.initial);
        btnNight=(Button)findViewById(R.id.initialNightButton);
        btnNormal=(Button)findViewById(R.id.initialNormalButton);
        btnNight.setOnClickListener(btnNightOnClick);
        btnNormal.setOnClickListener(btnNormalOnClick);
    }
	
	@Override
    public void onPause() {
		Log.d("JoeDebug", "Initial Screen onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
		Log.d("JoeDebug", "Initial Screen onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
		Log.d("JoeDebug", "Initial Screen onResume");
        super.onResume();
    }
	
    //Eat the menu press on the initial screen
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("JoeDebug", "Initial Screen menu button eaten (onCreate)");
	    return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	Log.d("JoeDebug", "Initial Screen menu button eaten (onPrepare)");
	    return true;
    }

	private void checkForScheduledDownloads() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				ScheduledDownloadsDao dao = new ScheduledDownloadsDao(ObservingLogActivityParent.this);
				int count = dao.getScheduledDownloadCount();
				return count > 0;
			}

			@Override
			protected void onPostExecute(Boolean downloadsScheduled) {
				if(downloadsScheduled) {
					startService(new Intent(ObservingLogActivityParent.this, ChartDownloadService.class));
				}
			}
		}.execute();
	}
}