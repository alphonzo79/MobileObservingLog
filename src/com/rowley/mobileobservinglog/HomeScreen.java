package com.rowley.mobileobservinglog;

import com.rowley.mobileobservinglog.R.menu;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.text.Html.TagHandler;
import android.util.AttributeSet;
import android.util.Log;

public class HomeScreen extends ActivityBase{

	//gather resources
	TextView headerText;
	TextView footerText;
	LinearLayout body;
	MenuItem toggle;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //setup the layout
        setContentView(R.layout.homescreen);
        headerText = (TextView)findViewById(R.id.homescreenHeader);
        footerText = (TextView)findViewById(R.id.initialFooter);
        body = (LinearLayout)findViewById(R.id.home_body);
        headerText.setBackgroundColor(getHeaderBack());
        headerText.setTextColor(getHeaderTextColor());
        body.setBackgroundResource(getBodyBack());
        footerText.setTextColor(getBodyText());
        footerText.setText("Home Screen -- Yay \r\n\r\n" + getSessionMode().toString());
        
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
	
	//create the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rowley.mobileobservinglog.R.menu.global_menu, menu);
	    setMenuBackground();
	    return true;
	}
	
	//Toggle Mode menu item method
	@SuppressWarnings("unused")
	public void toggleMode(MenuItem caller){
		Log.d("Joe", "toggleMode");
		switch (getSessionMode()){
			case night:
				setNormalMode();
				break;
			case normal:
				setNightMode();
				break;
			default:
				setNightMode();
				break;
		}
		body.postInvalidate();
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
	                            ((TextView) view).setTextColor(Color.RED);
	                            // sets the text size              
	                            ((TextView) view).setTextSize(18);
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

}
