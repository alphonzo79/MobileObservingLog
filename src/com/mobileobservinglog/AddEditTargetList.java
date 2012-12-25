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
import com.mobileobservinglog.support.database.TargetListsDAO;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AddEditTargetList extends ActivityBase {

	//gather resources
	RelativeLayout body;
	int listId;
	String listName;
	String listDescription;
	
	TextView listNameTextField;
	TextView listDescriptionTextField;
	Button saveButton;
	Button cancelButton;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
	
	RelativeLayout modalMain;
	TextView modalHeader;
	Button modalSave;
	Button modalCancel;
	Button modalExtra;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "AddEditTargetList onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		firstFocus = -1;
        firstClick = 1;
		
        //setup the layout
        setContentView(settingsRef.getAddEditTargetListLayout());
        body = (RelativeLayout)findViewById(R.id.add_list_main); 
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
		Log.d("JoeDebug", "AddEditTargetList onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        firstFocus = -1;
        firstClick = 1;
        setLayout();
    }
	
  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getAddEditTargetListLayout());
		super.setLayout();
		listName = this.getIntent().getStringExtra("com.mobileobservinglog.listName");
		listId = this.getIntent().getIntExtra("com.mobileobservinglog.listId", -1);
		
		findElementsSetListeners();
		findModalElements();
		populateTextFields();
		
		body.postInvalidate();
	}
	
	private void findElementsSetListeners() {
		listNameTextField = (TextView)findViewById(R.id.list_title);
		listDescriptionTextField = (TextView)findViewById(R.id.list_description_text_field);
		saveButton = (Button)findViewById(R.id.list_save);
		cancelButton = (Button)findViewById(R.id.list_cancel);
		
		saveButton.setOnClickListener(saveList);
		cancelButton.setOnClickListener(cancelList);
		
		listNameTextField.setOnClickListener(showLetters_click);
		listNameTextField.setOnFocusChangeListener(showLetters_focus);
		listNameTextField.setInputType(InputType.TYPE_NULL);
		listDescriptionTextField.setOnClickListener(showLetters_click);
		listDescriptionTextField.setOnFocusChangeListener(showLetters_focus);
		listDescriptionTextField.setInputType(InputType.TYPE_NULL);
	}
	
	private void findModalElements() {
		modalMain = (RelativeLayout)findViewById(R.id.alert_modal);
		modalHeader = (TextView)findViewById(R.id.alert_main_text);
		modalSave = (Button)findViewById(R.id.alert_ok_button);
		modalCancel = (Button)findViewById(R.id.alert_cancel_button);
		modalExtra = (Button)findViewById(R.id.alert_extra_button);
	}
	
	private void populateTextFields() {
		if(listId > -1) {
			TargetListsDAO db = new TargetListsDAO(getApplicationContext());
			Cursor listData = db.getTargetList(listId);
			listData.moveToFirst();
			listName = listData.getString(1);
			listDescription = listData.getString(2);
			listNameTextField.setText(listName);
			listDescriptionTextField.setText(listDescription);
			listData.close();
		}
	}
	
	private final Button.OnClickListener saveList = new Button.OnClickListener() {
		public void onClick(View view) {
			TargetListsDAO db = new TargetListsDAO(getApplicationContext());
			boolean success = false;
			if(listId > -1) {
				if(db.updateTargetList(listId, listNameTextField.getText().toString(), listDescriptionTextField.getText().toString())) {
					success = true;
				}
			} else {
				if(db.addTargetList(listNameTextField.getText().toString(), listDescriptionTextField.getText().toString())) {
					success = true;
				}
			}
			if(success) {
				onBackPressed();
			} else {
				prepForModal();
				modalHeader.setText("There was an error saving the target list. Please try again");
				modalMain.setVisibility(View.VISIBLE);
				modalSave.setText(getResources().getString(R.string.ok));
				modalSave.setOnClickListener(dismissModal);
				modalSave.setVisibility(View.VISIBLE);
				modalCancel.setVisibility(View.GONE);
				modalExtra.setVisibility(View.GONE);
			}
		}
	};
	
	private final Button.OnClickListener cancelList = new Button.OnClickListener() {
		public void onClick(View view) {
			onBackPressed();
		}
	};
	
	private final Button.OnClickListener dismissModal = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
		}
	};

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		
		body.setEnabled(false);
		saveButton.setEnabled(false);
		cancelButton.setEnabled(false);
		listNameTextField.setEnabled(false);
		listDescriptionTextField.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);		
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		
		body.setEnabled(true);
		saveButton.setEnabled(true);
		cancelButton.setEnabled(true);
		listNameTextField.setEnabled(true);
		listDescriptionTextField.setEnabled(true);
		blackOutLayer.setVisibility(View.GONE);
		modalMain.setVisibility(View.GONE);
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
    
    protected final Button.OnClickListener showLetters_click = new Button.OnClickListener(){
    	public void onClick(View view){
    		if(firstClick > 0){
    			showKeyboard(view);
    		}
    		firstClick = -1;
    	}
    };
    
    protected final EditText.OnFocusChangeListener showLetters_focus = new EditText.OnFocusChangeListener(){
    	public void onFocusChange(View view, boolean hasFocus) {
			if(firstFocus > 0 && hasFocus){
				showKeyboard(view);
			}
			else{
				tearDownKeyboard();
			}
			firstFocus = 1;
		}
    };

	private void showKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
		if(keyboardDriver != null)
			keyboardDriver = null;
		keyboardRoot.setVisibility(View.VISIBLE);
		keyboardDriver = new SoftKeyboard(AddEditTargetList.this, (EditText) view, TargetInputType.LETTERS);
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
}
