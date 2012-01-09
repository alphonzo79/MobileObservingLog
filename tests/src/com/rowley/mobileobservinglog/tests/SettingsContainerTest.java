/**
 * 
 */
package com.rowley.mobileobservinglog.tests;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.rowley.mobileobservinglog.SettingsContainer;
import com.rowley.mobileobservinglog.SettingsContainer.SessionMode;
import com.rowley.mobileobservinglog.R;

/**
 * @author Joe Rowley
 *
 */
public class SettingsContainerTest extends TestCase{
	
	//Class Under Test
	SettingsContainer mCut = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		mCut = SettingsContainer.getSettingsContainer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		mCut = null;
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getSettingsContainer()}.
	 * 
	 * Since the settings container is a singleton, we need to create a second SettingsContainer object, call the method to 
	 * populate it, and compare it to the one that was established at the beginning of the test. They should refer to the same 
	 * object. Also, we will test that the object was instantiated properly with night mode.
	 */
	@Test
	public void testGetSettingsContainer() {
		SettingsContainer newCut = SettingsContainer.getSettingsContainer();
		assertSame("The settings containers were not the same. mCut: " + mCut.toString() + ". newCut: " + newCut.toString() + ". ", mCut, newCut);
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The settings container did not instantiate with night mode. Current mode: " + currentMode + ". ", SessionMode.night, currentMode);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getSessionMode()}.
	 * 
	 * Testing this, we will get the default session mode, which should be night, then switch to normal mode
	 * and check, then switch back to night. This will likely be duplicated in part in the methods that test the
	 * set mode methods. 
	 */
	@Test
	public void testGetSessionMode() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		mCut.setNormalMode();
		currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NormalMode. CurrentMode: " + currentMode + ". ", SessionMode.normal, currentMode);
		mCut.setNightMode();
		currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getHomeLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 * 
	 */
	@Test
	public void testGetHomeLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.homescreen_night, mCut.getHomeLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.homescreen, mCut.getHomeLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.homescreen_night, mCut.getHomeLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getAddCatalogsLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 * 
	 */
	@Test
	public void testGetAddCatalogsLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.add_catalogs_screen_night, mCut.getAddCatalogsLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.add_catalogs_screen, mCut.getAddCatalogsLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.add_catalogs_screen_night, mCut.getAddCatalogsLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getCatalogsLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetCatalogsLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.catalogs_screen_night, mCut.getCatalogsLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.catalogs_screen, mCut.getCatalogsLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.catalogs_screen_night, mCut.getCatalogsLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getInfoLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetInfoLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.info_screen_night, mCut.getInfoLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.info_screen, mCut.getInfoLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.info_screen_night, mCut.getInfoLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getObjectDetailLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetObjectDetailLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.object_detail_screen_night, mCut.getObjectDetailLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.object_detail_screen, mCut.getObjectDetailLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.object_detail_screen_night, mCut.getObjectDetailLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getSettingsLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetSettingsLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.settings_screen_night, mCut.getSettingsLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.settings_screen, mCut.getSettingsLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.settings_screen_night, mCut.getSettingsLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getSettingsListLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetSettingsListLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.settings_list_night, mCut.getSettingsListLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.settings_list_normal, mCut.getSettingsListLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.settings_list_night, mCut.getSettingsListLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getTargetListsLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetTargetListsLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode. CurrentMode: " + currentMode + ". ", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen_night, mCut.getTargetListsLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen, mCut.getTargetListsLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen_night, mCut.getTargetListsLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getBackupRestoreLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	@Test
	public void testGetBackupRestoreLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen_night, mCut.getBackupRestoreLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen, mCut.getBackupRestoreLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen_night, mCut.getBackupRestoreLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getModeButtonText()}.
	 * 
	 * When the session mode changes, the text displayed on the toggle button in the menu should change.
	 * The default session for a new SettingsContiner object is night mode, and we reset to night mode at the end
	 * of each test. So we will confirm that, then check the text, switch to normal mode, check the text, then 
	 * return to night mode to clean up. 
	 */
	@Test
	public void testGetModeButtonText() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered text was not correct", R.string.menu_toggle_normal_mode, mCut.getModeButtonText());
		
		mCut.setNormalMode();
		assertEquals("The delivered text was not correct", R.string.menu_toggle_night_mode, mCut.getModeButtonText());
		
		mCut.setNightMode();
		assertEquals("The delivered text was not correct", R.string.menu_toggle_normal_mode, mCut.getModeButtonText());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getButtonBrightness()}.
	 * 
	 * When the session mode changes, the button brightness value should change
	 * The default session for a new SettingsContiner object is night mode, and we reset to night mode at the end
	 * of each test. So we will confirm that, then check the text, switch to normal mode, check the text, then 
	 * return to night mode to clean up. 
	 */
	@Test
	public void testGetButtonBrightness() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The button brightness value was not correct", (float)0.5f, mCut.getButtonBrightness());
		
		mCut.setNormalMode();
		assertEquals("The button brightness value was not correct", (float)1.0f, mCut.getButtonBrightness());
		
		mCut.setNightMode();
		assertEquals("The button brightness value was not correct", (float)0.5f, mCut.getButtonBrightness());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#setNightMode()}.
	 * 
	 * This method does a lot. It has basically been covered with all the preceeding tests, but this test will
	 * look at all of its actions in its entirety.
	 */
	@Test
	public void testSetNightMode() {
		mCut.setNightMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, mCut.getSessionMode());
		assertEquals("The delivered text was not correct", R.string.menu_toggle_normal_mode, mCut.getModeButtonText());
		assertEquals("The delivered layout was not correct", R.layout.homescreen_night, mCut.getHomeLayout());
		assertEquals("The delivered layout was not correct", R.layout.add_catalogs_screen_night, mCut.getAddCatalogsLayout());
		assertEquals("The delivered layout was not correct", R.layout.catalogs_screen_night, mCut.getCatalogsLayout());
		assertEquals("The delivered layout was not correct", R.layout.info_screen_night, mCut.getInfoLayout());
		assertEquals("The delivered layout was not correct", R.layout.object_detail_screen_night, mCut.getObjectDetailLayout());
		assertEquals("The delivered layout was not correct", R.layout.settings_screen_night, mCut.getSettingsLayout());
		assertEquals("The delivered layout was not correct", R.layout.settings_list_normal, mCut.getSettingsListLayout());
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen_night, mCut.getTargetListsLayout());
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen_night, mCut.getBackupRestoreLayout());
		assertEquals("The button brightness value was not correct", (float)0.5f, mCut.getButtonBrightness());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#setNormalMode()}.
	 * 
	 * This method does a lot. It has basically been covered with all the preceeding tests, but this test will
	 * look at all of its actions in its entirety.
	 */
	@Test
	public void testSetNormalMode() {
		mCut.setNormalMode();
		assertEquals("The default mode was not NightMode.", SessionMode.normal, mCut.getSessionMode());
		assertEquals("The delivered text was not correct", R.string.menu_toggle_night_mode, mCut.getModeButtonText());
		assertEquals("The delivered layout was not correct", R.layout.homescreen, mCut.getHomeLayout());
		assertEquals("The delivered layout was not correct", R.layout.add_catalogs_screen, mCut.getAddCatalogsLayout());
		assertEquals("The delivered layout was not correct", R.layout.catalogs_screen, mCut.getCatalogsLayout());
		assertEquals("The delivered layout was not correct", R.layout.info_screen, mCut.getInfoLayout());
		assertEquals("The delivered layout was not correct", R.layout.object_detail_screen, mCut.getObjectDetailLayout());
		assertEquals("The delivered layout was not correct", R.layout.settings_screen, mCut.getSettingsLayout());
		assertEquals("The delivered layout was not correct", R.layout.settings_list_normal, mCut.getSettingsListLayout());
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen, mCut.getTargetListsLayout());
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen, mCut.getBackupRestoreLayout());
		assertEquals("The button brightness value was not correct", (float)-1.0f, mCut.getButtonBrightness());
	}

}
