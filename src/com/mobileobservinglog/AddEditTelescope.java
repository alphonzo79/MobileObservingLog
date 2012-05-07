package com.mobileobservinglog;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddEditTelescope extends ActivityBase {

	//gather resources
	LinearLayout body;
	int telescopeId = -1;
	Button primaryUnit;
	Button focalLengthUnit;
	Button save;
	Button cancel;
	TextView primaryDiameter;
	TextView focalRatio;
	TextView focalLength;
	TextView type;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditTelescope onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        telescopeId = this.getIntent().getIntExtra("com.mobileobservinglog", -1);
        //setup the layout
        setContentView(settingsRef.getAddEditTelescopeLayout());
        setContentView(R.layout.add_edit_telescope);
        body = (LinearLayout)findViewById(R.id.edit_telescope_root); 
        findButtonsSetListeners();
        findTextFields();
        //TODO: populate if editing
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
        //TODO: Populate if we are editing
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getAddEditTelescopeLayout());
		super.setLayout();
		findButtonsSetListeners();
		findTextFields();
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
    		//TODO: Check for empty text fields
    		
    		if(!checkMeasurements()){
    			//TODO: display the dialog
    		}
    		
    		//TODO: Save it
    	}
    };
    
    protected final Button.OnClickListener cancelTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
    		AddEditTelescope.this.finish();
    	}
    };
    
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
