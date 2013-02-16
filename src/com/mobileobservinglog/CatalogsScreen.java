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

import com.mobileobservinglog.R;
import com.mobileobservinglog.support.database.CatalogsDAO;
import com.mobileobservinglog.support.database.DatabaseHelper;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CatalogsScreen extends ActivityBase{

	//gather resources
	RelativeLayout body;
	Button searchButton;
	TextView nothingHere;
	ArrayList<Catalog> catalogList;
	
	int listLocation = -1;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "CatalogsScreen onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getCatalogsLayout());
        body = (RelativeLayout)findViewById(R.id.catalogs_root);
	}
	
	@Override
    public void onPause() {
        super.onPause();
        ListView list = getListView();
        listLocation = list.getFirstVisiblePosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
		Log.d("JoeDebug", "CatalogsScreen onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getCatalogsLayout());
		super.setLayout();
		findButtonAddListener();
		prepareListView();
		body.postInvalidate();
		if(listLocation > 0) {
			ListView list = getListView();
			if(list.getCount() > listLocation) {
				list.setSelection(listLocation);
			}
		}
	}

	private void findButtonAddListener() {
		searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(searchCatalogs);
	}
    
    private final Button.OnClickListener searchCatalogs = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(CatalogsScreen.this.getApplication(), SearchScreen.class);
            startActivity(intent);
        }
    };
    
    private final Button.OnClickListener addCatalogs = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(CatalogsScreen.this.getApplication(), AddCatalogsScreen.class);
            startActivity(intent);
        }
    };
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		catalogList = new ArrayList<Catalog>();
		//Get the list of saved telescopes and populate the list
		CatalogsDAO db = new CatalogsDAO(this);
		Cursor catalogs = db.getAvailableCatalogs();
		
		catalogs.moveToFirst();
		
		for (int i = 0; i < catalogs.getCount(); i++)
        {
			Log.d("JoeDebug", "cursor size is " + catalogs.getCount());
			String name = catalogs.getString(0);
			String installed = catalogs.getString(1);
			String count = catalogs.getString(2);
			
			if (installed.equals("Yes")){
				int logged = db.getNumLogged(name);
				catalogList.add(new Catalog(name, count, logged));
			}
        	
        	catalogs.moveToNext();
        }
		catalogs.close();
		db.close();
		
		if (catalogList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setVisibility(View.VISIBLE);
			searchButton.setText("Available Catalogs");
			searchButton.setOnClickListener(addCatalogs);
		}
		else{
			Log.d("JoeTest", "List size is " + catalogList.size());
			setListAdapter(new CatalogAdapter(this, settingsRef.getCatalogsList(), catalogList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		String catalog = catalogList.get(position).name;
		Intent intent = new Intent(this.getApplication(), ObjectIndexScreen.class);
		intent.putExtra("com.mobileobservationlog.indexType", "catalog");
		intent.putExtra("com.mobileobservationlog.catalogName", catalog);
		intent.putExtra("com.mobileobservationlog.listName", "None");
        startActivity(intent);
	}
	
	@Override
	public void toggleMode() {
        ListView list = getListView();
        listLocation = list.getFirstVisiblePosition();
        super.toggleMode();
	}
    
    //////////////////////////////////////
    // Catalog List Inflation Utilities //
    //////////////////////////////////////
	
	static class Catalog{
		String name;
		String count;
		int logged;
		
		Catalog(String catalogName, String objectCount, int numLogged){
			name = catalogName;
			count = objectCount;
			logged = numLogged;
		}		
	}
	
	class CatalogAdapter extends ArrayAdapter<Catalog>{
		
		int listLayout;
		
		CatalogAdapter(Context context, int listLayout, ArrayList<Catalog> list){
			super(context, listLayout, R.id.catalog_name, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			CatalogWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new CatalogWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (CatalogWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class CatalogWrapper{
		
		private TextView name = null;
		private TextView specs = null;
		private ImageView icon = null;
		private View row = null;
		
		CatalogWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.catalog_name);
			}
			return name;
		}
		
		TextView getSpecs(){
			if (specs == null){
				specs = (TextView)row.findViewById(R.id.catalog_specs);
			}
			return specs;
		}
		
		ImageView getIcon(){
			if (icon == null){
				icon = (ImageView)row.findViewById(R.id.catalog_icon);
			}
			return icon;
		}
		
		void populateFrom(Catalog catalog){
			getName().setText(catalog.name);
			getSpecs().setText(formatStats(catalog));
			getIcon().setImageResource(getIcon(catalog.name));
		}
		
		private int getIcon(String catalogName){
			int retVal = 0;
			if(catalogName.equals("Messier Catalog")){
				retVal = settingsRef.getMessierIcon();
			}
			else if (catalogName.contains("NGC ")){
				retVal = settingsRef.getNgcIcon();
			}
			else if (catalogName.contains("IC ")){
				retVal = settingsRef.getIcIcon();
			}
			return retVal;
		}
		
		private String formatStats(Catalog catalog){
			double countDouble = Double.parseDouble(catalog.count);
			double percentFloor = Math.floor((catalog.logged/countDouble) * 100);
			String retVal = String.format("%d of %s logged - (%d%%)", catalog.logged, catalog.count, (int)percentFloor);
			return retVal;
		}
	}
}
