/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog;

import java.util.ArrayList;

import com.mobileobservinglog.strategies.BacklightNumberPicker;
import com.mobileobservinglog.strategies.GpsModePicker;
import com.mobileobservinglog.strategies.NumberPickerDriver;
import com.mobileobservinglog.strategies.SearchModePicker;
import com.mobileobservinglog.support.database.DatabaseHelper;
import com.mobileobservinglog.support.database.SettingsDAO;
import com.mobileobservinglog.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsScreen extends ActivityBase{

	//gather resources
	FrameLayout body;
	ArrayList<String> settingsList;
	TextView modalHeader;
	Button modalUp;
	TextView modalText;
	Button modalDown;
	Button modalSave;
	Button modalCancel;
	NumberPickerDriver numPickerStrategy;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "SettingsScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getSettingsLayout());
        body = (FrameLayout)findViewById(R.id.settings_root);         
        prepareListView();        
        findModalElements();
        setListeners();
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
    
    private void findModalElements(){
    	modalHeader = (TextView)findViewById(R.id.number_picker_header);
    	modalUp = (Button)findViewById(R.id.number_picker_up_button);
    	modalText = (TextView)findViewById(R.id.number_picker_input_field);
    	modalDown = (Button)findViewById(R.id.number_picker_down_button);
    	modalSave = (Button)findViewById(R.id.number_picker_save_button);
    	modalCancel = (Button)findViewById(R.id.number_picker_cancel_button);
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getSettingsLayout());
		super.setLayout();
		prepareListView();
		findModalElements();
        setListeners();
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
        SettingsDAO db = new SettingsDAO(this);
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
        settingsCursor.close();
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
			prepForModal();
			setBacklightIntensity(itemText);
			return;
		}
		
		//Use GPS
		if (itemText.contains("Use Device GPS"))
		{
			prepForModal();
			setUseGps(itemText);
			return;
		}
		
		//Search/filter mode
		if (itemText.contains("Search/Filter Type"))
		{
			prepForModal();
			setFilterType(itemText);
			return;
		}
		
		//Manage Catalogs
		if (itemText.contains("Manage Catalogs"))
		{
			Intent intent = new Intent(this.getApplication(), AddCatalogsScreen.class);
            startActivity(intent);
		}
		
		//Manage Equipment
		if (itemText.contains("Manage Equipment"))
		{
			Intent intent = new Intent(this.getApplication(), ManageEquipmentScreen.class);
            startActivity(intent);
		}
		
		//Manage Observing Locations
		if (itemText.contains("Manage Observing Locations"))
		{
			Intent intent = new Intent(this.getApplication(), ManageLocationsScreen.class);
            startActivity(intent);
		}
		
		//Personal Information
		if (itemText.contains("Personal Information"))
		{
			Intent intent = new Intent(this.getApplication(), PersonalInfoScreen.class);
            startActivity(intent);
		}
		
		//Information/About
		if (itemText.contains("Information/About"))
		{
			Intent intent = new Intent(this.getApplication(), InfoScreen.class);
            startActivity(intent);
		}
	}
	
	//set Listeners for each of the modal buttons
	private final Button.OnClickListener modalUpOnClick = new Button.OnClickListener() {
		public void onClick(View view){
			Log.d("OnClick", "upOnClick called");
	    	numPickerStrategy.upButton();
        	modalText.setText(numPickerStrategy.getCurrentValue());
        	modalText.postInvalidate();
        }
    };
	
	//set Listeners for each of the modal buttons
	private final Button.OnClickListener modalDownOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeTest", "downOnClick called");
        	numPickerStrategy.downButton();
        	modalText.setText(numPickerStrategy.getCurrentValue());
        }
    };
	
	//set Listeners for each of the modal buttons
	private final Button.OnClickListener modalSaveOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeTest", "saveOnClick called");
        	numPickerStrategy.save();
        	tearDownModal();
        	setLayout();
        	setListeners();
        }
    };
	
	//set Listeners for each of the modal buttons
	private final Button.OnClickListener modalCancelOnClick = new Button.OnClickListener() {
    	public void onClick(View view){
    		Log.d("JoeTest", "cancelOnClick called");
        	tearDownModal();
        	setListeners();
        }
    };
    
    private void setListeners(){
    	modalUp.setOnClickListener(modalUpOnClick);
    	modalDown.setOnClickListener(modalDownOnClick);
    	modalSave.setOnClickListener(modalSaveOnClick);
    	modalCancel.setOnClickListener(modalCancelOnClick);
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.settings_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.settings_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		RelativeLayout backlightModal = (RelativeLayout)findViewById(R.id.number_picker_modal);
		backlightModal.setVisibility(View.INVISIBLE);
	}

	/**
	 * Called by the listener method to bring up a widget to select the backlight intensity, save the setting to the database (which saves it to the settings
	 * container), and updates the display
	 */
	private void setBacklightIntensity(String itemText)
	{
		String currentValue = stripValue(itemText);
		ArrayList<String> possibleValues = new ArrayList<String>();
		for (int i = 1; i <= 10; i++){
			possibleValues.add(String.valueOf(i));
		}
		numPickerStrategy = new BacklightNumberPicker(possibleValues, currentValue, this);
		modalHeader.setText(R.string.set_backlight_intensity);
		modalText.setText(currentValue);
		RelativeLayout backlightModal = (RelativeLayout)findViewById(R.id.number_picker_modal);
		backlightModal.setVisibility(View.VISIBLE);
	}

	private void setUseGps(String itemText)
	{
		String currentValue = stripValue(itemText);
		ArrayList<String> possibleValues = new ArrayList<String>();
		possibleValues.add(getString(R.string.use_gps_yes));
		possibleValues.add(getString(R.string.use_gps_no));
		
		numPickerStrategy = new GpsModePicker(possibleValues, currentValue, this);
		modalHeader.setText(R.string.set_use_gps);
		modalText.setText(currentValue);
		RelativeLayout backlightModal = (RelativeLayout)findViewById(R.id.number_picker_modal);
		backlightModal.setVisibility(View.VISIBLE);
	}

	/**
	 * Called by the listener method to bring up a widget to select the prefered Search/Filter method, save the setting to the database (which also saves it to
	 * the settings container), and updates the display
	 */
	private void setFilterType(String itemText)
	{
		String currentValue = stripValue(itemText);
		ArrayList<String> possibleValues = new ArrayList<String>();
		possibleValues.add(getString(R.string.search_mode_simple));
		possibleValues.add(getString(R.string.search_mode_advanced));
		
		numPickerStrategy = new SearchModePicker(possibleValues, currentValue, this);
		modalHeader.setText(R.string.set_search_mode);
		modalText.setText(currentValue);
		RelativeLayout backlightModal = (RelativeLayout)findViewById(R.id.number_picker_modal);
		backlightModal.setVisibility(View.VISIBLE);
	}
	
	//We need to take the current text of the selected list item and pull out the value after the colon to populate the NumberPicker object with the current value.
	private String stripValue(String itemText){
		String[] parsedText = itemText.split(": ");
		if (parsedText.length > 1){
			return parsedText[1];
		}
		else{
			return null;
		}
	}
}
