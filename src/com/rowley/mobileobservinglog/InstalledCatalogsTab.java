package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import com.rowley.mobileobservinglog.ManageCatalogsTabParent.CatalogAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class InstalledCatalogsTab extends ManageCatalogsTabParent {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submitButton.setText(R.string.remove_selected_catalogs);
        submitButton.setOnClickListener(submitOnClick);
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
		if (installedCatalogList.size() == 0){
			submitButton.setVisibility(View.GONE);
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_left);
			nothingLeft.setText(R.string.no_installed_catalogs);
			nothingLeft.setVisibility(0);
		}
		else{
			setListAdapter(new CatalogAdapter(this, settingsRef.getAddCatalogsListLayout(), installedCatalogList));
		}
	}
	
	//set Listeners for the submit button
	private final Button.OnClickListener submitOnClick = new Button.OnClickListener() {
		public void onClick(View view){
			//check whether anything was selected
			
			//confirm removal
			
			//remove images
			
			//update database
			
			//relaunch activity
        }
    };
}
