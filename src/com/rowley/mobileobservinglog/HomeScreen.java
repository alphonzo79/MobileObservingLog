package com.rowley.mobileobservinglog;

import com.rowley.mobileobservinglog.R.menu;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.text.Html.TagHandler;
import android.util.AttributeSet;
import android.util.Log;

public class HomeScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	Button catalogsButton;
	Button targetsButton;
	Button addCatalogsButton;
	Button backupRestoreButton;
	Button settingsButton;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //setup the layout
        setContentView(getHomeLayout());
        catalogsButton = (Button)findViewById(R.id.navToCatalogsButton);
        targetsButton = (Button)findViewById(R.id.navToTargetsButton);
        addCatalogsButton = (Button)findViewById(R.id.navToAddCatalogsButton);
        backupRestoreButton = (Button)findViewById(R.id.navToBackupButton);
        settingsButton = (Button)findViewById(R.id.navToSettingsButton);
        body = (LinearLayout)findViewById(R.id.home_body); 
	}
	
	@Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //We want the home screen to behave like the bottom of the activity stack so we do not return to the initial screen
    //unless the application has been killed. Users can toggle the session mode with a menu item at all other times.
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
	
	//Toggle Mode menu item method
	@Override
	public void toggleMode(){
		switch (getSessionMode()){
			case night:
				setNormalMode();
				break;
			case normal:
				setNightMode();
				break;
			default:
				setNightMode();
				break;
		}
		setContentView(getHomeLayout());
		body.postInvalidate();
	}
}
