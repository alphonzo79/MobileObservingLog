/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog;

import java.util.ArrayList;
import java.util.List;

import com.mobileobservinglog.R;
import com.mobileobservinglog.support.BackupRestoreUtil;
import com.mobileobservinglog.support.HtmlExporter;
import com.mobileobservinglog.support.database.CatalogsDAO;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BackupRestoreScreen extends ActivityBase{

	//gather resources
	FrameLayout body;
	Button export;
	Button backup;
	Button restore;
	List<String> catalogList;
	List<String> selectedItems;

	RelativeLayout alertModal;
	LinearLayout alertSelectors;
	LinearLayout alertListOne;
	LinearLayout alertListTwo;
	TextView alertText;
	Button alertOk;
	Button alertCancel;
	RelativeLayout progressLayout;
	ImageView progressImage;
	TextView progressMessage;
	
	List<String> restorableFiles;
	
	BackupRestoreUtil util;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

		customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
        
        selectedItems = new ArrayList<String>();
		
        //setup the layout
        setContentView(settingsRef.getBackupRestoreLayout());
        body = (FrameLayout)findViewById(R.id.backup_restore_root); 

        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertOk = (Button)findViewById(R.id.alert_ok_button);
        alertCancel = (Button)findViewById(R.id.alert_cancel_button);
        alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        progressLayout = (RelativeLayout)findViewById(R.id.progress_modal);
        progressImage = (ImageView)findViewById(R.id.progress_image);
        progressMessage = (TextView)findViewById(R.id.progress_text);
	}
	
	@Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //When we resume, we need to make sure we have the right layout set, in case the user has changed the session mode.
    @Override
    public void onResume() {
        super.onResume();
        setLayout();
        util = new BackupRestoreUtil(progressMessage, progressImage, BackupRestoreScreen.this);
    }
	
  //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getBackupRestoreLayout());
		super.setLayout();
		findButtonsAddListeners();
		findModalElements();
		prepareListView();
		body.postInvalidate();
	}
	
	protected void findButtonsAddListeners() {
		export = (Button)findViewById(R.id.export_pdf_button);
		backup = (Button)findViewById(R.id.backup_data_button);
		restore = (Button)findViewById(R.id.restore_data_button);
		
		export.setOnClickListener(exportPdfs);
		backup.setOnClickListener(backupData);
		restore.setOnClickListener(restoreData);
	}
	
	private void findModalElements() {
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
		alertText = (TextView)findViewById(R.id.modal_header);
		alertOk = (Button)findViewById(R.id.alert_ok_button);
		alertCancel = (Button)findViewById(R.id.alert_cancel_button);
		alertSelectors = (LinearLayout)findViewById(R.id.selectors_container);
		alertListOne = (LinearLayout)findViewById(R.id.object_selector_modal_list_layout_one);
		alertListTwo = (LinearLayout)findViewById(R.id.object_selector_modal_list_layout_two);
		
		progressLayout = (RelativeLayout)findViewById(R.id.progress_modal);
		progressImage = (ImageView)findViewById(R.id.progress_image);
		progressMessage = (TextView)findViewById(R.id.progress_text);
	}
	
	private final Button.OnClickListener exportPdfs = new Button.OnClickListener() {
		public void onClick(View view) {
			if(selectedItems.size() > 0) {
				HtmlExporter exporter = new HtmlExporter(progressMessage, progressImage, BackupRestoreScreen.this);
				exporter.exportData(selectedItems);
			} else {
				prepForModal();
				alertText.setVisibility(View.VISIBLE);
				alertOk.setVisibility(View.VISIBLE);
				alertModal.setVisibility(View.VISIBLE);
				
				alertText.setText("Please select at least one catalog to export.");
				alertOk.setOnClickListener(dismissModal);;
			}
		}
	};
	
	private final Button.OnClickListener backupData = new Button.OnClickListener() {
		public void onClick(View view) {
			util.backupData();
		}
	};
	
	private final Button.OnClickListener restoreData = new Button.OnClickListener() {
		public void onClick(View view) {
			prepForModal();
			alertText.setVisibility(View.VISIBLE);
			alertOk.setVisibility(View.VISIBLE);
			alertCancel.setVisibility(View.VISIBLE);
			alertModal.setVisibility(View.VISIBLE);
			
			alertText.setText("WARNING: This action will over-write all existing log data.\n\nContinue?\n");
			alertOk.setOnClickListener(warningAccepted);
			alertCancel.setOnClickListener(dismissModal);
		}
	};
	
	private final Button.OnClickListener warningAccepted = new Button.OnClickListener() {
		public void onClick(View view) {
			restorableFiles = util.findRestorableFiles();
			
			prepForModal();
			alertText.setVisibility(View.VISIBLE);
	    	alertModal.setVisibility(View.VISIBLE);
	    	
			if(restorableFiles.size() > 0) {
		    	alertCancel.setVisibility(View.VISIBLE);
				alertCancel.setOnClickListener(dismissModal);
				alertText.setText("Which file would you like to backup from?");
				
				alertListOne.setVisibility(View.VISIBLE);
				findViewById(R.id.object_selector_modal_list_one_header).setVisibility(View.GONE);
				ListView modalList = (ListView)findViewById(R.id.modal_list_one);
				modalList.setAdapter(new ArrayAdapter<String>(BackupRestoreScreen.this, settingsRef.getSearchModalListLayout(), R.id.filter_option, restorableFiles));
				modalList.setOnItemClickListener(fileSelected);
				
				Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				int windowHeight = display.getHeight(); 
				RelativeLayout.LayoutParams listOneParams = (RelativeLayout.LayoutParams)alertListOne.getLayoutParams();
				if(listOneParams.height > (int) windowHeight * 0.6f) {
					listOneParams.height = (int) (windowHeight * 0.6f);
					alertListOne.setLayoutParams(listOneParams);
				}
			} else {
		    	alertOk.setVisibility(View.VISIBLE);
				String defaultDirectory = util.getDefaultDirectoryPath();
				alertText.setText("There were no restorable .maol files found in the default directory. \n\n" +
						"If you have a backup (.maol) file please place it in the directory " + defaultDirectory);
				alertOk.setOnClickListener(dismissModal);
			}
		}
	};
	
	protected final AdapterView.OnItemClickListener fileSelected = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			util.restoreData(restorableFiles.get(position));
		}
	};
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		catalogList = new ArrayList<String>();
		//Get the list of saved telescopes and populate the list
		CatalogsDAO db = new CatalogsDAO(this);
		Cursor catalogs = db.getAvailableCatalogs();
		
		catalogs.moveToFirst();

		for (int i = 0; i < catalogs.getCount(); i++)
        {
			String installed = catalogs.getString(1);
			
			if (installed.equals("Yes")){
				String name = catalogs.getString(0);
				catalogList.add(name);
			}
        	
        	catalogs.moveToNext();
        }
		catalogs.close();
		db.close();
		
		if (catalogList.size() == 0){
			TextView nothingLeft = (TextView)findViewById(R.id.nothing_here);
			nothingLeft.setVisibility(View.VISIBLE);
			export.setEnabled(false);
			backup.setEnabled(false);
		}
		else{
			setListAdapter(new CatalogAdapter(this, settingsRef.getBackupRestoreListLayout(), catalogList));
		}
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to swap out the image and add/remove it from the selected list
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView name = (TextView) v.findViewById(R.id.catalog_name);
		String catalog = name.getText().toString();
		
		ImageView checked = (ImageView) v.findViewById(R.id.checkbox);
		
		if (!selectedItems.contains(catalog)){ //This item is not currently checked
			selectedItems.add(catalog);
			checked.setImageResource(settingsRef.getCheckbox_Selected());
		}
		else{
			selectedItems.remove(catalog);
			checked.setImageResource(settingsRef.getCheckbox_Unselected());
		}
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.backup_restore_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		export.setEnabled(false);
		backup.setEnabled(false);
		restore.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);

		progressLayout.setVisibility(View.GONE);
		alertSelectors.setVisibility(View.GONE);
		alertListOne.setVisibility(View.GONE);
		alertListTwo.setVisibility(View.GONE);
		alertModal.setVisibility(View.GONE);
		alertText.setVisibility(View.GONE);
		alertOk.setVisibility(View.GONE);
		alertCancel.setVisibility(View.GONE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.backup_restore_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		export.setEnabled(true);
		backup.setEnabled(true);
		restore.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
		progressLayout.setVisibility(View.INVISIBLE);
	}
    
    protected final Button.OnClickListener dismissModal = new Button.OnClickListener() {
		public void onClick(View view){
			tearDownModal();
        }
    };
    
    /**
     * Take our existing alert modal and modify the layout to provide a progress indicator
     */
    public void prepProgressModal(){
    	prepForModal();
    	progressImage.setVisibility(View.VISIBLE);
    	progressMessage.setVisibility(View.VISIBLE);
    	progressLayout.setVisibility(View.VISIBLE);
    }
    
    public void showFailureMessage(String message){
    	prepForModal();
		alertModal.setVisibility(View.VISIBLE);
		alertText.setText(message);
		alertOk.setOnClickListener(dismissModal);
		alertText.setVisibility(View.VISIBLE);
		alertOk.setVisibility(View.VISIBLE);
    }
    
    public void showSuccessMessage(String message){
    	prepForModal();
		alertModal.setVisibility(View.VISIBLE);
		alertText.setText(message);
		alertOk.setOnClickListener(dismissModal);
		alertText.setVisibility(View.VISIBLE);
		alertOk.setVisibility(View.VISIBLE);
    }
    
    //////////////////////////////////////
    // Catalog List Inflation Utilities //
    //////////////////////////////////////
	
	class CatalogAdapter extends ArrayAdapter<String>{
		
		int listLayout;
		
		CatalogAdapter(Context context, int listLayout, List<String> list){
			super(context, listLayout, list);
			this.listLayout = listLayout;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			CatalogWrapper wrapper = null;
			
			if (convertView == null){
				convertView = getLayoutInflater().inflate(listLayout, null);
				wrapper = new CatalogWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else{
				wrapper = (CatalogWrapper)convertView.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return convertView;
		}
	}
	
	class CatalogWrapper{
		
		private TextView name = null;
		private ImageView icon = null;
		private View row = null;
		
		CatalogWrapper(View row){
			this.row = row;
		}
		
		TextView getName(){
			if (name == null){
				name = (TextView)row.findViewById(R.id.catalog_name);
			}
			return name;
		}
		
		ImageView getIcon(){
			if (icon == null){
				icon = (ImageView)row.findViewById(R.id.checkbox);
			}
			return icon;
		}
		
		void populateFrom(String catalog){
			getName().setText(catalog);
			getIcon().setImageResource(settingsRef.getCheckbox_Unselected());
		}
	}
}
