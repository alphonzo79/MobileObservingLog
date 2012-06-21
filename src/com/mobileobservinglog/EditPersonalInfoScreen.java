package com.mobileobservinglog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;

public class EditPersonalInfoScreen extends ActivityBase{

	//gather resources
	FrameLayout body;
	Button save;
	Button cancel;
	EditText userName;
	EditText userAddress;
	EditText userPhone;
	EditText userEmail;
	EditText userClub;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
	
	String userNameText;
	String userAddressText;
	String userPhoneText;
	String userEmailText;
	String userClubText;
	
	boolean paused;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "EditPersonalInfo onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		firstFocus = -1;
        firstClick = 1;
        
        userNameText = "";
        userAddressText = "";
        userPhoneText = "";
        userEmailText = "";
        userClubText = "";
    	
    	paused = false;
		
        //setup the layout
        setContentView(settingsRef.getEditPersonalInfoLayout());
        body = (FrameLayout)findViewById(R.id.edit_personal_info_root); 
        findButtonsSetListeners();
        findTextFields();
        populateFields();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        userNameText = userName.getText().toString();
        userAddressText = userAddress.getText().toString();
        userPhoneText = userPhone.getText().toString();
        userEmailText = userEmail.getText().toString();
        userClubText = userClub.getText().toString();
        
        paused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		Log.d("JoeDebug", "EditPersonalInfo onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        firstFocus = -1;
        firstClick = 1;
        setLayout();
    }
	
	private void findButtonsSetListeners(){
        save = (Button)findViewById(R.id.edit_personal_info_save_button);
        save.setOnClickListener(savePersonalInfo);
        cancel = (Button)findViewById(R.id.edit_personal_info_cancel_button);
        cancel.setOnClickListener(cancelPersonalInfo);
	}
	
	private void findTextFields(){
		userName = (EditText)findViewById(R.id.edit_personal_info_name);
		userAddress = (EditText)findViewById(R.id.edit_personal_info_address);
		userPhone = (EditText)findViewById(R.id.edit_personal_info_phone);
		userEmail = (EditText)findViewById(R.id.edit_personal_info_email);
		userClub = (EditText)findViewById(R.id.edit_personal_info_club);
		
		userName.setOnFocusChangeListener(showLetters_focus);
		userAddress.setOnFocusChangeListener(showNumbers_focus);
		userPhone.setOnFocusChangeListener(showNumbers_focus);
		userEmail.setOnFocusChangeListener(showLetters_focus);
		userClub.setOnFocusChangeListener(showLetters_focus);
		
		userName.setOnClickListener(showLetters_click);
		userAddress.setOnClickListener(showNumbers_click);
		userPhone.setOnClickListener(showNumbers_click);
		userEmail.setOnClickListener(showLetters_click);
		userClub.setOnClickListener(showLetters_click);
		
		userName.setInputType(InputType.TYPE_NULL);
		userAddress.setInputType(InputType.TYPE_NULL);
		userPhone.setInputType(InputType.TYPE_NULL);
		userEmail.setInputType(InputType.TYPE_NULL);
		userClub.setInputType(InputType.TYPE_NULL);
	}
	
	private void populateFields(){
		if(!paused){ //Only populate these from the db if we have not saved state after pausing. Otherwise use what onPause set
			DatabaseHelper db = new DatabaseHelper(this);
			Cursor personalInfoData = db.getPersonalInfo();
			userNameText = personalInfoData.getString(1);
			userAddressText = personalInfoData.getString(2);
			userPhoneText = personalInfoData.getString(3);
			userEmailText = personalInfoData.getString(4);
			userClubText = personalInfoData.getString(5);
			personalInfoData.close();
			db.close();
		}
		
		userName.setText(userNameText);
		userAddress.setText(userAddressText);
		userPhone.setText(userPhoneText);
		userEmail.setText(userEmailText);
		userClub.setText(userClubText);
	}
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getEditPersonalInfoLayout());
		super.setLayout();
		findButtonsSetListeners();
		findTextFields();
		populateFields();
		setMargins_noKeyboard();
		body.postInvalidate();
	}
    
    protected final Button.OnClickListener savePersonalInfo = new Button.OnClickListener(){
    	public void onClick(View view){
    		userNameText = userName.getText().toString();
    		userAddressText = userAddress.getText().toString();
            userPhoneText = userPhone.getText().toString();
            userEmailText = userEmail.getText().toString();
            userClubText = userClub.getText().toString();
        	
        	DatabaseHelper db = new DatabaseHelper(EditPersonalInfoScreen.this);
        	db.updatePersonalInfoData(userNameText, userAddressText, userPhoneText, userEmailText, userClubText);

        	tearDownKeyboard();
        	onBackPressed();
    	}
    };
    
    protected final Button.OnClickListener cancelPersonalInfo = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(keyboardDriver != null)
    		{
    			tearDownKeyboard();
    		}
    		onBackPressed();
    	}
    };
    
    protected final Button.OnClickListener showLetters_click = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(firstClick > 0){
    			showKeyboardLetters(view);
    		}
    		firstClick = -1;
    	}
    };
    
    protected final EditText.OnFocusChangeListener showLetters_focus = new EditText.OnFocusChangeListener(){
    	public void onFocusChange(View view, boolean hasFocus) {
			if(firstFocus > 0 && hasFocus){
				showKeyboardLetters(view);
			}
			else{
				tearDownKeyboard();
			}
			firstFocus = 1;
		}
    };
    
    protected final Button.OnClickListener showNumbers_click = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(firstClick > 0){
    			showKeyboardNumbers(view);
    		}
    		firstClick = -1;
    	}
    };
    
    protected final EditText.OnFocusChangeListener showNumbers_focus = new EditText.OnFocusChangeListener(){
    	public void onFocusChange(View view, boolean hasFocus) {
			if(firstFocus > 0 && hasFocus){
				showKeyboardNumbers(view);
			}
			else{
				tearDownKeyboard();
			}
			firstFocus = 1;
		}
    };

	private void showKeyboardLetters(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
		if(keyboardDriver != null)
			keyboardDriver = null;
		keyboardRoot.setVisibility(View.VISIBLE);
		keyboardDriver = new SoftKeyboard(this, (EditText) view, TargetInputType.LETTERS);
		setMargins_keyboard();
	}

	private void showKeyboardNumbers(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
		if(keyboardDriver != null)
			keyboardDriver = null;
		keyboardRoot.setVisibility(View.VISIBLE);
		keyboardDriver = new SoftKeyboard(this, (EditText) view, TargetInputType.NUMBER_DECIMAL);
		setMargins_keyboard();
	}
    
    private void tearDownKeyboard(){
    	if(keyboardDriver != null){
    		keyboardDriver.hideAll();
	    	keyboardRoot.setVisibility(View.INVISIBLE);
	    	keyboardDriver = null;
			setMargins_noKeyboard();
    	}
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
	
	private void setMargins_noKeyboard()
	{
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.edit_personal_info_scroll_view);
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
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.edit_personal_info_scroll_view);
		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
		int buttonsKeyboardSize = keyboardFrame.getHeight();
		
		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
		frameParams.setMargins(0, -buttonsKeyboardSize, 0, 0);
		
		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
		scrollParams.setMargins(0, 0, 0, buttonsKeyboardSize);
		
		keyboardFrame.setLayoutParams(frameParams);
		fieldsScroller.setLayoutParams(scrollParams);
	}
}
