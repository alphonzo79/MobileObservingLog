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

import java.io.File;

import com.mobileobservinglog.R;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.CatalogsDAO;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
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
				progressLayout.setVisibility(View.GONE);
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
				alertCancel.setVisibility(View.VISIBLE);
				alertOk.setVisibility(View.VISIBLE);
				alertCancel.setOnClickListener(dismissAlert);
				alertOk.setOnClickListener(confirmRemove);
				progressLayout.setVisibility(View.GONE);
				alertModal.setVisibility(View.VISIBLE);
				//Control picks up again after the user presses one of the buttons. The rest of removing the catalogs takes place in ConfirmRemove
			}
        }		
    };
    
    private final Button.OnClickListener confirmRemove = new Button.OnClickListener() {
		public void onClick(View view){
			asyncTaskRunning = true;
			new Thread(new RemoveCatalogsRunnable()).start();
        }
    };
    
    private class RemoveCatalogsRunnable implements Runnable{

		public void run() {
			boolean success = true;
			int filesToRemove = numFiles * 2; //double because we have night mode and normal mode
			int currentFileNumber = 0;
			prepProgressModalHandler.sendMessage(new Message());
			setRemoveProgressText(currentFileNumber, filesToRemove);
			
			//kick off another thread to update the progress image rotation
			keepRunningProgressUpdate = true;
			new Thread(new ProgressIndicator()).start();
						
			Bundle messageData = new Bundle();
			messageData.putInt("filesToDownLoad", filesToRemove);
			
			//Remove the images
			//check the settings table for the files location
			String fileLocationString = settingsRef.getPersistentSetting(settingsRef.STAR_CHART_DIRECTORY, InstalledCatalogsTab.this);
			File starChartRoot = null;
			CatalogsDAO db = new CatalogsDAO(InstalledCatalogsTab.this);
			
			//if it's not set yet, then first look for an external storage card and establish the file location
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
			
			//Now actually get the file location
			if (fileLocationString.equals(SettingsContainer.EXTERNAL)){
				starChartRoot = getExternalFilesDir(null);
			}
			else{
				starChartRoot = getFilesDir();
			}
			
			//Get a list of the file paths we need to fetch/save
			Cursor imagePaths = db.getImagePaths(selectedItems);
			imagePaths.moveToFirst();
			int rowCount = imagePaths.getCount();
			
			//Establish connection and download the files
			for (int i = 0; i < rowCount; i++){
				String normalPathString = imagePaths.getString(0);
				String nightPathString = imagePaths.getString(1);
				File normalPath = new File(starChartRoot + normalPathString);
				File nightPath = new File(starChartRoot + nightPathString);
				
				//If the file already exists then we will remove it
				if (normalPath.exists()){
					try{
						normalPath.delete();
					}
					catch(Exception e){
						//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
						success = false;
					}
					finally{
						currentFileNumber++;
						messageData.putInt("current", currentFileNumber);
						messageData.putInt("filesToDownLoad", filesToRemove);
						Message updateMessage = new Message();
						updateMessage.setData(messageData);
						uiUpdateHandler_Remove.sendMessage(updateMessage);
					}
				}
				
				shortDelay();
				
				if (nightPath.exists()){
					try{
						nightPath.delete();
					}
					catch(Exception e){
						//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
						success = false;
					}
					finally{
						currentFileNumber++;
						messageData.putInt("current", currentFileNumber);
						messageData.putInt("filesToDownLoad", filesToRemove);
						Message updateMessage = new Message();
						updateMessage.setData(messageData);
						uiUpdateHandler_Remove.sendMessage(updateMessage);
					}
				}
				
				shortDelay();
				
				imagePaths.moveToNext();
			}
			
			//Update the database regardless of success. This will avoid problems with images not being available.
			boolean dbSuccess = false;
			for (String catalog : selectedItems){
				dbSuccess = db.updateAvailableCatalogsInstalled(catalog, "No");
			}

			//stop the image update thread
			keepRunningProgressUpdate = false;
			
			successMessageHandler.sendMessage(new Message());
						
			imagePaths.close();
			db.close();
		}
    };
    
    private void shortDelay(){
    	try{
    		Thread.sleep(20);
    	}
    	catch(InterruptedException ex){
    		
    	}
    }
}
