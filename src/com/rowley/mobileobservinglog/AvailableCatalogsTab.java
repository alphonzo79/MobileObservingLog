package com.rowley.mobileobservinglog;

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
			prepForModal();
			//Check to make sure something was selected
			if (selectedItems.size() < 1){
				alertText.setText(R.string.no_items_selected);
				alertCancel.setVisibility(View.GONE);
				alertOk.setVisibility(View.VISIBLE);
				alertOk.setOnClickListener(dismissAlert);
				alertModal.setVisibility(View.VISIBLE);
			}
			else{
				//confirm Install
				String confirmInstallation = "Are you sure you want to install the following catalogs: ";
				confirmInstallation += selectedItems.get(0);
				
				for (int i = 1; i < selectedItems.size(); i++){ //start at 1 because we already did one
					confirmInstallation = confirmInstallation + ", " + selectedItems.get(i);
				}
				confirmInstallation = confirmInstallation + "? (This will add " + twoDigits.format(size) + " MB of star charts to the file system)";
				
				alertText.setText(confirmInstallation);
				alertCancel.setVisibility(View.VISIBLE);
				alertOk.setVisibility(View.VISIBLE);
				alertCancel.setOnClickListener(dismissAlert);
				alertOk.setOnClickListener(confirmInstall);
				alertModal.setVisibility(View.VISIBLE);
				//Control picks up again after the user presses one of the buttons. The rest of removing the catalogs takes place in ConfirmInstall
			}
        }
    };
    
    private final Button.OnClickListener confirmInstall = new Button.OnClickListener() {
		public void onClick(View view){
			//Download the images
			
			//Update the database
			
			//restart the activity
        }
    };
}
