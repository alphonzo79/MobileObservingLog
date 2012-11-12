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

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobileobservinglog.objectSearch.ObjectFilter;
import com.mobileobservinglog.objectSearch.ObjectFilterInformation;
import com.mobileobservinglog.objectSearch.ObjectIndexFilter;
import com.mobileobservinglog.objectSearch.TextSearch;
import com.mobileobservinglog.softkeyboard.SoftKeyboard;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;
import com.mobileobservinglog.support.DatabaseHelper;

public class SearchScreen extends ActivityBase {
	//gather resources
	FrameLayout body;
	Button showListButton;
	Button cancelButton;
	TextView header;
	EditText textSearchField;		
	ArrayList<ObjectFilterInformation> filterList;
	TreeMap<String, Boolean> filterChangeSet;
	ObjectFilterInformation focusedFilter;
	String indexType;
	String catalogName;
	ObjectFilter filter;
	TextSearch textSearch;
	TextView modalHeader;
	Button modalSave;
	Button modalCancel;
	Button modalClear;
	
	FrameLayout keyboardRoot;
	SoftKeyboard keyboardDriver;
	
	int firstFocus; //used to control the keyboard showing on first load
	int firstClick;
		
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "SearchScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		firstFocus = -1;
        firstClick = 1;
		
        //setup the layout
        setContentView(settingsRef.getSearchScreenLayout());
        body = (FrameLayout)findViewById(R.id.search_root);
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
		Log.d("JoeDebug", "SearchScreen onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
		
		firstFocus = -1;
        firstClick = 1;
        
		indexType = this.getIntent().getStringExtra("com.mobileobservationlog.indexType");
		catalogName = this.getIntent().getStringExtra("com.mobileobservationlog.catalogName");
        
		filter = ObjectIndexFilter.getReference(this);
		textSearch = ObjectIndexFilter.getReference(this);
		
		if(indexType != null) {
			if(indexType.equals("catalog")) {
				if(catalogName != null) {
					TreeMap<String, Boolean> setCatalog = new TreeMap<String, Boolean>();
					setCatalog.put(catalogName, true);
					filter.setFilter(setCatalog);
				}
			}
		}
		
		filterList = filter.getFilterInfo();
        
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getSearchScreenLayout());
		super.setLayout();
		
		findButtonsAddListener();
		findTextField();
		setMargins_noKeyboard();
		prepareListView();
		body.postInvalidate();
	}
	
	public void findTextField() {		
		textSearchField = (EditText)findViewById(R.id.search_string);
		textSearchField.setOnFocusChangeListener(showLetters_focus);
		textSearchField.setOnClickListener(showLetters_click);
		textSearchField.setInputType(InputType.TYPE_NULL);
		
		String searchText = textSearch.getStringSearchText();
		if(searchText.length() > 0) {
			textSearchField.setText(searchText);
		}
	}

	private void findButtonsAddListener() {
		showListButton = (Button)findViewById(R.id.show_results_button);
		showListButton.setOnClickListener(showResults);
		cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(cancelSearch);
	}
	    
    private final Button.OnClickListener showResults = new Button.OnClickListener() {
    	public void onClick(View view){
    		filter.setFilter(filterChangeSet);
    		String searchText = textSearchField.getText().toString();
    		textSearch.setStringSearchText(searchText);
    		Intent intent = new Intent(SearchScreen.this.getApplication(), ObjectIndexScreen.class);
    		startActivity(intent);
    		SearchScreen.this.finish();
        }
    };
    
    private final Button.OnClickListener cancelSearch = new Button.OnClickListener() {
    	public void onClick(View view){
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
//		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.search_scroll_view);
//		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
//		int buttonsKeyboardSize = keyboardFrame.getHeight();
//		
//		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
//		frameParams.setMargins(0, 0, 0, 0);
//		
//		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
//		scrollParams.setMargins(0, 0, 0, 0);
//		
//		keyboardFrame.setLayoutParams(frameParams);
//		fieldsScroller.setLayoutParams(scrollParams);
	}
	
	private void setMargins_keyboard()
	{
//		ScrollView fieldsScroller = (ScrollView)findViewById(R.id.search_scroll_view);
//		FrameLayout keyboardFrame = (FrameLayout)findViewById(R.id.keyboard_root);
//		int buttonsKeyboardSize = keyboardFrame.getHeight();
//		
//		MarginLayoutParams frameParams = (MarginLayoutParams)keyboardFrame.getLayoutParams();
//		frameParams.setMargins(0, -buttonsKeyboardSize, 0, 0);
//		
//		MarginLayoutParams scrollParams = (MarginLayoutParams)fieldsScroller.getLayoutParams();
//		scrollParams.setMargins(0, 0, 0, buttonsKeyboardSize);
//		
//		keyboardFrame.setLayoutParams(frameParams);
//		fieldsScroller.setLayoutParams(scrollParams);
	}
		
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView() {
		setListAdapter(new FilterAdapter(this, settingsRef.getSearchListLayout(), filterList));
	}
	
	protected void prepareModalListView() {
		
	}
		
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		focusedFilter = filterList.get(position);
		prepForModal();
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_index_main);
		
		mainBackLayer.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
		
		if(focusedFilter != null) {
			modalHeader.setText(focusedFilter.getFilterTitle());
			
			prepareModalListView();
			
			RelativeLayout backlightModal = (RelativeLayout)findViewById(R.id.alert_modal);
			backlightModal.setVisibility(View.VISIBLE);
		}
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_index_main);
		
		mainBackLayer.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		RelativeLayout filterModal = (RelativeLayout)findViewById(R.id.alert_modal);
		filterModal.setVisibility(View.INVISIBLE);
	}
	    
    //////////////////////////////////////
    // Catalog List Inflation Utilities //
    //////////////////////////////////////
	
	class FilterAdapter extends ArrayAdapter<ObjectFilterInformation>{
		
		int listLayout;
		
		FilterAdapter(Context context, int listLayout, ArrayList<ObjectFilterInformation> list){
			super(context, listLayout, R.id.filter_label, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			FilterWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new FilterWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (FilterWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class FilterWrapper{
		
		private TextView name = null;
		private TextView specs = null;
		private View row = null;
		
		FilterWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.filter_label);
			}
			return name;
		}
		
		TextView getSpecs(){
			if (specs == null){
				specs = (TextView)row.findViewById(R.id.filter_settings);
			}
			return specs;
		}
		
		void populateFrom(ObjectFilterInformation filter){
			getName().setText(filter.getFilterTitle());
			getSpecs().setText(formatSpecs(filter.getFilterValues()));
		}
		
		private String formatSpecs(TreeMap<String, Boolean> filterSpecs){
			String retVal = "All (Press To Select)";
			
			if(filterSpecs.containsValue(true)) {
				retVal = "";
				Set<String> keys = filterSpecs.keySet();
				for(String key : keys) {
					if(filterSpecs.get(key)) {
						if(retVal.length() > 0) {
							retVal = retVal.concat(", ");
						}
						retVal = retVal.concat(key);
					}
				}
			}
			
			return retVal;
		}
	}
}
