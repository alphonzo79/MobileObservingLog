package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.os.Bundle;

public class ActivityBase extends Activity{

	//Member Variables
	public static enum SessionMode{
		night,
		normal
	} 
	
	private SessionMode mSessionMode;
	public SessionMode getSessionMode(){
		return mSessionMode;
	}
	public void setSessionMode(SessionMode mode){
		this.mSessionMode = mode;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Set the session mode to night until it get changed
        //mSessionMode = SessionMode.night;
    }


}
