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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddEditEyepiece extends ActivityBase {
	
	//gather resources
	FrameLayout body;
	int eyepieceId = -1;
	Button focalLengthUnit;
	Button save;
	Button cancel;
	EditText focalLength;
	EditText type;

	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
    
    @Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        eyepieceId = this.getIntent().getIntExtra("com.mobileobservinglog.EyepieceID", -1);
		
        //setup the layout
        setContentView(settingsRef.getAddEditEyepieceLayout());
        body = (FrameLayout)findViewById(R.id.edit_eyepiece_root);
        findButtonsSetListeners();
        findTextFields();
        populateFields();
	}
	
	private void findModalElements(){
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_cancel_button);
        alertDelete = (Button)findViewById(R.id.alert_ok_button);
	}
	
	private void findButtonsSetListeners(){
        focalLengthUnit = (Button)findViewById(R.id.focal_length_unit);
        focalLengthUnit.setOnClickListener(switchFocalUnits);
        save = (Button)findViewById(R.id.eyepiece_save);
        save.setOnClickListener(saveEyepiece);
        cancel = (Button)findViewById(R.id.eyepiece_cancel);
        cancel.setOnClickListener(cancelEyepiece);
	}
	
	private void findTextFields(){
		focalLength = (EditText)findViewById(R.id.eyepiece_focal_length);
		type = (EditText)findViewById(R.id.eyepiece_type);
		
		focalLength.setOnClickListener(showNumbers);
		type.setOnClickListener(showLetters);
		
		focalLength.setInputType(InputType.TYPE_NULL);
		type.setInputType(InputType.TYPE_NULL);
	}
	
	private void populateFields(){
		if(eyepieceId >= 0){
			DatabaseHelper db = new DatabaseHelper(this);
			Cursor eyepieceData = db.getSavedEyepiece(eyepieceId);
			String length = removeUnits(eyepieceData.getString(1));
			String lengthUnits = getUnits(eyepieceData.getString(1));
			String eyepieceType = eyepieceData.getString(2);
			
			focalLength.setText(length);
			focalLengthUnit.setText(lengthUnits);
			type.setText(eyepieceType);
		}
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
		Log.d("JoeDebug", "AddEditEyepiece onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getAddEditEyepieceLayout());
		super.setLayout();
		findButtonsSetListeners();
		findTextFields();
		populateFields();
		body.postInvalidate();
	}
    
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
    
    protected final Button.OnClickListener saveEyepiece = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(focalLength.getText().length() < 1 || type.getText().length() < 1){
    			prepForModal();
    			findModalElements();
    			Log.d("JoeTest", "Setting up alert in AddEditEyepiece");
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
    			Log.d("JoeTest", "Done setting up alert in AddEditEyepiece");
    			Log.d("JoeTest", "alertModal Left and Top are " + alertModal.getLeft() + " and " + alertModal.getTop());
    			Log.d("JoeTest", "alertModal dimensions are " + alertModal.getWidth() + " by " + alertModal.getHeight());
        		return;
    		}
    		
    		saveData();
    	}
    };
    
    protected final Button.OnClickListener cancelEyepiece = new Button.OnClickListener(){
    	public void onClick(View view){
        	Intent intent = new Intent(AddEditEyepiece.this, ManageEquipmentScreen.class);
	    	intent.putExtra("com.mobileobservinglog.ActiveTab", "Eyepieces");
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
    
    protected final EditText.OnClickListener showNumbers = new EditText.OnClickListener(){
    	public void onClick(View view){
    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
    		if(keyboardDriver != null)
    			keyboardDriver = null;
    		keyboardRoot.setVisibility(View.VISIBLE);
    		keyboardDriver = new SoftKeyboard(AddEditEyepiece.this, (EditText) view, TargetInputType.NUMBER_DECIMAL);
    	}
    };
    
    protected final EditText.OnClickListener showLetters = new EditText.OnClickListener(){
    	public void onClick(View view){
    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
    		if(keyboardDriver != null)
    			tearDownKeyboard();
    		keyboardRoot.setVisibility(View.VISIBLE);
    		keyboardDriver = new SoftKeyboard(AddEditEyepiece.this, (EditText) view, TargetInputType.LETTERS);
    	}
    };
    
    private void tearDownKeyboard(){
    	keyboardDriver.hideAll();
    	keyboardRoot.setVisibility(View.INVISIBLE);
    	keyboardDriver = null;
    }
    
    private void saveData(){
    	String thisType = type.getText().toString();
    	String length = focalLength.getText().toString() + " " + focalLengthUnit.getText().toString();
    	
    	DatabaseHelper db = new DatabaseHelper(this);
    	if(eyepieceId == -1){
    		db.addEyepieceData(thisType, length);
    	}
    	else{
    		db.updateEyepieceData(eyepieceId, thisType, length);
    	}
    		    	
    	Intent intent = new Intent(this, ManageEquipmentScreen.class);
    	intent.putExtra("com.mobileobservinglog.ActiveTab", "Eyepieces");
        startActivity(intent);
        finish();
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		Log.d("JoeTest", "PrepForModal Called in AddEditEyepiece");
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_eyepiece_root);
		
		mainBackLayer.setEnabled(false);
		focalLength.setEnabled(false);
		focalLengthUnit.setEnabled(false);
		type.setEnabled(false);
		save.setEnabled(false);
		cancel.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		Log.d("JoeTest", "PrepForModal done in AddEditEyepiece");
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.edit_eyepiece_root);
		
		mainBackLayer.setEnabled(true);
		focalLength.setEnabled(true);
		focalLengthUnit.setEnabled(true);
		type.setEnabled(true);
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
	    	Intent intent = new Intent(this, ManageEquipmentScreen.class);
	    	intent.putExtra("com.mobileobservinglog.ActiveTab", "Eyepieces");
	        startActivity(intent);
	        finish();
		}
    }
}
