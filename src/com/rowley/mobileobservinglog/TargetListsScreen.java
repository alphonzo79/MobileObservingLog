package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.widget.LinearLayout;

public class TargetListsScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //setup the layout
        setContentView(SettingsContainer.getTargetListsLayout());
        body = (LinearLayout)findViewById(R.id.targets_root); 
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
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(SettingsContainer.getTargetListsLayout());
		body.postInvalidate();
	}
}
