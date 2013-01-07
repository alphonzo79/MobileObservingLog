/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.mobileobservinglog.BackupRestoreScreen;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

public class BackupRestoreUtil {
	TextView messageDisplay;
	ImageView spinnerDisplay;
	ProgressSpinner spinner;
	BackupRestoreScreen caller;
	int totalObjects;
	int completedObjects;
	
	String failureMessage;
	String successMessage;
	int errors;
	
	boolean success;
	
	public BackupRestoreUtil(TextView messageDisplay, ImageView spinnerDisplay, BackupRestoreScreen caller) {
		this.messageDisplay = messageDisplay;
		this.spinnerDisplay = spinnerDisplay;
		this.caller = caller;
		spinner = new ProgressSpinner(this.spinnerDisplay);
		errors = 0;
	}
	
	public void backupData() {
		new Thread(new BackupAsynch()).start();
	}
	
	protected class BackupAsynch implements Runnable {
		public void run() {
			caller.prepProgressModal();
			ProgressSpinner spinner = new ProgressSpinner(spinnerDisplay);
			spinner.startSpinner();
			
			ObservableObjectDAO db = new ObservableObjectDAO(caller);
			Cursor logData = db.getAllLogData();
			logData.moveToFirst();
			totalObjects = logData.getCount();
			if(totalObjects < 1) {
				failureMessage = "There was a problem getting data from the database. We didn't find any data to backup.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			
			File storageRoot = getDefaultDirectory();
			if(storageRoot == null) {
				failureMessage = "There was a problem finding the default downloads directory to save the file in.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			String filename = "ObservingLogBackup" + System.currentTimeMillis() + ".maol";
			File backupFile = new File(storageRoot + "/" + filename);
			
			try {
				FileWriter fw = new FileWriter(backupFile);
				BufferedWriter bw = new BufferedWriter(fw);
				do {
					completedObjects++;
					
					BackupMessageHandler.sendMessage(new Message());
					
					String designation = logData.getString(0);
					String logged = logData.getString(1);
					String logDate = logData.getString(2);
					String logTime = logData.getString(3);
					String logLocation = logData.getString(4);
					String equipment = logData.getString(5);
					int seeing = logData.getInt(6);
					int transparency = logData.getInt(7);
					String favorite = logData.getString(8);
					String findingMethod = logData.getString(9);
					String viewingNotes = logData.getString(10);
					
					//Replace semicolons in user-editable fields so they don't interfere with our string parsing when we restore (It's semicolon-delimited)
					if(logLocation.contains(";")) {
						logLocation = logLocation.replace(";", ".");
					}
					if(equipment.contains(";")) {
						equipment = equipment.replace(";", ".");
					}
					
					String line = designation + ";" + logged + ";" + logDate + ";" + logTime + ";" + logLocation + ";" + equipment + ";" + seeing + transparency + ";" + 
									favorite + ";" + findingMethod + ";" + viewingNotes + "/n";
					bw.write(line);
					bw.newLine();
				} while (logData.moveToNext());
			} catch (FileNotFoundException e) {
				failureMessage = "There was a problem while creating the file in which to back up your data.";
				failureMessageHandler.sendMessage(new Message());
				return;
			} catch (IOException e) {
				success = false;
				errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
			} finally {
				logData.close();
			}
			
			//Get the IO stuff
			//Go through the cursor and format each line, writing it to the file -- get rid of semicolons in any user-editable fields other than notes
			//close the IO stuff, close the cursor and set the success message to include the location of the file
			
			if(success) {
				successMessage = "Successfully backed up data for " + completedObjects + " objects to the file " + backupFile.toString() + ". Move this file to " +
						"your computer so you can restore it later";
				successMessageHandler.sendMessage(new Message());
			} else {
				failureMessage = "There was a problem backing up data for " + errors + " out of " + totalObjects + ". A backup file is located in " + backupFile + 
						" and may still be useable, but successful restore cannot be guaranteed";
				failureMessageHandler.sendMessage(new Message());
			}
		}
	}
    
    Handler BackupMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		updateBackupMessage();
    	}
    };
    
    private void updateBackupMessage() {
    	messageDisplay.setText("Backing up log data for object #" + completedObjects + " out of " + totalObjects);
    }
	
	public void restoreData() {
		
	}
	
	protected class RestoreAsynch implements Runnable {
		public void run() {
			
		}
	}
    
    Handler failureMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		showFailureMessage();
    		spinner.setComplete(true);
    	}
    };
    
    private void showFailureMessage() {
    	caller.showFailureMessage(failureMessage);
    }
    
    Handler successMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		showSuccessMessage();
    		spinner.setComplete(true);
    	}
    };
    
    private void showSuccessMessage() {
    	caller.showSuccessMessage(successMessage);
    }
    
    private File getDefaultDirectory() {
    	String state = Environment.getExternalStorageState();

		//If the external card is not available, then establish a file location on the internal file system
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		} else{
		   return Environment.getDownloadCacheDirectory();
		}
    }
}
