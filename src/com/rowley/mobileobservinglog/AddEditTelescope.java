package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class AddEditTelescope extends ActivityBase {

	//gather resources
	LinearLayout body;
	int telescopeId = -1;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditTelescope onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        telescopeId = this.getIntent().getIntExtra("com.rowley.mobileobservinglog", -1);
        //setup the layout
        //setContentView(settingsRef.getObjectDetailLayout());
        //body = (LinearLayout)findViewById(R.id.object_detail_root); 
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
		Log.d("JoeDebug", "AddEditTelescope onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		//setContentView(settingsRef.getObjectDetailLayout());
		super.setLayout();
		body.postInvalidate();
	}
}
