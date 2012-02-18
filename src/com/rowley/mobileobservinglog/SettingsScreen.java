package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SettingsScreen extends ActivityBase{

	//gather resources
	LinearLayout body;
	ArrayList<String> settingsList;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "SettingsScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getSettingsLayout());
        body = (LinearLayout)findViewById(R.id.settings_root); 
        
        prepareListView();
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
		prepareListView();
		body.postInvalidate();
	}
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	private void prepareListView()
	{
		settingsList = getSettingsList();
        setListAdapter(new ArrayAdapter<String>(this, settingsRef.getSettingsListLayout(), settingsList));
	}
	
	/**
	 * This method is used internally to gather the settings that are used in building the list view on the settings screen, along with their values
	 * 
	 * @return
	 */
	private ArrayList<String> getSettingsList()
	{
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor settingsCursor = db.getPersistentSettings();
        
        ArrayList<String> retVal = new ArrayList<String>();
        
        settingsCursor.moveToFirst();
        
        for (int i = 0; i < settingsCursor.getCount(); i++)
        {
        	//Check whether this is a currently-used setting
        	if (settingsCursor.getString(2).equals("1"))
        	{
        		//If there's a value associated with this setting then put together the string to display it
        		//Settings with a null value are meant to be display only, and will take us to management screens
        		if (!settingsCursor.getString(1).equalsIgnoreCase("null"))
        		{
        			retVal.add(String.format("%s: %s", settingsCursor.getString(0), settingsCursor.getString(1)));
        		}
        		else
        		{
	        		retVal.add(settingsCursor.getString(0));
        		}
        	}
        	
        	settingsCursor.moveToNext();
        }
        
		return retVal;
	}
	
	/**
	 * Take action on each of the list items when clicked. The settings with values will bring up a widget to adjust that setting. The items with no value
	 * associated will launch a new activity where more actions can be taken.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		String itemText = (String) l.getItemAtPosition(position);
		
		//Night Mode Backlight Intensity
		if (itemText.contains("Night Mode Backlight Intensity"))
		{
			setBacklightIntensity();
			return;
		}
		
		//Use GPS
		if (itemText.contains("Use Device GPS"))
		{
			setUseGps();
			return;
		}
		
		//Search/filter mode
		if (itemText.contains("Search/Filter Type"))
		{
			setFilterType();
			return;
		}
		
		//Manage Catalogs
		if (itemText.contains("Manage Catalogs"))
		{
			//TODO launch the manage catalogs activity
		}
		
		//Manage Equipment
		if (itemText.contains("Manage Equipement"))
		{
			//TODO launch the manage equipment activity
		}
		
		//Manage Observing Locations
		if (itemText.contains("Manage Observing Locations"))
		{
			//TODO launch the manage Observing Locations activity
		}
		
		//Personal Information
		if (itemText.contains("Personal Information"))
		{
			//TODO launch the personal information activity
		}
		
		//Information/About
		if (itemText.contains("Information/About"))
		{
			//TODO launch the Information/About activity
		}
	}

	/**
	 * Called by the listener method to bring up a widget to select the backlight intensity, save the setting to the database (which saves it to the settings
	 * container), and updates the display
	 */
	private void setBacklightIntensity()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Unused in the current version. In future update will be used to set the preference as to whether to use the device GPS for locations
	 */
	private void setUseGps()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called by the listener method to bring up a widget to select the prefered Search/Filter method, save the setting to the database (which also saves it to
	 * the settings container), and updates the display
	 */
	private void setFilterType()
	{
		// TODO Auto-generated method stub
		
	}
}
