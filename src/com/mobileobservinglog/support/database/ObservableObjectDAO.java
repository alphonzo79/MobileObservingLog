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

import java.util.TreeMap;

import com.mobileobservinglog.objectSearch.ObjectIndexFilter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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
	
	public Cursor getObjectData(int id) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM objects WHERE _id = ?";
		
		Cursor rs = db.rawQuery(sql, new String[]{Integer.toString(id)});
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	public boolean clearLogData(int id) {
		boolean success = false;
		
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement("UPDATE objects SET logged = ?, logDate = ?, logTime = ?, logLocation = ?, equipment = ?, " +
				"seeing = ?, transparency = ?, findingMethod = ?, viewingNotes = ? WHERE _id = ?");
		stmt.bindString(1, "false");
		stmt.bindNull(2);
		stmt.bindNull(3);
		stmt.bindNull(4);
		stmt.bindNull(5);
		stmt.bindNull(6);
		stmt.bindNull(7);
		stmt.bindNull(8);
		stmt.bindNull(9);
		stmt.bindLong(10, id);
		
		db.beginTransaction();
		try
		{
			stmt.execute();
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
			stmt.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Update log data. Put the new values into a tree map that may contain the keys logged, logDate, logTime, logLocation, telescope, eyepiece, seeing, 
	 * transparency, findingMethod, viewingNotes. If any of the values are left out they will not be included in the update.
	 * @param id
	 * @param values may contain the keys logged, logDate, logTime, logLocation, telescope, eyepiece, seeing, 
	 * transparency, findingMethod, viewingNotes. Any other values will be ignored
	 * @return
	 */
	public boolean updateLogData(int id, TreeMap<String, String> values, boolean updateOtherCatalogs) {
		//We go to all the work of two for loops so we can only update the data that has been changed.
		boolean success = false;
		String[] possibleValues = new String[]{"logged", "logDate", "logTime", "logLocation", "equipment", "seeing", "transparency", "findingMethod", "viewingNotes"};				
		String updateStatement = "UPDATE objects SET ";
		String innerSet = "";
		for(int i = 0; i < possibleValues.length; i++) {
			if(values.get(possibleValues[i]) != null) {
				if(innerSet.length() > 0) {
					innerSet = innerSet.concat(", ");
				}
				innerSet = innerSet.concat(possibleValues[i] + " = ?");
			}
		}		
		
		if(updateOtherCatalogs) {
			Cursor objectData = getObjectData(id);
			objectData.moveToFirst();
			String otherCats = objectData.getString(13);
			objectData.close();
			
			String[] catsSplit = otherCats.split(",");
			int[] otherIds = new int[catsSplit.length];
			
			for(int i = 0; i < catsSplit.length; i++) {
				Cursor otherCatalogData = getObjectData(catsSplit[i].trim());
				otherCatalogData.moveToFirst();
				if(otherCatalogData.getCount() > 0) {
					otherIds[i] = otherCatalogData.getInt(0);
				}
				otherCatalogData.close();
			}
			
			updateStatement = updateStatement.concat(innerSet + " WHERE _id IN (");
			
			String inBuilder = Integer.toString(id);
			for(int thisId : otherIds) {
				if(thisId > 0) {
					inBuilder = inBuilder.concat(", " + thisId);
				}
			}
			
			updateStatement = updateStatement.concat(inBuilder + ");");
		} else {
			updateStatement = updateStatement.concat(innerSet + " WHERE _id = " + id + ";");
		}
		
		if(innerSet.length() > 0) {
			SQLiteDatabase db = getWritableDatabase();
			SQLiteStatement stmt = db.compileStatement(updateStatement);
			int bindCounter = 1;
			for(int i = 0; i < possibleValues.length; i++) {
				String thisValue = possibleValues[i];
				if(values.get(thisValue) != null) {
					if(thisValue.equals("seeing") || thisValue.equals("transparency")){
						stmt.bindLong(bindCounter, Long.parseLong(values.get(thisValue)));
					} else {
						stmt.bindString(bindCounter, values.get(thisValue));
					}
					bindCounter++;
				}
			}
			
			db.beginTransaction();
			try
			{
				stmt.execute();
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
				stmt.close();
				db.close();
			}
		}
		
		return success;
	}
	
	/**
	 * Overload to allow us to update based on designation instead of id -- used primarily in restoring data. We don't back up the ID because that is managed by the DB
	 * and is not guaranteed to be consistent from installation to installation.
	 * 
	 * This method will hit the database to get the id, then pass off the work to it's overloaded brother. This may not be the most performant way to do this, but backup
	 * and restore are uncommon activities, so we don't mind taking the performance hit here.
	 * @param designation
	 * @param values
	 * @param updateOtherCatalogs
	 * @return
	 */
	public boolean updateLogData(String designation, TreeMap<String, String> values) {
		Cursor fetched = getObjectData(designation);
		int id = fetched.getInt(0);
		fetched.close();
		return updateLogData(id, values, false);
	}
	
	public boolean setFavorite(int id, boolean fav) {
		boolean success = false;
		
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement("UPDATE objects SET favorite = ? WHERE _id = ?;");
		stmt.bindString(1, Boolean.toString(fav).toLowerCase());
		stmt.bindLong(2, id);
		
		db.beginTransaction();
		try
		{
			stmt.execute();
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
			stmt.close();
			db.close();
		}
		
		return success;
	}
	
	public Cursor getDistinctTypes() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT DISTINCT type FROM objects;";
		
		Cursor rs = db.rawQuery(sql, null);
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
	
	/**
	 * Return the designation and log data for the full object data table. This is meant to be used in backing up the data. We retrieve the designation and not
	 * the id because this workflow inherently assumes that the user may be restoring to a different installation of the app and IDs, being managed by the DB, may
	 * not be the same from installation to installation.
	 * @return
	 */
	public Cursor getAllLogData() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT designation, logged, logDate, logTime, logLocation, equipment, seeing, transparency, favorite, findingMethod, viewingNotes FROM objects";
		
		Cursor rs = db.rawQuery(sql, null);
		rs.moveToFirst();
		
		db.close();
		return rs;
	}
}
