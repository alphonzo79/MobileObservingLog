package com.mobileobservinglog;

import java.util.ArrayList;
import java.util.List;

import com.mobileobservinglog.R;
import com.mobileobservinglog.TelescopeTab.TelescopeAdapter;
import com.mobileobservinglog.TelescopeTab.TelescopeData;
import com.mobileobservinglog.TelescopeTab.TelescopeWrapper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ManageLocationsScreen extends ActivityBase {

	//gather resources
	LinearLayout body;
	Button addLocationButton;
	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	Button alertCancel;
	
	ArrayList<LocationData> locationList = new ArrayList<LocationData>();
	int listItemId = -1;
	String locationTitle = "";
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "ManageLocations onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getManageLocationsLayout());
        body = (LinearLayout)findViewById(R.id.manage_locations_root); 
        prepareListView();             
        addLocationButton = (Button)findViewById(R.id.add_equipment_button);
        addLocationButton.setOnClickListener(addLocation);
        findModalElements();
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
		Log.d("JoeDebug", "ManageLocations onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getManageLocationsLayout());
		super.setLayout();
		body.postInvalidate();
	}
	
	protected void findModalElements(){
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_ok_button);
        alertDelete = (Button)findViewById(R.id.alert_cancel_button);
        alertCancel = (Button)findViewById(R.id.alert_extra_button);
	}
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		//Get the list of saved telescopes and populate the list
		DatabaseHelper db = new DatabaseHelper(this);
		Cursor locations = db.getSavedLocations();
		
		locations.moveToFirst();
		
		for (int i = 0; i < locations.getCount(); i++)
        {
			int id = locations.getInt(0);
			String name = locations.getString(1);
			String coordinates = locations.getString(2);
			String description = locations.getString(3);
			
			locationList.add(new LocationData(id, name, coordinates, description));
        	
        	locations.moveToNext();
        }
		
		locations.close();
		db.close();
		
		if (locationList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setText(R.string.no_locations_installed);
			nothingLeft.setVisibility(View.VISIBLE);
		}
		else{
			setListAdapter(new LocationAdapter(this, settingsRef.getLocationsListLayout(), locationList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		listItemId = locationList.get(position).id;
		locationTitle = getLocationDescription(position);
		prepForModal();
		alertText.setText("Edit or delete the location " + locationTitle + "?");
		alertText.setVisibility(View.VISIBLE);
		alertEdit.setText("Edit");
		alertEdit.setOnClickListener(editLocation);
		alertEdit.setTextSize(16f);
		alertEdit.setPadding(10, 10, 10, 10);
		alertEdit.setVisibility(View.VISIBLE);
		alertDelete.setText("Delete");
		alertDelete.setOnClickListener(deleteLocation);
		alertDelete.setTextSize(16f);
		alertDelete.setPadding(10, 10, 10, 10);
		alertDelete.setVisibility(View.VISIBLE);
		alertCancel.setText("View");
		alertCancel.setOnClickListener(viewLocation);
		alertCancel.setTextSize(16f);
		alertCancel.setPadding(10, 10, 10, 10);
		alertCancel.setVisibility(View.VISIBLE);
		alertModal.setVisibility(View.VISIBLE);
	}
    
    protected final Button.OnClickListener editLocation = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(getApplication(), AddEditObservingLocation.class);
			intent.putExtra("com.mobileobservinglog.LocationID", listItemId);
	        startActivity(intent);
        }
    };
    
    protected final Button.OnClickListener deleteLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		//Set up, display delete confirmation
    		alertText.setText("Delete the location " + locationTitle + "?");
    		alertEdit.setText("Delete");
    		alertEdit.setOnClickListener(confirmDelete);
    		alertDelete.setVisibility(View.GONE);
    	}
    };
    
    protected final Button.OnClickListener confirmDelete = new Button.OnClickListener(){
    	public void onClick(View view){
    		DatabaseHelper db = new DatabaseHelper(getApplication());
    		db.deleteTelescopeData(listItemId);
    		db.close();
    		Intent intent = new Intent(getApplication(), ManageLocationsScreen.class);
            startActivity(intent);
            finish();
    	}
    };
    
    protected final Button.OnClickListener viewLocation = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(getApplication(), ViewObservingLocation.class);
			intent.putExtra("com.mobileobservinglog.LocationID", listItemId);
	        startActivity(intent);
        }
    };
    
    private void cancelSelect(){
		listItemId = -1;
		locationTitle = "";
		tearDownModal();
	}
    
    protected final Button.OnClickListener addLocation = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent intent = new Intent(getApplication(), AddEditObservingLocation.class);
	        startActivity(intent);
    	}
    };
    
    @Override
    public void onBackPressed() {
    	if(alertModal.getVisibility() == View.VISIBLE){
    		cancelSelect();
    	}
    	else
    	{
    		super.onBackPressed();
    	}
    }
    
    private String getLocationDescription(int position){
    	return locationList.get(position).name;
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.manage_locations_root);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		addLocationButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.manage_locations_root);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		addLocationButton.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
    
    ////////////////////////////////////////
    // Telescope List Inflation Utilities //
    ////////////////////////////////////////   
	
	static class LocationData{
		int id;
		String name;
		String coordinates;
		String description;
		
		LocationData(int id, String name, String coordinates, String description){
			this.id = id;
			this.name = name;
			this.coordinates = coordinates;
			this.description = description;
		}		
	}
	
	class LocationAdapter extends ArrayAdapter<LocationData>{
		
		int listLayout;
		
		LocationAdapter(Context context, int listLayout, ArrayList<LocationData> list){
			super(context, listLayout, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LocationWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new LocationWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (LocationWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class LocationWrapper{
		
		private TextView name = null;
		private TextView coordinates = null;
		private TextView description = null;
		private int id = -1;
		private View row = null;
		
		LocationWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.location_name);
			}
			return name;
		}
		
		TextView getCoordinates(){
			if (coordinates == null){
				coordinates = (TextView)row.findViewById(R.id.location_coordinates);
			}
			return coordinates;
		}
		
		TextView getDescription(){
			if (description == null){
				description = (TextView)row.findViewById(R.id.location_description);
			}
			return description;
		}
		
		void populateFrom(LocationData location){
			getName().setText(location.name);
			getCoordinates().setText(location.coordinates);
			getDescription().setText(location.description);
			id = location.id;
		}
	}
}
