package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.widget.TextView;

public class ActivityBase extends Activity implements View.OnClickListener{

	//Member Variables
	
	//Menu
	MenuItem homeMenu;
	MenuItem toggleMenu;
	MenuItem settingsMenu;
	MenuItem infoMenu;
	
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
	
	//Home Layout
	private static int mHomeLayout;
	protected static int getHomeLayout(){
		return mHomeLayout;
	}
	private static void setHomeLayout(int layout){
		mHomeLayout = layout;
	}
	
	private static int mModeText;
	protected static int getModeButtonText(){
		return mModeText;
	}
	private static void setModeButtonText(int text){
		mModeText = text;
	}
	
	//Helper Methods
	//Called to change from normal mode to night mode. This method will set all of the style attributes that are used in setting layouts
	protected static void setNightMode(){
		setSessionMode(SessionMode.night);
		setModeButtonText(R.string.menu_toggle_normal_mode);
		setHomeLayout(R.layout.homescreen_night);
	}
	
	protected static void setNormalMode(){
		setSessionMode(SessionMode.normal);
		setModeButtonText(R.string.menu_toggle_night_mode);
		setHomeLayout(R.layout.homescreen);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
    
    //prepare the menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	homeMenu = menu.findItem(R.id.returnHome);
	    toggleMenu = menu.findItem(R.id.toggleMode);
	    settingsMenu = menu.findItem(R.id.settings);
	    infoMenu = menu.findItem(R.id.info);
	    //homeMenu.setIcon(getHomeButton());
	    //toggleMenu.setIcon(getModeButton());
	    toggleMenu.setTitle(getModeButtonText());
	    //settingsMenu.setIcon(getSettingsButton());
	    //infoMenu.setIcon(getInfoButton());
    	return true;
    }
	
	//create the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rowley.mobileobservinglog.R.menu.global_menu, menu);
	    setMenuBackground();	
	    return true;
	}
	
	//Used to customize the menu display
	protected void setMenuBackground(){                     
	    // Log.d(TAG, "Enterting setMenuBackGround");
		getLayoutInflater().setFactory( new Factory() {  
	        public View onCreateView(String name, Context context, AttributeSet attrs) {
	            if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	                try { // Ask our inflater to create the view  
	                    LayoutInflater f = getLayoutInflater();  
	                    final View view = f.createView( name, null, attrs );  
	                    /* The background gets refreshed each time a new item is added the options menu.  
	                    * So each time Android applies the default background we need to set our own  
	                    * background. This is done using a thread giving the background change as runnable 
	                    * object */
	                    new Handler().post( new Runnable() {  
	                        public void run () {  
	                            // sets the background color   
	                            view.setBackgroundResource(R.color.black);
	                            // sets the text color              
	                            ((TextView) view).setTextColor(0xAAFF0000);
	                            // sets the text size              
	                            ((TextView) view).setTextSize(14);
	                        }
	                    } );  
	                    return view;
	                }
	                catch ( InflateException e ) {}
	                catch ( ClassNotFoundException e ) {}  
	            } 
	            return null;
	        }
	    }); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.returnHome:
	        return true;
	    case R.id.toggleMode:
	        toggleMode();
	        return true;
	    case R.id.settings:
	        return true;
	    case R.id.info:
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void toggleMode() {
	}
}
