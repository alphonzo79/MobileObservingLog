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

import com.mobileobservinglog.strategies.CustomizeBrightness;
import com.mobileobservinglog.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class AddCatalogsScreen extends TabActivity{
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	CustomizeBrightness customizeBrightness = new CustomizeBrightness(this, this);

	//gather resources
	TabHost tabHost;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		
        //setup the layout
        setContentView(settingsRef.getAddCatalogsLayout()); 
        customizeBrightness.setBacklight();        
        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
        setupTabs();
	}

	private void setupTabs() {
		tabHost = getTabHost();  // The activity TabHost
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, InstalledCatalogsTab.class);
        setupTabIndicator(intent, R.string.installed_catalogs_label);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, AvailableCatalogsTab.class);
        setupTabIndicator(intent, R.string.available_catalogs_label);

        tabHost.setCurrentTab(0);
	}
	
	private void setupTabIndicator(Intent intent, int labelId){
		View tabIndicator = LayoutInflater.from(this).inflate(settingsRef.getTabIndicator(), getTabWidget(), false);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
		
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}
	
	@Override
    public void onPause() {
        super.onPause();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
        super.onResume();
        customizeBrightness.setBacklight();        
        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
    }
}
