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

import java.util.Set;

import com.mobileobservinglog.objectSearch.LoggedObjectFilter.LoggedFilterTypes;

public class TypeObjectFilter extends AbstractObjectFilter {
	public TypeObjectFilter() {
		title = "Object Type";
		multiSelect = true;
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
					inParens = inParens.concat(key.toString());
				}
			}
			
			retVal = retVal.concat(inParens + ")");
		}
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(TypeFilterTypes type : TypeFilterTypes.values()) {
			filters.put(type.toString(), false);
		}
	}

	public enum TypeFilterTypes {
		GLOB("Globular Cluster"), OPEN_CLUST("Open Cluster"), GALAXY("Galaxy"), REFLECTION("Reflection Nebula"), EMMISSION("Emmission Nebula"), PLANETARY("Planetary Nebula"), 
		DARK_NEB("Dark Nebula"), BINARY("Binary Star"), MILKY_WAY_PATCH("Milky Way Patch"), SUPERNOVA_REM("Supernova Remnant"), ASTERISM("Asterism"); 
		
		private String filterText;
		
		TypeFilterTypes(String text) {
			this.filterText = text;
		}
		
		public String toString() {
			return filterText;
		}
	}
}
