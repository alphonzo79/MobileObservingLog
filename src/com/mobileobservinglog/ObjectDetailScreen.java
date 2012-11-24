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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;
import com.mobileobservinglog.strategies.DatePicker;
import com.mobileobservinglog.strategies.NumberPickerDriver;
import com.mobileobservinglog.strategies.TimePicker;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.SettingsContainer.SessionMode;
import com.mobileobservinglog.support.database.EquipmentDAO;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

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
	int telescopeId;
	int eyepieceId;
	String equipmentString;
	int seeing;
	int transparency;
	boolean favorite;
	//String findingMethod;
	String viewingNotes;
	
	int newTelescopeId;
	int newEyepieceId;
	
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
	LinearLayout modalSelectorsLayout;
	LinearLayout modalSelectorSetOne;
	LinearLayout modalSelectorSetTwo;
	LinearLayout modalSelectorSetThree;
	Button modalSelectorOneUpButton;
	Button modalSelectorOneDownButton;
	Button modalSelectorTwoUpButton;
	Button modalSelectorTwoDownButton;
	Button modalSelectorThreeUpButton;
	Button modalSelectorThreeDownButton;
	TextView modalSelectorTextOne;
	TextView modalSelectorTextTwo;
	TextView modalSelectorTextThree;
	LinearLayout modalListOneContainer;
	TextView modalListHeaderOne;
	LinearLayout modalListTwoContainer;
	TextView modalListHeaderTwo;
	
	DatePicker datePicker;
	TimePicker timePicker;
	NumberPickerDriver numPickerOne;
	NumberPickerDriver numPickerTwo;
	NumberPickerDriver numPickerThree;
	
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
    	telescopeId = objectInfo.getInt(19);
    	eyepieceId = objectInfo.getInt(20);
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
    	
    	newTelescopeId = telescopeId;
    	newEyepieceId = eyepieceId; //Initialize these to match because when we save we are just going to grab the new values. This way the data is maintained if the value is not changed
    	equipmentString = formatEquipmentString(telescopeId, eyepieceId);
	}
	
	private String formatEquipmentString(int telescopeId, int eyepieceId) {
		String retVal = "";
		EquipmentDAO db = new EquipmentDAO(this);
		Cursor equip = db.getSavedTelescope(telescopeId);
		if(equip.getCount() > 0) {
			equip.moveToFirst();
			String telescopeType = equip.getString(1);
			String telescopeDiam = equip.getString(2);
			String telescopeRatio = equip.getString(3);
			String telescopeLength = equip.getString(4);
			equip.close();

			String eyepieceLength = "";
			String eyepieceType = "";
			equip = db.getSavedEyepiece(eyepieceId);
			if(equip.getCount() > 0) {
				equip.moveToFirst();
				eyepieceLength = equip.getString(1);
				eyepieceType = equip.getString(2);
				equip.close();
				db.close();
			}
			
			float telLengthInMm = 0;
			float eyeLengthInMm = 0;
			if(telescopeType != null && telescopeDiam != null && telescopeRatio != null && telescopeLength != null && eyepieceLength != null && eyepieceType != null) {
				try {
					if(telescopeLength.contains("in")) {
						telLengthInMm = Float.parseFloat(telescopeLength.split(" in")[0]) * 25.4f;
					} else {
						telLengthInMm = Float.parseFloat(telescopeLength.split(" mm")[0]);
					}
					if(eyepieceLength.contains("in")) {
						eyeLengthInMm = Float.parseFloat(eyepieceLength.split(" in")[0]) * 25.4f;
					} else {
						eyeLengthInMm = Float.parseFloat(eyepieceLength.split(" mm")[0]);
					}
				} catch (NumberFormatException e) {
					//Do nothing with it. We'll just have some garbage data
				} catch (NullPointerException e) {
					
				}
				int magnification = (int) (telLengthInMm / eyeLengthInMm);
				
				retVal = String.format("%s %s %s %s with %s %s eyepiece - %dx magnification", telescopeDiam, telescopeRatio, 
						telescopeLength, telescopeType, eyepieceLength, eyepieceType, magnification);
			}
		}
			
		equip.close();
		db.close();
		return retVal;
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
		dateInput.setOnClickListener(dateModal);
		
		timeInput.setInputType(InputType.TYPE_NULL);
		timeInput.setOnClickListener(timeModal);
		
		locationInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		equipmentInput.setInputType(InputType.TYPE_NULL);
		//TODO set listener
		
		seeingInput.setInputType(InputType.TYPE_NULL);
		seeingInput.setOnClickListener(seeingModal);
		
		transInput.setInputType(InputType.TYPE_NULL);
		transInput.setOnClickListener(transpModal);
		
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
		saveButton.setOnClickListener(saveLog);
		
		clearButton = (Button)findViewById(R.id.cancel_button);
		clearButton.setText(R.string.clear_log);
		clearButton.setOnClickListener(clearLogData);
	}
	
	private void setUpSaveCancelButtonsDisplay() {
		saveButton = (Button)findViewById(R.id.save_edit_log_button);
		saveButton.setText(R.string.edit_log);
		saveButton.setOnClickListener(editLog);
		
		clearButton = (Button)findViewById(R.id.cancel_button);
		clearButton.setText(R.string.delete_log);
		clearButton.setOnClickListener(deleteLogData);
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
			chart.setImageResource(settingsRef.getDefaultChartImage());
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
		if(equipmentString != null && equipmentString.length() > 0) {
			equipmentDisplay.setText(equipmentString);
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
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
			dateInput.setText(sdf.format(Calendar.getInstance().getTime()));
		}
		if(logTime != null && !logTime.equals("NULL")) {
			timeInput.setText(logTime);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			timeInput.setText(sdf.format(Calendar.getInstance().getTime()));
		}
		if(logLocation != null && !logLocation.equals("NULL")) {
			locationInput.setText(logLocation);
		} else {
			locationInput.setText("");
		}
		if(equipmentString != null && equipmentString.length() > 0) {
			equipmentInput.setText(equipmentString);
		} else {
			equipmentInput.setText("");
		}
		if(seeing > 0) {
			seeingInput.setText(String.format("%d/%d", seeing, 5));
		} else {
			seeingInput.setText("");
		}
		if(transparency > 0) {
			transInput.setText(String.format("%d/%d", transparency, 5));
		} else {
			transInput.setText("");
		}
		if(viewingNotes != null && !viewingNotes.equals("NULL")) {
			notesInput.setText(viewingNotes);
		} else {
			notesInput.setText("");
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
		
		dateInput.setVisibility(View.VISIBLE);
		timeInput.setVisibility(View.VISIBLE);
		locationInput.setVisibility(View.VISIBLE);
		equipmentInput.setVisibility(View.VISIBLE);
		seeingInput.setVisibility(View.VISIBLE);
		transInput.setVisibility(View.VISIBLE);
		notesInput.setVisibility(View.VISIBLE);
	}
	
	private void logEditElementsGone() {
		dateInput.setVisibility(View.GONE);
		timeInput.setVisibility(View.GONE);
		locationInput.setVisibility(View.GONE);
		equipmentInput.setVisibility(View.GONE);
		seeingInput.setVisibility(View.GONE);
		transInput.setVisibility(View.GONE);
		notesInput.setVisibility(View.GONE);

		dateDisplay.setVisibility(View.VISIBLE);
		timeDisplay.setVisibility(View.VISIBLE);
		locationDisplay.setVisibility(View.VISIBLE);
		equipmentDisplay.setVisibility(View.VISIBLE);
		seeingDisplay.setVisibility(View.VISIBLE);
		transDisplay.setVisibility(View.VISIBLE);
		notesDisplay.setVisibility(View.VISIBLE);
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
				modalHeader.setText("There was an error saving. Please Try Again.");
				modalCancel.setText(R.string.ok);
				modalCancel.setVisibility(View.VISIBLE);
				modalSave.setVisibility(View.GONE);
				modalClear.setVisibility(View.GONE);
				modalSelectorsLayout.setVisibility(View.GONE);
				modalListOneContainer.setVisibility(View.GONE);
				modalListTwoContainer.setVisibility(View.GONE);
				modalCancel.setOnClickListener(dismissModal);
			}
		}
	};
	
	protected final Button.OnClickListener deleteLogData = new Button.OnClickListener() {
		public void onClick(View view) {
			prepForModal();
			modalHeader.setText("Are you sure you want to delete the log data?");
			modalCancel.setText(R.string.cancel);
			modalCancel.setVisibility(View.VISIBLE);
			modalSave.setText(R.string.ok);
			modalSave.setVisibility(View.VISIBLE);
			modalClear.setVisibility(View.GONE);
			modalSelectorsLayout.setVisibility(View.GONE);
			modalListOneContainer.setVisibility(View.GONE);
			modalListTwoContainer.setVisibility(View.GONE);
			modalCancel.setOnClickListener(dismissModal);
			modalSave.setOnClickListener(confirmDeleteLog);
		}
	};
	
	protected final Button.OnClickListener confirmDeleteLog = new Button.OnClickListener() {
		public void onClick(View view) {
			ObservableObjectDAO db = new ObservableObjectDAO(ObjectDetailScreen.this);
			db.clearLogData(id);
			tearDownModal();
			Intent restartActivity = new Intent(ObjectDetailScreen.this, ObjectDetailScreen.class);
			restartActivity.putExtra("com.mobileobservationlog.objectName", objectName);
			restartActivity.putExtra("com.mobileobservationlog.editIntent", "true");
			startActivity(restartActivity);
			ObjectDetailScreen.this.finish();
		}
	};
	
	protected final Button.OnClickListener clearLogData = new Button.OnClickListener() {
		public void onClick(View view) {
			populateLogEditElements();
		}
	};
	
	protected final Button.OnClickListener dismissModal = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
		}
	};
	
	protected final View.OnClickListener dateModal = new View.OnClickListener() {
		public void onClick(View view) {
			//Date will be in format MMM dd, yyyy
			String fullDate = dateInput.getText().toString();
			String[] split = fullDate.split(" ");
			String month = split[0];
			String day = split[1].split(",")[0];
			String year = split[2];
			
			prepForModal();
			datePicker = new DatePicker(month, day, year, ObjectDetailScreen.this);
			
			numPickerOne = datePicker.month;
			numPickerTwo = datePicker.day;
			numPickerThree = datePicker.year;
			
			modalHeader.setText("Object Log Date");
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
			modalSelectorTextTwo.setText(numPickerTwo.getCurrentValue());
			modalSelectorTextThree.setText(numPickerThree.getCurrentValue());
			
			modalSelectorOneUpButton.setOnClickListener(incrementButtonOne);
			modalSelectorOneDownButton.setOnClickListener(decrementButtonOne);
			modalSelectorTwoUpButton.setOnClickListener(incrementButtonTwo);
			modalSelectorTwoDownButton.setOnClickListener(decrementButtonTwo);
			modalSelectorThreeUpButton.setOnClickListener(incrementButtonThree);
			modalSelectorThreeDownButton.setOnClickListener(decrementButtonThree);
			
			modalSelectorSetOne.setVisibility(View.VISIBLE);
			modalSelectorSetTwo.setVisibility(View.VISIBLE);
			modalSelectorSetThree.setVisibility(View.VISIBLE);
			modalListOneContainer.setVisibility(View.GONE);
			modalListTwoContainer.setVisibility(View.GONE);
			
			modalSave.setOnClickListener(saveDate);
			modalSave.setVisibility(View.VISIBLE);
			modalCancel.setOnClickListener(dismissModal);
			modalCancel.setVisibility(View.VISIBLE);
			modalClear.setVisibility(View.GONE);
		}
	};
	
	protected final View.OnClickListener timeModal = new View.OnClickListener() {
		public void onClick(View v) {
			//Time will be in format h:mm a
			String fullTime = timeInput.getText().toString();
			String[] firstSplit = fullTime.split(":");
			String[] secondSplit = firstSplit[1].split(" ");
			String hour = firstSplit[0];
			String minute = secondSplit[0];
			String amPm = secondSplit[1];
			
			prepForModal();
			timePicker = new TimePicker(hour, minute, amPm, ObjectDetailScreen.this);
			
			numPickerOne = timePicker.hourPicker;
			numPickerTwo = timePicker.minutePicker;
			numPickerThree = timePicker.amPmPicker;
			
			modalHeader.setText("Object Log Time");
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
			modalSelectorTextTwo.setText(numPickerTwo.getCurrentValue());
			modalSelectorTextThree.setText(numPickerThree.getCurrentValue());
			
			modalSelectorOneUpButton.setOnClickListener(incrementButtonOne);
			modalSelectorOneDownButton.setOnClickListener(decrementButtonOne);
			modalSelectorTwoUpButton.setOnClickListener(incrementButtonTwo);
			modalSelectorTwoDownButton.setOnClickListener(decrementButtonTwo);
			modalSelectorThreeUpButton.setOnClickListener(incrementButtonThree);
			modalSelectorThreeDownButton.setOnClickListener(decrementButtonThree);
			
			modalSelectorSetOne.setVisibility(View.VISIBLE);
			modalSelectorSetTwo.setVisibility(View.VISIBLE);
			modalSelectorSetThree.setVisibility(View.VISIBLE);
			modalListOneContainer.setVisibility(View.GONE);
			modalListTwoContainer.setVisibility(View.GONE);
			
			modalSave.setOnClickListener(saveTime);
			modalSave.setVisibility(View.VISIBLE);
			modalCancel.setOnClickListener(dismissModal);
			modalCancel.setVisibility(View.VISIBLE);
			modalClear.setVisibility(View.GONE);
		}
	};
	
	protected final View.OnClickListener seeingModal = new View.OnClickListener() {
		public void onClick(View view) {
			prepForModal();
			
			ArrayList<String> options = new ArrayList<String>();
			options.add("1");
			options.add("2");
			options.add("3");
			options.add("4");
			options.add("5");
			
			String currentValue = seeingInput.getText().toString();
			if(currentValue == null || currentValue.equals("")) {
				currentValue = "1";
			} else {
				if(currentValue.contains("/")) {
					currentValue = currentValue.split("/")[0];
				}
			}
			numPickerOne = new NumberPickerDriver(options, currentValue, ObjectDetailScreen.this) {
				@Override
				public boolean save() {
					return false;
				}
			};
			
			modalHeader.setText("Seeing Conditions (1 - 5)");
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
			
			modalSelectorOneUpButton.setOnClickListener(incrementButtonOne);
			modalSelectorOneDownButton.setOnClickListener(decrementButtonOne);
			
			modalSelectorSetTwo.setVisibility(View.GONE);
			modalSelectorSetThree.setVisibility(View.GONE);
			modalListOneContainer.setVisibility(View.GONE);
			modalListTwoContainer.setVisibility(View.GONE);
			
			modalSave.setOnClickListener(saveSeeing);
			modalSave.setVisibility(View.VISIBLE);
			modalCancel.setOnClickListener(dismissModal);
			modalCancel.setVisibility(View.VISIBLE);
			modalClear.setVisibility(View.GONE);
		}
	};
	
	protected final View.OnClickListener transpModal = new View.OnClickListener() {
		public void onClick(View view) {
			prepForModal();
			
			ArrayList<String> options = new ArrayList<String>();
			options.add("1");
			options.add("2");
			options.add("3");
			options.add("4");
			options.add("5");
			
			String currentValue = transInput.getText().toString();
			if(currentValue == null || currentValue.equals("")) {
				currentValue = "1";
			} else {
				if(currentValue.contains("/")) {
					currentValue = currentValue.split("/")[0];
				}
			}
			numPickerOne = new NumberPickerDriver(options, currentValue, ObjectDetailScreen.this) {
				@Override
				public boolean save() {
					return false;
				}
			};
			
			modalHeader.setText("Transparency Conditions (1 - 5)");
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
			
			modalSelectorOneUpButton.setOnClickListener(incrementButtonOne);
			modalSelectorOneDownButton.setOnClickListener(decrementButtonOne);
			
			modalSelectorSetTwo.setVisibility(View.GONE);
			modalSelectorSetThree.setVisibility(View.GONE);
			modalListOneContainer.setVisibility(View.GONE);
			modalListTwoContainer.setVisibility(View.GONE);
			
			modalSave.setOnClickListener(saveTrans);
			modalSave.setVisibility(View.VISIBLE);
			modalCancel.setOnClickListener(dismissModal);
			modalCancel.setVisibility(View.VISIBLE);
			modalClear.setVisibility(View.GONE);
		}
	};
	
	protected final Button.OnClickListener incrementButtonOne = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerOne.upButton();
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener decrementButtonOne = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerOne.downButton();
			modalSelectorTextOne.setText(numPickerOne.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener incrementButtonTwo = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerTwo.upButton();
			modalSelectorTextTwo.setText(numPickerTwo.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener decrementButtonTwo = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerTwo.downButton();
			modalSelectorTextTwo.setText(numPickerTwo.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener incrementButtonThree = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerThree.upButton();
			modalSelectorTextThree.setText(numPickerThree.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener decrementButtonThree = new Button.OnClickListener() {
		public void onClick(View view) {
			numPickerThree.downButton();
			modalSelectorTextThree.setText(numPickerThree.getCurrentValue());
		}
	};
	
	protected final Button.OnClickListener saveDate = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
			String date = String.format("%s %s, %s", numPickerOne.getCurrentValue(), numPickerTwo.getCurrentValue(), numPickerThree.getCurrentValue());
			dateInput.setText(date);
		}
	};
	
	protected final Button.OnClickListener saveTime = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
			String time = String.format("%s:%s %s", numPickerOne.getCurrentValue(), numPickerTwo.getCurrentValue(), numPickerThree.getCurrentValue());
			timeInput.setText(time);
		}
	};
	
	protected final Button.OnClickListener saveSeeing = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
			seeingInput.setText(numPickerOne.getCurrentValue() + "/5");
		}
	};
	
	protected final Button.OnClickListener saveTrans = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
			transInput.setText(numPickerOne.getCurrentValue() + "/5");
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
			if(hasFocus){
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
			if(hasFocus){
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
		if(dateInput.getText() != null && dateInput.getText().length() > 0) {
			retVal.put("logDate", dateInput.getText().toString());
		}
		if(timeInput.getText() != null && timeInput.getText().length() > 0) {
			retVal.put("logTime", timeInput.getText().toString());
		}
		if(locationInput.getText() != null && locationInput.getText().length() > 0) {
			retVal.put("logLocation", locationInput.getText().toString());
		}
		retVal.put("telescope", Integer.toString(newTelescopeId));
		retVal.put("eyepiece", Integer.toString(newEyepieceId));
		if(seeingInput.getText().length() > 0 && !seeingInput.getText().equals("")) {
			String seeing = seeingInput.getText().toString().split("/")[0];
			if(Integer.parseInt(seeing) > 0) {
				retVal.put("seeing", seeing);
			}
		}
		if(transInput.getText().length() > 0 && !transInput.getText().equals("")) {
			String trans = transInput.getText().toString().split("/")[0];
			if(Integer.parseInt(trans) > 0); {
				retVal.put("transparency", trans);
			}
		}
		//findingMethod not currently used
		if(notesInput.getText() != null && notesInput.getText().length() > 0) {
			retVal.put("viewingNotes", notesInput.getText().toString());
		}
		if(retVal.size() > 0) {
			retVal.put("logged", "true");
		} else {
			retVal.put("logged", "false");
		}
		return retVal;
	}
	
	private void findModalElements() {
		modalHeader = (TextView)findViewById(R.id.modal_header);
		modalButtonContainer = (LinearLayout)findViewById(R.id.save_cancel_container);
		modalSave = (Button)findViewById(R.id.alert_ok_button);
		modalCancel = (Button)findViewById(R.id.alert_cancel_button);
		modalClear = (Button)findViewById(R.id.alert_extra_button);
		modalSelectorsLayout = (LinearLayout)findViewById(R.id.selectors_container);
		modalSelectorSetOne = (LinearLayout)findViewById(R.id.selector_set_one);
		modalSelectorSetTwo = (LinearLayout)findViewById(R.id.selector_set_two);
		modalSelectorSetThree = (LinearLayout)findViewById(R.id.selector_set_three);
		modalSelectorOneUpButton = (Button)findViewById(R.id.number_picker_up_button_one);
		modalSelectorOneDownButton = (Button)findViewById(R.id.number_picker_down_button_one);
		modalSelectorTwoUpButton = (Button)findViewById(R.id.number_picker_up_button_two);
		modalSelectorTwoDownButton = (Button)findViewById(R.id.number_picker_down_button_two);
		modalSelectorThreeUpButton = (Button)findViewById(R.id.number_picker_up_button_three);
		modalSelectorThreeDownButton = (Button)findViewById(R.id.number_picker_down_button_three);
		modalSelectorTextOne = (TextView)findViewById(R.id.number_picker_input_field_one);
		modalSelectorTextTwo = (TextView)findViewById(R.id.number_picker_input_field_two);
		modalSelectorTextThree = (TextView)findViewById(R.id.number_picker_input_field_three);
		modalListOneContainer = (LinearLayout)findViewById(R.id.object_selector_modal_list_layout_one);
		modalListHeaderOne = (TextView)findViewById(R.id.object_selector_modal_list_one_header);
		modalListTwoContainer = (LinearLayout)findViewById(R.id.object_selector_modal_list_layout_two);
		modalListHeaderTwo = (TextView)findViewById(R.id.object_selector_modal_list_two_header);
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	public void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_detail_main);
		ScrollView scroller = (ScrollView)findViewById(R.id.object_detail_scroll_view);
		
		mainBackLayer.setEnabled(false);
		scroller.setEnabled(false);
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
		ScrollView scroller = (ScrollView)findViewById(R.id.object_detail_scroll_view);
		
		mainBackLayer.setEnabled(true);
		scroller.setEnabled(true);
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
	
	public void updateModalTextOne(String text) {
		modalSelectorTextOne.setText(text);
	}
	
	public void updateModalTextTwo(String text) {
		modalSelectorTextTwo.setText(text);
	}
	
	public void updateModalTextThree(String text) {
		modalSelectorTextThree.setText(text);
	}
}
