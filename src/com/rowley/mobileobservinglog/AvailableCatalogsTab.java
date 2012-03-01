package com.rowley.mobileobservinglog;

import android.app.ListActivity;
import android.os.Bundle;

public class AvailableCatalogsTab extends ActivityBase {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.available_catalogs_tab);
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
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	private void prepareListView()
	{
		//settingsList = getSettingsList();
        //setListAdapter(new ArrayAdapter<String>(this, settingsRef.getSettingsListLayout(), settingsList));
	}
	
	@Override
	protected void toggleMode(){
		super.toggleMode();
		//Relaunch the activity
	}
}
