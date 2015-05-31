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
import com.mobileobservinglog.ObjectIndexScreen.ObjectWrapper;
import com.mobileobservinglog.ObjectIndexScreen.ObservableObject;
import com.mobileobservinglog.objectSearch.ObjectFilterInformation;
import com.mobileobservinglog.support.database.TargetListsDAO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TargetListsScreen extends ActivityBase{

	//gather resources
	FrameLayout body;
	ArrayList<TargetListContainer> listValues;
	Button createListButton;
	
	RelativeLayout modalMain;
	TextView modalHeaderText;
	Button modalSave;
	Button modalCancel;
	Button modalExtra;
	
	String selectedListName;
	int selectedListId;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "TargetLists onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);
        
		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getTargetListsLayout());
        body = (FrameLayout)findViewById(R.id.targets_root); 
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
		Log.d("JoeDebug", "TargetLists onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout() {
		Log.d("JoeDebug", "TargetLists onCreate. Layout is " + settingsRef.getTargetListsLayout());
		setContentView(settingsRef.getTargetListsLayout());
		super.setLayout();
		
		selectedListName = "";
		selectedListId = -1;
		
		prepareListView();
		findButtonsSetListeners();
		findModalElements();
		body.postInvalidate();
	}
	
	private void findButtonsSetListeners() {
		createListButton = (Button)findViewById(R.id.add_list_button);
		createListButton.setOnClickListener(createList);
	}
	
	private void findModalElements() {
		modalMain = (RelativeLayout)findViewById(R.id.alert_modal);
		modalHeaderText = (TextView)findViewById(R.id.alert_main_text);
		modalSave = (Button)findViewById(R.id.alert_ok_button);
		modalCancel = (Button)findViewById(R.id.alert_cancel_button);
		modalExtra = (Button)findViewById(R.id.alert_extra_button);
	}
	
	private void prepareListView() {
		listValues = new ArrayList<TargetListContainer>();
		TargetListsDAO db = new TargetListsDAO(getApplicationContext());
		Cursor lists = db.getAllTargetLists();
		lists.moveToFirst();
		if(lists.getCount() > 0) {
			do{
				int id = lists.getInt(0);
				String listName = lists.getString(1);
				String description = lists.getString(2);
				Cursor count = db.getTargetListCount(listName);
				count.moveToFirst();
				int itemCount = count.getInt(0);
				count.close();
				listValues.add(new TargetListContainer(listName, description, itemCount, id));
			} while (lists.moveToNext());
		}
		lists.close();
		db.close();
		
		if(listValues.size() > 0) {
			setListAdapter(new TargetListAdapter(this, settingsRef.getTargetListsIndexList(), listValues));
			getListView().setOnItemLongClickListener(editDeleteList);
		} else { 
			TextView nothingHere = (TextView)findViewById(R.id.nothing_here);
			nothingHere.setVisibility(View.VISIBLE);
		}
	}
    
    private final Button.OnClickListener createList = new Button.OnClickListener() {
    	public void onClick(View view){
    		Intent intent = new Intent(TargetListsScreen.this, AddEditTargetList.class);
    		startActivity(intent);
        }
    };
    
    private final Button.OnClickListener deleteList = new Button.OnClickListener() {
    	public void onClick(View view) {
    		modalHeaderText.setText("Are you sure you want to delete target list " + selectedListName);
    		modalSave.setText("Delete");
    		modalSave.setOnClickListener(confirmDelete);
    		modalCancel.setText("Cancel");
    		modalCancel.setOnClickListener(cancelDeleteEdit);
    		modalExtra.setVisibility(View.GONE);
    	}
    };
    
    private final Button.OnClickListener editList = new Button.OnClickListener() {
    	public void onClick(View view) {
    		Intent intent = new Intent(TargetListsScreen.this, AddEditTargetList.class);
    		intent.putExtra("com.mobileobservinglog.listName", selectedListName);
    		intent.putExtra("com.mobileobservinglog.listId", selectedListId);
    		startActivity(intent);
    	}
    };
    
    private final Button.OnClickListener cancelDeleteEdit = new Button.OnClickListener() {
    	public void onClick(View view) {
    		tearDownModal();
    		selectedListName = "";
    		selectedListId = -1;
    	}
    };
    
    private final Button.OnClickListener confirmDelete = new Button.OnClickListener() {
    	public void onClick(View view) {
    		TargetListsDAO db = new TargetListsDAO(getApplicationContext());
    		if(db.deleteList(selectedListId, selectedListName)) {
        		Intent intent = new Intent(TargetListsScreen.this, TargetListsScreen.class);
        		startActivity(intent);
        		TargetListsScreen.this.finish();
    		} else {
    			modalHeaderText.setText("There was a problem deleting the list. Please try again");
    			modalSave.setVisibility(View.GONE);
    			modalCancel.setText("Ok");
    			modalCancel.setVisibility(View.VISIBLE);
    			modalCancel.setOnClickListener(cancelDeleteEdit);
    			modalExtra.setVisibility(View.GONE);
    		}
    	}
    };
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		String listName = listValues.get(position).name;
		Intent intent = new Intent(TargetListsScreen.this, ObjectIndexScreen.class);
		intent.putExtra("com.mobileobservationlog.indexType", "targetList");
		intent.putExtra("com.mobileobservationlog.listName", listName);
        startActivity(intent);
	}
	
	private final AdapterView.OnItemLongClickListener editDeleteList = new AdapterView.OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
			prepForModal();
			selectedListName = listValues.get(position).name;
			selectedListId = listValues.get(position).id;
			modalHeaderText.setText("Delete or edit the target list " + selectedListName + "?");
			modalHeaderText.setVisibility(View.VISIBLE);
			modalMain.setVisibility(View.VISIBLE);
			modalSave.setText("Delete");
			modalSave.setOnClickListener(deleteList);
			modalSave.setVisibility(View.VISIBLE);
			modalSave.setTextSize(14);
			modalCancel.setText("Edit");
			modalCancel.setOnClickListener(editList);
			modalCancel.setVisibility(View.VISIBLE);
			modalCancel.setTextSize(14);
			modalExtra.setText("Cancel");
			modalExtra.setOnClickListener(cancelDeleteEdit);
			modalExtra.setVisibility(View.VISIBLE);
			modalExtra.setTextSize(14);
			return false;
		}
	};

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	private void prepForModal() {
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.targets_main_relative);
		ListView list = getListView();
		
		mainBackLayer.setEnabled(false);
		list.setEnabled(false);
		createListButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);		
	}
	
	private void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.targets_main_relative);
		ListView list = getListView();
		
		mainBackLayer.setEnabled(true);
		list.setEnabled(true);
		createListButton.setEnabled(true);
		blackOutLayer.setVisibility(View.GONE);
		modalMain.setVisibility(View.GONE);
	}
    
    ///////////////////////////////////////////
    // Target Lists List Inflation Utilities //
    ///////////////////////////////////////////
	
	static class TargetListContainer{
		String name;
		int count;
		String description;
		int id;
		
		TargetListContainer(String listName, String description, int count, int id){
			name = listName;
			this.description = description;
			this.count = count;
			this.id = id;
		}		
	}
	
	class TargetListAdapter extends ArrayAdapter<TargetListContainer>{
		
		int listLayout;
		
		TargetListAdapter(Context context, int listLayout, ArrayList<TargetListContainer> list){
			super(context, listLayout, R.id.target_list_name, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			TargetListWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new TargetListWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (TargetListWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class TargetListWrapper{
		
		private TextView name = null;
		private TextView specs = null;
		private TextView description = null;
		private View row = null;
		
		TargetListWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.target_list_name);
			}
			return name;
		}
		
		TextView getSpecs(){
			if (specs == null){
				specs = (TextView)row.findViewById(R.id.target_list_item_count);
			}
			return specs;
		}
		
		TextView getDescription(){
			if (description == null){
				description = (TextView)row.findViewById(R.id.target_list_description);
			}
			return description;
		}
		
		void populateFrom(TargetListContainer list){
			getName().setText(list.name);
			getSpecs().setText(String.format("%d Objects", list.count));
			getDescription().setText(list.description);
		}
	}
}
