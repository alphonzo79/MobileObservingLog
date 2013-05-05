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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import android.content.Context;

public class DatePickerMonthPicker extends NumberPickerDriver {
	DatePicker parent;
	TreeMap<String, String> maxDaysInMonth;
	static ArrayList<String> months;
	
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
		months = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.MARCH);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.APRIL);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.MAY);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.JUNE);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.JULY);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.AUGUST);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.NOVEMBER);
		months.add(sdf.format(cal.getTime()));
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		months.add(sdf.format(cal.getTime()));
		return months;
	}
	
	private void setMaxDays() {
		maxDaysInMonth = new TreeMap<String, String>();
		maxDaysInMonth.put(months.get(0), "31");
		maxDaysInMonth.put("Feb_Leap", "29");
		maxDaysInMonth.put("Feb_Norm", "28");
		maxDaysInMonth.put(months.get(2), "31");
		maxDaysInMonth.put(months.get(3), "30");
		maxDaysInMonth.put(months.get(4), "31");
		maxDaysInMonth.put(months.get(5), "30");
		maxDaysInMonth.put(months.get(6), "31");
		maxDaysInMonth.put(months.get(7), "31");
		maxDaysInMonth.put(months.get(8), "30");
		maxDaysInMonth.put(months.get(9), "31");
		maxDaysInMonth.put(months.get(10), "30");
		maxDaysInMonth.put(months.get(11), "31");
	}
	
	public void skipToValue(String value) {
		currentValue = value;
		int iteratorIndex = potentialValues.indexOf(currentValue);
		iterator = potentialValues.listIterator(iteratorIndex);
		parent.client.updateModalTextOne(currentValue);
	}
	
	public String getMaxDays(String month) {
		if(!month.equals(months.get(1))){
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
