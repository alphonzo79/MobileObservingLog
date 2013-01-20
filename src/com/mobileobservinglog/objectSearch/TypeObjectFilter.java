/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.objectSearch;

import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;

import com.mobileobservinglog.objectSearch.LoggedObjectFilter.LoggedFilterTypes;
import com.mobileobservinglog.support.database.ObservableObjectDAO;

public class TypeObjectFilter extends AbstractObjectFilter {
	ArrayList<String> distinctTypes;
	Context context;
	
	public TypeObjectFilter(Context context) {
		super();
		this.context = context;
		
		title = "Object Type";
		multiSelect = true;
		
		ObservableObjectDAO db = new ObservableObjectDAO(context);
		Cursor types = db.getDistinctTypes();
		types.moveToFirst();
		
		distinctTypes = new ArrayList<String>();
		for(int i = 0; i < types.getCount(); i++) {
			distinctTypes.add(types.getString(0));
			types.moveToNext();
		}
		types.close();
		db.close();
	}
	
	@Override
	public String getSqlString() {
		String retVal = "";
		
		if(filters.containsValue(true)) {
			retVal = retVal.concat("type IN (");
			
			Set<String> keys = filters.keySet();
			String inParens = "";
			for(String key : keys) {
				if(filters.get(key)) {
					if(inParens.length() != 0) {
						inParens = inParens.concat(", ");
					}
					inParens = inParens.concat("'" + key + "'");
				}
			}
			
			retVal = retVal.concat(inParens + ")");
		}
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(String type : distinctTypes) {
			if(type != null) {
				filters.put(type, false);
			}
		}
	}
}
