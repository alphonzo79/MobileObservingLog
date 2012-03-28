package com.rowley.mobileobservinglog;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rowley.mobileobservinglog.ManageCatalogsTabParent.Catalog;
import com.rowley.mobileobservinglog.ManageCatalogsTabParent.CatalogWrapper;

public class ManageEquipmentTabParent extends ActivityBase {

	//joint member variables
	Button addEquipmentButton;
	RelativeLayout alertModal;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addEquipmentButton = (Button)findViewById(R.id.add_equipment_button);
        alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        setContentView(settingsRef.getTelescopeTabLayout());
        prepareListView();
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
//		availableCatalogList = new ArrayList<Catalog>();
//		installedCatalogList = new ArrayList<Catalog>();
//		
//		//Get the list of available and installed catalogs. Itterate through and populate the right list with the right values
//		DatabaseHelper db = new DatabaseHelper(this);
//		Cursor catalogs = db.getAvailableCatalogs();
//		
//		catalogs.moveToFirst();
//		
//		for (int i = 0; i < catalogs.getCount(); i++)
//        {
//			String name = catalogs.getString(0);
//			String installed = catalogs.getString(1);
//			String count = catalogs.getString(2);
//			String size = catalogs.getString(4);
//			
//			if (installed.equals("Yes")){
//				installedCatalogList.add(new Catalog(name, size, count));
//			}
//			else{
//				availableCatalogList.add(new Catalog(name, size, count));
//			}
//        	
//        	catalogs.moveToNext();
//        }
//		catalogs.close();
//		db.close();
	}
	
	//For this screen, the tabs layout was causing problems with our regular toggle mode handling. So instead on this screen we will simply relaunch the activity,
	//then kill the current instance so a back press will not take us back to the other mode.
	@Override
	protected void toggleMode(){
		super.toggleMode();
		Intent intent = new Intent(this.getApplication(), ManageEquipmentScreen.class);
        startActivity(intent);
        finish();
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to let the user edit or remove their equipment profile
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		
//		TextView name = (TextView) v.findViewById(R.id.catalog_name);
//		String catalog = name.getText().toString();
//		TextView description = (TextView) v.findViewById(R.id.catalog_description);
//		String catalogSizeText = description.getText().toString();
//		float catalogSizeFloat = extractCatalogSize(catalogSizeText);
//		int catalogObjects = extractNumObjects(catalogSizeText);
//		
//		ImageView checked = (ImageView) v.findViewById(R.id.checkbox);
//		
//		if (!selectedItems.contains(catalog)){ //This item is not currently checked
//			selectedItems.add(catalog);
//			size += catalogSizeFloat;
//			numFiles += catalogObjects;
//			Log.d("JoeTest", "numFiles: " + numFiles);
//			checked.setImageResource(settingsRef.getCheckbox_Selected());
//		}
//		else{
//			selectedItems.remove(catalog);
//			size -= catalogSizeFloat;
//			numFiles -= catalogObjects;
//			Log.d("JoeTest", "numFiles: " + numFiles);
//			checked.setImageResource(settingsRef.getCheckbox_Unselected());
//		}
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.manage_catalogs_tab_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		addEquipmentButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.manage_catalogs_tab_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		addEquipmentButton.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
	}
}
