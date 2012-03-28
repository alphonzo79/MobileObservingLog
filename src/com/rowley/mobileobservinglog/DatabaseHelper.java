package com.rowley.mobileobservinglog;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to handle all database interaction for the Mobile Observing Log app
 * 
 * @author Joe
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "mobileObservingLogDB";
	private static final int VERSION = 1;
	Context mContext;
	  
	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, VERSION);
		mContext = context;
		Log.d("JoeDebug", "DatabaseHelper constructor finish");
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.d("JoeDebug", "DatabaseHelper onCreate called from" + mContext.toString());
		
		//create tables
		db.beginTransaction();
		try {
			createTables(db);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
        	db.endTransaction();
        }
		
		//populate tables
		db.beginTransaction();
		try
		{
			populatePersistentSettings(db);
			populateAvailableCatalogs(db);
			populateObjects(db);
			db.setTransactionSuccessful();
		}
		catch (SQLException e)
		{
			Log.e("Error populating tables", e.toString());
		}
		catch (Exception e)
		{
			Log.e("Error populating tables", e.toString());
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Method used to initially create the tables for the database
	 */
	private void createTables(SQLiteDatabase db)
	{
		db.execSQL(mContext.getString(R.string.create_settings_table));
		db.execSQL(mContext.getString(R.string.create_personal_info_table));
		db.execSQL(mContext.getString(R.string.create_objects_table));
		db.execSQL(mContext.getString(R.string.create_locations_table));
		db.execSQL(mContext.getString(R.string.create_telescope_table));
		db.execSQL(mContext.getString(R.string.create_eyepiece_table));
		db.execSQL(mContext.getString(R.string.create_target_lists_table));
		db.execSQL(mContext.getString(R.string.create_target_list_items_table));
		db.execSQL(mContext.getString(R.string.create_available_catalogs_table));
	}

	/**
	 * Method used to seed values in the available catalogs table
	 * @throws Exception If the number of values don't match the number of columns in the table, we will throw
	 */
	private void populateAvailableCatalogs(SQLiteDatabase db) throws Exception
	{
		Log.d("JoeDebug", "Starting populateAvailableCatalogs");
		SQLiteStatement sqlStatement;
		
		//First, get the values and parse them into individual lines, then parse the values, 
		//create the sql statement, then execute it
		String[] settingsLines = parseResourceByLine(R.string.available_catalogs_default_values);
		
		for (int i = 0; i < settingsLines.length; i++)
		{
			String[] rowData = parseResourceByDelimiter(settingsLines[i]);
			
			//There are four columns to fill in this table
			if (rowData.length == 5)
			{
				sqlStatement = db.compileStatement("INSERT INTO availableCatalogs (catalogName, installed, numberOfObjects, description, size) " + 
						"VALUES (?, ?, ?, ?, ?)");
				for (int j = 0; j < rowData.length; j++)
				{
					sqlStatement.bindString(j + 1, rowData[j]);
				}
				sqlStatement.execute();
				sqlStatement.clearBindings();
			}
			else
			{
				Log.d("JoeDebug", "Error in populateAvailableCatalogs");
				throw new Exception("There were not 5 values to populate the catalogs table with. Values were" + rowData.toString());
			}
		}
	}

	/**
	 * Method used to seed values in the object table
	 * @throws Exception If the number of values don't match the number of columns in the table, we will throw
	 */
	private void populateObjects(SQLiteDatabase db) throws Exception
	{
		Log.d("JoeDebug", "Starting populateObjects");
		SQLiteStatement sqlStatement;
		
		//I think for mitigation of file size, we should maintain each catalog in an independent resource file. 
		//So we need to create a string array to hold each of the catalogs that we want to populate and itterate
		//through that.
		int[] availableCatalogs = {R.string.messier_catalog_default_values};
		for (int i = 0; i < availableCatalogs.length; i++)
		{
			//First, get the values and parse them into individual lines, then parse the values, 
			//create the sql statement, then execute it
			String[] settingsLines = parseResourceByLine(availableCatalogs[i]);
			
			for (int j = 0; j < settingsLines.length; j++)
			{
				String[] rowData = parseResourceByDelimiter(settingsLines[j]);
				
				//There are three columns to fill in this table
				if (rowData.length == 26)
				{
					Log.d("JoeDebug", "In Loop. rowData.length passed the test");
					
					sqlStatement = db.compileStatement("INSERT INTO objects (designation, commonName, type, magnitude, size," +
							" distance, constellation, season, rightAscension, declination, catalog, otherCatalogs," +
							" imageResource, nightImageResource) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					
					//We start this array with 1 to make it simple when using the index value. We start the count on our
					//bound arguments with 1 (it's 1-based), and we want to start with index 1 on the array because the 
					//first slot [0] is null for the autoincremented _id. We stop at 14 (<=14) because that is the number
					//of binding args we have
					for (int k = 1; k <= 14; k++)
					{
						sqlStatement.bindString(k, rowData[k]);
					}
					sqlStatement.execute();
					sqlStatement.clearBindings();
				}
				else
				{
					Log.d("JoeDebug", "Error in populateObjects");
					throw new Exception("There were not 26 values to populate the settings table with. Values were" + rowData.toString());
				}
			}
		}
	}

	/**
	 * Method used to seed data in the settings table
	 * @throws Exception If the number of values don't match the number of columns in the table, we will throw
	 */
	private void populatePersistentSettings(SQLiteDatabase db) throws Exception
	{
		Log.d("JoeDebug", "Starting populatePersistentSettings");
		SQLiteStatement sqlStatement;
		
		//First, get the values and parse them into individual lines, then parse the values, 
		//create the sql statement, then execute it
		String[] settingsLines = parseResourceByLine(R.string.settings_default_values);
		
		for (int i = 0; i < settingsLines.length; i++)
		{
			String[] rowData = parseResourceByDelimiter(settingsLines[i]);
			
			//There are three columns to fill in this table
			if (rowData.length == 3)
			{
				sqlStatement = db.compileStatement("INSERT INTO settings (settingName, settingValue, visible) VALUES (?, ?, ?)");
				for (int j = 0; j < rowData.length; j++)
				{
					sqlStatement.bindString(j + 1, rowData[j]);
				}
				sqlStatement.execute();
				sqlStatement.clearBindings();
			}
			else
			{
				Log.d("JoeDebug", "Error in populatePersistentSettings");
				throw new Exception("There were not 3 values to populate the settings table with. Values were" + rowData.toString());
			}
		}
	}
	
	/**
	 * We have initial database values saved as string resources with one line representing one row in the database.
	 * We have put in a \n character at the end of each line, and the system has given us a space to represent the 
	 * line break. We need to parse out to individual lines so we can establish default values row by row.
	 * 
	 * @param resource The resource that we want to parse
	 * @return String[] with each line from the resource in each array index
	 */
	public String[] parseResourceByLine(int resource)
	{
		String[] retVal;
		
		String fullText = mContext.getString(resource);
		retVal = fullText.split("\n");
		
		return retVal;
	}
	
	/**
	 * Once we have an array of parsed lines returned by parseResourceByLine, we need to split it into individual
	 * values to populate the database with. We have used semicolons as our delimiter.
	 * 
	 * @param resource The String[] that was returned to us from parseResourceByLine
	 * @return String[][]: Each index represents one line from the resource. Each index then contains a String[] that contains the individual values from that line
	 */
	public String[] parseResourceByDelimiter(String resource)
	{
		String[] retVal = resource.split(";", -1); //-1 on the limit so the return value will include all empty strings including one after the final delimiter
		
		return retVal;
	}

	/**
	 * This method is used to return all persistent settings in a single cursor. It's primary use is for creating
	 * and populating the listItem in the settings screen. If you want to get a single setting, use the
	 * getPersistentSettings(String setting) override.
	 * 
	 * @return A cursor containing the full contents of the settings table. This is not limited at this time because we don't expect the settings table to be very large
	 */
	public Cursor getPersistentSettings()
	{
		Cursor retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT * FROM settings WHERE settingName IS NOT NULL";
		
		retVal = db.rawQuery(sqlStatement, null);
		retVal.moveToFirst();
		
		db.close();
				
		return retVal;
	}
	
	/**
	 * This method is used to return the value of a single persistent setting. It's primary purpose is to be used on the initial screen to 
	 * set the night-mode backlight intensity, for example.
	 * 
	 * @param setting A string representing the setting to look for in the table
	 * @return A string representing the value of the desired setting 
	 */
	public String getPersistentSettings(String setting)
	{
		String retVal = null;
		
		SQLiteDatabase db = getReadableDatabase();
		String sqlStatement = "SELECT settingValue FROM settings where settingName = ?";
		
		Cursor dbCursor = db.rawQuery(sqlStatement, new String[] {setting});
		dbCursor.moveToFirst();
		retVal = dbCursor.getString(0);
		
		dbCursor.close();
		db.close();
		
		return retVal;
	}
	
	/**
	 * This method is used to populate the settings database with a new value for a given setting, and to simultaneously
	 * set that value in the settings container so it can become immediately available to other parts of the application
	 * 
	 * @param setting String representing the setting you wish to set. Preferably use the String Constants provided in the SettingsContainer
	 * @param value String representing the value to insert.
	 * @return Was the operation successful?
	 */
	public boolean setPersistentSetting(String setting, String value)
	{
		SQLiteDatabase db = getWritableDatabase();
		SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
		
		SQLiteStatement sqlStatement = db.compileStatement("UPDATE settings SET settingValue = ? WHERE settingName = ?");
		sqlStatement.bindString(1, value);
		sqlStatement.bindString(2, setting);
		
		boolean success = false;
		db.beginTransaction();
		try
		{
			Log.d("JoeTest", "SqlStatement is " + sqlStatement.toString());
			sqlStatement.execute();
			db.setTransactionSuccessful();
			settingsRef.setPersistentSetting(setting, value);
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
			db.close();
		}
		
		return success;
	}
}
