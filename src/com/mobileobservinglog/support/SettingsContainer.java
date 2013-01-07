/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support;

import java.util.HashMap;
import java.util.Map;

import com.mobileobservinglog.R;
import com.mobileobservinglog.support.database.DatabaseHelper;
import com.mobileobservinglog.support.database.SettingsDAO;

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
	public static final String STAR_CHART_DIRECTORY = "Star Chart Location";
	public static final String EXTERNAL = "External";
	public static final String INTERNAL = "Internal";
	public static final String IMAGE_DOWNLOAD_ROOT = "http://www.scinvites.com/mobile_observing_log";
	
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
	
	private int mCatalogsList;
	public int getCatalogsList(){
		return mCatalogsList;
	}
	private void setCatalogsList(int layout){
		mCatalogsList = layout;
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
	
	private int mTargetListsIndexList;
	public int getTargetListsIndexList() {
		return mTargetListsIndexList;
	}
	private void setTargetListsIndexList(int layout) {
		mTargetListsIndexList = layout;
	}
	
	private int mAddEditTargetListLayout;
	public int getAddEditTargetListLayout() {
		return mAddEditTargetListLayout;
	}
	private void setAddEditTargetListLayout(int layout) {
		mAddEditTargetListLayout = layout;
	}
	
	private int mBackupRestoreLayout;
	public int getBackupRestoreLayout(){
		return mBackupRestoreLayout;
	}
	private void setBackupRestoreLayout(int layout){
		mBackupRestoreLayout = layout;
	}
	
	private int mBackupRestoreListLayout;
	public int getBackupRestoreListLayout() {
		return mBackupRestoreListLayout;
	}
	private void setBackupRestoreListLayout(int layout) {
		mBackupRestoreListLayout = layout;
	}
	
	private int mManageEquipmentLayout;
	public int getManageEquipmentLayout(){
		return mManageEquipmentLayout;
	}
	private void setManageEquipmentLayout(int layout){
		mManageEquipmentLayout = layout;
	}
	
	private int mTelescopeTabLayout;
	public int getTelescopeTabLayout(){
		return mTelescopeTabLayout;
	}
	private void setTelescopeTabLayout(int layout){
		mTelescopeTabLayout = layout;
	}
	
	private int mTelescopeListLayout;
	public int getTelescopeListLayout(){
		return mTelescopeListLayout;
	}
	private void setTelescopeListLayout(int layout){
		mTelescopeListLayout = layout;
	}
	
	private int mEyepieceTabLayout;
	public int getEyepieceTabLayout(){
		return mEyepieceTabLayout;
	}
	private void setEyepieceTabLayout(int layout){
		mEyepieceTabLayout = layout;
	}
	
	private int mEyepieceListLayout;
	public int getEyepieceListLayout(){
		return mEyepieceListLayout;
	}
	private void setEyepieceListLayout(int layout){
		mEyepieceListLayout = layout;
	}
	
	private int mManageLocationsLayout;
	public int getManageLocationsLayout(){
		return mManageLocationsLayout;
	}
	private void setManageLocationsLayout(int layout){
		mManageLocationsLayout = layout;
	}
	
	private int mLocationsListLayout;
	public int getLocationsListLayout(){
		return mLocationsListLayout;
	}
	private void setLocationsListLayout(int layout){
		mLocationsListLayout = layout;
	}
	
	private int mPersonalInfoLayout;
	public int getPersonalInfoLayout(){
		return mPersonalInfoLayout;
	}
	private void setPersonalInfoLayout(int layout){
		mPersonalInfoLayout = layout;
	}
	
	private int mAddEditTelescopeLayout;
	public int getAddEditTelescopeLayout(){
		return mAddEditTelescopeLayout;
	}
	private void setAddEditTelescopeLayout(int layout){
		mAddEditTelescopeLayout = layout;
	}
	
	private int mAddEditEyepieceLayout;
	public int getAddEditEyepieceLayout(){
		return mAddEditEyepieceLayout;
	}
	private void setAddEditEyepieceLayout(int layout){
		mAddEditEyepieceLayout = layout;
	}
	
	private int mAddEditLocationsLayout;
	public int getAddEditLocationsLayout(){
		return mAddEditLocationsLayout;
	}
	private void setAddEditLocationsLayout(int layout){
		mAddEditLocationsLayout = layout;
	}
	
	private int mViewLocationsLayout;
	public int getViewLocationsLayout(){
		return mViewLocationsLayout;
	}
	private void setViewLocationsLayout(int layout){
		mViewLocationsLayout = layout;
	}
	
	private int mEditPersonalInfoLayout;
	public int getEditPersonalInfoLayout(){
		return mEditPersonalInfoLayout;
	}
	private void setEditPersonalInfoLayout(int layout){
		mEditPersonalInfoLayout = layout;
	}
	
	private int mObjectIndexLayout;
	public int getObjectIndexLayout(){
		return mObjectIndexLayout;
	}
	private void setObjectIndexLayout(int layout){
		mObjectIndexLayout = layout;
	}
	
	private int mObjectIndexListLayout;
	public int getObjectIndexListLayout(){
		return mObjectIndexListLayout;
	}
	private void setObjectIndexListLayout(int layout){
		mObjectIndexListLayout = layout;
	}
	
	private int mSearchScreenLayout;
	public int getSearchScreenLayout(){
		return mSearchScreenLayout;
	}
	private void setSearchScreenLayout(int layout){
		mSearchScreenLayout = layout;
	}
	
	private int mSearchListLayout;
	public int getSearchListLayout(){
		return mSearchListLayout;
	}
	private void setSearchListLayout(int layout){
		mSearchListLayout = layout;
	}
	
	private int mSearchModalListLayout;
	public int getSearchModalListLayout(){
		return mSearchModalListLayout;
	}
	private void setSearchModalListLayout(int layout){
		mSearchModalListLayout = layout;
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
	
	private int mMessierIcon;
	public int getMessierIcon(){
		return mMessierIcon;
	}
	private void setMessierIcon(int imageResource){
		mMessierIcon = imageResource;
	}
	
	private int mNgcIcon;
	public int getNgcIcon(){
		return mNgcIcon;
	}
	private void setNgcIcon(int imageResource){
		mNgcIcon = imageResource;
	}
	
	private int mIcIcon;
	public int getIcIcon(){
		return mIcIcon;
	}
	private void setIcIcon(int imageResource){
		mIcIcon = imageResource;
	}
	
	private void setCatalogIconsNight(){
		setMessierIcon(R.drawable.messier_icon_night);
		setNgcIcon(R.drawable.ngc_icon_night);
		setIcIcon(R.drawable.ic_icon_night);
	}
	
	private void setCatalogIconsNormal(){
		setMessierIcon(R.drawable.messier_icon);
		setNgcIcon(R.drawable.ngc_icon);
		setIcIcon(R.drawable.ic_icon);		
	}
	
	private int favoriteStar;
	public int getFavoriteStar() {
		return favoriteStar;
	}
	private void setFavoriteStar(int imageResource) {
		favoriteStar = imageResource;
	}
	
	private int notFavoriteStar;
	public int getNotFavoriteStar() {
		return notFavoriteStar;
	}
	private void setNotFavoriteStar(int imageResource) {
		notFavoriteStar = imageResource;
	}
	
	private int defaultChartImage;
	public int getDefaultChartImage() {
		return defaultChartImage;
	}
	private void setDefaultChartImage(int imageResource) {
		defaultChartImage = imageResource;
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
		setCatalogsList(R.layout.catalogs_list_night);
		setInfoLayout(R.layout.info_screen_night);
		setObjectDetailLayout(R.layout.object_detail_screen_night);
		setSettingsLayout(R.layout.settings_screen_night);
		setSettingsListLayout(R.layout.settings_list_night);
		setTargetListsLayout(R.layout.target_lists_screen_night);
		setTargetListsIndexList(R.layout.target_list_index_list_night);
		setAddEditTargetListLayout(R.layout.add_edit_target_list_night);
		setBackupRestoreLayout(R.layout.backup_restore_screen_night);
		setBackupRestoreListLayout(R.layout.backup_restore_list_night);
		setManageEquipmentLayout(R.layout.manage_equipment_screen_night);
		setTelescopeTabLayout(R.layout.telescope_tab_night);
		setTelescopeListLayout(R.layout.telescope_list_night);
		setEyepieceTabLayout(R.layout.eyepiece_tab_night);
		setEyepieceListLayout(R.layout.eyepiece_list_night);
		setManageLocationsLayout(R.layout.manage_locations_screen_night);
		setPersonalInfoLayout(R.layout.personal_info_screen_night);
		setAddEditTelescopeLayout(R.layout.add_edit_telescope_night);
		setAddEditEyepieceLayout(R.layout.add_edit_eyepiece_night);
		setAddEditLocationsLayout(R.layout.add_edit_location_night);
		setViewLocationsLayout(R.layout.view_locations_screen_night);
		setEditPersonalInfoLayout(R.layout.edit_personal_info_night);
		setObjectIndexLayout(R.layout.object_index_screen_night);
		setObjectIndexListLayout(R.layout.object_index_list_night);
		setLocationsListLayout(R.layout.locations_list_night);
		setSearchScreenLayout(R.layout.search_screen_nite);
		setSearchListLayout(R.layout.search_screen_list_night);
		setSearchModalListLayout(R.layout.search_modal_list_night);
		setTabIndicator(R.layout.tab_indicator_layout_night);
		setCheckbox_Selected(R.drawable.checked_night);
		setCheckbox_Unselected(R.drawable.unchecked_night);
		setFavoriteStar(R.drawable.favorite_night);
		setNotFavoriteStar(R.drawable.not_favorite_night);
		setDefaultChartImage(R.drawable.default_star_chart_night);
		setButtonBrightness(0.0F);
		setCatalogIconsNight();
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
		setCatalogsList(R.layout.catalogs_list);
		setInfoLayout(R.layout.info_screen);
		setObjectDetailLayout(R.layout.object_detail_screen);
		setSettingsLayout(R.layout.settings_screen);
		setSettingsListLayout(R.layout.settings_list_normal);
		setTargetListsLayout(R.layout.target_lists_screen);
		setTargetListsIndexList(R.layout.target_list_index_list_normal);
		setAddEditTargetListLayout(R.layout.add_edit_target_list_normal);
		setBackupRestoreLayout(R.layout.backup_restore_screen);
		setBackupRestoreListLayout(R.layout.backup_restore_list_normal);
		setManageEquipmentLayout(R.layout.manage_equipment_screen);
		setTelescopeTabLayout(R.layout.telescope_tab_normal);
		setTelescopeListLayout(R.layout.telescope_list_normal);
		setEyepieceTabLayout(R.layout.eyepiece_tab_normal);
		setEyepieceListLayout(R.layout.eyepiece_list_normal);
		setManageLocationsLayout(R.layout.manage_locations_screen);
		setPersonalInfoLayout(R.layout.personal_info_screen);
		setAddEditTelescopeLayout(R.layout.add_edit_telescope);
		setAddEditEyepieceLayout(R.layout.add_edit_eyepiece);
		setAddEditLocationsLayout(R.layout.add_edit_location);
		setViewLocationsLayout(R.layout.view_locations_screen);
		setEditPersonalInfoLayout(R.layout.edit_personal_info);
		setObjectIndexLayout(R.layout.object_index_screen);
		setObjectIndexListLayout(R.layout.object_index_list);
		setLocationsListLayout(R.layout.locations_list_normal);
		setSearchScreenLayout(R.layout.search_screen);
		setSearchListLayout(R.layout.search_screen_list);
		setSearchModalListLayout(R.layout.search_modal_list);
		setTabIndicator(R.layout.tab_indicator_layout);
		setCheckbox_Selected(R.drawable.checked_normal);
		setCheckbox_Unselected(R.drawable.unchecked_normal);
		setFavoriteStar(R.drawable.favorite_normal);
		setNotFavoriteStar(R.drawable.not_favorite_normal);
		setDefaultChartImage(R.drawable.default_star_chart_normal);
		setButtonBrightness(getOriginalButtonBrightness());
		setCatalogIconsNormal();
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
			SettingsDAO db = new SettingsDAO(context);
			persistentSettingsMap.put(NM_BACKLIGHT, db.getPersistentSettings(NM_BACKLIGHT));
			persistentSettingsMap.put(SEARCH_MODE, db.getPersistentSettings(SEARCH_MODE));
			persistentSettingsMap.put(USE_GPS, db.getPersistentSettings(USE_GPS));
			persistentSettingsMap.put(STAR_CHART_DIRECTORY, db.getPersistentSettings(STAR_CHART_DIRECTORY));
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
