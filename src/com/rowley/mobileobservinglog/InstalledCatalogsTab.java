package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.view.View;
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
		if (installedCatalogList.size() < 1){
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
			prepForModal();
			//check whether anything was selected
			if (selectedItems.size() < 1){
				alertText.setText(R.string.no_items_selected);
				alertCancel.setVisibility(View.GONE);
				alertOk.setVisibility(View.VISIBLE);
				alertOk.setOnClickListener(dismissAlert);
				alertModal.setVisibility(View.VISIBLE);
			}
			else{
				//confirm removal
				String confirmRemoval = "Are you sure you want to remove the following catalogs: ";
				confirmRemoval += selectedItems.get(0);
				
				for (int i = 1; i < selectedItems.size(); i++){ //start at 1 because we already did one
					confirmRemoval = confirmRemoval + ", " + selectedItems.get(i);
				}
				confirmRemoval = confirmRemoval + "? (This will remove all of the related star charts from the file system, freeing up " + twoDigits.format(size) + " MB of space)";
				
				alertText.setText(confirmRemoval);
				alertModal.setVisibility(View.VISIBLE);
				alertCancel.setVisibility(View.VISIBLE);
				alertOk.setVisibility(View.VISIBLE);
				alertCancel.setOnClickListener(dismissAlert);
				alertOk.setOnClickListener(confirmRemove);
				//Control picks up again after the user presses one of the buttons. The rest of removing the catalogs takes place in ConfirmRemove
			}
        }		
    };
    
    private final Button.OnClickListener confirmRemove = new Button.OnClickListener() {
		public void onClick(View view){
			//remove images
			
			//update database
			
			//relaunch activity
        }
    };
}
