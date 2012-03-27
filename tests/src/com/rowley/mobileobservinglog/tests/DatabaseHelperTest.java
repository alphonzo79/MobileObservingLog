/**
 * 
 */
package com.rowley.mobileobservinglog.tests;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;

import com.rowley.mobileobservinglog.DatabaseHelper;
import com.rowley.mobileobservinglog.HomeScreen;
import com.rowley.mobileobservinglog.R;
import com.rowley.mobileobservinglog.SettingsContainer;

/**
 * @author Joe Rowley
 *We need to set this up as one of our activities from the app for context to pass into the helper. With no compelling
 *reason to do otherwise, I have chosen homescreen as a good basic activity.
 */
public class DatabaseHelperTest extends SingleLaunchActivityTestCase<HomeScreen>
{
	
	//Class Under Test
	DatabaseHelper mCut = null;
	HomeScreen mAut = null;
	Instrumentation mInstrumentation = null;
	
	public DatabaseHelperTest(){
		super("com.rowley.mobileobservinglog", HomeScreen.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception 
	{
		super.setUp();
		mAut = getActivity();
		mInstrumentation = getInstrumentation();
		Log.d("JoeDebug", "Helper Test Constructor. About to call new DatabaseHelper");
		mCut = new DatabaseHelper(mAut);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void tearDown() throws Exception 
	{
		super.tearDown();
		mCut = null;
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#onCreate(SQLiteDatabase db)}.
	 * 
	 * We need to call onCreate() on the database and check that the tables were created and spot check some 
	 * of the seeded values
	 */
	public void testOnCreate() 
	{
		Log.d("JoeDebug", "testOnCreate");
		
		//First drop the tables so we can start fresh (Only do this on an emulator where you don't have data that you want to save)
		SQLiteDatabase db = mCut.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS settings;");
		db.execSQL("DROP TABLE IF EXISTS personalInfo;");
		db.execSQL("DROP TABLE IF EXISTS objects;");
		db.execSQL("DROP TABLE IF EXISTS locations;");
		db.execSQL("DROP TABLE IF EXISTS telescopes;");
		db.execSQL("DROP TABLE IF EXISTS eyepieces;");
		db.execSQL("DROP TABLE IF EXISTS targetLists;");
		db.execSQL("DROP TABLE IF EXISTS targetListItems;");
		db.execSQL("DROP TABLE IF EXISTS availableCatalogs;");
		
		mCut.onCreate(db);
		
		//check settings table
		Cursor dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'settings'", null);
		assertTrue("The settings table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check personal info table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'personalInfo'", null);
		assertTrue("The personalInfo table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check objects table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'objects'", null);
		assertTrue("The objects table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check location table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'locations'", null);
		assertTrue("The locations table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check telescopes table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'telescopes'", null);
		assertTrue("The telescopes table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check eyepieces table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'eyepieces'", null);
		assertTrue("The eyepieces table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check target lists table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'targetLists'", null);
		assertTrue("The targetLists table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check target list items table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'targetListItems'", null);
		assertTrue("The targetListItems table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check available catalogs table
		dbCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'availableCatalogs'", null);
		assertTrue("The availableCatalogs table was not present", dbCursor.getCount() > 0);
		dbCursor.close();
		
		//check data in available catalogs
		dbCursor = db.rawQuery("select numberOfObjects from availableCatalogs where catalogName = 'Messier Catalog'", null);
		dbCursor.moveToFirst();
		assertEquals("The data in the availableCatalogs table did not match", 110, dbCursor.getInt(0));
		dbCursor.close();
		
		//check data in settings
		dbCursor = db.rawQuery("select settingValue from settings where settingName = 'Night Mode Backlight Intensity'", null);
		dbCursor.moveToFirst();
		assertEquals("The data in the settings table did not match", "1", dbCursor.getString(0));
		dbCursor.close();
		
		//check data in objects
		dbCursor = db.rawQuery("select type, size, declination from objects where designation = 'M1'", null);
		dbCursor.moveToFirst();
		assertEquals("The data in the objects table did not match", "Supernova Remnant", dbCursor.getString(0));
		assertEquals("The data in the objects table did not match", "6' � 4'", dbCursor.getString(1));
		assertEquals("The data in the objects table did not match", "+22� 00\' 52.1\"", dbCursor.getString(2));
		dbCursor.close();
		
		db.close();
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#parseResourceByLine(int resource)}.
	 * 
	 * We will send the method a resource with multiple lines, parse it and check the array that is returned
	 */
	public void testParseResourceByLine() 
	{
		Log.d("JoeDebug", "testParseResourceByLine");
		String[] lines = mCut.parseResourceByLine(R.string.settings_default_values);
		assertEquals("Wrong number of lines", 8, lines.length);
		assertEquals("Wrong string in the first place", "Night Mode Backlight Intensity;1;1", lines[0]);
		assertEquals("Wrong string in the last place", "Information/About;NULL;1", lines[7]);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#parseResourceByDelimiter(String[] resource)}.
	 * 
	 * We will send the method a string array (returned to us by parseResourceByLine), parse it and check the array that is returned
	 */
	public void testParseResourceByDelimiter() 
	{
		Log.d("JoeDebug", "testParseResourceByDelimiter");
		String lines = "Night Mode Backlight Intensity;5;1";
		
		String[] parsedValues = mCut.parseResourceByDelimiter(lines);
		
		assertEquals("Wrong number of entries", 3, parsedValues.length);
		assertEquals("Wrong string in [0]", "Night Mode Backlight Intensity", parsedValues[0]);
		assertEquals("Wrong string in [1]", "5", parsedValues[1]);
		assertEquals("Wrong string in [2]", "1", parsedValues[2]);
		
		//test handling of empty columns
		lines = "Night Mode Backlight Intensity;;";
		
		parsedValues = mCut.parseResourceByDelimiter(lines);
		
		assertEquals("Wrong number of entries", 3, parsedValues.length);
		assertEquals("Wrong string in [0]", "Night Mode Backlight Intensity", parsedValues[0]);
		assertEquals("Wrong string in [1]", "", parsedValues[1]);
		assertEquals("Wrong string in [2]", "", parsedValues[2]);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getPersistentSettings()}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetPersistentSettings() 
	{
		Log.d("JoeDebug", "testGetPersistentSettings");
		
		//Call the method, then check for the correct number of rows and columns
		Cursor dbCursor = mCut.getPersistentSettings();
		assertEquals("We got the wrong number of rows", 8, dbCursor.getCount());
		assertEquals("We got the wrong number of columns", 3, dbCursor.getColumnCount());
		dbCursor.close();
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getPersistentSettings(String setting)}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetPersistentSettingsOverride() 
	{
		Log.d("JoeDebug", "testGetPersistentSettingsOverride");
		
		//Call the method, then check for the correct number of rows and columns
		String value = mCut.getPersistentSettings("Search/Filter Type");
		assertEquals("We got the wrong value", "Simple", value);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#setPersistentSetting(String setting, String value)}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testSetPersistentSetting() 
	{
		Log.d("JoeDebug", "testSetPersistentSetting");
		
		//Call the method, check the database using getPersistentSettings(String), then check the settingsContainer
		//for the correct value
		
		//Get access to the settings container singleton
		SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
		
		mCut.setPersistentSetting(SettingsContainer.NM_BACKLIGHT, "8");
		
		String value = mCut.getPersistentSettings(SettingsContainer.NM_BACKLIGHT);
		assertEquals("We got the wrong value", "8", value);
		String scValue = settingsRef.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, mAut);
		assertEquals("We got the wrong value", "8", scValue);
		
		//Return the setting back to what it was
		mCut.setPersistentSetting(SettingsContainer.NM_BACKLIGHT, "5");
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getAvailableCatalogs()}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetAvailableCatalogs(){
		Log.d("JoeDebug", "testGetAvailableCatalogs");
		
		Cursor catalogs = mCut.getAvailableCatalogs();
		assertEquals("We found the wrong number of available catalogs", 1, catalogs.getCount());
		assertEquals("Messier was not included in the list", "Messier Catalog", catalogs.getString(0));
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#updateAvailableCatalogsInstalled(String catalog, String installed)}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testUpdateAvailableCatalogsInstalled(){
		Log.d("JoeDebug", "testUpdateAvailableCatalogsInstalled");
		
		String catalogName = "Messier Catalog";
		
		mCut.updateAvailableCatalogsInstalled(catalogName, "Yes");
		
		Cursor catalogs = mCut.getAvailableCatalogs();
		assertEquals("Messier did not show as installed", "Yes", catalogs.getString(1));
		
		mCut.updateAvailableCatalogsInstalled(catalogName, "No");
		
		catalogs = mCut.getAvailableCatalogs();
		assertEquals("Messier did not show as installed", "No", catalogs.getString(1));
		
		//Try giving it a bad value
		mCut.updateAvailableCatalogsInstalled("Fake Catalog Name", "Yes");
		
		catalogs = mCut.getAvailableCatalogs();
		assertEquals("We found the wrong number of available catalogs", 1, catalogs.getCount());
		assertEquals("Messier did not show as installed", "No", catalogs.getString(1));
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getImagePaths(ArrayList<String> catalogs)}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetImagePaths(){
		Log.d("JoeDebug", "testGetImagePaths");
		
		Cursor paths = mCut.getAvailableCatalogs();
		assertEquals("We found the wrong imageResource", "/messier/normal/M1.gif", paths.getString(0));
		assertEquals("We found the wrong imageResource", "/messier/night/M1.gif", paths.getString(1));
		
		paths.moveToPosition(46);
		assertEquals("We found the wrong imageResource", "/messier/normal/M47.gif", paths.getString(0));
		assertEquals("We found the wrong imageResource", "/messier/night/M47.gif", paths.getString(1));
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getSavedTelescopes()}.
	 * this method inherently also tests addTelescopeData() since that test would be essentially the same
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetSavedTelescopes(){
		Log.d("JoeDebug", "testGetSavedTelescopes");
		
		//Since this table has nothing in it to start with we need to first seed it with some info
		mCut.addTelescopeData("Reflector", "10\"", "f/7", "1787 mm");
		
		Cursor telescopes = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 1, telescopes.getCount());
		assertEquals("We found the data", "Reflector", telescopes.getString(1));
		assertEquals("We found the data", "10\"", telescopes.getString(2));
		assertEquals("We found the data", "f/7", telescopes.getString(3));
		assertEquals("We found the data", "1787 mm", telescopes.getString(4));
		
		//Clean up by deleting our seed data
		int id = telescopes.getInt(0);
		mCut.deleteTelescopeData(id);
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#updateTelescopeData()}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testUpdateTelescopeData(){
		Log.d("JoeDebug", "testUpdateTelescopeData");
		
		//Since this table has nothing in it to start with we need to first stock it with some info
		mCut.addTelescopeData("Reflector", "10\"", "f/7", "1787 mm");
		
		//Check the first data
		Cursor telescopes = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 1, telescopes.getCount());
		assertEquals("We found the data", "Reflector", telescopes.getString(1));
		assertEquals("We found the data", "10\"", telescopes.getString(2));
		assertEquals("We found the data", "f/7", telescopes.getString(3));
		assertEquals("We found the data", "1787 mm", telescopes.getString(4));
		
		//change the data
		int id = telescopes.getInt(0);
		mCut.updateTelescopeData(id, "Refractor", "45 mm", "f/4", "180 mm");
		
		//check the changed data
		telescopes = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 1, telescopes.getCount());
		assertEquals("We found the data", "Refractor", telescopes.getString(1));
		assertEquals("We found the data", "45 mm", telescopes.getString(2));
		assertEquals("We found the data", "f/4", telescopes.getString(3));
		assertEquals("We found the data", "180 mm", telescopes.getString(4));
		
		//Clean up by deleting our seed data
		id = telescopes.getInt(0);
		mCut.deleteTelescopeData(id);
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#deleteTelescopeData()}.
	 * this method inherently also tests addTelescopeData() since that test would be essentially the same
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testDeleteTelescopeData(){
		Log.d("JoeDebug", "testDeleteTelescopeData");
		
		//Since this table has nothing in it to start with we need to first seed it with some info
		mCut.addTelescopeData("Reflector", "10\"", "f/7", "1787 mm");
		
		Cursor telescopes = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 1, telescopes.getCount());
		
		int id = telescopes.getInt(0);
		mCut.deleteTelescopeData(id);
		
		telescopes = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 0, telescopes.getCount());
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#getSavedEyepieces()}.
	 * this method inherently also tests addEyepieceData() since that test would be essentially the same
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testGetSavedEyepieces(){
		Log.d("JoeDebug", "testGetSavedEyepieces");
		
		//Since this table has nothing in it to start with we need to first seed it with some info
		mCut.addEyepieceData("Plossl", "29 mm");
		
		Cursor eyepieces = mCut.getSavedEyepieces();
		assertEquals("We found the wrong number of rows", 1, eyepieces.getCount());
		assertEquals("We found the data", "Plossl", eyepieces.getString(1));
		assertEquals("We found the data", "29 mm", eyepieces.getString(2));
		
		//Clean up by deleting our seed data
		int id = eyepieces.getInt(0);
		mCut.deleteEyepieceData(id);
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#updateEyepieceData()}.
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testUpdateEyepieceData(){
		Log.d("JoeDebug", "testUpdateEyepieceData");
		
		//Since this table has nothing in it to start with we need to first stock it with some info
		mCut.addEyepieceData("Plossl", "29 mm");
		
		Cursor eyepieces = mCut.getSavedEyepieces();
		assertEquals("We found the wrong number of rows", 1, eyepieces.getCount());
		assertEquals("We found the data", "Plossl", eyepieces.getString(1));
		assertEquals("We found the data", "29 mm", eyepieces.getString(2));
		
		//change the data
		int id = eyepieces.getInt(0);
		mCut.updateEyepieceData(id, "Nagler", "45 mm");
		
		//check the changed data
		eyepieces = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 1, eyepieces.getCount());
		assertEquals("We found the data", "Nagler", eyepieces.getString(1));
		assertEquals("We found the data", "45 mm", eyepieces.getString(2));
		
		//Clean up by deleting our seed data
		id = eyepieces.getInt(0);
		mCut.deleteEyepieceData(id);
	}
	
	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#deleteEyepieceData()}.
	 * this method inherently also tests addEyepieceData() since that test would be essentially the same
	 * 
	 * We will get a cursor from the method and check the contents against expectations
	 */
	public void testDeleteEyepieceData(){
		Log.d("JoeDebug", "testDeleteEyepieceData");
		
		//Since this table has nothing in it to start with we need to first seed it with some info
		mCut.addEyepieceData("Plossl", "29 mm");
		
		Cursor eyepieces = mCut.getSavedEyepieces();
		assertEquals("We found the wrong number of rows", 1, eyepieces.getCount());
		
		int id = eyepieces.getInt(0);
		mCut.deleteEyepieceData(id);
		
		eyepieces = mCut.getSavedTelescopes();
		assertEquals("We found the wrong number of rows", 0, eyepieces.getCount());
	}
}
