package com.rowley.mobileobservinglog;

import android.app.Activity;
import android.content.Context;
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
		Log.d("Joe:Debug", "ActivityBase preparing the menu");
    	homeMenu = menu.findItem(R.id.returnHome);
	    toggleMenu = menu.findItem(R.id.toggleMode);
	    settingsMenu = menu.findItem(R.id.settings);
	    infoMenu = menu.findItem(R.id.info);
	    //homeMenu.setIcon(getHomeButton());
	    //toggleMenu.setIcon(getModeButton());
	    toggleMenu.setTitle(SettingsContainer.getModeButtonText());
	    //settingsMenu.setIcon(getSettingsButton());
	    //infoMenu.setIcon(getInfoButton());
    	return true;
    }
	
	//create the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Joe:Debug", "ActivityBase creating the menu");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rowley.mobileobservinglog.R.menu.global_menu, menu);
	    setMenuBackground();	
	    return true;
	}
	
	//Used to customize the menu display
	protected void setMenuBackground(){                     
	    Log.d("Joe:Debug", "Enterting setMenuBackGround");
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
		Log.d("Joe:Debug", "ActivityBase Options Menu Item pressed: " + item.getItemId());
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
	
	//called by the Toggle Mode options menu item. Changes the layout references.
	private void toggleMode() {
		Log.d("Joe:Debug", "ActivityBase toggle mode menu. Current session setting: " + SettingsContainer.getSessionMode());
		switch (SettingsContainer.getSessionMode()){
		case night:
			Log.d("Joe:Debug", "ActivityBase setting normal mode");
			SettingsContainer.setNormalMode();
			break;
		case normal:
			SettingsContainer.setNightMode();
			Log.d("Joe:Debug", "ActivityBase setting night mode");
			break;
		default:
			Log.d("Joe:Debug", "ActivityBase default switch in toggleMode");
			break;
		}
		
		setLayout();
	}
	
	//Meant to be overridden by the calling classes to set their individual layouts. 
	public void setLayout(){
		
	}
}
