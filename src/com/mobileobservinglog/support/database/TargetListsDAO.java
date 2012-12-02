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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TargetListsDAO extends DatabaseHelper {

	public TargetListsDAO(Context context) {
		super(context);
	}
	
	public Cursor getAllTargetLists() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT _id, listName, listDescription FROM targetLists";
		
		Cursor rs = db.rawQuery(sql, null);
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	public Cursor getTargetListCount(String listName) {
		SQLiteDatabase db = getReadableDatabase();
		String stmt = "SELECT count(*) FROM targetListItems WHERE list = ?";
		
		Cursor rs = db.rawQuery(stmt, new String[]{listName});
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	public Cursor getTargetListItems(String listName) {
		SQLiteDatabase db = getReadableDatabase();
		String stmt = "SELECT objectDesignation FROM targetListItems WHERE list = ?";
		Cursor items = db.rawQuery(stmt, new String[]{listName});
		
		Cursor rs = null;
		int count = items.getCount();
		if(count > 0) {
			String[] itemList = new String[items.getCount()];
			items.moveToFirst();
			for(int i = 0; i < count; i++) {
				itemList[i] = items.getString(0);
				items.moveToNext();
			}
			String objStmt = "SELECT designation, constellation, type, magnitude, logged FROM objects WHERE designation IN ? ORDER BY designation ASC";
			rs = db.rawQuery(objStmt, itemList);
			rs.moveToFirst();
		}
		return rs;
	}
}
