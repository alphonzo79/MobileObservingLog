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

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.mobileobservinglog.R;
import com.mobileobservinglog.support.SettingsContainer.SessionMode;

public class ProgressSpinner {

	ArrayList<Integer> progressImages = new ArrayList<Integer>();
	Iterator<Integer> imageIterator;
	boolean workComplete = false;
	SettingsContainer settingsRef;
	ImageView spinnerDisplay;
	Context caller;
	
	public ProgressSpinner(ImageView imageView, Context caller) {
		settingsRef = SettingsContainer.getSettingsContainer();
		this.caller = caller;
		prepareImageArray();
		spinnerDisplay = imageView;
		advanceProgressImage();
	}
	
	public synchronized void setComplete(boolean complete) {
		workComplete = complete;
	}
	
	public synchronized boolean getComplete() {
		return workComplete;
	}
	
	public void startSpinner() {
		workComplete = false;
		new Thread(new ProgressIndicator()).start();
	}
    
    protected class ProgressIndicator implements Runnable{
		public void run() {
			while (!workComplete){
				ProgressImageHandler.sendMessage(new Message());
				try{
					Thread.sleep(100);
				}
				catch (InterruptedException ex){
					
				}
			}
		}
    }
    
    private void prepareImageArray(){
    	if (settingsRef.getSessionMode() == SessionMode.normal){
    		progressImages.add(R.drawable.progress_normal_01);
    		progressImages.add(R.drawable.progress_normal_02);
    		progressImages.add(R.drawable.progress_normal_03);
    		progressImages.add(R.drawable.progress_normal_04);
    		progressImages.add(R.drawable.progress_normal_05);
    		progressImages.add(R.drawable.progress_normal_06);
    		progressImages.add(R.drawable.progress_normal_07);
    		progressImages.add(R.drawable.progress_normal_08);
    	}
    	else{
    		progressImages.add(R.drawable.progress_night_01);
    		progressImages.add(R.drawable.progress_night_02);
    		progressImages.add(R.drawable.progress_night_03);
    		progressImages.add(R.drawable.progress_night_04);
    		progressImages.add(R.drawable.progress_night_05);
    		progressImages.add(R.drawable.progress_night_06);
    		progressImages.add(R.drawable.progress_night_07);
    		progressImages.add(R.drawable.progress_night_08);
    	}
    	imageIterator = progressImages.listIterator();
    }
    
    Handler ProgressImageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		advanceProgressImage();
    	}
    };
    
    private void advanceProgressImage(){
    	if(!imageIterator.hasNext()){
    		imageIterator = progressImages.listIterator();
    	}
    	spinnerDisplay.setImageDrawable(caller.getResources().getDrawable(imageIterator.next()));
    }
}
