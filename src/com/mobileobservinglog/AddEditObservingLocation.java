package com.mobileobservinglog;

import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AddEditObservingLocation extends ActivityBase{

	//gather resources
	FrameLayout body;
	int locationId = -1;
	Button save;
	Button cancel;
	EditText locationName;
	EditText locationCoordinates;
	EditText locationDescription;

	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
	
	String locationNameText;
	String locationCoordinatesText;
	String locationDescriptionText;
	
	boolean paused;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditLocations onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		firstFocus = -1;
        firstClick = 1;
        
        locationNameText = "";
        locationCoordinatesText = "";
        locationDescriptionText = "";
    	
    	paused = false;
		
        locationId = this.getIntent().getIntExtra("com.mobileobservinglog.LocationID", -1);
		
        //setup the layout
        setContentView(settingsRef.getAddEditLocationsLayout());
        body = (FrameLayout)findViewById(R.id.edit_location_root); 
        findButtonsSetListeners();
        findTextFields();
        populateFields();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        locationNameText = locationName.getText().toString();
        locationCoordinatesText = locationCoordinates.getText().toString();
        locationDescriptionText = locationDescription.getText().toString();
        
        paused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		Log.d("JoeDebug", "AddEditLocations onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        firstFocus = -1;
        firstClick = 1;
        setLayout();
    }
	
	private void findModalElements(){
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_cancel_button);
        alertDelete = (Button)findViewById(R.id.alert_ok_button);
	}
	
	private void findButtonsSetListeners(){
        save = (Button)findViewById(R.id.location_save);
        save.setOnClickListener(saveLocation);
        cancel = (Button)findViewById(R.id.location_cancel);
        cancel.setOnClickListener(cancelLocation);
	}
	
	private void findTextFields(){
		locationName = (EditText)findViewById(R.id.location_title);
		locationCoordinates = (EditText)findViewById(R.id.location_coordinates);
		locationDescription = (EditText)findViewById(R.id.location_description_text_field);
		
		locationName.setOnFocusChangeListener(showLetters_focus);
		locationCoordinates.setOnFocusChangeListener(showLetters_focus);
		locationDescription.setOnFocusChangeListener(showLetters_focus);
		
		locationName.setOnClickListener(showLetters_click);
		locationCoordinates.setOnClickListener(showLetters_click);
		locationDescription.setOnClickListener(showLetters_click);
		
		locationName.setInputType(InputType.TYPE_NULL);
		locationCoordinates.setInputType(InputType.TYPE_NULL);
		locationDescription.setInputType(InputType.TYPE_NULL);
	}
	
	private void populateFields(){
		if(locationId >= 0 && !paused){ //Only populate these from the db if we have not saved state after pausing. Otherwise use what onPause set
			DatabaseHelper db = new DatabaseHelper(this);
			Cursor locationData = db.getSavedLocation(locationId);
			locationNameText = locationData.getString(1);
			locationCoordinatesText = locationData.getString(2);
			locationDescriptionText = locationData.getString(3);
		}
		
		locationName.setText(locationNameText);
		locationCoordinates.setText(locationCoordinatesText);
		locationDescription.setText(locationDescriptionText);
	}
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getAddEditLocationsLayout());
		super.setLayout();
		findButtonsSetListeners();
		findTextFields();
		populateFields();
		body.postInvalidate();
		setMargins_noKeyboard();
	}
	
	private void setMargins_noKeyboard()
	{
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.scroll_fields_view);
		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
		int buttonsKeyboardSize = keyboardFrame.getHeight();
		
		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
		frameParams.setMargins(0, 0, 0, 0);
		
		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
		scrollParams.setMargins(0, 0, 0, 0);
		
		keyboardFrame.setLayoutParams(frameParams);
		fieldsScroller.setLayoutParams(scrollParams);
	}
	
	private void setMargins_keyboard()
	{
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.scroll_fields_view);
		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
		int buttonsKeyboardSize = keyboardFrame.getHeight();
		
		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
		frameParams.setMargins(0, -buttonsKeyboardSize, 0, 0);
		
		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
		scrollParams.setMargins(0, 0, 0, buttonsKeyboardSize);
		
		keyboardFrame.setLayoutParams(frameParams);
		fieldsScroller.setLayoutParams(scrollParams);
	}
    
    protected final Button.OnClickListener saveLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(locationName.getText().length() < 1 || locationDescription.getText().length() < 1){
    			prepForModal();
    			findModalElements();
    			Log.d("JoeTest", "Setting up alert in AddEditLocation");
    			alertText.setText("At least one of the fields does not have anything in it. Do you want to continue and save or return and make changes?");
    			alertText.setVisibility(View.VISIBLE);
    			alertEdit.setText("Edit");
    			alertEdit.setOnClickListener(cancelSave);
    			alertEdit.setVisibility(View.VISIBLE);
    			alertDelete.setText("Save");
    			alertDelete.setOnClickListener(confirmSave);
    			alertDelete.setVisibility(View.VISIBLE);
    			alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
    			alertModal.setVisibility(View.VISIBLE);
        		return;
    		}
    		
    		saveData();
    	}
    };
    
    protected final Button.OnClickListener cancelLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent intent = new Intent(AddEditObservingLocation.this, ManageLocationsScreen.class);
            startActivity(intent);
            finish();
    	}
    };
    
    protected final Button.OnClickListener cancelSave = new Button.OnClickListener(){
    	public void onClick(View view){
    		tearDownModal();
    	}
    };
    
    protected final Button.OnClickListener confirmSave = new Button.OnClickListener(){
    	public void onClick(View view){
    		saveData();
    	}
    };
    
    protected final Button.OnClickListener showLetters_click = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(firstClick > 0){
    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
	    		if(keyboardDriver != null)
	    			keyboardDriver = null;
	    		keyboardRoot.setVisibility(View.VISIBLE);
	    		keyboardDriver = new SoftKeyboard(AddEditObservingLocation.this, (EditText) view, TargetInputType.LETTERS);
	    		setMargins_keyboard();
    		}
    		firstClick = -1;
    	}
    };
    
    protected final EditText.OnFocusChangeListener showLetters_focus = new EditText.OnFocusChangeListener(){
    	public void onFocusChange(View view, boolean hasFocus) {
			if(firstFocus > 0 && hasFocus){
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
	    		if(keyboardDriver != null)
	    			keyboardDriver = null;
	    		keyboardRoot.setVisibility(View.VISIBLE);
	    		keyboardDriver = new SoftKeyboard(AddEditObservingLocation.this, (EditText) view, TargetInputType.LETTERS);
	    		setMargins_keyboard();
			}
			else{
				tearDownKeyboard();
			}
			firstFocus = 1;
		}
    };
    
    private void tearDownKeyboard(){
    	if(keyboardDriver != null){
    		keyboardDriver.hideAll();
	    	keyboardRoot.setVisibility(View.INVISIBLE);
	    	keyboardDriver = null;
    		setMargins_noKeyboard();
    	}
    }
    
    private void saveData(){
    	locationNameText = locationName.getText().toString();
        locationCoordinatesText = locationCoordinates.getText().toString();
        locationDescriptionText = locationDescription.getText().toString();
    	
    	DatabaseHelper db = new DatabaseHelper(this);
    	if(locationId == -1){
    		db.addLocationData(locationNameText, locationCoordinatesText, locationDescriptionText);
    	}
    	else{
    		db.updateLocationData(locationId, locationNameText, locationCoordinatesText, locationDescriptionText);
    	}
    		    	
    	Intent intent = new Intent(this, ManageLocationsScreen.class);
        startActivity(intent);
        finish();
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		Log.d("JoeTest", "PrepForModal Called in AddEditObservingLocation");
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_telescope_root);
		
		mainBackLayer.setEnabled(false);
		locationName.setEnabled(false);
		locationCoordinates.setEnabled(false);
		locationDescription.setEnabled(false);
		save.setEnabled(false);
		cancel.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		Log.d("JoeTest", "PrepForModal done in AddEditObservingLocation");
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_telescope_root);
		
		mainBackLayer.setEnabled(true);
		locationName.setEnabled(true);
		locationCoordinates.setClickable(true);
		locationDescription.setEnabled(true);
		save.setEnabled(true);
		cancel.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}

    //We killed the ManageEquipment screen when launching this activity, otherwise we would go back to it in a stale state. We need to kill this activity and relaunch
    //that one when the back button is pressed.
    @Override
    public void onBackPressed() {
		if(keyboardDriver != null){
			tearDownKeyboard();
		}
		else{
	    	super.onBackPressed();
		}
    }
}
