package com.mobileobservinglog.strategies;

import java.util.ArrayList;

import android.content.Context;

import com.rowley.mobileobservinglog.DatabaseHelper;
import com.rowley.mobileobservinglog.SettingsContainer;

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
