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
import java.util.ListIterator;

import android.content.Context;

public abstract class NumberPickerDriver {
	
	ArrayList<String> potentialValues;
	String currentValue;
	ListIterator<String> iterator;
	Context context;
	
	public NumberPickerDriver(ArrayList<String> values, String currentValue, Context context){
		potentialValues = values;
		setCurrentValue(currentValue);
		int iteratorIndex = potentialValues.indexOf(currentValue);
		if(iteratorIndex < 0 || iteratorIndex >= potentialValues.size()) {
			iteratorIndex = 0;
		}
		iterator = potentialValues.listIterator(iteratorIndex);
		this.context = context;
	}

	public void upButton() {
		if (iterator.hasNext()){
			incrementList();
		}
		else{
			iterator = potentialValues.listIterator(0);
			incrementList();
		}
	}

	public void downButton() {
		if (iterator.hasPrevious()){
			decrementList();
		}
		else{
			iterator = potentialValues.listIterator(potentialValues.size());
			decrementList();
		}
	}

	public String getCurrentValue() {
		return currentValue;
	}
	
	private void setCurrentValue(String value){
		currentValue = value;
	}
	
	public abstract boolean save();
	
	//cancel is handled in the client class since no real action is required and closing the modal is 
	//a universal action

	private void incrementList() {
		String temp = (String) iterator.next();
		//Avoid dead clicks by advancing the iterator an extra time if we need to
		//this is necessary on the first click, and when changing directions
		if (temp.equals(currentValue)){
			//Also, we must do our bounds check again
			if (!iterator.hasNext()){
				iterator = potentialValues.listIterator(0);
			}
			temp = (String) iterator.next();
		}
		setCurrentValue(temp);
	}

	private void decrementList() {
		String temp = (String) iterator.previous();
		//Avoid dead clicks by advancing the iterator an extra time if we need to
		//this is necessary on the first click, and when changing directions
		if (temp.equals(currentValue)){
			//Also, we must do our bounds check again
			if (!iterator.hasPrevious()){
				iterator = potentialValues.listIterator(potentialValues.size());
			}
			temp = (String) iterator.previous();
		}
		setCurrentValue(temp);
	}
}
