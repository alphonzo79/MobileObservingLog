package com.rowley.mobileobservinglog;

import com.mobileobservinglog.strategies.CustomizeBrightness;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class ManageEquipmentScreen extends TabActivity {
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	CustomizeBrightness customizeBrightness = new CustomizeBrightness(this, this);

	//gather resources
	TabHost tabHost;
	
	@Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
        customizeBrightness.setBacklight();        
		
        //setup the layout
        setContentView(settingsRef.getManageEquipmentLayout());
        setupTabs(); 
	}

	private void setupTabs() {
		tabHost = getTabHost();  // The activity TabHost
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, TelescopeTab.class);
        setupTabIndicator(intent, R.string.manage_telescopes_tab);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, EyepiecesTab.class);
        setupTabIndicator(intent, R.string.manage_eyepieces_tab);

        tabHost.setCurrentTab(0);
	}
	
	private void setupTabIndicator(Intent intent, int labelId){
		View tabIndicator = LayoutInflater.from(this).inflate(settingsRef.getTabIndicator(), getTabWidget(), false);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
		
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}
	
	@Override
    public void onPause() {
        super.onPause();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customizeBrightness.setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		super.onResume();
        customizeBrightness.setBacklight();        
        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
    }
}
