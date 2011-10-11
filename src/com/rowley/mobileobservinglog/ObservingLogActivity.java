package com.rowley.mobileobservinglog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.rowley.mobileobservinglog.R;

public class ObservingLogActivity extends ActivityBase implements View.OnClickListener{
	
	Button btnNight;
	Button btnNormal;
    
	//Create listeners
    private final Button.OnClickListener btnNightOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
        	setSessionMode(SessionMode.night);
        	btnNight.setText("Night Mode Set. mode = " + getSessionMode().toString());
        	btnNormal.setText("Normal Mode");
        	Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener btnNormalOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		setSessionMode(SessionMode.normal);
        	btnNormal.setText("Normal Mode Set. mode = " + getSessionMode().toString());
        	btnNight.setText("Night Mode");
        	Intent intent = new Intent(ObservingLogActivity.this.getApplication(), HomeScreen.class);
            startActivity(intent);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Set the session mode to night until it get changed
        setSessionMode(SessionMode.night);
        
        setContentView(R.layout.initial);
        btnNight=(Button)findViewById(R.id.initialNightButton);
        btnNormal=(Button)findViewById(R.id.initialNormalButton);
        btnNight.setOnClickListener(btnNightOnClick);
        btnNormal.setOnClickListener(btnNormalOnClick);
    }
    
    
    
    //The compiler was not recognizing the onClick implementation inside of the onClickListeners created at the top of the file. 
    //This empty method satisfies the compiler so it won't yell about not implementing a method from the interface
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
    
}