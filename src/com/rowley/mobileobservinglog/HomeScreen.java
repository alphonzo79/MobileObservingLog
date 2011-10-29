package com.rowley.mobileobservinglog;

import android.content.Intent;
import android.os.Bundle;
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
    
	//Create listeners
    private final Button.OnClickListener catalogsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
        	setNightMode();
        	Intent intent = new Intent(HomeScreen.this.getApplication(), CatalogsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener targetsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		setNormalMode();
    		Intent intent = new Intent(HomeScreen.this.getApplication(), TargetListsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener addCatalogsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		setNormalMode();
    		Intent intent = new Intent(HomeScreen.this.getApplication(), AddCatalogsScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener backupRestoreButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		setNormalMode();
    		Intent intent = new Intent(HomeScreen.this.getApplication(), BackupRestoreScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener settingsButtonOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		setNormalMode();
    		Intent intent = new Intent(HomeScreen.this.getApplication(), SettingsScreen.class);
            startActivity(intent);
        }
    };
	
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
        body = (LinearLayout)findViewById(R.id.home_root); 
        
        //set listeners
        catalogsButton.setOnClickListener(catalogsButtonOnClick);
        targetsButton.setOnClickListener(targetsButtonOnClick);
        addCatalogsButton.setOnClickListener(addCatalogsButtonOnClick);
        backupRestoreButton.setOnClickListener(backupRestoreButtonOnClick);
        settingsButton.setOnClickListener(settingsButtonOnClick);
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
		super.toggleMode();
		setContentView(getHomeLayout());
		body.postInvalidate();
	}
}
