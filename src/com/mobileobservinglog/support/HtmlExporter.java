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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;
import com.mobileobservinglog.BackupRestoreScreen;
import com.mobileobservinglog.support.database.CatalogsDAO;
import com.mobileobservinglog.support.database.ObservableObjectDAO;
import com.mobileobservinglog.support.database.PersonalInfoDAO;

public class HtmlExporter {
	TextView messageDisplay;
	ImageView spinnerDisplay;
	ProgressSpinner spinner;
	BackupRestoreScreen caller;
	
	String failureMessage;
	String successMessage;
	int errors;
	
	List<String> catalogs;
	
	boolean success;
	
	public HtmlExporter(TextView messageDisplay, ImageView spinnerDisplay, BackupRestoreScreen caller) {
		this.messageDisplay = messageDisplay;
		this.spinnerDisplay = spinnerDisplay;
		this.caller = caller;
		spinner = new ProgressSpinner(this.spinnerDisplay, this.caller);
		errors = 0;
	}
	
	public void exportData(List<String> catalogs) {
		this.catalogs = catalogs;
		new Thread(new ExportAsynch()).start();
	}
	
	protected class ExportAsynch implements Runnable {
		public void run() {
			success = true;
			
			PrepProgressModalHandler.sendMessage(new Message());
			spinner.startSpinner();
			
			Bundle data = new Bundle();
			data.putString("messageString", "Building the export file");
			Message msg = Message.obtain();
			msg.setData(data);
			ProgressMessageHandler.sendMessage(msg);
			
			File storageRoot = getDefaultDirectory();
			if(storageRoot == null) {
				failureMessage = "There was a problem finding the default downloads directory to save the file in.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			String filename = "ObservingLogExport" + System.currentTimeMillis() + ".html";
			File exportFile = new File(storageRoot + "/" + filename);
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(exportFile);
			} catch (IOException e1) {
				e1.printStackTrace();failureMessage = "There was a problem exporting your data.";
				failureMessageHandler.sendMessage(new Message());
				return;
			}
			BufferedWriter bw = new BufferedWriter(fw);
			
			//Start the file
			try {
				exportFile.getParentFile().mkdirs();
				exportFile.createNewFile();
				
				bw.write("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN''http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
				bw.newLine();
				bw.write("<html>");
				bw.newLine();
				bw.write("	<head>");
				bw.newLine();
				bw.write("		<title>Mobile Astronomical Observing Log Exported Data</title>");
				bw.newLine();
				bw.write("		<STYLE type=\"text/css\">");
				bw.newLine();
				bw.write("			H1 { text-align: center; page-break-before: always}");
				bw.newLine();
				bw.write("			H2 { text-align: center}");
				bw.newLine();
				bw.write("			H3 { text-align: center}");
				bw.newLine();
				bw.write("			H6 { text-align: center}");
				bw.newLine();
				bw.write("			TABLE { margin-left: auto; margin-right: auto; margin-top: 50px; page-break-before: auto; width: 70%}");
				bw.newLine();
				bw.write("			TD { text-align: center }");
				bw.newLine();
				bw.write("		</STYLE>");
				bw.newLine();
				bw.write("	</head>");
				bw.newLine();
				bw.write("	<body>");
				bw.newLine();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				failureMessage = "There was a problem exporting your data.";
				failureMessageHandler.sendMessage(new Message());
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
				errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
			}
			
			//Fill in the catalog data
			for(String catalog : catalogs) {
				PersonalInfoDAO userDb = new PersonalInfoDAO(caller);
				Cursor info = userDb.getPersonalInfo();
				String personalInfo = formatPersonalData(info);
				info.close();
				userDb.close();
				
				CatalogsDAO catsDb = new CatalogsDAO(caller);
				int catalogTotalLogged = catsDb.getNumLogged(catalog);
				catsDb.close();
				
				ObservableObjectDAO objectDb = new ObservableObjectDAO(caller);
				Cursor catalogList = objectDb.getUnfilteredObjectList_Catalog(catalog);
				catalogList.moveToFirst();
				List<String> designations = new ArrayList<String>();
				do {
					designations.add(catalogList.getString(0));
				} while (catalogList.moveToNext());
				catalogList.close();
				
				int objectCount = designations.size();
				int workingOn = 0;
				
				try{
					bw.write(String.format("<h1>%s Observing Log Data</h1>", catalog));
					bw.newLine();
					bw.write(String.format("<h3>%d out of %d objects logged.</h3>", catalogTotalLogged, objectCount));
					bw.write(String.format("<h6>%s<h6>", personalInfo));
					bw.newLine();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					failureMessage = "There was a problem while creating the file in which to back up your data.";
					failureMessageHandler.sendMessage(new Message());
					try {
						bw.close();
						fw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return;
				} catch (IOException e) {
					e.printStackTrace();
					success = false;
					errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
				}
				
				for(String objectName : designations) {
					data.clear();
					data.putString("messageString", String.format("Working on catalog %s. Preparing object #%,d out of %,d", catalog, ++workingOn, objectCount));
					msg = Message.obtain();
					msg.setData(data);
					ProgressMessageHandler.sendMessage(msg);
					
					Cursor objectInfo = objectDb.getObjectData(objectName);
					objectInfo.moveToFirst();
					
					String commonName = objectInfo.getString(2);
					String type = objectInfo.getString(3) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(3)) : "N/A";
					String magnitude = objectInfo.getString(4) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(4)) : "N/A";
					String size = objectInfo.getString(5) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(5)) : "N/A";
					String distance = objectInfo.getString(6) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(6)) : "N/A";
					String constellation = objectInfo.getString(7) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(7)) : "N/A";
					String season = objectInfo.getString(8) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(8)) : "N/A";
					String rightAscension = objectInfo.getString(9) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(9)) : "N/A";
					String declination = objectInfo.getString(10) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(10)) : "N/A";
					String catalogDescription = objectInfo.getString(11) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(11)) : "N/A";
					String otherCats = StringEscapeUtils.escapeHtml4(objectInfo.getString(13));
			    	String loggedString = objectInfo.getString(16);
			    	boolean logged;
			    	if(loggedString != null) {
			    		logged = loggedString.toLowerCase().equals("true");
			    	} else {
			    		logged = false;
			    	}
			    	String logDate = objectInfo.getString(17) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(17)) : "N/A";
			    	String logTime = objectInfo.getString(18) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(18)) : "N/A";
			    	String logLocation = objectInfo.getString(19) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(19)) : "N/A";
			    	String equipment = objectInfo.getString(20) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(20)) : "N/A";
			    	int seeing = objectInfo.getInt(21);
			    	int transparency = objectInfo.getInt(22);
			    	String viewingNotes = objectInfo.getString(25) != null ? StringEscapeUtils.escapeHtml4(objectInfo.getString(25)) : "";
					
					try{
						bw.write("						<table>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td colspan=2>");
						bw.newLine();
						bw.write(String.format("								<h2>%s -- %s</h2>", objectName, logged ? "Logged" : "Not Logged"));
						bw.newLine();
						bw.write("								<hr>");
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						if(commonName != null || otherCats != null) {
							bw.write("						<tr>");
							bw.newLine();
							bw.write("							<td colspan=2>");
							bw.newLine();
							StringBuilder sb = new StringBuilder();
							if(commonName != null) {
								sb.append("<strong>Common Name:</strong> " + commonName);
							}
							if(otherCats != null) {
								if(sb.length() > 0)
									sb.append("</br>");
								sb.append("<strong>Other Designations:</strong> " + otherCats);
							}
							bw.write("								" + sb.toString());
							bw.newLine();
							bw.write("							</td>");
							bw.newLine();
							bw.write("						</tr>");
							bw.newLine();
						}
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Right Ascension:</strong> " + rightAscension);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Declination:</strong> " + declination);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Magnitude:</strong> " + magnitude);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Size:</strong> " + size);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Type:</strong> " + type);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Distance:</strong> " + distance);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Constellation:</strong> " + constellation);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Season:</strong> " + season);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td colspan=2>");
						bw.newLine();
						bw.write("								<hr>");
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Logged Date:</strong> " + logDate);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Logged Time:</strong> " + logTime);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td colspan=2>");
						bw.newLine();
						bw.write("								<strong>Logged Location:</strong> " + logLocation);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td colspan=2>");
						bw.newLine();
						bw.write("								<strong>Logged Equipment:</strong> " + equipment);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Seeing:</strong> ");
						if(seeing > 0)
							bw.write(String.format("%d/5", seeing));
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("							<td>");
						bw.newLine();
						bw.write("								<strong>Transparency:</strong> ");
						if(transparency > 0)
							bw.write(String.format("%d/5", transparency));
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("						<tr>");
						bw.newLine();
						bw.write("							<td colspan=2>");
						bw.newLine();
						bw.write("								<strong>Log Notes:</strong> " + viewingNotes);
						bw.newLine();
						bw.write("							</td>");
						bw.newLine();
						bw.write("						</tr>");
						bw.newLine();
						bw.write("					</table>");
						bw.newLine();
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
					
					objectInfo.close();
				}

				objectDb.close();
			}
			
			//Finish off the file
			try {
				bw.write("	</body>");
				bw.newLine();
				bw.write("</html>");
				
				bw.close();
				fw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				failureMessage = "There was a problem exporting your data.";
				failureMessageHandler.sendMessage(new Message());
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
				errors++; //Some may succeed. We want to communicate a level of success to the user and leave the file for possible use
			}
			
			if(success) {
				successMessage = String.format("Successfully exported data for %,d catalogs to the printable file %s. Move this file to " +
						"your computer so you can print it out", catalogs.size(), exportFile);
				successMessageHandler.sendMessage(new Message());
			} else {
				failureMessage = "There was a problem exporting your data. A printable file may be located in " + exportFile + 
						" and may still be useable";
				failureMessageHandler.sendMessage(new Message());
			}
		}
	}
    
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
    
    public String getDefaultDirectoryPath() {
    	File storageRoot = getDefaultDirectory();
    	return storageRoot.getAbsolutePath();
    }
    
    private String formatPersonalData(Cursor info) {
    	info.moveToFirst();
    	List<String> foundData = new ArrayList<String>();
    	foundData.add(info.getString(1));
    	foundData.add(info.getString(2));
    	foundData.add(info.getString(3));
    	foundData.add(info.getString(4));
    	foundData.add(info.getString(5));
    	
    	StringBuilder sb = new StringBuilder();
    	for(String dataPoint : foundData) {
    		if(dataPoint != null) {
    			if(sb.length() > 0)
    				sb.append("</br>");
    			sb.append(dataPoint);
    		}
    	}
    	
    	return sb.toString();
    }
}