package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TelescopeTab extends ManageEquipmentTabParent {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
		super.prepareListView();
//		if (availableCatalogList.size() == 0){
//			submitButton.setVisibility(View.GONE);
//			TextView nothingLeft = (TextView)findViewById(R.id.nothing_left);
//			nothingLeft.setText(R.string.no_available_catalogs);
//			nothingLeft.setVisibility(0);
//		}
//		else{
//			setListAdapter(new CatalogAdapter(this, settingsRef.getAddCatalogsListLayout(), availableCatalogList));
//		}
	}
}
