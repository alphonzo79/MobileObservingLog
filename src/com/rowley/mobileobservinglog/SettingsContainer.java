package com.rowley.mobileobservinglog;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public final class SettingsContainer {
	
	//Private constructor to make it non-instantiable
	private SettingsContainer(){
		setNightMode();
	}
	
	//Singleton reference variable and accessor
	private static SettingsContainer ref;
	public static synchronized SettingsContainer getSettingsContainer(){
		if (ref == null){
			ref = new SettingsContainer();
			
			//Initialize persistent settings map upon creation
			ref.persistentSettingsMap = new HashMap<String, String>();
		}
		return ref;
	}

	//Member Variables
	
	//Persistent Setting String Constants
	public static final String NM_BACKLIGHT = "Night Mode Backlight Intensity";
	public static final String USE_GPS = "Use Device GPS";
	public static final String SEARCH_MODE = "Search/Filter Type";
	public static final String IMAGE_DOWNLOAD_ROOT = "http://www.scinvites.com/mobile_observing_log/";
	public static final String MESSIER_DIRECTORY = "messier/";
	
	//Persistent Settings Map
	private Map<String, String> persistentSettingsMap = null;
	
	//SessionMode
	public static enum SessionMode{
		night,
		normal
	} 
	
	private SessionMode mSessionMode;
	public SessionMode getSessionMode(){
		return mSessionMode;
	}
	private void setSessionMode(SessionMode mode){
		mSessionMode = mode;
	}
	
	//Layouts. We need to maintain the layout references dependant on the session mode
	private int mHomeLayout;
	public int getHomeLayout(){
		return mHomeLayout;
	}
	private void setHomeLayout(int layout){
		mHomeLayout = layout;
	}
	
	private int mAddCatalogsLayout;
	public int getAddCatalogsLayout(){
		return mAddCatalogsLayout;
	}
	private void setAddCatalogsLayout(int layout){
		mAddCatalogsLayout = layout;
	}
	
	private int mAddCatalogsTabLayout;
	public int getAddCatalogsTabLayout(){
		return mAddCatalogsTabLayout;
	}
	private void setAddCatalogsTabLayout(int layout){
		mAddCatalogsTabLayout = layout;
	}
	
	private int mAddCatalogsListLayout;
	public int getAddCatalogsListLayout(){
		return mAddCatalogsListLayout;
	}
	private void setAddCatalogsListLayout(int layout){
		mAddCatalogsListLayout = layout;
	}
	
	private int mCatalogsLayout;
	public int getCatalogsLayout(){
		return mCatalogsLayout;
	}
	private void setCatalogsLayout(int layout){
		mCatalogsLayout = layout;
	}
	
	private int mInfoLayout;
	public int getInfoLayout(){
		return mInfoLayout;
	}
	private void setInfoLayout(int layout){
		mInfoLayout = layout;
	}
	
	private int mObjectDetailLayout;
	public int getObjectDetailLayout(){
		return mObjectDetailLayout;
	}
	private void setObjectDetailLayout(int layout){
		mObjectDetailLayout = layout;
	}
	
	private int mSettingsLayout;
	public int getSettingsLayout(){
		return mSettingsLayout;
	}
	private void setSettingsLayout(int layout){
		mSettingsLayout = layout;
	}
	
	private int mSettingsListLayout;
	public int getSettingsListLayout(){
		return mSettingsListLayout;
	}
	private void setSettingsListLayout(int layout){
		mSettingsListLayout = layout;
	}
	
	private int mTargetListsLayout;
	public int getTargetListsLayout(){
		return mTargetListsLayout;
	}
	private void setTargetListsLayout(int layout){
		mTargetListsLayout = layout;
	}
	
	private int mBackupRestoreLayout;
	public int getBackupRestoreLayout(){
		return mBackupRestoreLayout;
	}
	private void setBackupRestoreLayout(int layout){
		mBackupRestoreLayout = layout;
	}
	
	private int mManageEquipmentLayout;
	public int getManageEquipmentLayout(){
		return mManageEquipmentLayout;
	}
	private void setManageEquipmentLayout(int layout){
		mManageEquipmentLayout = layout;
	}
	
	private int mManageLocationsLayout;
	public int getManageLocationsLayout(){
		return mManageLocationsLayout;
	}
	private void setManageLocationsLayout(int layout){
		mManageLocationsLayout = layout;
	}
	
	private int mPersonalInfoLayout;
	public int getPersonalInfoLayout(){
		return mPersonalInfoLayout;
	}
	private void setPersonalInfoLayout(int layout){
		mPersonalInfoLayout = layout;
	}
	
	//Options Menu toggle mode button text changes according to the mode.
	private int mModeText;
	public int getModeButtonText(){
		return mModeText;
	}
	private void setModeButtonText(int text){
		mModeText = text;
	}
	
	//Hard button intensity setting. Toned down for night mode.
	private float mButtonBrightness;
	public float getButtonBrightness(){
		return mButtonBrightness;
	}
	private void setButtonBrightness(float brightness){
		mButtonBrightness = brightness;
	}
	
	//Original Hard button intensity setting. Set at task launch to be used in restoring upon pause or destroy.
	private float mOriginalButtonBrightness;
	public float getOriginalButtonBrightness(){
		return mOriginalButtonBrightness;
	}
	public void setOriginalButtonBrightness(float brightness){
		mOriginalButtonBrightness = brightness;
	}
	
	//Tab indicator used for customizing the tab widget layout.
	private int mTabIndicator;
	public int getTabIndicator(){
		return mTabIndicator;
	}
	private void setTabIndicator(int indicator){
		mTabIndicator = indicator;
	}
	
	//Custom check box image resource
	private int mCheckbox_Selected;
	public int getCheckbox_Selected(){
		return mCheckbox_Selected;
	}
	private void setCheckbox_Selected(int imageResource){
		mCheckbox_Selected = imageResource;
	}
	
	//Custom check box image resource
	private int mCheckbox_Unselected;
	public int getCheckbox_Unselected(){
		return mCheckbox_Unselected;
	}
	private void setCheckbox_Unselected(int imageResource){
		mCheckbox_Unselected = imageResource;
	}
	
	//Helper Methods
	//Called to change from normal mode to night mode. This method will set all of the style attributes that are used in setting layouts
	public void setNightMode(){
		//Log line get commented out temporarily for testing because they cause a noClassDefFoundError when running the basic Junit TestCase
		Log.d("JoeDebug", "SettingsContainer.setNightMode. Current session mode is " + getSessionMode());
		setSessionMode(SessionMode.night);
		setModeButtonText(R.string.menu_toggle_normal_mode);
		setHomeLayout(R.layout.homescreen_night);
		setAddCatalogsLayout(R.layout.add_catalogs_screen_night);
		setAddCatalogsListLayout(R.layout.manage_catalogs_list_night);
		setAddCatalogsTabLayout(R.layout.manage_catalogs_tab_night);
		setCatalogsLayout(R.layout.catalogs_screen_night);
		setInfoLayout(R.layout.info_screen_night);
		setObjectDetailLayout(R.layout.object_detail_screen_night);
		setSettingsLayout(R.layout.settings_screen_night);
		setSettingsListLayout(R.layout.settings_list_night);
		setTargetListsLayout(R.layout.target_lists_screen_night);
		setBackupRestoreLayout(R.layout.backup_restore_screen_night);
		setManageEquipmentLayout(R.layout.manage_equipment_screen_night);
		setManageLocationsLayout(R.layout.manage_locations_screen_night);
		setPersonalInfoLayout(R.layout.personal_info_screen_night);
		setTabIndicator(R.layout.tab_indicator_layout_night);
		setCheckbox_Selected(R.drawable.checked_night);
		setCheckbox_Unselected(R.drawable.unchecked_night);
		setButtonBrightness(0.0F);
	}
	
	public void setNormalMode(){
		Log.d("JoeDebug", "SettingsContainer.setNormalMode. Current session mode is " + getSessionMode());
		setSessionMode(SessionMode.normal);
		setModeButtonText(R.string.menu_toggle_night_mode);
		setHomeLayout(R.layout.homescreen);
		setAddCatalogsLayout(R.layout.add_catalogs_screen);
		setAddCatalogsTabLayout(R.layout.manage_catalogs_tab_normal);
		setAddCatalogsListLayout(R.layout.manage_catalogs_list_normal);
		setCatalogsLayout(R.layout.catalogs_screen);
		setInfoLayout(R.layout.info_screen);
		setObjectDetailLayout(R.layout.object_detail_screen);
		setSettingsLayout(R.layout.settings_screen);
		setSettingsListLayout(R.layout.settings_list_normal);
		setTargetListsLayout(R.layout.target_lists_screen);
		setBackupRestoreLayout(R.layout.backup_restore_screen);
		setManageEquipmentLayout(R.layout.manage_equipment_screen);
		setManageLocationsLayout(R.layout.manage_locations_screen);
		setPersonalInfoLayout(R.layout.personal_info_screen);
		setTabIndicator(R.layout.tab_indicator_layout);
		setCheckbox_Selected(R.drawable.checked_normal);
		setCheckbox_Unselected(R.drawable.unchecked_normal);
		setButtonBrightness(getOriginalButtonBrightness());
	}
	
	/**
	 * This method returns the persistent setting value that you want. If the settingsContainer object has not yet
	 * initialized the persistentSettingsMap, then it will do so with a db query. Otherwise it will just return what
	 * it has in the map
	 * 
	 * @param setting PersistentSetting enum for the setting that you are looking for
	 * @param context The activity context that you are calling this from, passed on to the db for use there.
	 * @return The string value of the setting. If it is meant to be used as another type, it will need to be parsed by the consumer
	 */
	public String getPersistentSetting(String setting, Context context)
	{
		//First we need to see if we need to populate the settings from the database. I tried to include this in the constructor, but it cause a circular reference
		//And then I tried to include it in getSettingsContainer, but that caused a null reference exception because of the context dependency -- trying to create too
		//many inter-dependant things at once. So I moved it down here. Everything but these values should be initialized at this time and overcome all of our
		//dependency issues
		if (persistentSettingsMap.keySet().size() < 3) //At this time we have three persistent settings. If we add more this number will need to be updated. We look for the full number because it is possible that we have called setPersistentSetting before we called this method
		{			
			DatabaseHelper db = new DatabaseHelper(context);
			persistentSettingsMap.put(NM_BACKLIGHT, db.getPersistentSettings(NM_BACKLIGHT));
			persistentSettingsMap.put(SEARCH_MODE, db.getPersistentSettings(SEARCH_MODE));
			persistentSettingsMap.put(USE_GPS, db.getPersistentSettings(USE_GPS));
		}
		
		return persistentSettingsMap.get(setting);
	}
	
	/**
	 * Called exclusively by a databaseHelper object. When setting a persistent setting, we call the set method in 
	 * the database helper, which saves the value to the db, then calls this method to keep the updated value 
	 * available for the app's immediate use. This is a sort of invalidation. DO NOT CALL THIS METHOD MANUALLY! Otherwise
	 * there will be a disparity between the db and the currently-loaded settings.
	 * 
	 * @param setting The String value (use the constants provided) for the setting that will be set
	 * @param value The String value that will be associated with the setting
	 */
	public void setPersistentSetting(String setting, String value)
	{
		persistentSettingsMap.put(setting, value);
	}
}
