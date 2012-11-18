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

public class PersonalInfoDAO extends DatabaseHelper {

	public PersonalInfoDAO(Context context) {
		super(context);
	}
	
	/**
	 * Called by the PersonalLayout screens to get the saved personal information in the database
	 * @return
	 */
	public Cursor getPersonalInfo(){
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM personalInfo WHERE _id = 1";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	/**
	 * Called by the PersonalInfo screen to update the personal info in the database
	 * @param id
	 * @param locationName
	 * @param coordinates
	 * @param description
	 * @return
	 */
	public boolean updatePersonalInfoData(String fullName, String address, String phone, String eMail, String localClub){
		boolean success = false;
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "UPDATE personalInfo SET fullName = ?, address = ?, phone = ?, eMail = ?, localClub= ? where _id = 1";
		
		SQLiteStatement sqlStatement = db.compileStatement(sql);
			sqlStatement.bindString(1, fullName);
			sqlStatement.bindString(2, address);
			sqlStatement.bindString(3, phone);
			sqlStatement.bindString(4, eMail);
			sqlStatement.bindString(5, localClub);
		
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
