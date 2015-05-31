/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.strategies;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class TimePicker {
	public NumberPickerDriver hourPicker;
	public NumberPickerDriver minutePicker;
	public NumberPickerDriver amPmPicker;
	
	public TimePicker(String hour, String minute, String amPm, Context context) {
		Log.i("JoeDebug", String.format("Hour: %s, Minute: %s, AM/PM: %s", hour, minute, amPm));
		hourPicker = new NumberPickerDriver(setHours(), hour, context) {
			@Override
			public boolean save() {
				return false;
			}
		};
		minutePicker = new NumberPickerDriver(setMinutes(), minute, context) {
			@Override
			public boolean save() {
				return false;
			}
		};
		amPmPicker = new NumberPickerDriver(setAmPm(), amPm, context) {
			@Override
			public boolean save() {
				return false;
			}
		};
	}
	
	private static ArrayList<String> setHours() {
		ArrayList<String> retVal = new ArrayList<String>();
		for(int i = 1; i <= 12; i++) {
			retVal.add(Integer.toString(i));
		}		
		return retVal;
	}
	
	private static ArrayList<String> setMinutes() {
		ArrayList<String> retVal = new ArrayList<String>();
		for(int i = 0; i < 60; i++) {
			Log.i("JoeDebug", String.format("%02d", i));
			retVal.add(String.format("%02d", i));
		}		
		return retVal;
	}
	
	private static ArrayList<String> setAmPm() {
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add("AM");
		retVal.add("PM");
		return retVal;
	}
}
