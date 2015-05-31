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
import java.util.TreeMap;

import android.content.Context;

public class DatePickerMonthPicker extends NumberPickerDriver {
	DatePicker parent;
	TreeMap<String, String> maxDaysInMonth;
	
	public DatePickerMonthPicker(DatePicker parent, String currentValue, Context context) {
		super(setMonths(), currentValue, context);
		this.parent = parent;
		setMaxDays();
	}

	public DatePickerMonthPicker(ArrayList<String> values, String currentValue,
			Context context) {
		super(values, currentValue, context);
	}

	private static ArrayList<String> setMonths() {
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add("Jan");
		retVal.add("Feb");
		retVal.add("Mar");
		retVal.add("Apr");
		retVal.add("May");
		retVal.add("Jun");
		retVal.add("Jul");
		retVal.add("Aug");
		retVal.add("Sep");
		retVal.add("Oct");
		retVal.add("Nov");
		retVal.add("Dec");
		return retVal;
	}
	
	private void setMaxDays() {
		maxDaysInMonth = new TreeMap<String, String>();
		maxDaysInMonth.put("Jan", "31");
		maxDaysInMonth.put("Feb_Leap", "29");
		maxDaysInMonth.put("Feb_Norm", "28");
		maxDaysInMonth.put("Mar", "31");
		maxDaysInMonth.put("Apr", "30");
		maxDaysInMonth.put("May", "31");
		maxDaysInMonth.put("Jun", "30");
		maxDaysInMonth.put("Jul", "31");
		maxDaysInMonth.put("Aug", "31");
		maxDaysInMonth.put("Sep", "30");
		maxDaysInMonth.put("Oct", "31");
		maxDaysInMonth.put("Nov", "30");
		maxDaysInMonth.put("Dec", "31");
	}
	
	public void skipToValue(String value) {
		currentValue = value;
		int iteratorIndex = potentialValues.indexOf(currentValue);
		iterator = potentialValues.listIterator(iteratorIndex);
		parent.client.updateModalTextOne(currentValue);
	}
	
	public String getMaxDays(String month) {
		if(!month.equals("Feb")){
			return maxDaysInMonth.get(month);
		} else {
			if(parent.year.isLeapYear()) {
				return maxDaysInMonth.get("Feb_Leap");
			} else {
				return maxDaysInMonth.get("Feb_Norm");
			}
		}
	}
	
	@Override
	public void upButton() {
		super.upButton();
		if(Integer.parseInt(parent.day.getCurrentValue()) > Integer.parseInt(getMaxDays(currentValue))) {
			parent.day.skipToValue(getMaxDays(currentValue));
		}
	}
	
	@Override
	public void downButton() {
		super.downButton();
		if(Integer.parseInt(parent.day.getCurrentValue()) > Integer.parseInt(getMaxDays(currentValue))) {
			parent.day.skipToValue(getMaxDays(currentValue));
		}
	}

	@Override
	public boolean save() {
		return false;
	}

}
