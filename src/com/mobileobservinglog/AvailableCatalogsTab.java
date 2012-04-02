package com.mobileobservinglog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import com.mobileobservinglog.R;

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
			new Thread(new InstallCatalogsRunnable()).start();
        }
    };
    
    private class InstallCatalogsRunnable implements Runnable{

		public void run() {
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
			DatabaseHelper db = new DatabaseHelper(AvailableCatalogsTab.this);
			
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
				db.setPersistentSetting(SettingsContainer.STAR_CHART_DIRECTORY, fileLocationString);
			}
			
			addNoMediaFile(starChartRoot);
			
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
			Log.d("JoeTest", "imagePaths cursor had " + rowCount + " rows");
			
			//create intermediate directory structure
			String tempNormalFilePath = imagePaths.getString(0);
			
			//Cut the filename off the end of the path
			String[] filePathSplit = tempNormalFilePath.split("/");
			tempNormalFilePath = "";
			for (int i = 0; i < (filePathSplit.length - 1); i++){
				tempNormalFilePath += "/" + filePathSplit[i];
			}
			File directoryBuilder = new File(starChartRoot.toString() + tempNormalFilePath);
			directoryBuilder.mkdirs();
			
			String tempNightFilePath = imagePaths.getString(1);
			
			//Cut the filename off the end of the path
			filePathSplit = tempNightFilePath.split("/");
			tempNightFilePath = "";
			for (int i = 0; i < (filePathSplit.length - 1); i++){
				tempNightFilePath += "/" + filePathSplit[i];
			}
			directoryBuilder = new File(starChartRoot.toString()  + tempNightFilePath);
			directoryBuilder.mkdirs();
			//We have left the cursor at the first row, so we can just continue and grab the columns again in the next loop.
			
			//Establish connection and download the files
			for (int i = 0; i < rowCount; i++){
				String normalPathString = imagePaths.getString(0);
				String nightPathString = imagePaths.getString(1);
				File normalPath = new File(starChartRoot + normalPathString);
				File nightPath = new File(starChartRoot + nightPathString);
				
				//If the file already exists (maybe from a previous, unsuccessful attempt to install the images) then we will skip this image and move to the next
				if (!normalPath.exists()){
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
						success = false;
						if (normalPath.exists()){
							Log.d("JoeTest", "Deleting the file");
							normalPath.delete();
						}
					}
					finally{
						//Log.d("JoeTest", "Updating the alert");
						currentFileNumber++;
						messageData.putInt("current", currentFileNumber);
						messageData.putInt("filesToDownLoad", filesToDownload);
						Message updateMessage = new Message();
						updateMessage.setData(messageData);
						uiUpdateHandler_Download.sendMessage(updateMessage);
					}
				}
				
				if (!nightPath.exists()){
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
						success = false;
						if (nightPath.exists()){
							Log.d("JoeTest", "Deleting the file");
							nightPath.delete();
						}
					}
					finally{
						//Log.d("JoeTest", "Updating the alert");
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
					dbSuccess = db.updateAvailableCatalogsInstalled(catalog, "Yes");
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
			db.close();
		}
    }
    
    private void addNoMediaFile(File rootDirectory){
    	try{
    		File noMedia = new File(rootDirectory + ".nomedia");
			FileOutputStream fos = new FileOutputStream(noMedia);
			fos.write(null);
			fos.close();
		}
		catch(IOException e){
			//delete the file if it exists in case it is corrupt. Set success to false so we can display an error later
			e.printStackTrace();
		}
    }
}
