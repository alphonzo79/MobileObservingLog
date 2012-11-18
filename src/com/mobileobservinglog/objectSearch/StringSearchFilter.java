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

import com.mobileobservinglog.support.database.CatalogsDAO;

import android.content.Context;
import android.database.Cursor;

public class StringSearchFilter implements FilterBasicStrategy {
	private String searchText;
	private Context context;
	private boolean filterIsSet;
	
	public StringSearchFilter(Context context) {
		searchText = "";
		this.context = context;
		filterIsSet = false;
	}
	
	public void setSearchString(String text) {
		this.searchText = text;
		filterIsSet = true;
	}
	
	public String getSearchDescription() {
		return searchText;
	}
	
	public String getSqlString() {
		String retVal = "";
		
		//First make sure we have a search string
		if(searchText.length() > 0) {
			//Split into individual strings
			String[] terms = searchText.split(",");
			for(int i = 0; i < terms.length; i++) {
				terms[i] = terms[i].trim();
			}
			
			String[] columns = new String[]{"designation", "commonName", "type", "constellation", "season", "catalog", "otherCatalogs", "logLocation", "viewingNotes"};
			String builtTerms = "";
			for(int i = 0; i < columns.length; i++) {
				for(int j = 0; j < terms.length; j++) {
					if(builtTerms.length() > 0) {
						builtTerms = builtTerms.concat(" OR ");
					}
					builtTerms = builtTerms.concat(columns[i] + " LIKE '%" + terms[j] + "%'");
				}
			}
			retVal = retVal.concat(builtTerms);
		}
		
		return retVal;
	}
	
	private ArrayList<String> getInstalledCatalogsList() {
		ArrayList<String> catalogs = new ArrayList<String>();
		
		CatalogsDAO db = new CatalogsDAO(context);
		Cursor catsCursor = db.getInstalledCatalogs();
		
		catsCursor.moveToFirst();
		for(int i = 0; i < catsCursor.getCount(); i++) {
			catalogs.add(catsCursor.getString(0));
        	
			catsCursor.moveToNext();
        }
		
		catsCursor.close();
		db.close();
		
		return catalogs;
	}

	public boolean isSet() {
		return filterIsSet;
	}

	public void resetFilter() {
		searchText = "";
		filterIsSet = false;
	}
}
