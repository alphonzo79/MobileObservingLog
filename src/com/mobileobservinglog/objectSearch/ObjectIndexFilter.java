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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.util.Log;

public class ObjectIndexFilter implements ObjectFilter, TextSearch{
	private boolean filterIsSet;
	ObjectFilter[] filters;
	StringSearchFilter searchFilter;
	Context context;
	
	private ObjectIndexFilter(Context context){
		filters = new ObjectFilter[]{new LoggedObjectFilter(), new TypeObjectFilter(), new CatalogObjectFilter(context), 
				new MagnitudeObjectFilter(), new SeasonObjectFilter(), new ConstellationObjectFilter()
		};
		searchFilter = new StringSearchFilter(context);
		this.context = context;
		resetFilter();
	}
	
	private static ObjectIndexFilter ref;
	public static synchronized ObjectIndexFilter getReference(Context context){
		if(ref == null){
			ref = new ObjectIndexFilter(context);
		}
		return ref;
	}
	
	public void resetFilter(){
		filterIsSet = false;
		for(ObjectFilter filter : filters) {
			filter.resetFilter();
		}
		searchFilter.resetFilter();
	}

	public boolean isSet(){
		return filterIsSet;
	}

	public String getSearchDescription() {
		String retVal = "";
		
		for(ObjectFilter filter : filters) {
			if(filter.isSet()) {
				String description = filter.getSearchDescription();
				if(description.length() > 0) {
					if(retVal.length() > 0) {
						retVal = retVal.concat("; "); //add a comma if stuff is already in there, but only if we got a non-empty string from this filter
					}
					retVal = retVal.concat(description);
				}
			}
		}
		
		if(searchFilter.isSet()) {
			String searchText = searchFilter.getSearchDescription();
			if(searchText.length() > 0) {
				if(retVal.length() > 0) {
					retVal = retVal.concat("; "); //add a comma if stuff is already in there, but only if we got a non-empty string from this filter
				}
				retVal = retVal.concat("Text Search: " + searchText);
			}
		}
		
		if(retVal.equals("")) {
			retVal = "None";
		}
		
		return retVal;
	}

	public String getSqlString() { 
		String stmt = "SELECT designation, constellation, type, magnitude, logged FROM objects WHERE ";
		
		String filterStmt = "";
		for(ObjectFilter filter : filters) {
			if(filter.isSet()) {
				String segment = filter.getSqlString();
				if(segment.length() > 0) { //Only if we got something good back
					if(filterStmt.length() > 0) { //Only add the AND if we have previously put something in here
						filterStmt = filterStmt.concat(" AND ");
					}
					filterStmt = filterStmt.concat(segment);
				}
			}
		}
		stmt = stmt.concat(filterStmt);
		
		String textSearchStmt = searchFilter.getSqlString();
		if(!textSearchStmt.equals("")) {
			if(!filterStmt.equals("")) {
				textSearchStmt = " AND (" + textSearchStmt + ")"; 
			}
			stmt = stmt.concat(textSearchStmt);
		}
		
		stmt = stmt.concat(";");
		Log.d("JoeDebug", "ObjectIndexFilter Sql String: " + stmt);
		
		return stmt;
	}

	public void setFilter(TreeMap<String, Boolean> filters) {
		if(!filterIsSet) {
			filterIsSet = true;
		}
		
		if(filters != null && filters.size() > 0) {
			for(ObjectFilter filter : this.filters){
				filter.setFilter(filters);
			}
		}
	}

	public ArrayList<ObjectFilterInformation> getFilterInfo() {
		ArrayList<ObjectFilterInformation> retVal = new ArrayList<ObjectFilterInformation>();
		
		for(ObjectFilter filter : filters) {
			retVal.addAll(filter.getFilterInfo());			
		}
		
		return retVal;
	}
	
	public String getStringSearchText() {
		return searchFilter.getSearchDescription();
	}
	
	public void setStringSearchText(String text) {
		searchFilter.setSearchString(text);
	}
}
