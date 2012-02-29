package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class ManageLocationsScreen extends ActivityBase {

	//gather resources
		LinearLayout body;
		
		@Override
	    public void onCreate(Bundle icicle) {
			Log.d("JoeDebug", "ManageLocations onCreate. Current session mode is " + settingsRef.getSessionMode());
	        super.onCreate(icicle);

			setDimButtons(settingsRef.getButtonBrightness());
			
	        //setup the layout
	        setContentView(settingsRef.getManageLocationsLayout());
	        body = (LinearLayout)findViewById(R.id.manage_locations_root); 
		}
		
		@Override
	    public void onPause() {
	        super.onPause();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }

	    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
	    @Override
	    public void onResume() {
			Log.d("JoeDebug", "ManageLocations onResume. Current session mode is " + settingsRef.getSessionMode());
	        super.onResume();
	        setLayout();
	    }
		
	  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
		@Override
		public void setLayout(){
			setContentView(settingsRef.getManageLocationsLayout());
			super.setLayout();
			body.postInvalidate();
		}
}
