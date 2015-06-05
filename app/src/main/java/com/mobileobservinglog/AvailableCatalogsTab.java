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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.mobileobservinglog.R;
import com.mobileobservinglog.service.ChartDownloadService;
import com.mobileobservinglog.support.SettingsContainer;
import com.mobileobservinglog.support.database.CatalogsDAO;
import com.mobileobservinglog.support.database.DatabaseHelper;
import com.mobileobservinglog.support.database.ScheduledDownloadsDao;
import com.mobileobservinglog.support.database.SettingsDAO;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
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
			nothingLeft.setVisibility(View.VISIBLE);
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
				progressLayout.setVisibility(View.GONE);
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
				progressLayout.setVisibility(View.GONE);
				alertModal.setVisibility(View.VISIBLE);
				//Control picks up again after the user presses one of the buttons. The rest of removing the catalogs takes place in ConfirmInstall
			}
        }
    };
    
    private final Button.OnClickListener confirmInstall = new Button.OnClickListener() {
		public void onClick(View view){
			Log.d("JoeTest", "confirmInstall called");
			asyncTaskRunning = true;
			new Thread(new InstallCatalogsRunnable()).start();
        }
    };
    
    private class InstallCatalogsRunnable implements Runnable{

		public void run() {
			CatalogsDAO catalogsDb = new CatalogsDAO(AvailableCatalogsTab.this);
			Cursor imagePaths = catalogsDb.getImagePaths(selectedItems);
			imagePaths.moveToFirst();

			List<String> pathsList = new ArrayList<>(imagePaths.getCount() * 2);
			while(!imagePaths.isAfterLast()) {
				pathsList.add(imagePaths.getString(0)); //normal
				pathsList.add(imagePaths.getString(1)); //night
				imagePaths.moveToNext();
			}

			ScheduledDownloadsDao downloadsDao = new ScheduledDownloadsDao(AvailableCatalogsTab.this);
			downloadsDao.scheduleChartsToDownload(pathsList);

			Log.d("JoeTest", "Install Was successfull. Going to update the DB");
			boolean dbSuccess = false;
			for (String catalog : selectedItems){
				Log.d("JoeTest", "Updating catalog " + catalog + " in the database");
				dbSuccess = catalogsDb.updateAvailableCatalogsInstalled(catalog, "Yes");
			}

			if (dbSuccess){
				Log.d("JoeTest", "DB Updated successful. Displaying success message");
				successMessage = getString(R.string.install_catalogs_success);
				successMessageHandler.sendMessage(new Message());
			}
			else{
				failureMessage = "There was a problem scheduling the star chart downloads. Please try again";
				Log.d("JoeTest", "Db Update unsuccessfull. Displaying failure message");
				failureMessageHandler.sendMessage(new Message());
			}

			startService(new Intent(AvailableCatalogsTab.this, ChartDownloadService.class));
		}
    }
}
