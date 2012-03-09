package com.rowley.mobileobservinglog;

import java.util.ArrayList;

import com.rowley.mobileobservinglog.ManageCatalogsTabParent.CatalogAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class InstalledCatalogsTab extends ManageCatalogsTabParent {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(settingsRef.getAddCatalogsTabLayout());
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
		super.prepareListView();
		setListAdapter(new CatalogAdapter(this, settingsRef.getAddCatalogsListLayout(), installedCatalogList));
	}
}
