package com.rowley.strategies;

import java.util.ArrayList;

import com.rowley.mobileobservinglog.DatabaseHelper;
import com.rowley.mobileobservinglog.SettingsContainer;

import android.content.Context;

public class GpsModePicker extends NumberPickerDriver {

	public GpsModePicker(ArrayList<String> values, String currentValue,
			Context context) {
		super(values, currentValue, context);
	}
	
	@Override
	public boolean save(){
		DatabaseHelper db = new DatabaseHelper(context);
		if (db.setPersistentSetting(SettingsContainer.USE_GPS, currentValue)){
			return true;
		}
		else{
			return false;
		}
	}

}
