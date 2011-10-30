package com.rowley.mobileobservinglog;

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
		}
		return ref;
	}

	//Member Variables
	
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
	
	//Options Menu toggle mode button text changes according to the mode.
	private int mModeText;
	public int getModeButtonText(){
		return mModeText;
	}
	private void setModeButtonText(int text){
		mModeText = text;
	}
	
	//Helper Methods
	//Called to change from normal mode to night mode. This method will set all of the style attributes that are used in setting layouts
	public void setNightMode(){
		Log.d("JoeDebug", "SettingsContainer.setNightMode. Current session mode is " + getSessionMode());
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
	
	public void setNormalMode(){
		Log.d("JoeDebug", "SettingsContainer.setNormalMode. Current session mode is " + getSessionMode());
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
