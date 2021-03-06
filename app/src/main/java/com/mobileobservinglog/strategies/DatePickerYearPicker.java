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

public class DatePickerYearPicker extends NumberPickerDriver {
	DatePicker parent;
	
	public DatePickerYearPicker(DatePicker parent, String currentValue, Context context) {
		super(setYears(), currentValue, context);
		this.parent = parent;
	}

	public DatePickerYearPicker(ArrayList<String> values, String currentValue,
			Context context) {
		super(values, currentValue, context);
	}
	
	private static ArrayList<String> setYears() {
		ArrayList<String> retVal = new ArrayList<String>();
		for(int i = 1900; i <= 2100; i++) {
			retVal.add(Integer.toString(i));
		}
		return retVal;
	}
	
	public boolean isLeapYear() {
		boolean leapYear = false;
		int year = Integer.parseInt(getCurrentValue());
		if(year % 4 == 0) {
			leapYear = true;
			if(year % 100 == 0 && year % 400 != 0) {
				leapYear = false;
			}
		}
		return leapYear;
	}
	
	@Override
	public void upButton() {
		super.upButton();
		if(Integer.parseInt(parent.day.getCurrentValue()) > Integer.parseInt(parent.month.getMaxDays(parent.month.getCurrentValue()))) {
			parent.day.skipToValue(parent.month.getMaxDays(parent.month.getCurrentValue()));
		}
	}
	
	@Override
	public void downButton() {
		super.downButton();
		if(Integer.parseInt(parent.day.getCurrentValue()) > Integer.parseInt(parent.month.getMaxDays(parent.month.getCurrentValue()))) {
			parent.day.skipToValue(parent.month.getMaxDays(parent.month.getCurrentValue()));
		}
	}

	@Override
	public boolean save() {
		return false;
	}

}
