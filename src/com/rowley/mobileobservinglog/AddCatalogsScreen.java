package com.rowley.mobileobservinglog;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class AddCatalogsScreen extends TabActivity{
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();

	//gather resources
	LinearLayout body;
	TabHost tabHost;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		
        //setup the layout
        setContentView(settingsRef.getAddCatalogsLayout());
        body = (LinearLayout)findViewById(R.id.add_catalogs_root); 
        
        setupTabs();
	}

	private void setupTabs() {
		tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, InstalledCatalogsTab.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("installedCatalogs").setIndicator("Installed Catalogs").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, AvailableCatalogsTab.class);
        spec = tabHost.newTabSpec("availableCatalogs").setIndicator("Available Catalogs").setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
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
    }
}
