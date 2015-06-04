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

			/*
			Log.d("JoeTest", "InstallCatalogsRunnable.run called");
			boolean success = true;
			int filesToDownload = numFiles * 2; //double because we have night mode and normal mode
			int currentFileNumber = 1;
			prepProgressModalHandler.sendMessage(new Message());
			setDownloadProgressText(currentFileNumber, filesToDownload);
			
			//kick off another thread to update the progress image rotation
			keepRunningProgressUpdate = true;
			new Thread(new ProgressIndicator()).start();
						
			Bundle messageData = new Bundle();
			messageData.putInt("filesToDownLoad", filesToDownload);
			
			//Download the images
			//check the settings table for the files location
			String fileLocationString = settingsRef.getPersistentSetting(settingsRef.STAR_CHART_DIRECTORY, AvailableCatalogsTab.this);
			Log.d("JoeTest", "FileLocationString is " + fileLocationString);
			File starChartRoot = null;
			SettingsDAO settingsDb = new SettingsDAO(AvailableCatalogsTab.this);
			
			//if it's not set yet, then first look for an external storage card and establish the file location
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();

			//First, check whether we have established a file location already. If not then do so, with the preference going to the external system (for size)
			if (fileLocationString.equals("NULL")){
				//If the external card is not available, then establish a file location on the internal file system
				if (Environment.MEDIA_MOUNTED.equals(state)) {
				    // We can read and write the media
				    mExternalStorageAvailable = mExternalStorageWriteable = true;
				    fileLocationString = SettingsContainer.EXTERNAL;
				} else{
				   fileLocationString = SettingsContainer.INTERNAL;
				}
				
				//If we just established the file location, store it in the database
				settingsDb.setPersistentSetting(SettingsContainer.STAR_CHART_DIRECTORY, fileLocationString);
			}
			
			//Now actually get the file location
			if (fileLocationString.equals(SettingsContainer.EXTERNAL)){
				starChartRoot = getExternalFilesDir(null);
			}
			else{
				starChartRoot = getFilesDir();
			}
			
			addNoMediaFile(starChartRoot);
			
			//Get a list of the file paths we need to fetch/save
//			Cursor imagePaths = catalogsDb.getImagePaths(selectedItems);
//			imagePaths.moveToFirst();
			int rowCount = imagePaths.getCount();
			Log.d("JoeTest", "imagePaths cursor had " + rowCount + " rows");
			
			//Make directories
			String tempNormalFilePath = imagePaths.getString(0);
			String currentNormalDirectory = createDirectoryStructure(starChartRoot.toString(), tempNormalFilePath);
			
			String tempNightFilePath = imagePaths.getString(1);
			String currentNightDirectory = createDirectoryStructure(starChartRoot.toString(), tempNightFilePath);
			
			//Establish connection and download the files
			for (int i = 0; i < rowCount; i++){				
				String normalPathString = imagePaths.getString(0);
				String nightPathString = imagePaths.getString(1);
				
				File normalPath = new File(starChartRoot + normalPathString);
				File nightPath = new File(starChartRoot + nightPathString);
				
				//If the file already exists (maybe from a previous, unsuccessful attempt to install the images) then we will skip this image and move to the next
				if (normalPathString != null && !normalPathString.equals("NULL") && !normalPath.exists()){
					if(!normalPathString.contains(currentNormalDirectory)) {
						currentNormalDirectory = createDirectoryStructure(starChartRoot.toString(), normalPathString);
					}
					if(!nightPathString.contains(currentNightDirectory)) {
						currentNightDirectory = createDirectoryStructure(starChartRoot.toString(), nightPathString);
					}
					
					//Log.d("JoeTest", "NormalPath #" + i + " is " + normalPath.toString());
					try{
						//setup input streams
						URL imageFile = new URL(SettingsContainer.IMAGE_DOWNLOAD_ROOT + normalPathString);
						URLConnection ucon = imageFile.openConnection();
						InputStream is = ucon.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						
						//Read in the remote file until we hit a -1 bit
						ByteArrayBuffer buf = new ByteArrayBuffer(50);
						int current = 0;
						while ((current = bis.read()) != -1){
							buf.append((byte) current);
						}
						
						FileOutputStream fos = new FileOutputStream(normalPath);
						fos.write(buf.toByteArray());
						fos.close();
					}
					catch(IOException e){
						//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
						Log.w("JoeDebug", e.getMessage());
						success = false;
						if (normalPath.exists()){
							Log.w("JoeTest", "Deleting the file " + normalPathString);
							normalPath.delete();
						}
					}
					finally{
						//Log.d("JoeTest", "Updating the alert");
						if(!normalPath.exists()) {
							Log.w("JoeTest", "Failed to download the file " + normalPathString);
							success = false;
						}
						currentFileNumber++;
						messageData.putInt("current", currentFileNumber);
						messageData.putInt("filesToDownLoad", filesToDownload);
						Message updateMessage = new Message();
						updateMessage.setData(messageData);
						uiUpdateHandler_Download.sendMessage(updateMessage);
					}
				}
				
				if (nightPathString != null && !nightPathString.equals("NULL") && !nightPath.exists()){
					try{
						//setup input streams
						URL imageFile = new URL(SettingsContainer.IMAGE_DOWNLOAD_ROOT + nightPathString);
						URLConnection ucon = imageFile.openConnection();
						InputStream is = ucon.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						
						//Read in the remote file until we hit a -1 bit
						ByteArrayBuffer buf = new ByteArrayBuffer(50);
						int current = 0;
						while ((current = bis.read()) != -1){
							buf.append((byte) current);
						}
						
						FileOutputStream fos = new FileOutputStream(nightPath);
						fos.write(buf.toByteArray());
						fos.close();
					}
					catch(Exception e){
						//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
						Log.w("JoeDebug", e.getMessage());
						success = false;
						if (nightPath.exists()){
							Log.w("JoeTest", "Deleting the file " + nightPathString);
							nightPath.delete();
						}
					}
					finally{
						//Log.d("JoeTest", "Updating the alert");
						if(!normalPath.exists()) {
							Log.w("JoeTest", "Failed to download the file " + nightPathString);
							success = false;
						}
						currentFileNumber++;
						messageData.putInt("current", currentFileNumber);
						messageData.putInt("filesToDownLoad", filesToDownload);
						Message updateMessage = new Message();
						updateMessage.setData(messageData);
						uiUpdateHandler_Download.sendMessage(updateMessage);
					}
				}
				
				imagePaths.moveToNext();
			}
			
			//stop the image update thread
			keepRunningProgressUpdate = false;
			
			//Update the database
			if(success){
				Log.d("JoeTest", "Install Was successfull. Going to update the DB");
				boolean dbSuccess = false;
				for (String catalog : selectedItems){
					Log.d("JoeTest", "Updating catalog " + catalog + " in the database");
					dbSuccess = catalogsDb.updateAvailableCatalogsInstalled(catalog, "Yes");
				}
				
				if (dbSuccess){
					Log.d("JoeTest", "DB Updated successful. Displaying success message");
					successMessageHandler.sendMessage(new Message());
				}
				else{
					failureMessage = "There was a problem downloading at least one of the images. (Others may have been successfully installed) Please try again";
					Log.d("JoeTest", "Db Update unsuccessfull. Displaying failure message");
					failureMessageHandler.sendMessage(new Message());
				}
			}
			else{
				failureMessage = "There was a problem downloading at least one of the images. (Others may have been successfully installed) Please try again";
				//show alert message
				Log.d("JoeTest", "Install was unsuccessfull. Displaying failure message");
				failureMessageHandler.sendMessage(new Message());
			}
			
			imagePaths.close();
			catalogsDb.close();
			*/
		}
    }
    
    private String createDirectoryStructure(String root, String filepath) {
    	//Cut the filename off the end of the path
    	Log.d("JoeDebug", "Creating directory structure. Filepath passed in: " + filepath);
    	int index = filepath.lastIndexOf("/");
    	String directoryPath = filepath.subSequence(0, index).toString();
		File directoryBuilder = new File(root.toString() + directoryPath);
		directoryBuilder.mkdirs();
		Log.d("JoeDebug", "Created directory structure for " + directoryPath);
		return directoryPath;
    }
    
    private void addNoMediaFile(File rootDirectory){
    	try{
    		File noMedia = new File(rootDirectory + ".nomedia");
			FileOutputStream fos = new FileOutputStream(noMedia);
			fos.write(1);
			fos.close();
		}
		catch(IOException e){
			//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
			e.printStackTrace();
		}
    }
}
