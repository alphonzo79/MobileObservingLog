package com.rowley.mobileobservinglog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.rowley.mobileobservinglog.R;

public class ObservingLogActivity extends ActivityBase{
	
	Button btnNight;
	Button btnNormal;
    
	//Create listeners
    private final Button.OnClickListener btnNightOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("Joe:Debug", "Initial Screen Night Mode Button Clicked");
        	SettingsContainer.setNightMode();
        	Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener btnNormalOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("Joe:Debug", "Initial Screen Normal Mode Button Clicked");
    		SettingsContainer.setNormalMode();
    		Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("Joe:Debug", "Initial Screen onCreate");
        
        //Set the session mode to night until it get changed
        SettingsContainer.setNightMode();
        
        setContentView(R.layout.initial);
        btnNight=(Button)findViewById(R.id.initialNightButton);
        btnNormal=(Button)findViewById(R.id.initialNormalButton);
        btnNight.setOnClickListener(btnNightOnClick);
        btnNormal.setOnClickListener(btnNormalOnClick);
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
    
    //The compiler was not recognizing the onClick implementation inside of the onClickListeners created at the top of the file. 
    //This empty method satisfies the compiler so it won't yell about not implementing a method from the interface
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
    //Eat the menu press on the initial screen
	public boolean onKey(View v, int keyCode, KeyEvent event) {
	    switch (keyCode) {
	        case KeyEvent.KEYCODE_MENU:
	    		Log.d("Joe:Debug", "Initial Screen menu button eaten");
	         /* This is a sample for handling the Enter button */
	      return true;
	    }
	    return false;
	}
}