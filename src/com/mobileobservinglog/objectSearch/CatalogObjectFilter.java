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
import android.util.Log;

import com.mobileobservinglog.support.database.CatalogsDAO;

public class CatalogObjectFilter extends AbstractObjectFilter {
	ArrayList<String> installedCatalogs;
	Context context;
	
	public CatalogObjectFilter(Context context) {
		super();
		title = "Catalog";
		multiSelect = true;
		
		CatalogsDAO db = new CatalogsDAO(context);
		Cursor catalogs = db.getInstalledCatalogs();
		
		catalogs.moveToFirst();
		
		installedCatalogs = new ArrayList<String>();
		for (int i = 0; i < catalogs.getCount(); i++)
        {
			Log.d("JoeDebug", "cursor size is " + catalogs.getCount());
			installedCatalogs.add(catalogs.getString(0));
        	
        	catalogs.moveToNext();
        }
		catalogs.close();
		db.close();
	}

	@Override
	public String getSearchDescription() {
		String retVal = "";
		
		if(filters.containsValue(true)) {
			Set<String> filterKeys = filters.keySet();
			for(String key : filterKeys) {
				if(filters.get(key)) {
					if(retVal.length() != 0) {
						retVal = retVal.concat(", ");
					}
					retVal = retVal.concat(key);
				}
			}
		}
		
		return retVal;
	}
	
	@Override
	public boolean isSet() {
		return true;
	}

	@Override
	public String getSqlString() {
		String retVal = "catalog IN (";
		
		Set<String> keys = filters.keySet();
		String inParens = "";
		if(filters.containsValue(true)) { //If we've filtered on catalog return according to the filter
			for(String key : keys) {
				if(filters.get(key)) {
					if(inParens.length() != 0) {
						inParens = inParens.concat(", ");
					}
					inParens = inParens.concat("'" + key + "'");
				}
			}
		} else { //Otherwise return all installed catalogs (filtering out catalogs that are not installed, but exist in the DB
			for(String key : keys) {
				if(inParens.length() != 0) {
					inParens = inParens.concat(", ");
				}
				inParens = inParens.concat("'" + key + "'");
			}
		}
		retVal = retVal.concat(inParens + ")");
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(String catalog : installedCatalogs) {
			filters.put(catalog, false);
		}
	}
}
