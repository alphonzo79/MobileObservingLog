package com.rowley.mobileobservinglog;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SettingsScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "SettingsScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getSettingsLayout());
        body = (LinearLayout)findViewById(R.id.settings_root); 
        
        SettingObject[] settingsList = getSettingsList();
        
        setListAdapter(new ArrayAdapter<SettingObject>(this, settingsRef.getSettingsListLayout(), settingsList));

        //ListView lv = getListView();
        //lv.setTextFilterEnabled(true);

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
		Log.d("JoeDebug", "SettingsScreen onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getSettingsLayout());
		super.setLayout();
		body.postInvalidate();
	}
	
	/**
	 * This method is used internally to gather the settings that are used in building the list view on the settings screen, along with their values
	 * 
	 * @return
	 */
	private SettingObject[] getSettingsList()
	{
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor settingsCursor = db.getPersistentSettings();
        
        SettingObject[] retVal = new SettingObject[settingsCursor.getCount()];
        
        settingsCursor.moveToFirst();
        
        for (int i = 0; i < settingsCursor.getCount(); i++)
        {
        	retVal[i] = new SettingObject(settingsCursor.getString(0), settingsCursor.getString(1), settingsCursor.getString(2));
        	settingsCursor.moveToNext();
        }
        
		return retVal;
	}
	
	/**
	 * Internal class used to hold values for persistent settings, to be passed into the array adapter for inflating the list view
	 * 
	 * @author Joe
	 *
	 */
	@SuppressWarnings("unused")
	private static class SettingObject
	{
		public String settingName;
		public String settingValue;
		public String isVisible;
		
		public SettingObject(String name, String value, String visible)
		{
			settingName = name;
			settingValue = value;
			isVisible = visible;
		}
	}
}
