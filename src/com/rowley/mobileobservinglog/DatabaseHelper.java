package com.rowley.mobileobservinglog;

import java.util.UnknownFormatConversionException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
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
		String sqlStatement;
		
		//First, get the values and parse them into individual lines, then parse the values, 
		//create the sql statement, then execute it
		String[] settingsLines = parseResourceByLine(R.string.available_catalogs_default_values);
		
		for (int i = 0; i < settingsLines.length; i++)
		{
			String[] rowData = parseResourceByDelimiter(settingsLines[i]);
			
			//There are four columns to fill in this table
			if (rowData.length == 4)
			{
				sqlStatement = String.format("INSERT INTO availableCatalogs (catalogName, installed, numberOfObjects, description) " + 
						"VALUES ('%s', '%s', '%s', '%s')", rowData[0], rowData[1], rowData[2], rowData[3]);
				Log.d("JoeDebug", sqlStatement);
				db.execSQL(sqlStatement);
			}
			else
			{
				Log.d("JoeDebug", "Error in populateAvailableCatalogs");
				throw new Exception("There were not 4 values to populate the settings table with. Values were" + rowData.toString());
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
		String sqlStatement;
		
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
					
					for (int k = 0; k < rowData.length; k++)
					{
						Log.d("JoeDebug", "rowData[" + k + "]: " + String.format("'%s'", rowData[k]));
					}
					
					sqlStatement = String.format("INSERT INTO objects (designation, commonName, type, magnitude, size," +
							" distance, constellation, season, rightAscension, declination, catalog, otherCatalogs," +
							" imageResource, nightImageResource) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s'," +
							" '%s', '%s', '%s', '%s', '%s', '%s', '%s')", rowData[1], rowData[2], rowData[3], rowData[4], 
							rowData[5], rowData[6], rowData[7], rowData[8], rowData[9], rowData[10], rowData[11], 
							rowData[12], rowData[13], rowData[14]);
					Log.d("JoeDebug", sqlStatement);
					db.execSQL(sqlStatement);
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
		String sqlStatement;
		
		//First, get the values and parse them into individual lines, then parse the values, 
		//create the sql statement, then execute it
		String[] settingsLines = parseResourceByLine(R.string.settings_default_values);
		
		for (int i = 0; i < settingsLines.length; i++)
		{
			String[] rowData = parseResourceByDelimiter(settingsLines[i]);
			
			//There are three columns to fill in this table
			if (rowData.length == 3)
			{
				sqlStatement = String.format("INSERT INTO settings (settingName, settingValue, visible) VALUES ('%s', '%s', '%d')", rowData[0], rowData[1], Integer.parseInt(rowData[2]));
				Log.d("JoeDebug", sqlStatement);
				db.execSQL(sqlStatement);
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

}
