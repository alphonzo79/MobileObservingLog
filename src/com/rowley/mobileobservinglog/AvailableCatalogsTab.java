package com.rowley.mobileobservinglog;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AvailableCatalogsTab extends ManageCatalogsTabParent {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submitButton.setText(R.string.install_selected_catalogs);
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
		if (availableCatalogList.size() == 0){
			submitButton.setVisibility(View.GONE);
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_left);
			nothingLeft.setText(R.string.no_available_catalogs);
			nothingLeft.setVisibility(0);
		}
		else{
			setListAdapter(new CatalogAdapter(this, settingsRef.getAddCatalogsListLayout(), availableCatalogList));
		}
	}
	
	//set Listeners for the submit button
	private final Button.OnClickListener submitOnClick = new Button.OnClickListener() {
		public void onClick(View view){
			//Check to make sure something was selected
			
			//Download the images
			
			//Update the database
			
			//restart the activity
        }
    };
}
