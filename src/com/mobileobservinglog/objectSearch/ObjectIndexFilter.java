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

public class ObjectIndexFilter implements ObjectFilter {
	private boolean filterIsSet;
	ObjectFilter[] filters;
	
	private ObjectIndexFilter(){
		filters = new ObjectFilter[]{new LoggedObjectFilter(), new TypeObjectFilter(), new MagnitudeObjectFilter(), 
				new ConstellationObjectFilter(), new SeasonObjectFilter(), new CatalogObjectFilter()
		};
		resetFilter();
	}
	
	private static ObjectIndexFilter ref;
	public static synchronized ObjectIndexFilter getReference(){
		if(ref == null){
			ref = new ObjectIndexFilter();
		}
		return ref;
	}
	
	public void resetFilter(){
		filterIsSet = false;
		for(ObjectFilter filter : filters) {
			filter.resetFilter();
		}
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
						retVal = retVal.concat(", "); //add a comma if stuff is already in there, but only if we got a non-empty string from this filter
					}
					retVal = retVal.concat(description);
				}
			}
		}
		
		return retVal;
	}

	public String getSqlString() {
		String stmt = "SELECT * FROM objects WHERE";
		
		String theRest = "";
		for(ObjectFilter filter : filters) {
			if(filter.isSet()) {
				String segment = filter.getSqlString();
				if(segment.length() > 0) { //Only if we got something good back
					if(theRest.length() > 0) { //Only add the AND if we have previously put something in here
						theRest = theRest.concat(" AND ");
					}
					theRest = theRest.concat(segment);
				}
			}
		}
		stmt = stmt.concat(theRest + ";");
		
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
}
