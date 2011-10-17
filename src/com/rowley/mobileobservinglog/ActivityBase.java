package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

public class ActivityBase extends Activity implements View.OnClickListener{

	//Member Variables
	
	//SessionMode
	protected static enum SessionMode{
		night,
		normal
	} 
	
	private static SessionMode mSessionMode;
	protected static SessionMode getSessionMode(){
		return mSessionMode;
	}
	private static void setSessionMode(SessionMode mode){
		mSessionMode = mode;
	}
	
	//Header
	private static int mHeaderBackground;
	protected static int getHeaderBack(){
		return mHeaderBackground;
	}
	private static void setHeaderBack(int color){
		mHeaderBackground = color;
	}
	
	private static int mHeaderTextColor;
	protected static int getHeaderTextColor(){
		return mHeaderTextColor;
	}
	private static void setHeaderTextColor(int color){
		mHeaderTextColor = color;
	}
	
	//General Body attributes
	private static int mBodyBack;
	protected static int getBodyBack(){
		return mBodyBack;
	}
	private static void setBodyBack(int drawable){
		mBodyBack = drawable;
	}
	
	private static int mBodyText;
	protected static int getBodyText(){
		return mBodyText;
	}
	private static void setBodyText(int color){
		mBodyText = color;
	}
	
	//Helper Methods
	//Called to change from normal mode to night mode. This method will set all of the style attributes that are used in setting layouts
	protected static void setNightMode(){
		setSessionMode(SessionMode.night);
		setHeaderBack(Color.RED);
		setHeaderTextColor(Color.BLACK);
		setBodyBack(com.rowley.mobileobservinglog.R.drawable.black_back);
		setBodyText(Color.RED);
	}
	
	protected static void setNormalMode(){
		setSessionMode(SessionMode.normal);
		setHeaderBack(Color.WHITE);
		setHeaderTextColor(Color.BLACK);
		setBodyBack(com.rowley.mobileobservinglog.R.drawable.parchment);
		setBodyText(Color.BLACK);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
}
