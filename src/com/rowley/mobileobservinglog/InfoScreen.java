package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.widget.LinearLayout;

public class InfoScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //setup the layout
        setContentView(getInfoLayout());
        body = (LinearLayout)findViewById(R.id.info_root); 
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
	
	//Toggle Mode menu item method
	@Override
	public void toggleMode(){
		super.toggleMode();
		setContentView(getInfoLayout());
		body.postInvalidate();
	}
}
