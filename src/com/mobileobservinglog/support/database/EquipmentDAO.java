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

public class EquipmentDAO extends DatabaseHelper {

	public EquipmentDAO(Context context) {
		super(context);
	}
	
	/**
	 * Called by the TelescopesTab to get all saved telescopes in the database
	 * @return
	 */
	public Cursor getSavedTelescopes(){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM telescopes WHERE _id IS NOT NULL";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	public Cursor getSavedTelescope(int id){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM telescopes WHERE _id = '" + id + "'";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
		
		return retVal;
	}
	
	/**
	 * Called by AddTelescope page to add a telescope to the database
	 * @param type
	 * @param primaryDiameter
	 * @param focalRatio
	 * @param focalLength
	 * @return
	 */
	public boolean addTelescopeData(String type, String primaryDiameter, String focalRatio, String focalLength){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("INSERT INTO telescopes (type, primaryDiameter, focalRatio, focalLength) VALUES (?, ?, ?, ?)");
			sqlStatement.bindString(1, type);
			sqlStatement.bindString(2, primaryDiameter);
			sqlStatement.bindString(3, focalRatio);
			sqlStatement.bindString(4, focalLength);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the AddTelescopePage to update the date in a saved telescope
	 * @param id
	 * @param type
	 * @param primaryDiameter
	 * @param focalRatio
	 * @param focalLength
	 * @return
	 */
	public boolean updateTelescopeData(int id, String type, String primaryDiameter, String focalRatio, String focalLength){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "UPDATE telescopes SET type = ?, primaryDiameter = ?, focalRatio = ?, focalLength = ? where _id = " + id;
		
		SQLiteStatement sqlStatement = db.compileStatement(sql);
			sqlStatement.bindString(1, type);
			sqlStatement.bindString(2, primaryDiameter);
			sqlStatement.bindString(3, focalRatio);
			sqlStatement.bindString(4, focalLength);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the AddTelescopePage to delete a saved telescope
	 * @param id
	 * @return
	 */
	public boolean deleteTelescopeData(int id){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("DELETE from telescopes WHERE _id = " + id);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the EyepiecesTab to get all saved eyepieces in the database
	 * @return
	 */
	public Cursor getSavedEyepieces(){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM eyepieces WHERE _id IS NOT NULL";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	public Cursor getSavedEyepiece(int id){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM eyepieces WHERE _id = '" + id + "'";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
		
		return retVal;
	}
	
	/**
	 * Called by AddEyepiecee page to add an eyepiece to the database
	 * @param type
	 * @param focalLength
	 * @return
	 */
	public boolean addEyepieceData(String type, String focalLength){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("INSERT INTO eyepieces (type, focalLength) VALUES (?, ?)");
			sqlStatement.bindString(1, type);
			sqlStatement.bindString(2, focalLength);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the AddEyepiecee Page to update the data in a saved eyepiece
	 * @param id
	 * @param type
	 * @param focalLength
	 * @return
	 */
	public boolean updateEyepieceData(int id, String type, String focalLength){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "UPDATE eyepieces SET type = ?, focalLength = ? where _id = " + id;
		
		SQLiteStatement sqlStatement = db.compileStatement(sql);
			sqlStatement.bindString(1, type);
			sqlStatement.bindString(2, focalLength);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
	
	/**
	 * Called by the AddEyepiecePage to delete a saved telescope
	 * @param id
	 * @return
	 */
	public boolean deleteEyepieceData(int id){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("DELETE from eyepieces WHERE _id = " + id);
		
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
			sqlStatement.close();
			db.close();
		}
		
		return success;
	}
}
