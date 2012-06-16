package com.mobileobservinglog;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ViewObservingLocation extends ActivityBase{

	//gather resources
			FrameLayout body;
			
			@Override
		    public void onCreate(Bundle icicle) {
				Log.d("JoeDebug", "ViewLocations onCreate. Current session mode is " + settingsRef.getSessionMode());
		        super.onCreate(icicle);

				customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
				
		        //setup the layout
		        setContentView(settingsRef.getViewLocationsLayout());
		        body = (FrameLayout)findViewById(R.id.view_location_root); 
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
				Log.d("JoeDebug", "ViewLocations onResume. Current session mode is " + settingsRef.getSessionMode());
		        super.onResume();
		        setLayout();
		    }
			
		  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
			@Override
			public void setLayout(){
				setContentView(settingsRef.getViewLocationsLayout());
				super.setLayout();
				
				ScrollView fieldsScroller = (ScrollView)findViewById(R.id.view_locations_scroll_fields_view);
				LinearLayout buttonsLayout = (LinearLayout)findViewById(R.id.view_location_buttons_layout);
				int buttonsKeyboardSize = buttonsLayout.getHeight();
				
				MarginLayoutParams frameParams = (MarginLayoutParams)buttonsLayout.getLayoutParams();
				frameParams.setMargins(0, -buttonsKeyboardSize, 0, 0);
				
				MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
				scrollParams.setMargins(0, 0, 0, buttonsKeyboardSize);
				
				buttonsLayout.setLayoutParams(frameParams);
				fieldsScroller.setLayoutParams(scrollParams);
				
				body.postInvalidate();
			}
}
