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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileobservinglog.objectSearch.FilterBasicStrategy;
import com.mobileobservinglog.objectSearch.ObjectIndexFilter;
import com.mobileobservinglog.objectSearch.StringSearchFilter;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.CatalogsDAO;
import com.mobileobservinglog.support.database.ObservableObjectDAO;
import com.mobileobservinglog.support.database.TargetListsDAO;

public class ObjectIndexScreen extends ActivityBase {
	//gather resources
	FrameLayout body;
	Button clearButton;
	Button refineButton;
	TextView header;
	TextView loggedSpecs;
	TextView currentFilter;
	ArrayList<ObservableObject> objectList;
	String indexType;
	String catalogName;
	String listName;
	ObjectIndexFilter filter;
	
	RelativeLayout modalMain;
	TextView modalHeader;
	Button modalSave;
	Button modalCancel;
	Button modalExtra;
	
	String selectedItemName;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
		filter = ObjectIndexFilter.getReference(this);
		
        //setup the layout
        setContentView(settingsRef.getObjectIndexLayout());
        body = (FrameLayout)findViewById(R.id.object_index_root);
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
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getObjectIndexLayout());
		super.setLayout();
		indexType = this.getIntent().getStringExtra("com.mobileobservationlog.indexType");
		catalogName = this.getIntent().getStringExtra("com.mobileobservationlog.catalogName");
		listName = this.getIntent().getStringExtra("com.mobileobservationlog.listName");
		findButtonsAddListener();
		setHeader();
		findModalElements();
		prepareListView();
		body.postInvalidate();
	}

	private void findButtonsAddListener() {
		clearButton = (Button)findViewById(R.id.clear_filter);
		clearButton.setOnClickListener(clearFilter);
		refineButton = (Button)findViewById(R.id.refine_filter);
		refineButton.setOnClickListener(refineFilter);
	}
	
	private void findModalElements() {
		modalMain = (RelativeLayout)findViewById(R.id.alert_modal);
		modalHeader = (TextView)findViewById(R.id.alert_main_text);
		modalSave = (Button)findViewById(R.id.alert_ok_button);
		modalCancel = (Button)findViewById(R.id.alert_cancel_button);
		modalExtra = (Button)findViewById(R.id.alert_extra_button);
	}
    
    private final Button.OnClickListener clearFilter = new Button.OnClickListener() {
    	public void onClick(View view){
    		filter.resetFilter();
    		if(catalogName != null) {
    			ObjectIndexScreen.this.getIntent().putExtra("com.mobileobservationlog.indexType", "catalog");
    			ObjectIndexScreen.this.getIntent().putExtra("com.mobileobservationlog.catalogName", catalogName);//retain the catalog when clearing search
    		}
    		setLayout();
        }
    };
    
    private final Button.OnClickListener refineFilter = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(ObjectIndexScreen.this.getApplication(), SearchScreen.class);
    		if(catalogName != null) {
	    		if(catalogName.length() > 1){
		    		intent.putExtra("com.mobileobservationlog.indexType", "catalog");
		    		intent.putExtra("com.mobileobservationlog.catalogName", catalogName);
	    		}
    		}
    		startActivity(intent);
        }
    };
    
    private void setHeader(){
    	header = (TextView)findViewById(R.id.object_index_header);
    	loggedSpecs = (TextView)findViewById(R.id.catalog_logged_specs);
    	currentFilter = (TextView)findViewById(R.id.current_filter);
    	if(indexType != null) {
	    	if(indexType.equals("catalog")){
	    		catalogName = this.getIntent().getStringExtra("com.mobileobservationlog.catalogName");
	    		header.setText(catalogName);
	    		String loggedSpecsString = getLoggedSpecs();
	    		loggedSpecs.setText(loggedSpecsString);
	    		filter.resetFilter();
	    	}
	    	else if(indexType.equals("targetList")){
	    		listName = this.getIntent().getStringExtra("com.mobileobservationlog.listName");
	    		header.setText(listName);
	    		loggedSpecs.setVisibility(View.GONE);
	    		currentFilter.setVisibility(View.GONE);
	    		LinearLayout buttonsLayout = (LinearLayout)findViewById(R.id.buttons_layout);
	    		buttonsLayout.setVisibility(View.GONE);
	    		filter.resetFilter();
	    	}
    	} else {
    		header.setText(R.string.app_name);
    		loggedSpecs.setVisibility(View.GONE);
    	}
    	
    	String filterText = "None";
    	if(filter.isSet()) {
    		filterText = filter.getSearchDescription();
    	}
    	currentFilter.setText("Current Filter: " + filterText);
    }
    
    private String getLoggedSpecs(){
    	CatalogsDAO db = new CatalogsDAO(this);
    	int numLogged = db.getNumLogged(catalogName);
    	Cursor specs = db.getCatalogSpecs(catalogName);
    	String count = specs.getString(2);
    	specs.close();
    	db.close();
    	
    	double countDouble = Double.parseDouble(count);
		double percentFloor = Math.floor((numLogged/countDouble) * 100);
		String retVal = String.format("%d of %s logged - (%d%%)", numLogged, count, (int)percentFloor);
		return retVal;
    }
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		objectList = new ArrayList<ObservableObject>();
		//Get the list of saved telescopes and populate the list
		ObservableObjectDAO db = new ObservableObjectDAO(this);
		
		Cursor objects = null;
		
		if(indexType != null) {
			if(indexType.equals("catalog")){
	    		objects = db.getUnfilteredObjectList_Catalog(catalogName);
			}
	    	else if(indexType.equals("targetList")){
	    		TargetListsDAO targetDb = new TargetListsDAO(getApplicationContext());
	    		objects = targetDb.getTargetListItems(listName);
	    	}
		} else {
    		objects = db.getFilteredObjectList();
    	}
		
		if(objects != null){
			objects.moveToFirst();
			
			for (int i = 0; i < objects.getCount(); i++)
	        {
				String name = objects.getString(0);
				String constellation = objects.getString(1);
				String type = objects.getString(2);
				String magnitude = objects.getString(3);
				boolean logged = false;
				try{
					logged = objects.getString(4).equals("true");
				}
				catch(NullPointerException e){/*leave it false*/}
	
				objectList.add(new ObservableObject(name, constellation, type, magnitude, logged));
				
	        	objects.moveToNext();
	        }
			objects.close();
			db.close();
		}
		
		if (objectList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setVisibility(View.VISIBLE);
			if(indexType != null && indexType.equals("targetList")) {
				nothingLeft.setText("No objects in this target list yet");
			} else {
				nothingLeft.setText(getResources().getString(R.string.no_objects_match_search));
			}
		}
		else{
			setListAdapter(new ObjectAdapter(this, settingsRef.getObjectIndexListLayout(), objectList));
			if(indexType != null && indexType.equals("targetList")) {
				getListView().setOnItemLongClickListener(removeItemFromList);
			}
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		String objectName = objectList.get(position).name;
		Intent intent = new Intent(this.getApplication(), ObjectDetailScreen.class);
		intent.putExtra("com.mobileobservationlog.objectName", objectName);
        startActivity(intent);
	}
	
	private final AdapterView.OnItemLongClickListener removeItemFromList = new AdapterView.OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
			prepForModal();
			selectedItemName = objectList.get(position).name;
			modalHeader.setText(String.format("Remove %s from target list %s?", selectedItemName, listName));
			modalHeader.setVisibility(View.VISIBLE);
			modalMain.setVisibility(View.VISIBLE);
			modalSave.setText("Remove");
			modalSave.setOnClickListener(removeFromList);
			modalSave.setVisibility(View.VISIBLE);
			modalSave.setTextSize(14);
			modalCancel.setText("Cancel");
			modalCancel.setOnClickListener(cancelRemove);
			modalCancel.setVisibility(View.VISIBLE);
			modalCancel.setTextSize(14);
			modalExtra.setVisibility(View.GONE);
			return false;
		}
	};
	
	private final Button.OnClickListener removeFromList = new Button.OnClickListener() {
		public void onClick(View view) {
			TargetListsDAO db = new TargetListsDAO(getApplicationContext());
			if(db.removeObjectFromList(listName, selectedItemName)) {
				tearDownModal();
				setLayout();
			} else {
				modalHeader.setText("There was a problem removing the object. Please try again.");
				modalSave.setText("Ok");
				modalSave.setOnClickListener(cancelRemove);
				modalCancel.setVisibility(View.GONE);
				modalExtra.setVisibility(View.GONE);
			}
		}
	};
	
	private final Button.OnClickListener cancelRemove = new Button.OnClickListener() {
		public void onClick(View view) {
			tearDownModal();
		}
	};

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_index_main);
		ListView list = getListView();
		
		mainBackLayer.setEnabled(false);
		list.setEnabled(false);
		clearButton.setEnabled(false);
		refineButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);		
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.object_index_main);
		ListView list = getListView();
		
		mainBackLayer.setEnabled(true);
		list.setEnabled(true);
		clearButton.setEnabled(true);
		refineButton.setEnabled(true);
		blackOutLayer.setVisibility(View.GONE);
		modalMain.setVisibility(View.GONE);
	}
    
    //////////////////////////////////////
    // Catalog List Inflation Utilities //
    //////////////////////////////////////
	
	static class ObservableObject{
		String name;
		String constellation;
		String type;
		String magnitude;
		boolean logged;
		
		ObservableObject(String objectName, String objectConstellation, String objectType, String objectMagnitude, boolean isLogged){
			name = objectName;
			constellation = objectConstellation;
			type = objectType;
			magnitude = objectMagnitude;
			logged = isLogged;
		}		
	}
	
	class ObjectAdapter extends ArrayAdapter<ObservableObject>{
		
		int listLayout;
		
		ObjectAdapter(Context context, int listLayout, ArrayList<ObservableObject> list){
			super(context, listLayout, R.id.object_designation, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ObjectWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new ObjectWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (ObjectWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class ObjectWrapper{
		
		private TextView name = null;
		private TextView specs = null;
		private ImageView icon = null;
		private View row = null;
		
		ObjectWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.object_designation);
			}
			return name;
		}
		
		TextView getSpecs(){
			if (specs == null){
				specs = (TextView)row.findViewById(R.id.object_specs);
			}
			return specs;
		}
		
		ImageView getIcon(){
			if (icon == null){
				icon = (ImageView)row.findViewById(R.id.checkbox);
			}
			return icon;
		}
		
		void populateFrom(ObservableObject object){
			getName().setText(object.name);
			getSpecs().setText(formatSpecs(object));
			getIcon().setImageResource(getIcon(object.logged));
		}
		
		private String formatSpecs(ObservableObject object){
			String lineSeparator = System.getProperty("line.separator");
			return String.format("%s,%s%s,%sMagnitude %s", object.constellation, lineSeparator, object.type, lineSeparator, object.magnitude);
		}
		
		private int getIcon(boolean isLogged){
			if(isLogged)
				return settingsRef.getCheckbox_Selected();
			else
				return settingsRef.getCheckbox_Unselected();
		}
	}
}
