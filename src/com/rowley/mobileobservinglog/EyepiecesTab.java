package com.rowley.mobileobservinglog;

import java.util.ArrayList;

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

public class EyepiecesTab extends ManageEquipmentTabParent {
	
	ArrayList<EyepieceData> eyepieceList = new ArrayList<EyepieceData>();
	int listItemId = -1;
	String eyepieceDescription = "";

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(settingsRef.getEyepieceTabLayout());
        prepareListView();
        addEquipmentButton.setOnClickListener(addEyepiece);
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
		DatabaseHelper db = new DatabaseHelper(this);
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
		eyepieceDescription = getEyepieceDescription(v);
		prepForModal();
		alertText.setText("Edit or delete the telescope " + eyepieceDescription + "?");
		alertText.setVisibility(View.VISIBLE);
		alertEdit.setText("Edit");
		alertEdit.setOnClickListener(editEyepiece);
		alertEdit.setVisibility(View.VISIBLE);
		alertDelete.setText("Delete");
		alertDelete.setOnClickListener(deleteEyepiece);
		alertDelete.setVisibility(View.VISIBLE);
		alertCancel.setText("Cancel");
		alertCancel.setOnClickListener(cancelSelect);
		alertCancel.setVisibility(View.VISIBLE);
		alertModal.setVisibility(View.VISIBLE);
	}
    
    protected final Button.OnClickListener editEyepiece = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(EyepiecesTab.this.getApplication(), AddEditEyepiece.class);
			intent.putExtra("com.rowley.mobileobservinglog.EyepieceID", listItemId);
	        startActivity(intent);
	        
	        EyepiecesTab.this.finish();
        }
    };
    
    protected final Button.OnClickListener deleteEyepiece = new Button.OnClickListener(){
    	public void onClick(View view){
    		//Set up, display delete confirmation
    		alertText.setText("Delete the eyepiece " + eyepieceDescription + "?");
    		alertEdit.setText("Confirm Delete");
    		alertEdit.setOnClickListener(confirmDelete);
    		alertDelete.setVisibility(View.GONE);
    	}
    };
    
    protected final Button.OnClickListener confirmDelete = new Button.OnClickListener(){
    	public void onClick(View view){
    		DatabaseHelper db = new DatabaseHelper(EyepiecesTab.this);
    		db.deleteEyepieceData(listItemId);
    		db.close();
    		Intent intent = new Intent(EyepiecesTab.this.getParent(), ManageEquipmentScreen.class);
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
    	}
    };
    
    private String getEyepieceDescription(View v){
    	String retVal = "";
    	
    	TextView text = (TextView) v.findViewById(R.id.eyepiece_focal_length);		
    	retVal.concat(text.getText().toString() + " ");
    	
    	text = (TextView) v.findViewById(R.id.eyepiece_type);		
    	retVal.concat(text.getText().toString());
    			
    	return retVal;
    }
    
    ///////////////////////////////////////
    // Eyepiece List Inflation Utilities //
    ///////////////////////////////////////   
	
	static class EyepieceData{
		int id;
		String type;
		String primaryDiameter;
		String focalRatio;
		String focalLength;
		
		EyepieceData(int id, String type, String focalLength){
			this.id = id;
			this.type = type;
			this.focalLength = focalLength;
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
