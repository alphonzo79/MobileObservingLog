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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobileobservinglog.objectSearch.FilterBasicStrategy;
import com.mobileobservinglog.objectSearch.ObjectIndexFilter;
import com.mobileobservinglog.objectSearch.StringSearchFilter;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.CatalogsDAO;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

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
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "ObjectIndexScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
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
		Log.d("JoeDebug", "ObjectIndex onResume. Current session mode is " + settingsRef.getSessionMode());
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
		findButtonsAddListener();
		setHeader();
		prepareListView();
		body.postInvalidate();
	}

	private void findButtonsAddListener() {
		clearButton = (Button)findViewById(R.id.clear_filter);
		clearButton.setOnClickListener(clearFilter);
		refineButton = (Button)findViewById(R.id.refine_filter);
		refineButton.setOnClickListener(refineFilter);
	}
    
    private final Button.OnClickListener clearFilter = new Button.OnClickListener() {
    	public void onClick(View view){
    		filter.resetFilter();
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
	    		//TODO
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
		}
		else{
			Log.d("JoeTest", "List size is " + objectList.size());
			setListAdapter(new ObjectAdapter(this, settingsRef.getObjectIndexListLayout(), objectList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		String objectName = objectList.get(position).name;
		Intent intent = new Intent(this.getApplication(), ObjectDetailScreen.class);
		intent.putExtra("com.mobileobservationlog.objectName", objectName);
        startActivity(intent);
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
