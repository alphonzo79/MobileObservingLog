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

import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;
import com.mobileobservinglog.support.database.EquipmentDAO;

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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;

public class AddEditTelescope extends ActivityBase {

	//gather resources
	FrameLayout body;
	int telescopeId = -1;
	Button primaryUnit;
	Button focalLengthUnit;
	Button save;
	Button cancel;
	EditText primaryDiameter;
	EditText focalRatio;
	EditText focalLength;
	EditText type;

	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
	
	String primaryDiameterText;
	String focalLengthText;
	String focalRatioText;
	String typeText;
	String diameterUnits;
	String lengthUnits;
	
	boolean paused;
	
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditTelescope onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);
        
        firstFocus = -1;
        firstClick = 1;
        
        primaryDiameterText = "";
    	focalLengthText = "";
    	focalRatioText = "";
    	typeText = "";
    	diameterUnits = "mm";
    	lengthUnits = "mm";
    	
    	paused = false;

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        telescopeId = this.getIntent().getIntExtra("com.mobileobservinglog.TelescopeID", -1);
        //setup the layout
        setContentView(settingsRef.getAddEditTelescopeLayout());
        body = (FrameLayout)findViewById(R.id.edit_telescope_root); 
        findButtonsSetListeners();
        findTextFields();
        populateFields();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        primaryDiameterText = primaryDiameter.getText().toString();
		diameterUnits = primaryUnit.getText().toString();
		focalRatioText = focalRatio.getText().toString();
		focalLengthText = focalLength.getText().toString();
		lengthUnits = focalLengthUnit.getText().toString();
		typeText = type.getText().toString();
		
		paused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		Log.d("JoeDebug", "AddEditTelescope onResume. Current session mode is " + settingsRef.getSessionMode());
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
        primaryUnit = (Button)findViewById(R.id.primary_diameter_unit);
        primaryUnit.setOnClickListener(switchPrimaryUnits);
        focalLengthUnit = (Button)findViewById(R.id.focal_length_unit);
        focalLengthUnit.setOnClickListener(switchFocalUnits);
        save = (Button)findViewById(R.id.telescope_save);
        save.setOnClickListener(saveTelescope);
        cancel = (Button)findViewById(R.id.telescope_cancel);
        cancel.setOnClickListener(cancelTelescope);
	}
	
	private void findTextFields(){
		primaryDiameter = (EditText)findViewById(R.id.telescope_primary_diameter);
		focalRatio = (EditText)findViewById(R.id.telescope_focal_ratio);
		focalLength = (EditText)findViewById(R.id.telescope_focal_length);
		type = (EditText)findViewById(R.id.telescope_type);
		
		primaryDiameter.setOnFocusChangeListener(showNumbers_focus);
		focalRatio.setOnFocusChangeListener(showNumbers_focus);
		focalLength.setOnFocusChangeListener(showNumbers_focus);
		type.setOnFocusChangeListener(showLetters_focus);
		
		primaryDiameter.setOnClickListener(showNumbers_click);
		focalRatio.setOnClickListener(showNumbers_click);
		focalLength.setOnClickListener(showNumbers_click);
		type.setOnClickListener(showLetters_click);
		
		primaryDiameter.setInputType(InputType.TYPE_NULL);
		focalRatio.setInputType(InputType.TYPE_NULL);
		focalLength.setInputType(InputType.TYPE_NULL);
		type.setInputType(InputType.TYPE_NULL);
	}
	
	private void populateFields(){
		if(telescopeId >= 0 && !paused){ //Only populate these from the db if we have not saved state after pausing. Otherwise use what onPause set
			EquipmentDAO db = new EquipmentDAO(this);
			Cursor telescopeData = db.getSavedTelescope(telescopeId);
			primaryDiameterText = removeUnits(telescopeData.getString(2));
			diameterUnits = getUnits(telescopeData.getString(2));
			focalRatioText = telescopeData.getString(3);
			focalLengthText = removeUnits(telescopeData.getString(4));
			lengthUnits = getUnits(telescopeData.getString(4));
			typeText = telescopeData.getString(1);
		}
		
		primaryDiameter.setText(primaryDiameterText);
		primaryUnit.setText(diameterUnits);
		focalRatio.setText(focalRatioText);
		focalLength.setText(focalLengthText);
		focalLengthUnit.setText(lengthUnits);
		type.setText(typeText);
	}
	
	private String getUnits(String measurement){
		if(measurement.contains("mm"))
			return "mm";
		else
			return "in";
	}
	
	private String removeUnits(String rawString){
		int mmIndex = rawString.indexOf(" mm");
		int inIndex = rawString.indexOf(" in");
		//One of these will return an index, the other will return -1. Rather than try to figure which is larger, we'll just add them together. 
		//That will always give us the index - 1, so then we'll add 1 back in and we have our index.
		int index = mmIndex + inIndex + 1;
		return rawString.substring(0, index);
	}
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getAddEditTelescopeLayout());
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
    
    protected final Button.OnClickListener switchPrimaryUnits = new Button.OnClickListener(){
    	public void onClick(View view){
    		String currentUnits = primaryUnit.getText().toString();
    		if(currentUnits.equals(getString(R.string.millimeters))){
    			primaryUnit.setText(R.string.inches);
    		}
    		else{
    			primaryUnit.setText(R.string.millimeters);
    		}
    	}
    };
    
    protected final Button.OnClickListener switchFocalUnits = new Button.OnClickListener(){
    	public void onClick(View view){
    		String currentUnits = focalLengthUnit.getText().toString();
    		if(currentUnits.equals(getString(R.string.millimeters))){
    			focalLengthUnit.setText(R.string.inches);
    		}
    		else{
    			focalLengthUnit.setText(R.string.millimeters);
    		}
    	}
    };
    
    protected final Button.OnClickListener saveTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(primaryDiameter.getText().length() < 1 || focalRatio.getText().length() < 1 || focalLength.getText().length() < 1 || type.getText().length() < 1){
    			prepForModal();
    			findModalElements();
    			Log.d("JoeTest", "Setting up alert in AddEditTelescope");
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
    			Log.d("JoeTest", "Done setting up alert in AddEditTelescope");
    			Log.d("JoeTest", "alertModal Left and Top are " + alertModal.getLeft() + " and " + alertModal.getTop());
    			Log.d("JoeTest", "alertModal dimensions are " + alertModal.getWidth() + " by " + alertModal.getHeight());
        		return;
    		}
    		
    		if(!checkMeasurements()){
    			prepForModal();
    			findModalElements();
    			alertText.setText("The specs don't seem to agree (ObjectiveDiameter x FocalRatio = FocalLength). Do you want to continue and save or return and make changes?");
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
    
    protected final Button.OnClickListener cancelTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
        	Intent intent = new Intent(AddEditTelescope.this, ManageEquipmentScreen.class);
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
    
    protected final Button.OnClickListener showNumbers_click = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(firstClick > 0){
    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
	    		if(keyboardDriver != null)
	    			keyboardDriver = null;
	    		keyboardRoot.setVisibility(View.VISIBLE);
	    		keyboardDriver = new SoftKeyboard(AddEditTelescope.this, (EditText) view, TargetInputType.NUMBER_DECIMAL);
	    		setMargins_keyboard();
    		}
    		firstClick = -1;
    	}
    };
    
    protected final EditText.OnFocusChangeListener showNumbers_focus = new EditText.OnFocusChangeListener(){
    	public void onFocusChange(View view, boolean hasFocus) {
			if(firstFocus > 0 && hasFocus){
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
	    		if(keyboardDriver != null)
	    			keyboardDriver = null;
	    		keyboardRoot.setVisibility(View.VISIBLE);
	    		keyboardDriver = new SoftKeyboard(AddEditTelescope.this, (EditText) view, TargetInputType.NUMBER_DECIMAL);
	    		setMargins_keyboard();
			}
			else{
				tearDownKeyboard();
			}
			firstFocus = 1;
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
	    		keyboardDriver = new SoftKeyboard(AddEditTelescope.this, (EditText) view, TargetInputType.LETTERS);
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
	    		keyboardDriver = new SoftKeyboard(AddEditTelescope.this, (EditText) view, TargetInputType.LETTERS);
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
    	String thisType = type.getText().toString();
    	String diameter = primaryDiameter.getText().toString() + " " + primaryUnit.getText().toString();
    	String ratio = focalRatio.getText().toString();
    	String length = focalLength.getText().toString() + " " + focalLengthUnit.getText().toString();
    	
    	EquipmentDAO db = new EquipmentDAO(this);
    	if(telescopeId == -1){
    		db.addTelescopeData(thisType, diameter, ratio, length);
    	}
    	else{
    		db.updateTelescopeData(telescopeId, thisType, diameter, ratio, length);
    	}
    		    	
    	Intent intent = new Intent(this, ManageEquipmentScreen.class);
        startActivity(intent);
        finish();
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		Log.d("JoeTest", "PrepForModal Called in AddEditTelescop");
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_telescope_root);
		
		mainBackLayer.setEnabled(false);
		primaryDiameter.setEnabled(false);
		primaryDiameter.setClickable(false);
		primaryUnit.setEnabled(false);
		focalRatio.setEnabled(false);
		focalLength.setEnabled(false);
		focalLengthUnit.setEnabled(false);
		type.setEnabled(false);
		save.setEnabled(false);
		cancel.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		Log.d("JoeTest", "PrepForModal done in AddEditTelescop");
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_telescope_root);
		
		mainBackLayer.setEnabled(true);
		primaryDiameter.setEnabled(true);
		primaryDiameter.setClickable(true);
		primaryUnit.setEnabled(true);
		focalRatio.setEnabled(true);
		focalLength.setEnabled(true);
		focalLengthUnit.setEnabled(true);
		type.setEnabled(true);
		save.setEnabled(true);
		cancel.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
    
    private boolean checkMeasurements(){
    	boolean retVal = false;
    	
    	try{
	    	//Get focal length, ratio and primary diameter
	    	float diameter = Float.parseFloat(primaryDiameter.getText().toString());
	    	float ratio = Float.parseFloat(focalRatio.getText().toString());
	    	float length = Float.parseFloat(focalLength.getText().toString());
	    	//Convert all to mm
	    	if(primaryUnit.getText().toString().equals(getString(R.string.inches))){
	    		diameter = diameter * 25.4f;
	    	}
	    	if(focalLengthUnit.getText().toString().equals(getString(R.string.inches))){
	    		length = length * 25.4f;
	    	}
	    	//Check if they agree within 1.5%
	    	float figuredLength = diameter * ratio;
	    	float minThresh = figuredLength * 0.985f;
	    	float maxThresh = figuredLength * 1.015f;
	    	
	    	if(minThresh < length && length < maxThresh){
	    		retVal = true;
	    	}
    	}
    	catch(NumberFormatException e){
    		//return false
    	}
    	
    	return retVal;
    }

    //We killed the ManageEquipment screen when launching this activity, otherwise we would go back to it in a stale state. We need to kill this activity and relaunch
    //that one when the back button is pressed.
    @Override
    public void onBackPressed() {
		if(keyboardDriver != null){
			tearDownKeyboard();
		}
		else{
	    	Intent intent = new Intent(this, ManageEquipmentScreen.class);
	        startActivity(intent);
	        finish();
		}
    }
}
