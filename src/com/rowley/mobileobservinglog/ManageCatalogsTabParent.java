package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageCatalogsTabParent extends ActivityBase {
	
	ArrayList<Catalog> availableCatalogList;
	ArrayList<Catalog> installedCatalogList;
	Button submitButton;
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		availableCatalogList = new ArrayList<Catalog>();
		installedCatalogList = new ArrayList<Catalog>();
		
		//Get the list of available and installed catalogs. Itterate through and populate the right list with the right values
		DatabaseHelper db = new DatabaseHelper(this);
		Cursor catalogs = db.getAvailableCatalogs();
		
		//temp for testing
		for (int i = 0; i < 5; i++){
			availableCatalogList.add(new Catalog("Available Catalog " + (i + 1), "3.5 MB", "100"));
			installedCatalogList.add(new Catalog("Installed Catalog " + (i + 1), "3.5 MB", "100"));
		}
		
		
	}
	
	//For this screen, the tabs layout was causing problems with our regular toggle mode handling. So instead on this screen we will simply relaunch the activity,
	//then kill the current instance so a back press will not take us back to the other mode.
	@Override
	protected void toggleMode(){
		super.toggleMode();
		Intent intent = new Intent(this.getApplication(), AddCatalogsScreen.class);
        startActivity(intent);
        finish();
	}
	
	static class Catalog{
		String name;
		String size;
		String count;
		
		Catalog(String catalogName, String catalogSize, String objectCount){
			name = catalogName;
			size = catalogSize;
			count = objectCount;
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
		private TextView description = null;
		private ImageView checked = null;
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
		
		TextView getDescription(){
			if (description == null){
				description = (TextView)row.findViewById(R.id.catalog_description);
			}
			return description;
		}
		
		ImageView getChecked(){
			if (checked == null){
				checked = (ImageView)row.findViewById(R.id.checkbox);
			}
			return checked;
		}
		
		void populateFrom(Catalog catalog){
			getName().setText(catalog.name);
			getDescription().setText(catalog.count + " Objects | " + catalog.size);
			getChecked().setImageResource(settingsRef.getCheckbox_Unselected());
		}
	}

}
