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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class LocationsDAO extends DatabaseHelper {

	public LocationsDAO(Context context) {
		super(context);
	}
	
	/**
	 * Called by the ManageLocations screen to get all saved observing locations in the database
	 * @return
	 */
	public Cursor getSavedLocations(){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM locations WHERE _id IS NOT NULL";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	public Cursor getSavedLocation(int id){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM locations WHERE _id = '" + id + "'";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
		
		return retVal;
	}
	
	/**
	 * Called by ManageLocations screen to add a location to the database
	 * @param locationName
	 * @param coordinates
	 * @param description
	 * @return
	 */
	public boolean addLocationData(String locationName, String coordinates, String description){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("INSERT INTO locations (locationName, coordinates, description) VALUES (?, ?, ?)");
			sqlStatement.bindString(1, locationName);
			sqlStatement.bindString(2, coordinates);
			sqlStatement.bindString(3, description);
		
		db.beginTransaction();
		try
		{
			sqlStatement.execute();
			db.setTransactionSuccessful();
			success = true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the ManageLocations screen to update a location in the database
	 * @param id
	 * @param locationName
	 * @param coordinates
	 * @param description
	 * @return
	 */
	public boolean updateLocationData(int id, String locationName, String coordinates, String description){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "UPDATE locations SET locationName = ?, coordinates = ?, description = ? where _id = ?";
		
		SQLiteStatement sqlStatement = db.compileStatement(sql);
			sqlStatement.bindString(1, locationName);
			sqlStatement.bindString(2, coordinates);
			sqlStatement.bindString(3, description);
			sqlStatement.bindLong(4, id);
		
		db.beginTransaction();
		try
		{
			sqlStatement.execute();
			db.setTransactionSuccessful();
			success = true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the ManageLocations Screen to delete a saved location
	 * @param id
	 * @return
	 */
	public boolean deleteLocationData(int id){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("DELETE from locations WHERE _id = ?");
		sqlStatement.bindLong(1, id);
		
		db.beginTransaction();
		try
		{
			sqlStatement.execute();
			db.setTransactionSuccessful();
			success = true;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
		return success;
	}
}
