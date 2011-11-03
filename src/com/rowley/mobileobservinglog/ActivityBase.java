package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
    
    //prepare the menu. This originally would have changed the mode button text, the icons and the text color according to the 
	//session mode, but the text color is handled in the inflator, which is only called on onCreateOptionsMenu. It causes problems
	//with the factory already being created if we do it in this method. That means that I currently do not have a way to change
	//the text color each time the menu displays. So I have commented out the lines that would have customized anything other
	//than the mode text.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
		Log.d("JoeDebug", "ActivityBase preparing the menu");
    	homeMenu = menu.findItem(R.id.returnHome);
	    toggleMenu = menu.findItem(R.id.toggleMode);
	    settingsMenu = menu.findItem(R.id.settings);
	    infoMenu = menu.findItem(R.id.info);
	    //homeMenu.setIcon(getHomeButton());
	    //toggleMenu.setIcon(getModeButton());
	    toggleMenu.setTitle(settingsRef.getModeButtonText());
	    //settingsMenu.setIcon(getSettingsButton());
	    //infoMenu.setIcon(getInfoButton());
    	return true;
    }
	
	//create the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("JoeDebug", "ActivityBase creating the menu");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rowley.mobileobservinglog.R.menu.global_menu, menu);
	    setMenuBackground();	
	    return true;
	}
	
	//Used to customize the menu display
	protected void setMenuBackground(){                     
	    Log.d("JoeDebug", "Enterting setMenuBackGround");
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
	
	//the actions taken by each menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("JoeDebug", "ActivityBase Options Menu Item pressed: " + item.getItemId());
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.returnHome:
	    	//Check whether we are already on home screen
	    	//If not on home screen then navigate or tear down the activity stack
	        return true;
	    case R.id.toggleMode:
	        toggleMode();
	        return true;
	    case R.id.settings:
    		Intent settingsIntent = new Intent(this.getApplication(), SettingsScreen.class);
            startActivity(settingsIntent);
	        return true;
	    case R.id.info:
    		Intent infoIntent = new Intent(this.getApplication(), InfoScreen.class);
            startActivity(infoIntent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	//called by the Toggle Mode options menu item. Changes the layout references.
	private void toggleMode() {
		Log.d("JoeDebug", "ActivityBase toggle mode menu. Current session setting: " + settingsRef.getSessionMode());
		switch (settingsRef.getSessionMode()){
		case night:
			Log.d("JoeDebug", "ActivityBase setting normal mode");
			settingsRef.setNormalMode();
			break;
		case normal:
			settingsRef.setNightMode();
			Log.d("JoeDebug", "ActivityBase setting night mode");
			break;
		default:
			Log.d("JoeDebug", "ActivityBase default switch in toggleMode");
			break;
		}
		
		setLayout();
	}
	
	//Meant to be overridden by the calling classes to set their individual layouts. 
	public void setLayout(){
		
	}
}
