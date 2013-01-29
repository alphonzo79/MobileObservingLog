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

public class FavoriteObjectFilter extends AbstractObjectFilter {
	public FavoriteObjectFilter() {
		title = "Favorite";
		multiSelect = true;
	}

	@Override
	public String getSqlString() {
		String retVal = "";
		
		if(filters.get(FavoriteFilterTypes.FAVORITE.toString())) {
			retVal = retVal.concat("favorite = 'true'");
		}
		if(filters.get(FavoriteFilterTypes.NOT_FAVORITE.toString())) {
			if(retVal.length() != 0) {
				retVal = retVal.concat(" OR ");
			}
			retVal = retVal.concat("favorite IS NOT 'true'"); //this value may be null or false (initialized to null)
		}
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(FavoriteFilterTypes type : FavoriteFilterTypes.values()) {
			filters.put(type.toString(), false);
		}
	}

	public enum FavoriteFilterTypes {
		FAVORITE("Favorite"), NOT_FAVORITE("Not Favorite");
		
		private String filterText;
		
		FavoriteFilterTypes(String text) {
			this.filterText = text;
		}
		
		public String toString() {
			return filterText;
		}
	}
}
