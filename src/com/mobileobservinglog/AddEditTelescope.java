package com.mobileobservinglog;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddEditTelescope extends ActivityBase {

	//gather resources
	FrameLayout body;
	int telescopeId = -1;
	Button primaryUnit;
	Button focalLengthUnit;
	Button save;
	Button cancel;
	TextView primaryDiameter;
	TextView focalRatio;
	TextView focalLength;
	TextView type;

	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	Button alertCancel;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditTelescope onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        telescopeId = this.getIntent().getIntExtra("com.mobileobservinglog", -1);
        //setup the layout
        setContentView(settingsRef.getAddEditTelescopeLayout());
        setContentView(R.layout.add_edit_telescope);
        body = (FrameLayout)findViewById(R.id.edit_telescope_root); 
        findButtonsSetListeners();
        findTextFields();
        populateFields();
        
        alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_ok_button);
        alertDelete = (Button)findViewById(R.id.alert_cancel_button);
        alertCancel = (Button)findViewById(R.id.alert_extra_button);
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
		primaryDiameter = (TextView)findViewById(R.id.telescope_primary_diameter);
		focalRatio = (TextView)findViewById(R.id.telescope_focal_ratio);
		focalLength = (TextView)findViewById(R.id.telescope_focal_length);
		type = (TextView)findViewById(R.id.telescope_type);
	}
	
	private void populateFields(){
		if(telescopeId >= 0){
			DatabaseHelper db = new DatabaseHelper(this);
			Cursor telescopeData = db.getSavedTelescope(telescopeId);
			String diameter = telescopeData.getString(2);
			String ratio = telescopeData.getString(3);
			String length = telescopeData.getString(4);
			String telescopeType = telescopeData.getString(1);
			
			primaryDiameter.setText(diameter);
			focalRatio.setText(ratio);
			focalLength.setText(length);
			type.setText(telescopeType);
		}
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
		Log.d("JoeDebug", "AddEditTelescope onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
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
    			alertText.setText("At least one of the fields does not have anything in it. Do you want to continue and save or return and make changes?");
    			alertText.setVisibility(View.VISIBLE);
    			alertEdit.setText("Edit");
    			alertEdit.setOnClickListener(cancelSave);
    			alertEdit.setVisibility(View.VISIBLE);
    			alertDelete.setText("Save");
    			alertDelete.setOnClickListener(confirmSave);
    			alertDelete.setVisibility(View.VISIBLE);
    			alertCancel.setVisibility(View.GONE);
    			alertModal.setVisibility(View.VISIBLE);
        		return;
    		}
    		
    		if(!checkMeasurements()){
    			prepForModal();
    			alertText.setText("The specs don't seem to agree (ObjectiveDiameter x FocalRatio = LocalLength). Do you want to continue and save or return and make changes?");
    			alertText.setVisibility(View.VISIBLE);
    			alertEdit.setText("Edit");
    			alertEdit.setOnClickListener(cancelSave);
    			alertEdit.setVisibility(View.VISIBLE);
    			alertDelete.setText("Save");
    			alertDelete.setOnClickListener(confirmSave);
    			alertDelete.setVisibility(View.VISIBLE);
    			alertCancel.setVisibility(View.GONE);
    			alertModal.setVisibility(View.VISIBLE);
        		return;
    		}
    		
    		saveData();
    	}
    };
    
    protected final Button.OnClickListener cancelTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
    		AddEditTelescope.this.finish();
    	}
    };
    
    protected final Button.OnClickListener cancelSave = new Button.OnClickListener(){
    	public void onClick(View view){
    		tearDownModal();
    	}
    };
    
    protected final Button.OnClickListener confirmSave = new Button.OnClickListener(){
    	public void onClick(View view){
    		
    	}
    };
    
    private void saveData(){
    	DatabaseHelper db = new DatabaseHelper(this);
    	db.addTelescopeData(type.getText().toString(), primaryDiameter.getText().toString(), focalRatio.getText().toString(), focalLength.getText().toString());
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.add_telescope_main);
		
		mainBackLayer.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.add_telescope_main);
		
		mainBackLayer.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
    
    private boolean checkMeasurements(){
    	boolean retVal = false;
    	
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
    	
    	return retVal;
    }
}
