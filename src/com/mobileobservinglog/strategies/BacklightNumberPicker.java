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

import com.mobileobservinglog.DatabaseHelper;
import com.mobileobservinglog.SettingsContainer;

public class BacklightNumberPicker extends NumberPickerDriver {
	
	/**
	 * Return a BacklightNumberPicker object that can be used to control the button actions on the number picker
	 * widget. Input the possible range of values as an array list of strings, along with the currently-set value
	 * @param values
	 * @param currentValue
	 * @param context
	 */
	public BacklightNumberPicker(ArrayList<String> values, String currentValue, Context context){
		super(values, currentValue, context);
	}

	public boolean save() {
		DatabaseHelper db = new DatabaseHelper(context);
		if (db.setPersistentSetting(SettingsContainer.NM_BACKLIGHT, currentValue)){
			return true;
		}
		else{
			return false;
		}
	}

}
