package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class AvailableCatalogsTab extends ActivityBase {

	ArrayList<String> availableCatalogList;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.available_catalogs_tab);
        
        availableCatalogList = new ArrayList<String>();
        availableCatalogList.add("Over There");
        availableCatalogList.add("GoodBye");
        availableCatalogList.add("Hello");

        setListAdapter(new ArrayAdapter<String>(this, settingsRef.getSettingsListLayout(), availableCatalogList));
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
	
	//For this screen, the tabs layout was causing problems with our regular toggle mode handling. So instead on this screen we will simply relaunch the activity,
	//then kill the current instance so a back press will not take us back to the other mode.
	@Override
	protected void toggleMode(){
		super.toggleMode();
		Intent intent = new Intent(this.getApplication(), AddCatalogsScreen.class);
        startActivity(intent);
        finish();
	}
}
