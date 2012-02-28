package com.rowley.strategies;

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
