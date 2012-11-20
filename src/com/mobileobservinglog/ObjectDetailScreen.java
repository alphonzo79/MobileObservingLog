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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Currency;
import java.util.TreeMap;

import com.mobileobservinglog.R;
import com.mobileobservinglog.objectSearch.ObjectFilterInformation;
import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.DatabaseHelper;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ObjectDetailScreen extends ActivityBase{

	//gather resources
	FrameLayout body;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
	
	boolean intentEdit;
	
	//ObjectData
	int id;
	String objectName;
	String commonName;
	String type;
	String magnitude;
	String size;
	String distance;
	String constellation;
	String season;
	String rightAscension;
	String declination;
	String catalog;
	String otherCats;
	String imagePath;
	String nightImagePath;
	boolean logged;
	String logDate;
	String logTime;
	String logLocation;
	String telescope;
	String eyepiece;
	int seeing;
	int transparency;
	boolean favorite;
	//String findingMethod;
	String viewingNotes;
	
	boolean newLogged;
	String newLogDate;
	String newLogTime;
	String newLogLocation;
	String newLogTelescope;
	String newLogEyepiece;
	int newSeeing;
	int newTransparency;
	//boolean newFavorite; (Unneeded since favorite changes are saved to the DB immediately. No need for roll back
	//String newFinidingMethod;
	String newViewingNotes;
	
	//LayoutElements
	TextView headerText;
	TextView commonNameDisplay;
	TextView otherCatalogsDisplay;
	ImageView chart;
	ImageView favoriteStar;
	TextView rightAscDisplay;
	TextView decDisplay;
	TextView magDisplay;
	TextView sizeDisplay;
	TextView typeDisplay;
	TextView distanceDisplay;
	TextView constellationDisplay;
	TextView seasonDisplay;
	
	TextView dateDisplay;
	EditText dateInput;
	TextView timeDisplay;
	EditText timeInput;
	TextView locationDisplay;
	EditText locationInput;
	TextView equipmentDisplay;
	EditText equipmentInput;
	TextView seeingDisplay;
	EditText seeingInput;
	TextView transDisplay;
	EditText transInput;
	TextView notesDisplay;
	EditText notesInput;
	
	Button addToList;
	Button saveButton;
	Button clearButton;
	
	TextView modalHeader;
	LinearLayout modalButtonContainer;
	Button modalSave;
	Button modalCancel;
	Button modalClear;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "ObjectDetails onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		firstFocus = -1;
        firstClick = 1;
		
        //setup the layout
        setContentView(settingsRef.getObjectDetailLayout());
        body = (FrameLayout)findViewById(R.id.object_detail_root); 
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
		Log.d("JoeDebug", "ObjectDetails onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        firstFocus = -1;
        firstClick = 1;
        setLayout();
    }
	
  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getObjectDetailLayout());
		super.setLayout();
        
        setObjectData();
        findDisplayElements();
        setUpListButtonAndFavorite();
        populateInfoDisplayElements();
        
        if(!logged || intentEdit) {
        	setEditableMode();
        } else {
        	setDisplayMode();
        }
        
        findModalElements();

		setMargins_noKeyboard();
		body.postInvalidate();
	}
	
	private void setObjectData() {
		//Gather data on object
        objectName = getIntent().getStringExtra("com.mobileobservationlog.objectName");
        
        String intentEditString = getIntent().getStringExtra("com.mobileobservationlog.editIntent");
        if(intentEditString != null && intentEditString.equals("true")) {
        	intentEdit = true;
        } else {
        	intentEdit = false;
        }
        
        ObservableObjectDAO db = new ObservableObjectDAO(this);
        Cursor objectInfo = db.getObjectData(objectName);
        objectInfo.moveToFirst();

    	id = objectInfo.getInt(0);
    	commonName = objectInfo.getString(2);
    	type = objectInfo.getString(3);
    	magnitude = objectInfo.getString(4);
    	size = objectInfo.getString(5);
    	distance = objectInfo.getString(6);
    	constellation = objectInfo.getString(7);
    	season = objectInfo.getString(8);
    	rightAscension = objectInfo.getString(9);
    	declination = objectInfo.getString(10);
    	catalog = objectInfo.getString(11);
    	otherCats = objectInfo.getString(12);
    	imagePath = objectInfo.getString(13);
    	nightImagePath = objectInfo.getString(14);
    	String loggedString = objectInfo.getString(15);
    	if(loggedString != null) {
    		logged = loggedString.toLowerCase().equals("true");
    	} else {
    		logged = false;
    	}
    	logDate = objectInfo.getString(16);
    	logTime = objectInfo.getString(17);
    	logLocation = objectInfo.getString(18);
    	telescope = objectInfo.getString(19);
    	eyepiece = objectInfo.getString(20);
    	seeing = objectInfo.getInt(21);
    	transparency = objectInfo.getInt(22);
    	String favoriteString = objectInfo.getString(23);
    	if(favoriteString != null) {
    		favorite = favoriteString.toLowerCase().equals("true");
    	} else {
    		favorite = false;
    	}
    	//findingMethod = objectInfo.getString(24);
    	viewingNotes = objectInfo.getString(25);
    	
    	objectInfo.close();
    	db.close();
	}
	
	//finds all the display text views, the input edit texts, the images and the regular layout buttons. It also adds a listener to the Add To List 
	//button. It does not add listeners to the save and clear buttons because their behavior will change based on state. These listeners are handled
	//in the methods to set "mode" on this screen, along with the buttons' text.
	//Also, modal elements are not handled here
	private void findDisplayElements() {
		headerText = (TextView)findViewById(R.id.object_detail_header);
		commonNameDisplay = (TextView)findViewById(R.id.common_name_data);
		otherCatalogsDisplay = (TextView)findViewById(R.id.other_catalogs_data);
		chart = (ImageView)findViewById(R.id.star_chart);
		favoriteStar = (ImageView)findViewById(R.id.fav_star);
		rightAscDisplay = (TextView)findViewById(R.id.ra_data);
		decDisplay = (TextView)findViewById(R.id.dec_data);
		magDisplay = (TextView)findViewById(R.id.mag_data);
		sizeDisplay = (TextView)findViewById(R.id.size_data);
		typeDisplay = (TextView)findViewById(R.id.type_data);
		distanceDisplay = (TextView)findViewById(R.id.dist_data);
		constellationDisplay = (TextView)findViewById(R.id.const_data);
		seasonDisplay = (TextView)findViewById(R.id.season_data);
		
		dateDisplay = (TextView)findViewById(R.id.date_data);
		dateInput = (EditText)findViewById(R.id.date_input);
		timeDisplay = (TextView)findViewById(R.id.time_data);
		timeInput = (EditText)findViewById(R.id.time_input);
		locationDisplay = (TextView)findViewById(R.id.location_data);
		locationInput = (EditText)findViewById(R.id.location_input);
		equipmentDisplay = (TextView)findViewById(R.id.equipment_data);
		equipmentInput = (EditText)findViewById(R.id.equipment_input);
		seeingDisplay = (TextView)findViewById(R.id.seeing_data);
		seeingInput = (EditText)findViewById(R.id.seeing_input);
		transDisplay = (TextView)findViewById(R.id.trans_data);
		transInput = (EditText)findViewById(R.id.trans_input);
		notesDisplay = (TextView)findViewById(R.id.notes_data);
		notesInput = (EditText)findViewById(R.id.notes_input);
	}
	
	private void setUpLogEditElements() {
		dateInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		timeInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		locationInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		equipmentInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		seeingInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		transInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		notesInput.setInputType(InputType.TYPE_NULL);
		notesInput.setOnFocusChangeListener(showLetters_focus);
		notesInput.setOnClickListener(showLetters_click);
	}
	
	private void setUpListButtonAndFavorite() {
		addToList = (Button)findViewById(R.id.add_to_list_button);
		//TODO add listener
		
		favoriteStar.setOnClickListener(changeFavorite);
	}
	
	private void setUpSaveCancelButtonsEditable() {
		saveButton = (Button)findViewById(R.id.save_edit_log_button);
		saveButton.setText(R.string.save_log);
		//TODO add listener
		
		clearButton = (Button)findViewById(R.id.cancel_button);
		clearButton.setText(R.string.clear_log);
		//TODO add listener
	}
	
	private void setUpSaveCancelButtonsDisplay() {
		saveButton = (Button)findViewById(R.id.save_edit_log_button);
		saveButton.setText(R.string.edit_log);
		saveButton.setOnClickListener(editLog);
		
		clearButton = (Button)findViewById(R.id.cancel_button);
		clearButton.setText(R.string.delete_log);
		//TODO add listener
	}
	
	private void populateInfoDisplayElements() {
		if(headerText != null && !headerText.equals("NULL")) {
			headerText.setText(String.format("%s: %s", catalog, objectName));
		}
		
		if(commonName != null && !commonName.equals("NULL")) {
			commonNameDisplay.setText(commonName);
			commonNameDisplay.setVisibility(View.VISIBLE);
			TextView commonNameLabel = (TextView)findViewById(R.id.common_name_label);
			commonNameLabel.setVisibility(View.VISIBLE);
		}
		if(otherCats != null && !otherCats.equals("NULL")) {
			otherCatalogsDisplay.setText(otherCats);
			otherCatalogsDisplay.setVisibility(View.VISIBLE);
			TextView otherCatalogsLabel = (TextView)findViewById(R.id.other_catalogs_label);
			otherCatalogsLabel.setVisibility(View.VISIBLE);
		}
		
		setStarChartImage();
		
		if(favorite) {
			favoriteStar.setImageResource(settingsRef.getFavoriteStar());
		} else {
			favoriteStar.setImageResource(settingsRef.getNotFavoriteStar());
		}
		
		
		if(rightAscension != null && !rightAscension.equals("NULL")) {
			rightAscDisplay.setText(rightAscension);
		}
		if(declination != null && !declination.equals("NULL")) {
			decDisplay.setText(declination);
		}
		if(magnitude != null && !magnitude.equals("NULL")) {
			magDisplay.setText(magnitude);
		}
		if(size != null && !size.equals("NULL")) {
			sizeDisplay.setText(size);
		}
		if(type != null && !type.equals("NULL")) {
			typeDisplay.setText(type);
		}
		if(distance != null && !distance.equals("NULL")) {
			distanceDisplay.setText(distance);
		}
		if(constellation != null && !constellation.equals("NULL")) {
			constellationDisplay.setText(constellation);
		}
		if(season != null && !season.equals("NULL")) {
			seasonDisplay.setText(season);
		}
	}
	
	private void setStarChartImage() {
		String fileLocationString = settingsRef.getPersistentSetting(SettingsContainer.STAR_CHART_DIRECTORY, ObjectDetailScreen.this);
		Log.i("SetChart", "File Location String: " + fileLocationString);
		File starChartRoot = null;
		
		//Now actually get the file location
		if (fileLocationString.equals(SettingsContainer.EXTERNAL)){
			starChartRoot = getExternalFilesDir(null);
		}
		else{
			starChartRoot = getFilesDir();
		}
		
		//Build up the path to the actual file
		File chartPath = new File(starChartRoot.toString() + imagePath);
		
		Bitmap image = null;
		if (chartPath.exists()){
			image = BitmapFactory.decodeFile(chartPath.toString());
		}
		if(image != null) {
			chart.setImageBitmap(image);
		} else {
			//TODO default image
		}
	}
	
	private void populateLogDisplayElements() {
		if(logDate != null && !logDate.equals("NULL")) {
			dateDisplay.setText(logDate);
		}
		if(logTime != null && !logTime.equals("NULL")) {
			timeDisplay.setText(logTime);
		}
		if(logLocation != null && !logLocation.equals("NULL")) {
			locationDisplay.setText(logLocation);
		}
		if(telescope != null && !telescope.equals("NULL")) {
			equipmentDisplay.setText(telescope);
		}
		if(seeing > 0) {
			seeingDisplay.setText(String.format("%d/%d", seeing, 5));
		}
		if(transparency > 0) {
			transDisplay.setText(String.format("%d/%d", transparency, 5));
		}
		if(viewingNotes != null && !viewingNotes.equals("NULL")) {
			notesDisplay.setText(viewingNotes);
		}
	}
	
	private void populateLogEditElements() {
		if(logDate != null && !logDate.equals("NULL")) {
			dateInput.setText(logDate);
		}
		if(logTime != null && !logTime.equals("NULL")) {
			timeInput.setText(logTime);
		}
		if(logLocation != null && !logLocation.equals("NULL")) {
			locationInput.setText(logLocation);
		}
		if(telescope != null && !telescope.equals("NULL")) {
			equipmentInput.setText(telescope);
		}
		if(seeing > 0) {
			seeingInput.setText(String.format("%d/%d", seeing, 5));
		}
		if(transparency > 0) {
			transInput.setText(String.format("%d/%d", transparency, 5));
		}
		if(viewingNotes != null && !viewingNotes.equals("NULL")) {
			notesInput.setText(viewingNotes);
		}
	}
	
	private void logDisplayElementsGone() {
		dateDisplay.setVisibility(View.GONE);
		timeDisplay.setVisibility(View.GONE);
		locationDisplay.setVisibility(View.GONE);
		equipmentDisplay.setVisibility(View.GONE);
		seeingDisplay.setVisibility(View.GONE);
		transDisplay.setVisibility(View.GONE);
		notesDisplay.setVisibility(View.GONE);
	}
	
	private void logEditElementsGone() {
		dateInput.setVisibility(View.GONE);
		timeInput.setVisibility(View.GONE);
		locationInput.setVisibility(View.GONE);
		equipmentInput.setVisibility(View.GONE);
		seeingInput.setVisibility(View.GONE);
		transInput.setVisibility(View.GONE);
		notesInput.setVisibility(View.GONE);
	}
	
	private void setEditableMode() {
		setUpLogEditElements();
		populateLogEditElements();
		setUpSaveCancelButtonsEditable();
		logDisplayElementsGone();
	}
	
	private void setDisplayMode() {
		populateLogDisplayElements();
		setUpSaveCancelButtonsDisplay();
		logEditElementsGone();
	}
	
	protected final View.OnClickListener changeFavorite = new View.OnClickListener() {
		public void onClick(View arg0) {
			favorite = !favorite;
			ObservableObjectDAO db = new ObservableObjectDAO(ObjectDetailScreen.this);
			boolean success = db.setFavorite(id, favorite);
			if(favorite && success) {
				favoriteStar.setImageResource(settingsRef.getFavoriteStar());
			} else if(!favorite && success) { //Only update the image if we successfully hit the db
				favoriteStar.setImageResource(settingsRef.getNotFavoriteStar());
			}
			db.close();
		}
	};
	
	protected final Button.OnClickListener editLog = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent(ObjectDetailScreen.this, ObjectDetailScreen.class);
			intent.putExtra("com.mobileobservationlog.objectName", objectName);
			intent.putExtra("com.mobileobservationlog.editIntent", "true");
			startActivity(intent);
			ObjectDetailScreen.this.finish();
		}
	};
	
	protected final Button.OnClickListener saveLog = new Button.OnClickListener() {
		public void onClick(View arg0) {
			TreeMap<String, String> updateArgs = gatherUpdateArgs();
			ObservableObjectDAO db = new ObservableObjectDAO(ObjectDetailScreen.this);
			boolean success = db.updateLogData(id, updateArgs);
			
			if(success) {
				Intent intent = new Intent(ObjectDetailScreen.this, ObjectDetailScreen.class);
				intent.putExtra("com.mobileobservationlog.objectName", objectName);
				startActivity(intent);
				ObjectDetailScreen.this.finish();
			} else {
				prepForModal();
				modalHeader.setText("There was an error saving. Please Try Again");
				modalCancel.setText(R.string.ok);
				modalCancel.setVisibility(View.VISIBLE);
				modalSave.setVisibility(View.GONE);
				modalClear.setVisibility(View.GONE);
				modalCancel.setOnClickListener(dismissModal);
			}
		}
	};
	
	protected final Button.OnClickListener dismissModal = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
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
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.object_detail_scroll_view);
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
		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.object_detail_scroll_view);
		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
		int buttonsKeyboardSize = keyboardFrame.getHeight();
		
		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
		frameParams.setMargins(0, -buttonsKeyboardSize, 0, 0);
		
		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
		scrollParams.setMargins(0, 0, 0, buttonsKeyboardSize);
		
		keyboardFrame.setLayoutParams(frameParams);
		fieldsScroller.setLayoutParams(scrollParams);
	}
	
	private TreeMap<String, String> gatherUpdateArgs() {
		TreeMap<String, String> retVal = new TreeMap<String, String>();
		if(newLogged) {
			retVal.put("logged", "true");
			if(newLogDate != null && newLogDate.length() > 0) {
				retVal.put("logDate", newLogDate);
			}
			if(newLogTime != null && newLogTime.length() > 0) {
				retVal.put("logTime", newLogTime);
			}
			if(newLogLocation != null && newLogLocation.length() > 0) {
				retVal.put("logLocation", newLogLocation);
			}
			if(newLogTelescope != null && newLogTelescope.length() > 0) {
				retVal.put("telescope", newLogTelescope);
			}
			if(newLogEyepiece != null && newLogEyepiece.length() > 0) {
				retVal.put("eyepiece", newLogEyepiece);
			}
			if(newSeeing > 0) {
				retVal.put("seeing", Integer.toString(newSeeing));
			}
			if(newTransparency > 0) {
				retVal.put("transparency", Integer.toString(newTransparency));
			}
			//findingMethod not currently used
			if(newViewingNotes != null && newViewingNotes.length() > 0) {
				retVal.put("viewingNotes", newViewingNotes);
			}
		}
		return retVal;
	}
	
	private void findModalElements() {
		modalHeader = (TextView)findViewById(R.id.alert_main_text);
		modalButtonContainer = (LinearLayout)findViewById(R.id.save_cancel_container);
		modalSave = (Button)findViewById(R.id.alert_ok_button);
		modalCancel = (Button)findViewById(R.id.alert_cancel_button);
		modalClear = (Button)findViewById(R.id.alert_extra_button);
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_detail_main);
		
		mainBackLayer.setEnabled(false);
		addToList.setEnabled(false);
		favoriteStar.setEnabled(false);
		saveButton.setEnabled(false);
		clearButton.setEnabled(false);
		dateInput.setEnabled(false);
		timeInput.setEnabled(false);
		locationInput.setEnabled(false);
		equipmentInput.setEnabled(false);
		seeingInput.setEnabled(false);
		transInput.setEnabled(false);
		notesInput.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		
		RelativeLayout alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
		alertModal.setVisibility(View.VISIBLE);
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_detail_main);
		
		mainBackLayer.setEnabled(true);
		addToList.setEnabled(true);
		favoriteStar.setEnabled(true);
		saveButton.setEnabled(true);
		clearButton.setEnabled(true);
		dateInput.setEnabled(true);
		timeInput.setEnabled(true);
		locationInput.setEnabled(true);
		equipmentInput.setEnabled(true);
		seeingInput.setEnabled(true);
		transInput.setEnabled(true);
		notesInput.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		RelativeLayout alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
		alertModal.setVisibility(View.INVISIBLE);
	}
}
