package com.rowley.mobileobservinglog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
	  
	public DatabaseHelper(Context context, CursorFactory factory) 
	{
		super(context, DATABASE_NAME, factory, VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
		
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
		retVal = fullText.split("\\n ");
		
		return retVal;
	}
	
	/**
	 * Once we have an array of parsed lines returned by parseResourceByLine, we need to split it into individual
	 * values to populate the database with. We have used semicolons as our delimiter.
	 * 
	 * @param resource The String[] that was returned to us from parseResourceByLine
	 * @return String[][]: Each index represents one line from the resource. Each index then contains a String[] that contains the individual values from that line
	 */
	public String[][] parseResourceByDelimiter(String[] resource)
	{
		String[][] retVal = new String[resource.length][];
		
		for (int i = 0; i < resource.length; i++)
		{
			retVal[i] = resource[i].split(";");
		}
		
		return retVal;
	}

}
