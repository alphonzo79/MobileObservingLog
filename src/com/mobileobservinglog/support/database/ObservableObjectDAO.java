/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support.database;

import com.mobileobservinglog.objectSearch.ObjectIndexFilter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ObservableObjectDAO extends DatabaseHelper {

	public ObservableObjectDAO(Context context) {
		super(context);
	}
	
	public Cursor getUnfilteredObjectList_Catalog(String catalogName){
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT designation, constellation, type, magnitude, logged FROM objects WHERE catalog = ?";
		
		Cursor rs = db.rawQuery(sql, new String[]{catalogName});
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	public Cursor getFilteredObjectList() {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor rs = db.rawQuery(ObjectIndexFilter.getReference(mContext).getSqlString(), null);
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	public Cursor getObjectData(String designation) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM objects WHERE designation = ?";
		
		Cursor rs = db.rawQuery(sql, new String[]{designation});
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
}
