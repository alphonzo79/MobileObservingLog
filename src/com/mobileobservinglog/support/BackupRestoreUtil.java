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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mobileobservinglog.BackupRestoreScreen;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

import android.os.Bundle;
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
	
	String restorableFilename;
	
	boolean success;
	
	public BackupRestoreUtil(TextView messageDisplay, ImageView spinnerDisplay, BackupRestoreScreen caller) {
		this.messageDisplay = messageDisplay;
		this.spinnerDisplay = spinnerDisplay;
		this.caller = caller;
		spinner = new ProgressSpinner(this.spinnerDisplay, this.caller);
		errors = 0;
	}
	
	/////////////////////////////////////////
	//Backup Section
	/////////////////////////////////////////
	
	public void backupData() {
		new Thread(new BackupAsynch()).start();
	}
	
	protected class BackupAsynch implements Runnable {
		public void run() {
			success = true;
			completedObjects = 0;
			
			PrepProgressModalHandler.sendMessage(new Message());
			spinner.startSpinner();
			
			Bundle data = new Bundle();
			data.putString("messageString", "Preparing the backup data");
			Message msg = Message.obtain();
			msg.setData(data);
			ProgressMessageHandler.sendMessage(msg);
			
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
			ProgressMessageHandler.sendMessage(msg);
			
			File storageRoot = getDefaultDirectory();
			if(storageRoot == null) {
				failureMessage = "There was a problem finding the default downloads directory to save the file in.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			String filename = "ObservingLogBackup" + System.currentTimeMillis() + ".maol";
			File backupFile = new File(storageRoot + "/" + filename);
			
			try {
				backupFile.getParentFile().mkdirs();
				backupFile.createNewFile();
				
				FileWriter fw = new FileWriter(backupFile);
				BufferedWriter bw = new BufferedWriter(fw);
				
				for(String row : logData) {
					completedObjects++;
					
					data.clear();
					data.putString("messageString", String.format("Backing up log data for object #%,d out of %,d", completedObjects, totalObjects));
					msg = Message.obtain();
					msg.setData(data);
					ProgressMessageHandler.sendMessage(msg);
					
					bw.write(row);
					bw.newLine();
				}
				
				bw.close();
				fw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				failureMessage = "There was a problem while creating the file in which to back up your data.";
				failureMessageHandler.sendMessage(new Message());
				return;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
				errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
			}
			
			if(success) {
				successMessage = String.format("Successfully backed up data for %,d objects to the file %s. Move this file to " +
						"your computer so you can restore it later", totalObjects, backupFile);
				successMessageHandler.sendMessage(new Message());
			} else {
				failureMessage = "There was a problem backing up data. A backup file may be located in " + backupFile + 
						" and may still be useable, but successful restore cannot be guaranteed";
				failureMessageHandler.sendMessage(new Message());
			}
		}
	}
    
    //////////////////////////////////////////////
    //Restore section
    //////////////////////////////////////////////
	
	public void restoreData(String filename) {
		restorableFilename = filename;
		new Thread(new RestoreAsynch()).start();
	}
	
	protected class RestoreAsynch implements Runnable {
		public void run() {
			success = true;
			completedObjects = 0;
			
			PrepProgressModalHandler.sendMessage(new Message());
			spinner.startSpinner();
			
			Bundle data = new Bundle();
			data.putString("messageString", "Preparing the restore data");
			Message msg = Message.obtain();
			msg.setData(data);
			ProgressMessageHandler.sendMessage(msg);
			
			File storageRoot = getDefaultDirectory();
			File restoreFile = new File(storageRoot + "/" + restorableFilename);
			
			ObservableObjectDAO db = new ObservableObjectDAO(caller);
			totalObjects = db.getRowCount();
			
			List<Map<String, String>> updateArgs = new ArrayList<Map<String, String>>();
			
			try {
				FileReader fr = new FileReader(restoreFile);
				BufferedReader br = new BufferedReader(fr);
				
				String line;
				while((line = br.readLine()) != null) {
					completedObjects++;
					
					data.clear();
					data.putString("messageString", String.format("Reading in the restore file. Preparing object #%,d out of %,d", completedObjects, totalObjects));
					msg = Message.obtain();
					msg.setData(data);
					ProgressMessageHandler.sendMessage(msg);
					
					try {
						String[] fields = line.split(";", 11);
						String designation = fields[0];
						String logged = fields[1];
						String logDate = fields[2];
						String logTime = fields[3];
						String logLocation = fields[4];
						String equipment = fields[5];
						int seeing = Integer.parseInt(fields[6]);
						int transparency = Integer.parseInt(fields[7]);
						String favorite = fields[8];
						String findingMethod = fields[9];
						String viewingNotes = fields[10];
						
						Map<String, String> args = new TreeMap<String, String>();
						args.put("designation", designation);
						args.put("logged", logged);
						args.put("logDate", logDate);
						args.put("logTime", logTime);
						args.put("logLocation", logLocation);
						args.put("equipment", equipment);
						args.put("seeing", Integer.toString(seeing));
						args.put("transparency", Integer.toString(transparency));
						args.put("favorite", favorite);
						args.put("findingMethod", findingMethod);
						args.put("viewingNotes", viewingNotes);
						
						updateArgs.add(args);
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
				
				br.close();
				fr.close();
			} catch (IOException e) {
				success = false;
				e.printStackTrace();
			} 
			
			data.clear();
			data.putString("messageString", "Restoring log data. This may take a minute");
			msg = Message.obtain();
			msg.setData(data);
			ProgressMessageHandler.sendMessage(msg);
			
			success = db.restoreLogData(updateArgs);
			db.close();
			
			if(success) {
				successMessage = String.format("Successfully restored data for %,d objects.", completedObjects);
				successMessageHandler.sendMessage(new Message());
			} else {
				failureMessage = "There was a problem restoring data. Please try again";
				failureMessageHandler.sendMessage(new Message());
			}
		}
	}
	
	//////////////////////////////////////////////////////
	// Common Section
	//////////////////////////////////////////////////////
    
    Handler ProgressMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		updateProgressMessage(msg.getData().getString("messageString"));
    	}
    };
    
    private void updateProgressMessage(String message) {
    	messageDisplay.setText(message);
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
    
    public List<String> findRestorableFiles() {
    	List<String> retVal = new ArrayList<String>();
    	
    	File storageRoot = getDefaultDirectory();
    	File[] foundFiles = storageRoot.listFiles();
    	if(foundFiles != null) {
	    	for(File file : storageRoot.listFiles()) {
	    		String filename = file.getName();
	    		if(filename.endsWith(".maol"))
	    			retVal.add(filename);
	    	}
    	}
    	
    	return retVal;
    }
    
    public String getDefaultDirectoryPath() {
    	File storageRoot = getDefaultDirectory();
    	return storageRoot.getAbsolutePath();
    }
}
