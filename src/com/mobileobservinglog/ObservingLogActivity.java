package com.mobileobservinglog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import com.mobileobservinglog.R;

public class ObservingLogActivity extends ActivityBase{
	
	Button btnNight;
	Button btnNormal;
	
	//Create listeners
    private final Button.OnClickListener btnNightOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeDebug", "Initial Screen Night Mode Button Clicked");
    		settingsRef.setNightMode();
        	Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener btnNormalOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeDebug", "Initial Screen Normal Mode Button Clicked");
    		settingsRef.setNormalMode();
    		Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("JoeDebug", "Initial Screen onCreate");
        
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
}