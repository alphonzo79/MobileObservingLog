package com.rowley.mobileobservinglog;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	Button catalogsButton;
	Button targetsButton;
	Button addCatalogsButton;
	Button backupRestoreButton;
	Button settingsButton;
	
	//Create listeners for each of the main buttons
    private final Button.OnClickListener catalogsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
        	Intent intent = new Intent(HomeScreen.this.getApplication(), CatalogsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener targetsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(HomeScreen.this.getApplication(), TargetListsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener addCatalogsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(HomeScreen.this.getApplication(), AddCatalogsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener backupRestoreButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(HomeScreen.this.getApplication(), BackupRestoreScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener settingsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(HomeScreen.this.getApplication(), SettingsScreen.class);
            startActivity(intent);
        }
    };
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "HomeScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);
        
        //setup the layout
        setContentView(settingsRef.getHomeLayout());
        findButtons();
        setListeners();
        
		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
	}
	
	@Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		Log.d("JoeDebug", "HomeScreen onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }

    //We want the home screen to behave like the bottom of the activity stack so we do not return to the initial screen
    //unless the application has been killed. Users can toggle the session mode with a menu item at all other times.
    @Override
    public void onBackPressed() {
    	//Check the activity stack and see if it's more than two deep (initial screen and home screen)
    	//If it's more than two deep, then let the app proccess the press
    	ActivityManager am = (ActivityManager)this.getSystemService(Activity.ACTIVITY_SERVICE);
    	List<RunningTaskInfo> tasks = am.getRunningTasks(3); //3 because we have to give it something. This is an arbitrary number
    	int activityCount = tasks.get(0).numActivities;
    	
    	Log.d("JoeDebug", "Activity Count is " + activityCount);
    	
    	if (activityCount < 3)
    	{
    		moveTaskToBack(true);
    	}
    	else
    	{
    		super.onBackPressed();
    	}
    }
    
    //Find all the buttons for use in the class. Abstracted so it can be used each time the layout is refreshed
    private void findButtons(){
        catalogsButton = (Button)findViewById(R.id.navToCatalogsButton);
        targetsButton = (Button)findViewById(R.id.navToTargetsButton);
        addCatalogsButton = (Button)findViewById(R.id.navToAddCatalogsButton);
        backupRestoreButton = (Button)findViewById(R.id.navToBackupButton);
        settingsButton = (Button)findViewById(R.id.navToSettingsButton);
        body = (LinearLayout)findViewById(R.id.home_root); 
    }
    
    //set all the listeners. Abstracted so it can be used each time the layout is refreshed
    private void setListeners(){
    	catalogsButton.setOnClickListener(catalogsButtonOnClick);
        targetsButton.setOnClickListener(targetsButtonOnClick);
        addCatalogsButton.setOnClickListener(addCatalogsButtonOnClick);
        backupRestoreButton.setOnClickListener(backupRestoreButtonOnClick);
        settingsButton.setOnClickListener(settingsButtonOnClick);
    }
	
	//Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		Log.d("JoeDebug", "HomeScreen setLayout. Current session mode is " + settingsRef.getSessionMode());
		setContentView(settingsRef.getHomeLayout());
		findButtons();
		setListeners();
		super.setLayout();
		body.postInvalidate();
	}
}
