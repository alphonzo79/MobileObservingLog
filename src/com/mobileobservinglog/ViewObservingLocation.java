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

import com.mobileobservinglog.support.DatabaseHelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewObservingLocation extends ActivityBase{

	//gather resources
	FrameLayout body;
	
	TextView locationName;
	TextView locationCoordinates;
	TextView locationDescription;
	
	Button editDelete;
	Button cancel;

	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	Button alertCancel;
	
	int locationId;
	String locationNameText;
	String locationCoordinatesText;
	String locationDescriptionText;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "ViewLocations onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        locationId = this.getIntent().getIntExtra("com.mobileobservinglog.LocationID", -1);
		
        //setup the layout
        setContentView(settingsRef.getViewLocationsLayout());
        body = (FrameLayout)findViewById(R.id.view_location_root); 
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
		Log.d("JoeDebug", "ViewLocations onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
  	//Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getViewLocationsLayout());
		super.setLayout();
		findButtonsSetListeners();
		findTextFields();
		
		populateFields();
		
		body.postInvalidate();
	}
	
	private void populateFields(){
		if(locationId >= 0){ //Only populate these from the db if we have not saved state after pausing. Otherwise use what onPause set
			DatabaseHelper db = new DatabaseHelper(this);
			Cursor locationData = db.getSavedLocation(locationId);
			locationNameText = locationData.getString(1);
			locationCoordinatesText = locationData.getString(2);
			locationDescriptionText = locationData.getString(3);
			locationData.close();
			db.close();
		}
		else{
			locationNameText = "";
			locationCoordinatesText = "";
			locationDescriptionText = "";
		}
		
		locationName.setText(locationNameText);
		locationCoordinates.setText(locationCoordinatesText);
		locationDescription.setText(locationDescriptionText);
	}
	
	private void findButtonsSetListeners(){
		editDelete = (Button)findViewById(R.id.view_location_edit);
		cancel = (Button)findViewById(R.id.view_location_cancel);
		
		editDelete.setOnClickListener(editDeleteLocation);
		cancel.setOnClickListener(cancelLocation);
	}
	
	private void findTextFields(){
		locationName = (TextView)findViewById(R.id.view_header);
		locationCoordinates = (TextView)findViewById(R.id.location_coordinates);
		locationDescription = (TextView)findViewById(R.id.location_description_text_field);
	}	
	
	private void findModalElements(){
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_cancel_button);
        alertDelete = (Button)findViewById(R.id.alert_ok_button);
        alertCancel = (Button)findViewById(R.id.alert_extra_button);
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		Log.d("JoeTest", "PrepForModal Called in AddEditObservingLocation");
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.view_location_root);
		
		mainBackLayer.setEnabled(false);
		editDelete.setEnabled(false);
		cancel.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		Log.d("JoeTest", "PrepForModal done in ViewObservingLocation");
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.view_location_root);
		
		mainBackLayer.setEnabled(true);
		editDelete.setEnabled(true);
		cancel.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
    
    protected final Button.OnClickListener editDeleteLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		findModalElements();
    		prepForModal();
    		
    		alertText.setText("Edit or delete the location \"" + locationNameText + "\"?");
    		alertText.setVisibility(View.VISIBLE);
    		alertEdit.setText("Edit");
    		alertEdit.setOnClickListener(editLocation);
    		alertEdit.setTextSize(16f);
    		alertEdit.setPadding(10, 10, 10, 10);
    		alertEdit.setVisibility(View.VISIBLE);
    		alertDelete.setText("Delete");
    		alertDelete.setOnClickListener(deleteLocation);
    		alertDelete.setTextSize(16f);
    		alertDelete.setPadding(10, 10, 10, 10);
    		alertDelete.setVisibility(View.VISIBLE);
    		alertCancel.setText("Cancel");
    		alertCancel.setOnClickListener(closeModal);
    		alertCancel.setTextSize(16f);
    		alertCancel.setPadding(10, 10, 10, 10);
    		alertCancel.setVisibility(View.VISIBLE);
    		alertModal.setVisibility(View.VISIBLE);
    	}
    };
    
    protected final Button.OnClickListener editLocation = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(ViewObservingLocation.this.getApplication(), AddEditObservingLocation.class);
			intent.putExtra("com.mobileobservinglog.LocationID", locationId);
	        startActivity(intent);
        }
    };
    
    protected final Button.OnClickListener deleteLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		//Set up, display delete confirmation
    		alertText.setText("Delete the location \"" + locationNameText + "\"?");
    		alertEdit.setText("Delete");
    		alertEdit.setOnClickListener(confirmDelete);
    		alertCancel.setText("Cancel");
    		alertCancel.setOnClickListener(closeModal);
    		alertDelete.setVisibility(View.GONE);
    	}
    };
    
    protected final Button.OnClickListener closeModal = new Button.OnClickListener() {
		public void onClick(View view){
			tearDownModal();
        }
    };
    
    protected final Button.OnClickListener confirmDelete = new Button.OnClickListener(){
    	public void onClick(View view){
    		DatabaseHelper db = new DatabaseHelper(ViewObservingLocation.this);
    		db.deleteLocationData(locationId);
    		db.close();
    		onBackPressed();
    	}
    };
    
    protected final Button.OnClickListener cancelLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		onBackPressed();
    	}
    };
}
