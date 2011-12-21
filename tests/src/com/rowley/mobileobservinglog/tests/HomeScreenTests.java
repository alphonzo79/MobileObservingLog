package com.rowley.mobileobservinglog.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.inputmethodservice.Keyboard.Key;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.rowley.mobileobservinglog.HomeScreen;
import com.rowley.mobileobservinglog.R;
import com.rowley.mobileobservinglog.SettingsContainer;

public class HomeScreenTests extends ActivityInstrumentationTestCase2<HomeScreen>{

	HomeScreen mAut = null;
	Instrumentation mInstrumentation = null;
	Menu mMenu = null;
	SettingsContainer mSettings = null;
	
	public HomeScreenTests(){
		super("com.rowley.mobileobservinglog", HomeScreen.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		mAut = new HomeScreen();
		setActivity(mAut);
		mInstrumentation = getInstrumentation();
		mSettings = SettingsContainer.getSettingsContainer();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/**
	 * This test will press the menu button and ensure that it is eaten on the inital screen
	 * @throws Throwable 
	 */
	public void testMenu() throws Throwable
	{
		Log.d("JoeTest", "Starting testMenu");
		MenuItem menuItemRef = null;
		runTestOnUiThread(new Runnable()
		{
			public void run()
			{
				Log.d("JoeTest", "Pressing menu Button");
				mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
			}
		});
		
		Thread.sleep(2000);
		
		try
		{
			Log.d("JoeTest", "finding the menu item for toggle mode");
			menuItemRef = (MenuItem)mAut.findViewById(R.id.menuItem_toggleMode);
			Log.d("JoeTest", "found: " + menuItemRef.toString() + "Expected: " + R.id.menuItem_toggleMode);
		}
		catch (NullPointerException e)
		{
			Log.d("JoeTest", "Caught null pointer exception");
		}

		assertNull("The menu was not null", menuItemRef);
	}
	
	/*
	public void testOnPrepareMenu(){
		//Get the settings container
		//mSettings = SettingsContainer.getSettingsContainer();
		
		//Set night mode and check the values
		mSettings.setNightMode();
		mAut.onPrepareOptionsMenu(mMenu);
		mMenu = (Menu)mAut.findViewById(R.menu.global_menu);
		assertEquals(R.string.menu_toggle_normal_mode, mMenu.findItem(R.id.toggleMode).getTitle());
		
		//set Normal Mode and check the values
		mSettings.setNormalMode();
		mAut.onPrepareOptionsMenu(mMenu);
		mMenu = (Menu)mAut.findViewById(R.menu.global_menu);
		assertEquals(R.string.menu_toggle_night_mode, mMenu.findItem(R.id.toggleMode).getTitle());
	}
	*/
	
	public void testMenuHomePress()
	{
		
	}
	
	public void testMenuModePress()
	{
		
	}
	
	public void testMenuSettingsPress()
	{
		
	}
	
	public void testMenuInfoPress()
	{
		
	}
	
	public void testToggleMode()
	{
		
	}
}
