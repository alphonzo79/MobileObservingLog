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

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class CatalogsDAO extends DatabaseHelper {

	public CatalogsDAO(Context context) {
		super(context);
	}

	/**
	 * This method is used to return the list of installed/available catalogs in a single cursor to be parsed by the client
	 * 
	 * @return A cursor containing the full contents of the available catalogs table.
	 */
	public Cursor getAvailableCatalogs()
	{
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM availableCatalogs WHERE catalogName IS NOT NULL";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	public Cursor getInstalledCatalogs() {
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT catalogName FROM availableCatalogs WHERE installed = ?;";
		
		retVal = db.rawQuery(sqlStatement, new String[]{"Yes"});
		retVal.moveToFirst();
		
		db.close();
		
		return retVal;
	}
	
	public Cursor getCatalogSpecs(String catalogName){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM availableCatalogs WHERE catalogName = ?";
		
		retVal = db.rawQuery(sqlStatement, new String[]{catalogName});
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}

	/**
	 * This method is used to update the database for whether a catalog has been installed or not for the given catalogs. It is used primarily by AvailableCatalogsTab.java and
	 * InstalledCatalogsTab.java installing or removing catalogs
	 * 
	 * @return
	 */
	public boolean updateAvailableCatalogsInstalled(String catalog, String installed)
	{
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		SQLiteStatement sqlStatement = db.compileStatement("UPDATE availableCatalogs SET installed = ? WHERE catalogName = ?");
		sqlStatement.bindString(1, installed);
		sqlStatement.bindString(2, catalog);
		
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
	 * This method is used to return the list of image paths from the databse for the given catalogs. It is used primarily by AvailableCatalogsTab.java and
	 * InstalledCatalogsTab.java to get the images we need to download and save or delete
	 * 
	 * @return A cursor containing the list of image paths for the given catalogs.
	 */
	public Cursor getImagePaths(ArrayList<String> catalogs)
	{
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT imageResource, nightImageResource FROM objects WHERE catalog ='" + catalogs.get(0);
		
		for (int i = 1; i < catalogs.size(); i++){
			sqlStatement = sqlStatement + "' OR '" + catalogs.get(i);
		}
		
		sqlStatement += "'";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	public int getNumLogged(String catalogName){
		int retVal = 0;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT count(*) AS count FROM objects WHERE logged = 'Yes' AND catalog = ?;";
		
		Cursor rs = db.rawQuery(sql, new String[]{catalogName});
		rs.moveToFirst();
		
		retVal = rs.getInt(0);
		
		rs.close();
		db.close();
				
		return retVal;
	}
}
