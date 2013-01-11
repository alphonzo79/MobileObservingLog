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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.mobileobservinglog.BackupRestoreScreen;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
		spinner = new ProgressSpinner(this.spinnerDisplay, this.caller);
		errors = 0;
	}
	
	public void backupData() {
		new Thread(new BackupAsynch()).start();
	}
	
	protected class BackupAsynch implements Runnable {
		public void run() {
			success = true;
			
			PrepProgressModalHandler.sendMessage(new Message());
			spinner.startSpinner();
			
			Bundle data = new Bundle();
			data.putString("messageString", "Preparing the backup data");
			Message msg = Message.obtain();
			msg.setData(data);
			BackupMessageHandler.sendMessage(msg);
			
			ObservableObjectDAO db = new ObservableObjectDAO(caller);
			List<String> logData = db.getAllLogData();
			totalObjects = logData.size();
			if(totalObjects < 1) {
				failureMessage = "There was a problem getting data from the database. We didn't find any data to backup.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			
			data.clear();
			data.putString("messageString", "Preparing the backup file location");
			msg = Message.obtain();
			msg.setData(data);
			BackupMessageHandler.sendMessage(msg);
			
			File storageRoot = getDefaultDirectory();
			if(storageRoot == null) {
				failureMessage = "There was a problem finding the default downloads directory to save the file in.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			String filename = "ObservingLogBackup" + System.currentTimeMillis() + ".maol";
			File backupFile = new File(storageRoot + "/" + filename);
			
			try {
				backupFile.createNewFile();
				
				FileWriter fw = new FileWriter(backupFile);
				BufferedWriter bw = new BufferedWriter(fw);
				
				for(String row : logData) {
					completedObjects++;
					
					data.clear();
					data.putString("messageString", "Backing up log data for object #" + completedObjects + " out of " + totalObjects);
					msg = Message.obtain();
					msg.setData(data);
					BackupMessageHandler.sendMessage(msg);
					
					bw.write(row);
					bw.newLine();
				}
				
				bw.close();
				fw.close();
			} catch (FileNotFoundException e) {
				Log.d("JoeDebug", e.getMessage());
				e.printStackTrace();
				failureMessage = "There was a problem while creating the file in which to back up your data.";
				failureMessageHandler.sendMessage(new Message());
				return;
			} catch (IOException e) {
				Log.d("JoeDebug", e.getMessage());
				e.printStackTrace();
				success = false;
				errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
			}
			
			if(success) {
				successMessage = "Successfully backed up data for " + completedObjects + " objects to the file " + backupFile.toString() + ". Move this file to " +
						"your computer so you can restore it later";
				successMessageHandler.sendMessage(new Message());
			} else {
				failureMessage = "There was a problem backing up data. A backup file may be located in " + backupFile + 
						" and may still be useable, but successful restore cannot be guaranteed";
				failureMessageHandler.sendMessage(new Message());
			}
		}
	}
    
    Handler BackupMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		updateBackupMessage(msg.getData().getString("messageString"));
    	}
    };
    
    private void updateBackupMessage(String message) {
    	messageDisplay.setText(message);
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
    
    Handler PrepProgressModalHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		caller.prepProgressModal();
    	}
    };
    
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
