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
import com.mobileobservinglog.support.database.EquipmentDAO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EyepiecesTab extends ManageEquipmentTabParent {
	
	ArrayList<EyepieceData> eyepieceList = new ArrayList<EyepieceData>();
	int listItemId = -1;
	String eyepieceDescription = "";

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(settingsRef.getEyepieceTabLayout());
        prepareListView();
        addEquipmentButton = (Button)findViewById(R.id.add_equipment_button);
        addEquipmentButton.setOnClickListener(addEyepiece);
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

    @Override
    public void onResume() {
        super.onResume();
    }
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		//Get the list of saved eyepieces and populate the list
		EquipmentDAO db = new EquipmentDAO(this);
		Cursor eyepieces = db.getSavedEyepieces();
		
		eyepieces.moveToFirst();
		
		for (int i = 0; i < eyepieces.getCount(); i++)
        {
			int id = eyepieces.getInt(0);
			String type = eyepieces.getString(2);
			String focalLength = eyepieces.getString(1);
			
			eyepieceList.add(new EyepieceData(id, type, focalLength));
        	
        	eyepieces.moveToNext();
        }
		
		eyepieces.close();
		db.close();
		
		if (eyepieceList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setText(R.string.no_eyepieces_installed);
			nothingLeft.setVisibility(View.VISIBLE);
		}
		else{
			setListAdapter(new EyepieceAdapter(this, settingsRef.getTelescopeListLayout(), eyepieceList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		listItemId = eyepieceList.get(position).id;
		eyepieceDescription = getEyepieceDescription(position);
		prepForModal();
		alertText.setText("Edit or delete the telescope " + eyepieceDescription + "?");
		alertText.setVisibility(View.VISIBLE);
		alertEdit.setText("Edit");
		alertEdit.setOnClickListener(editEyepiece);
		alertEdit.setTextSize(16f);
		alertEdit.setPadding(10, 10, 10, 10);
		alertEdit.setVisibility(View.VISIBLE);
		alertDelete.setText("Delete");
		alertDelete.setOnClickListener(deleteEyepiece);
		alertDelete.setTextSize(16f);
		alertDelete.setPadding(10, 10, 10, 10);
		alertDelete.setVisibility(View.VISIBLE);
		alertCancel.setText("Cancel");
		alertCancel.setOnClickListener(cancelSelect);
		alertCancel.setTextSize(16f);
		alertCancel.setPadding(10, 10, 10, 10);
		alertCancel.setVisibility(View.VISIBLE);
		alertModal.setVisibility(View.VISIBLE);
	}
    
    protected final Button.OnClickListener editEyepiece = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(EyepiecesTab.this.getApplication(), AddEditEyepiece.class);
			intent.putExtra("com.mobileobservinglog.EyepieceID", listItemId);
	        startActivity(intent);
	        
	        EyepiecesTab.this.finish();
        }
    };
    
    protected final Button.OnClickListener deleteEyepiece = new Button.OnClickListener(){
    	public void onClick(View view){
    		//Set up, display delete confirmation
    		alertText.setText("Delete the eyepiece " + eyepieceDescription + "?");
    		alertEdit.setText("Delete");
    		alertEdit.setOnClickListener(confirmDelete);
    		alertDelete.setVisibility(View.GONE);
    	}
    };
    
    protected final Button.OnClickListener confirmDelete = new Button.OnClickListener(){
    	public void onClick(View view){
    		EquipmentDAO db = new EquipmentDAO(EyepiecesTab.this);
    		db.deleteEyepieceData(listItemId);
    		db.close();
    		Intent intent = new Intent(EyepiecesTab.this.getParent(), ManageEquipmentScreen.class);
	    	intent.putExtra("com.mobileobservinglog.ActiveTab", "Eyepieces");
            startActivity(intent);
            finish();
    	}
    };
    
    protected final Button.OnClickListener cancelSelect = new Button.OnClickListener(){
    	public void onClick(View view){
    		listItemId = -1;
    		eyepieceDescription = "";
    		tearDownModal();
    	}
    };
    
    protected final Button.OnClickListener addEyepiece = new Button.OnClickListener(){
    	public void onClick(View view){
    		Intent intent = new Intent(EyepiecesTab.this.getApplication(), AddEditEyepiece.class);
	        startActivity(intent);
	        finish();
    	}
    };
    
    private String getEyepieceDescription(int position){
    	return String.format("%s %s", eyepieceList.get(position).focalLength, eyepieceList.get(position).type);
    }

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.eyepiece_tab_root);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		addEquipmentButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		FrameLayout mainBackLayer = (FrameLayout)findViewById(R.id.eyepiece_tab_root);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		addEquipmentButton.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
    
    ///////////////////////////////////////
    // Eyepiece List Inflation Utilities //
    ///////////////////////////////////////   
	
	static class EyepieceData{
		int id;
		String type;
		String focalLength;
		
		EyepieceData(int id, String type, String focalLength){
			this.id = id;
			this.type = type;
			if(focalLength.split(" ")[0].length() > 0) {
				this.focalLength = focalLength;
			} else {
				this.focalLength = "";
			}
		}		
	}
	
	class EyepieceAdapter extends ArrayAdapter<EyepieceData>{
		
		int listLayout;
		
		EyepieceAdapter(Context context, int listLayout, ArrayList<EyepieceData> list){
			super(context, listLayout, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			EyepieceWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new EyepieceWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (EyepieceWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class EyepieceWrapper{
		
		private TextView type = null;
		private TextView focalLength = null;
		private int id = -1;
		private View row = null;
		
		EyepieceWrapper(View row){
			this.row = row;
		}
		
		TextView getType(){
			if (type == null){
				type = (TextView)row.findViewById(R.id.telescope_type);
			}
			return type;
		}
		
		TextView getFocalLength(){
			if (focalLength == null){
				focalLength = (TextView)row.findViewById(R.id.telescope_focal_length);
			}
			return focalLength;
		}
		
		void populateFrom(EyepieceData eyepiece){
			getType().setText(eyepiece.type);
			getFocalLength().setText(eyepiece.focalLength);
			id = eyepiece.id;
		}
	}
}
