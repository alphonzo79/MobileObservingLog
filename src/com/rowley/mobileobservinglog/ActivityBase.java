package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.os.Bundle;

public class ActivityBase extends Activity{

	//Instantiate the styles to use all over the app
	//Styles appStyles = new Styles();
	
	//Member Variables
	public enum SessionMode{
		night,
		normal
	} 
	
	private static SessionMode mSessionMode;
	public static SessionMode getSessionMode(){
		return mSessionMode;
	}
	public static void setSessionMode(SessionMode mode){
		mSessionMode = mode;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
