package com.mobileobservinglog;

import java.util.ArrayList;

import com.mobileobservinglog.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TelescopeTab extends ManageEquipmentTabParent {
	
	ArrayList<TelescopeData> telescopeList = new ArrayList<TelescopeData>();
	int listItemId = -1;
	String telescopeDescription = "";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(settingsRef.getTelescopeTabLayout());
        prepareListView();        
        addEquipmentButton = (Button)findViewById(R.id.add_equipment_button);
        addEquipmentButton.setOnClickListener(addTelescope);
    }

	@Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		//Get the list of saved telescopes and populate the list
		DatabaseHelper db = new DatabaseHelper(this);
		Cursor telescopes = db.getSavedTelescopes();
		
		telescopes.moveToFirst();
		
		for (int i = 0; i < telescopes.getCount(); i++)
        {
			int id = telescopes.getInt(0);
			String type = telescopes.getString(1);
			String primaryDiameter = telescopes.getString(2);
			String focalRatio = telescopes.getString(3);
			String focalLength = telescopes.getString(4);
			
			telescopeList.add(new TelescopeData(id, type, primaryDiameter, focalRatio, focalLength));
        	
        	telescopes.moveToNext();
        }
		
		telescopes.close();
		db.close();
		
		if (telescopeList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setText(R.string.no_telescopes_installed);
			nothingLeft.setVisibility(View.VISIBLE);
		}
		else{
			setListAdapter(new TelescopeAdapter(this, settingsRef.getTelescopeListLayout(), telescopeList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		listItemId = telescopeList.get(position).id;
		telescopeDescription = getTelescopeDescription(v);
		prepForModal();
		alertText.setText("Edit or delete the telescope " + telescopeDescription + "?");
		alertText.setVisibility(View.VISIBLE);
		alertEdit.setText("Edit");
		alertEdit.setOnClickListener(editTelescope);
		alertEdit.setVisibility(View.VISIBLE);
		alertDelete.setText("Delete");
		alertDelete.setOnClickListener(deleteTelescope);
		alertDelete.setVisibility(View.VISIBLE);
		alertCancel.setText("Cancel");
		alertCancel.setOnClickListener(cancelSelect);
		alertCancel.setVisibility(View.VISIBLE);
		alertModal.setVisibility(View.VISIBLE);
	}
    
    protected final Button.OnClickListener editTelescope = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(TelescopeTab.this.getApplication(), AddEditTelescope.class);
			intent.putExtra("com.mobileobservinglog.TelescopeID", listItemId);
	        startActivity(intent);
	        
			TelescopeTab.this.finish();
        }
    };
    
    protected final Button.OnClickListener deleteTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
    		//Set up, display delete confirmation
    		alertText.setText("Delete the telescope " + telescopeDescription + "?");
    		alertEdit.setText("Confirm Delete");
    		alertEdit.setOnClickListener(confirmDelete);
    		alertDelete.setVisibility(View.GONE);
    	}
    };
    
    protected final Button.OnClickListener confirmDelete = new Button.OnClickListener(){
    	public void onClick(View view){
    		DatabaseHelper db = new DatabaseHelper(TelescopeTab.this);
    		db.deleteTelescopeData(listItemId);
    		db.close();
    		Intent intent = new Intent(TelescopeTab.this.getParent(), ManageEquipmentScreen.class);
            startActivity(intent);
            finish();
    	}
    };
    
    protected final Button.OnClickListener cancelSelect = new Button.OnClickListener(){
    	public void onClick(View view){
    		listItemId = -1;
    		telescopeDescription = "";
    		tearDownModal();
    	}
    };
    
    protected final Button.OnClickListener addTelescope = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent intent = new Intent(TelescopeTab.this.getApplication(), AddEditTelescope.class);
	        startActivity(intent);
    	}
    };
    
    private String getTelescopeDescription(View v){
    	String retVal = "";
    	
    	TextView text = (TextView) v.findViewById(R.id.telescope_primary_diameter);		
    	retVal.concat(text.getText().toString() + " ");
    	
    	text = (TextView) v.findViewById(R.id.telescope_focal_ratio);		
    	retVal.concat(text.getText().toString() + " ");
    	
    	text = (TextView) v.findViewById(R.id.telescope_focal_length);		
    	retVal.concat(text.getText().toString() + " ");
    	
    	text = (TextView) v.findViewById(R.id.telescope_type);		
    	retVal.concat(text.getText().toString());
    			
    	return retVal;
    }
    
    ////////////////////////////////////////
    // Telescope List Inflation Utilities //
    ////////////////////////////////////////   
	
	static class TelescopeData{
		int id;
		String type;
		String primaryDiameter;
		String focalRatio;
		String focalLength;
		
		TelescopeData(int id, String type, String primaryDiameter, String focalRatio, String focalLength){
			this.id = id;
			this.type = type;
			this.primaryDiameter = primaryDiameter;
			this.focalRatio = focalRatio;
			this.focalLength = focalLength;
		}		
	}
	
	class TelescopeAdapter extends ArrayAdapter<TelescopeData>{
		
		int listLayout;
		
		TelescopeAdapter(Context context, int listLayout, ArrayList<TelescopeData> list){
			super(context, listLayout, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			TelescopeWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new TelescopeWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (TelescopeWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class TelescopeWrapper{
		
		private TextView type = null;
		private TextView primaryDiameter = null;
		private TextView focalRatio = null;
		private TextView focalLength = null;
		private int id = -1;
		private View row = null;
		
		TelescopeWrapper(View row){
			this.row = row;
		}
		
		TextView getType(){
			if (type == null){
				type = (TextView)row.findViewById(R.id.telescope_type);
			}
			return type;
		}
		
		TextView getDiameter(){
			if (primaryDiameter == null){
				primaryDiameter = (TextView)row.findViewById(R.id.telescope_primary_diameter);
			}
			return primaryDiameter;
		}
		
		TextView getFocalRatio(){
			if (focalRatio == null){
				focalRatio = (TextView)row.findViewById(R.id.telescope_focal_ratio);
			}
			return focalRatio;
		}
		
		TextView getFocalLength(){
			if (focalLength == null){
				focalLength = (TextView)row.findViewById(R.id.telescope_focal_length);
			}
			return focalLength;
		}
		
		void populateFrom(TelescopeData telescope){
			getType().setText(telescope.type);
			getDiameter().setText(telescope.primaryDiameter);
			getFocalRatio().setText(telescope.focalRatio);
			getFocalLength().setText(telescope.focalLength);
			id = telescope.id;
		}
	}
}
