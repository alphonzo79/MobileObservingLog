package com.rowley.mobileobservinglog;

import android.util.Log;

public final class SettingsContainer {
	
	//Private constructor to make it non-instantiable
	private SettingsContainer(){
		setNightMode();
	}

	//Member Variables
	
	//SessionMode
	public static enum SessionMode{
		night,
		normal
	} 
	
	private static SessionMode mSessionMode;
	public static SessionMode getSessionMode(){
		return mSessionMode;
	}
	private static void setSessionMode(SessionMode mode){
		mSessionMode = mode;
	}
	
	//Layouts. We need to maintain the layout references dependant on the session mode
	private static int mHomeLayout;
	public static int getHomeLayout(){
		return mHomeLayout;
	}
	private static void setHomeLayout(int layout){
		mHomeLayout = layout;
	}
	
	private static int mAddCatalogsLayout;
	public static int getAddCatalogsLayout(){
		return mAddCatalogsLayout;
	}
	private static void setAddCatalogsLayout(int layout){
		mAddCatalogsLayout = layout;
	}
	
	private static int mCatalogsLayout;
	public static int getCatalogsLayout(){
		return mCatalogsLayout;
	}
	private static void setCatalogsLayout(int layout){
		mCatalogsLayout = layout;
	}
	
	private static int mInfoLayout;
	public static int getInfoLayout(){
		return mInfoLayout;
	}
	private static void setInfoLayout(int layout){
		mInfoLayout = layout;
	}
	
	private static int mObjectDetailLayout;
	public static int getObjectDetailLayout(){
		return mObjectDetailLayout;
	}
	private static void setObjectDetailLayout(int layout){
		mObjectDetailLayout = layout;
	}
	
	private static int mSettingsLayout;
	public static int getSettingsLayout(){
		return mSettingsLayout;
	}
	private static void setSettingsLayout(int layout){
		mSettingsLayout = layout;
	}
	
	private static int mTargetListsLayout;
	public static int getTargetListsLayout(){
		return mTargetListsLayout;
	}
	private static void setTargetListsLayout(int layout){
		mTargetListsLayout = layout;
	}
	
	private static int mBackupRestoreLayout;
	public static int getBackupRestoreLayout(){
		return mBackupRestoreLayout;
	}
	private static void setBackupRestoreLayout(int layout){
		mBackupRestoreLayout = layout;
	}
	
	//Options Menu toggle mode button text changes according to the mode.
	private static int mModeText;
	public static int getModeButtonText(){
		return mModeText;
	}
	private static void setModeButtonText(int text){
		mModeText = text;
	}
	
	//Helper Methods
	//Called to change from normal mode to night mode. This method will set all of the style attributes that are used in setting layouts
	public static void setNightMode(){
		Log.d("Joe:Debug", "SettingsContainer.setNightMode. Current session mode is " + getSessionMode());
		setSessionMode(SessionMode.night);
		setModeButtonText(R.string.menu_toggle_normal_mode);
		setHomeLayout(R.layout.homescreen_night);
		setAddCatalogsLayout(R.layout.add_catalogs_screen_night);
		setCatalogsLayout(R.layout.catalogs_screen_night);
		setInfoLayout(R.layout.info_screen_night);
		setObjectDetailLayout(R.layout.object_detail_screen_night);
		setSettingsLayout(R.layout.settings_screen_night);
		setTargetListsLayout(R.layout.target_lists_screen_night);
		setBackupRestoreLayout(R.layout.backup_restore_screen_night);
	}
	
	public static void setNormalMode(){
		Log.d("Joe:Debug", "SettingsContainer.setNormalMode. Current session mode is " + getSessionMode());
		setSessionMode(SessionMode.normal);
		setModeButtonText(R.string.menu_toggle_night_mode);
		setHomeLayout(R.layout.homescreen);
		setAddCatalogsLayout(R.layout.add_catalogs_screen);
		setCatalogsLayout(R.layout.catalogs_screen);
		setInfoLayout(R.layout.info_screen);
		setObjectDetailLayout(R.layout.object_detail_screen);
		setSettingsLayout(R.layout.settings_screen);
		setTargetListsLayout(R.layout.target_lists_screen);
		setBackupRestoreLayout(R.layout.backup_restore_screen);
	}
}
