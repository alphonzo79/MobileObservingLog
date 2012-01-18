/**
 * 
 */
package com.rowley.mobileobservinglog.tests;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;

import com.rowley.mobileobservinglog.DatabaseHelper;
import com.rowley.mobileobservinglog.HomeScreen;
import com.rowley.mobileobservinglog.R;

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
		mCut = new DatabaseHelper(mAut, null);
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
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#parseResourceByLine(int resource)}.
	 * 
	 * We will send the method a resource with multiple lines, parse it and check the array that is returned
	 */
	public void testParseResourceByLine() 
	{
		String[] lines = mCut.parseResourceByLine(R.string.settings_default_values);
		assertEquals("Wrong number of lines", 7, lines.length);
		assertEquals("Wrong string in the first place", "Night Mode Backlight Intensity;5;1", lines[0]);
		assertEquals("Wrong string in the last place", "Information/About;;1", lines[6]);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.DatabaseHelper#parseResourceByDelimiter(String[] resource)}.
	 * 
	 * We will send the method a string array (returned to us by parseResourceByLine), parse it and check the array that is returned
	 */
	public void testParseResourceByDelimiter() 
	{
		String[] lines = new String[]{"Night Mode Backlight Intensity;5;true",
				"Use Device GPS;Yes;false",
				"Search/Filter Type;Simple;true",
				"Manage Equipment;;true",
				"Manage Observing Locations;;true",
				"Personal Information;;true",
				"Information/About;;true" };
		
		String[][] parsedValues = mCut.parseResourceByDelimiter(lines);
		
		assertEquals("Wrong number of lines", 7, parsedValues.length);
		assertEquals("Wrong number of entries in the first line", 3, parsedValues[0].length);
		assertEquals("Wrong number of entries in the fourth line", 3, parsedValues[3].length);
		assertEquals("Wrong string in [0][0]", "Night Mode Backlight Intensity", parsedValues[0][0]);
		assertEquals("Wrong string in [0][1]", "5", parsedValues[0][1]);
		assertEquals("Wrong string in [0][2]", "true", parsedValues[0][2]);
		assertEquals("Wrong string in [3][0]", "Manage Equipment", parsedValues[3][0]);
		assertEquals("Wrong string in [3][1]", "", parsedValues[3][1]);
		assertEquals("Wrong string in [3][2]", "true", parsedValues[3][2]);
	}
}
