package com.mobileobservinglog.strategies;

import java.util.ArrayList;

import com.rowley.mobileobservinglog.DatabaseHelper;
import com.rowley.mobileobservinglog.SettingsContainer;

import android.content.Context;

public class SearchModePicker extends NumberPickerDriver {

	public SearchModePicker(ArrayList<String> values, String currentValue, Context context) {
		super(values, currentValue, context);
	}
	
	@Override
	public boolean save(){
		DatabaseHelper db = new DatabaseHelper(context);
		if (db.setPersistentSetting(SettingsContainer.SEARCH_MODE, currentValue)){
			return true;
		}
		else{
			return false;
		}
	}

}
