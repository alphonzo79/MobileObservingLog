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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.mobileobservinglog.support.CustomizeBrightness;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class ActivityBase extends ListActivity implements View.OnClickListener{

	//Member Variables
	CustomizeBrightness customizeBrightness = new CustomizeBrightness(this, this);
	
	//Menu
	Button menuButton;
	
	Button menuHomeButton;
	Button menuModeButton;
	Button menuSettingsButton;
	Button menuInfoButton;
	Button menuDonateButton;
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	
	//Most of the activity life cycle methods are included here to be inherited by other classes to ensure that
	//button brightness will be set upon each resume and restored on each destroy and pause. OnCreate is not
	//included because we need to ensure that we can capture the existing setting to be restored on the Initial
	//screen launch. Since the super method is called first, including it here would destroy that setting before
	//we can capture it. That means that setDimButtons needs to be called in each child class
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		
        customizeBrightness.setBacklight();
	}
	
	@Override
    public void onPause() {
		Log.d("JoeDebug", "ActivityBase onPause");
        super.onPause();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onDestroy() {
		Log.d("JoeDebug", "ActivityBase onDestroy");
        super.onDestroy();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onResume() {
		Log.d("JoeDebug", "ActivityBase onResume");
        super.onResume();
		
        customizeBrightness.setBacklight();        
        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
    }     
    
    //The compiler was not recognizing the onClick implementation inside of the onClickListeners created at the top of the file. 
    //This empty method satisfies the compiler so it won't yell about not implementing a method from the interface
	public void onClick(View v) {
		// TODO Auto-generated method stub		
	}
	
	//called by the Toggle Mode options menu item. Changes the layout references.
	protected void toggleMode() {
		Log.d("JoeDebug", "ActivityBase toggle mode menu. Current session setting: " + settingsRef.getSessionMode());
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
		
		setLayout();
	}
	
	//Meant to be overridden by the calling classes to set their individual layouts. 
	public void setLayout(){
		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		customizeBrightness.setBacklight();
		setUpMenuButtons();
	}
    
    private void setUpMenuButtons() {
    	menuButton = (Button)findViewById(R.id.menu_launcher);
    	if(menuButton != null) {
    		menuButton.setOnClickListener(launchMenu);
    	}
    	
    	menuHomeButton = (Button)findViewById(R.id.custom_menu_home_button);
    	menuHomeButton.setOnClickListener(returnHome);
    	menuModeButton = (Button)findViewById(R.id.custom_menu_mode_button);
    	menuModeButton.setOnClickListener(toggleMode);
    	menuSettingsButton = (Button)findViewById(R.id.custom_menu_settings_button);
    	menuSettingsButton.setOnClickListener(goToSettings);
    	menuInfoButton = (Button)findViewById(R.id.custom_menu_info_button);
    	menuInfoButton.setOnClickListener(goToInfoAbout);
    	menuDonateButton = (Button)findViewById(R.id.custom_menu_donate_button);
    	menuDonateButton.setOnClickListener(handleDonation);
    }
    
    protected final Button.OnClickListener launchMenu = new Button.OnClickListener(){
    	public void onClick(View view){
    		RelativeLayout customMenu = (RelativeLayout)findViewById(R.id.custom_menu);
    		customMenu.setVisibility(View.VISIBLE);
    		menuButton.setOnClickListener(dismissMenu);
    	}
    };
    
    protected final Button.OnClickListener dismissMenu = new Button.OnClickListener(){
    	public void onClick(View view){
    		RelativeLayout customMenu = (RelativeLayout)findViewById(R.id.custom_menu);
    		customMenu.setVisibility(View.GONE);
    		menuButton.setOnClickListener(launchMenu);
    	}
    };
    
    protected final Button.OnClickListener returnHome = new Button.OnClickListener(){
    	public void onClick(View view){
    		if (!ActivityBase.this.getComponentName().toShortString().contains("HomeScreen"))
	    	{
	    		Intent settingsIntent = new Intent(ActivityBase.this.getApplication(), HomeScreen.class);
	            startActivity(settingsIntent);
	    	}
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

    		setLayout();
    	}
    };
    
    protected final Button.OnClickListener goToSettings = new Button.OnClickListener(){
    	public void onClick(View view){
    		if (!ActivityBase.this.getComponentName().toShortString().contains("SettingsScreen"))
	    	{
	    		Intent settingsIntent = new Intent(ActivityBase.this.getApplication(), SettingsScreen.class);
	    		startActivity(settingsIntent);
	    	}
    	}
    };
    
    protected final Button.OnClickListener goToInfoAbout = new Button.OnClickListener(){
    	public void onClick(View view){
    		if (!ActivityBase.this.getComponentName().toShortString().contains("InfoScreen"))
	    	{
	    		Intent infoIntent = new Intent(ActivityBase.this.getApplication(), InfoScreen.class);
	    		startActivity(infoIntent);
	    	}
    	}
    };
    
    protected final Button.OnClickListener handleDonation = new Button.OnClickListener(){
    	public void onClick(View view){
    		
    	}
    };
	
    //Eat the menu press on the initial screen
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
	    return true;
    }
	
	//Hook to allow testing of toggle mode private method
	public void hookToggleMode(){
		toggleMode();
	}
}
