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

import com.mobileobservinglog.support.CustomizeBrightness;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class AddCatalogsScreen extends TabActivity{
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	CustomizeBrightness customizeBrightness = new CustomizeBrightness(this, this);

	//gather resources
	TabHost tabHost;
	Button menuButton;
	
	RelativeLayout customMenu;
	Button menuHomeButton;
	Button menuModeButton;
	Button menuSettingsButton;
	Button menuInfoButton;
	Button menuDonateButton;
	
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
        setUpMenuButtons();
    }
    
    private void setUpMenuButtons() {
    	menuButton = (Button)findViewById(R.id.menu_launcher);
    	if(menuButton != null) {
    		menuButton.setOnClickListener(toggleMenu);
    	}
    	
    	customMenu = (RelativeLayout)findViewById(R.id.custom_menu);
    	
    	menuHomeButton = (Button)findViewById(R.id.custom_menu_home_button);
    	if(menuHomeButton != null) {
    		menuHomeButton.setOnClickListener(returnHome);
    	}
    	menuModeButton = (Button)findViewById(R.id.custom_menu_mode_button);
    	if(menuModeButton != null) {
    		menuModeButton.setOnClickListener(toggleMode);
    	}
    	menuSettingsButton = (Button)findViewById(R.id.custom_menu_settings_button);
    	if(menuSettingsButton != null) {
    		menuSettingsButton.setOnClickListener(goToSettings);
    	}
    	menuInfoButton = (Button)findViewById(R.id.custom_menu_info_button);
    	if(menuInfoButton != null) {
    		menuInfoButton.setOnClickListener(goToInfoAbout);
    	}
    	menuDonateButton = (Button)findViewById(R.id.custom_menu_donate_button);
    	if(menuDonateButton != null) {
    		menuDonateButton.setOnClickListener(handleDonation);
    	}
    }
    
    protected final Button.OnClickListener toggleMenu = new Button.OnClickListener(){
    	public void onClick(View view){
    		toggleMenu();
    	}
    };
    
    void toggleMenu() {
    	if(customMenu != null) {
    		if(customMenu.getVisibility() == View.VISIBLE) {
    			customMenu.setVisibility(View.GONE);
    		} else {
    			customMenu.setVisibility(View.VISIBLE);
    		}
    	}
    }
    
    protected final Button.OnClickListener returnHome = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent settingsIntent = new Intent(AddCatalogsScreen.this.getApplication(), HomeScreen.class);
            startActivity(settingsIntent);
    	}
    };
    
    protected final Button.OnClickListener toggleMode = new Button.OnClickListener(){
    	public void onClick(View view){
    		switch (settingsRef.getSessionMode()){
    		case night:
    			Log.d("JoeDebug", "ActivityBase setting normal mode");
    			settingsRef.setNormalMode();
    			break;
    		case normal:
    			settingsRef.setNightMode();
    			Log.d("JoeDebug", "ActivityBase setting night mode");
    			break;
    		default:
    			Log.d("JoeDebug", "ActivityBase default switch in toggleMode");
    			break;
    		}
    		
    		Intent relaunch = new Intent(AddCatalogsScreen.this.getApplication(), AddCatalogsScreen.class);
    		startActivity(relaunch);
    		AddCatalogsScreen.this.finish();
    	}
    };
    
    protected final Button.OnClickListener goToSettings = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent settingsIntent = new Intent(AddCatalogsScreen.this.getApplication(), SettingsScreen.class);
            startActivity(settingsIntent);
    	}
    };
    
    protected final Button.OnClickListener goToInfoAbout = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent infoIntent = new Intent(AddCatalogsScreen.this.getApplication(), InfoScreen.class);
            startActivity(infoIntent);
    	}
    };
    
    protected final Button.OnClickListener handleDonation = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent donationIntent = new Intent(AddCatalogsScreen.this.getApplication(), DonationScreen.class);
            startActivity(donationIntent);
    	}
    };
	
    //Eat the menu press on the initial screen
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	toggleMenu();
	    return true;
    }
}
