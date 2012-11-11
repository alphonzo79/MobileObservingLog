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

import android.content.Context;

public class MagnitudeObjectFilter extends AbstractObjectFilter {
	public MagnitudeObjectFilter() {
		title = "Minimum Magnitude";
		multiSelect = false;
	}

	@Override
	public String getSqlString() {
		String retVal = "";
		
		if(filters.containsValue(true)) {
			//Not multi-select. This is the minimum limiting magnitude
			retVal = retVal.concat("magnitude <= ");
			
			Set<String> keys = filters.keySet();
			for(String key : keys) {
				if(filters.get(key)) {
					retVal = retVal.concat(key.toString());
					break; //guarantee only one is entered
				}
			}
		}
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(MagnitudeFilterTypes type : MagnitudeFilterTypes.values()) {
			filters.put(type.toString(), false);
		}
	}

	public enum MagnitudeFilterTypes {
		ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), ELEVEN("11"), 
		TWELVE("12"), THIRTEEN("13"), FOURTEEN("14"); 
		
		private String filterText;
		
		MagnitudeFilterTypes(String text) {
			this.filterText = text;
		}
		
		public String toString() {
			return filterText;
		}
	}
}
