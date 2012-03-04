/**
 * 
 */
package com.rowley.mobileobservinglog.tests;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;

import com.rowley.mobileobservinglog.ObservingLogActivity;
import com.rowley.mobileobservinglog.SettingsContainer;
import com.rowley.mobileobservinglog.SettingsContainer.SessionMode;
import com.rowley.mobileobservinglog.R;

/**
 * @author Joe Rowley
 *
 */
public class SettingsContainerTest extends SingleLaunchActivityTestCase<ObservingLogActivity>{
	
	//Class Under Test
	SettingsContainer mCut = null;
	ObservingLogActivity mAut = null;
	Instrumentation mInstrumentation = null;
	
	public SettingsContainerTest(){
		super("com.rowley.mobileobservinglog", ObservingLogActivity.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mAut = getActivity();
		mInstrumentation = getInstrumentation();
		mCut = SettingsContainer.getSettingsContainer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
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
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getManageEquipmentLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	public void testGetManageEquipmentLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.manage_equipment_screen_night, mCut.getManageEquipmentLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.manage_equipment_screen, mCut.getManageEquipmentLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.manage_equipment_screen_night, mCut.getManageEquipmentLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getManageLocationsLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	public void testGetManageLocationsLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.manage_locations_screen_night, mCut.getManageLocationsLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.manage_locations_screen, mCut.getManageLocationsLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.manage_locations_screen_night, mCut.getManageLocationsLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getPersonalInfoLayout()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	public void testGetPersonalInfoLayout() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.personal_info_screen_night, mCut.getPersonalInfoLayout());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.personal_info_screen, mCut.getPersonalInfoLayout());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.personal_info_screen_night, mCut.getPersonalInfoLayout());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getModeButtonText()}.
	 * 
	 * When the session mode changes, the text displayed on the toggle button in the menu should change.
	 * The default session for a new SettingsContiner object is night mode, and we reset to night mode at the end
	 * of each test. So we will confirm that, then check the text, switch to normal mode, check the text, then 
	 * return to night mode to clean up. 
	 */
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
	public void testGetButtonBrightness() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The button brightness value was not correct", (float)0.0f, mCut.getButtonBrightness());
		
		mCut.setNormalMode();
		assertEquals("The button brightness value was not correct", (float)-1.0f, mCut.getButtonBrightness());
		
		mCut.setNightMode();
		assertEquals("The button brightness value was not correct", (float)0.0f, mCut.getButtonBrightness());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getTabIndicator()}.
	 * 
	 * The first layout delivered should be night mode, since the class will instantiate by default with night mode.
	 * Switch to normal mode and check the layout delivered, then switch back to night mode and check the layout.
	 */
	public void testGetTabIndicator() {
		SessionMode currentMode = mCut.getSessionMode();
		assertEquals("The default mode was not NightMode.", SessionMode.night, currentMode);
		assertEquals("The delivered layout was not correct", R.layout.tab_indicator_layout_night, mCut.getTabIndicator());
		
		mCut.setNormalMode();
		assertEquals("The delivered layout was not correct", R.layout.tab_indicator_layout, mCut.getTabIndicator());
		
		mCut.setNightMode();
		assertEquals("The delivered layout was not correct", R.layout.tab_indicator_layout_night, mCut.getTabIndicator());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#setNightMode()}.
	 * 
	 * This method does a lot. It has basically been covered with all the preceeding tests, but this test will
	 * look at all of its actions in its entirety.
	 */
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
		assertEquals("The delivered layout was not correct", R.layout.settings_list_night, mCut.getSettingsListLayout());
		assertEquals("The delivered layout was not correct", R.layout.target_lists_screen_night, mCut.getTargetListsLayout());
		assertEquals("The delivered layout was not correct", R.layout.backup_restore_screen_night, mCut.getBackupRestoreLayout());
		assertEquals("The delivered layout was not correct", R.layout.manage_equipment_screen_night, mCut.getManageEquipmentLayout());
		assertEquals("The delivered layout was not correct", R.layout.manage_locations_screen_night, mCut.getManageLocationsLayout());
		assertEquals("The delivered layout was not correct", R.layout.personal_info_screen_night, mCut.getPersonalInfoLayout());
		assertEquals("The delivered layout was not correct", R.layout.tab_indicator_layout_night, mCut.getTabIndicator());
		assertEquals("The button brightness value was not correct", (float)0.0f, mCut.getButtonBrightness());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#setNormalMode()}.
	 * 
	 * This method does a lot. It has basically been covered with all the preceeding tests, but this test will
	 * look at all of its actions in its entirety.
	 */
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
		assertEquals("The delivered layout was not correct", R.layout.manage_equipment_screen, mCut.getManageEquipmentLayout());
		assertEquals("The delivered layout was not correct", R.layout.manage_locations_screen, mCut.getManageLocationsLayout());
		assertEquals("The delivered layout was not correct", R.layout.personal_info_screen, mCut.getPersonalInfoLayout());
		assertEquals("The delivered layout was not correct", R.layout.tab_indicator_layout, mCut.getTabIndicator());
		assertEquals("The button brightness value was not correct", (float)-1.0f, mCut.getButtonBrightness());
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#getPersistentSetting()}.
	 * 
	 * This method tests getting a persistent setting and just looks for a non-null return
	 */
	public void testGetPersistentSetting() {
		Log.d("JoeTest", "mAut: " + mAut);
		assertNotNull("Night Mode Backlight Intensity was null", mCut.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, mAut));
		assertNotNull("Search Mode was null", mCut.getPersistentSetting(SettingsContainer.SEARCH_MODE, mAut));
		assertNotNull("Ue GPS was null", mCut.getPersistentSetting(SettingsContainer.USE_GPS, mAut));
	}

	/**
	 * Test method for {@link com.rowley.mobileobservinglog.SettingsContainer#setPersistentSetting()}.
	 * 
	 * This method tests setting a persistent setting. First we get it so we can restore, then set it and check the change, then restore
	 */
	public void testSetPersistentSetting() {
		String originalSetting = mCut.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, mAut);
		String newSetting;
		
		if (originalSetting == "2")
			newSetting = "7";
		else
			newSetting = "2";
		
		mCut.setPersistentSetting(SettingsContainer.NM_BACKLIGHT, newSetting);
		assertEquals("Night Mode Backlight Intensity was not set correctly", newSetting, mCut.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, mAut));
		
		mCut.setPersistentSetting(SettingsContainer.NM_BACKLIGHT, originalSetting);
		assertEquals("Night Mode Backlight Intensity was not reset correctly", originalSetting, mCut.getPersistentSetting(SettingsContainer.NM_BACKLIGHT, mAut));
	}
}
