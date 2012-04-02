package com.mobileobservinglog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;

import com.mobileobservinglog.SettingsContainer.SessionMode;
import com.mobileobservinglog.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ManageCatalogsTabParent extends ActivityBase {
	
	ArrayList<Catalog> availableCatalogList;
	ArrayList<Catalog> installedCatalogList;
	Button submitButton;
	ArrayList<String> selectedItems;
	float size;
	DecimalFormat twoDigits = new DecimalFormat("###.##");
	int numFiles;
	RelativeLayout alertModal;
	TextView alertText;
	Button alertOk;
	Button alertCancel;
	RelativeLayout progressLayout;
	ImageView progressImage;
	TextView progressMessage;
	ArrayList<Integer> progressImages = new ArrayList<Integer>();
	ListIterator<Integer> imageIterator;
	String failureMessage;
	boolean keepRunningProgressUpdate = false;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(settingsRef.getAddCatalogsTabLayout());
        submitButton = (Button)findViewById(R.id.submit);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertOk = (Button)findViewById(R.id.alert_ok_button);
        alertCancel = (Button)findViewById(R.id.alert_cancel_button);
        alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        progressLayout = (RelativeLayout)findViewById(R.id.progress_modal);
        progressImage = (ImageView)findViewById(R.id.progress_image);
        progressMessage = (TextView)findViewById(R.id.progress_text);
        prepareListView();
        
        selectedItems = new ArrayList<String>();
        size = 0.0F;
    }

	@Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
	
	/**
	 * Internal method to handle preparation of the list view upon creation or to be called by setLayout when session mode changes or onResume.
	 */
	protected void prepareListView()
	{
		availableCatalogList = new ArrayList<Catalog>();
		installedCatalogList = new ArrayList<Catalog>();
		
		//Get the list of available and installed catalogs. Itterate through and populate the right list with the right values
		DatabaseHelper db = new DatabaseHelper(this);
		Cursor catalogs = db.getAvailableCatalogs();
		
		catalogs.moveToFirst();
		
		for (int i = 0; i < catalogs.getCount(); i++)
        {
			String name = catalogs.getString(0);
			String installed = catalogs.getString(1);
			String count = catalogs.getString(2);
			String size = catalogs.getString(4);
			
			if (installed.equals("Yes")){
				installedCatalogList.add(new Catalog(name, size, count));
			}
			else{
				availableCatalogList.add(new Catalog(name, size, count));
			}
        	
        	catalogs.moveToNext();
        }
		catalogs.close();
		db.close();
	}
	
	//For this screen, the tabs layout was causing problems with our regular toggle mode handling. So instead on this screen we will simply relaunch the activity,
	//then kill the current instance so a back press will not take us back to the other mode.
	@Override
	protected void toggleMode(){
		super.toggleMode();
		Intent intent = new Intent(this.getApplication(), AddCatalogsScreen.class);
        startActivity(intent);
        finish();
	}
	
	/**
	 * Take action on each of the list items when clicked. We need to swap out the image and add/remove it from the selected list
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		
		TextView name = (TextView) v.findViewById(R.id.catalog_name);
		String catalog = name.getText().toString();
		TextView description = (TextView) v.findViewById(R.id.catalog_description);
		String catalogSizeText = description.getText().toString();
		float catalogSizeFloat = extractCatalogSize(catalogSizeText);
		int catalogObjects = extractNumObjects(catalogSizeText);
		
		ImageView checked = (ImageView) v.findViewById(R.id.checkbox);
		
		if (!selectedItems.contains(catalog)){ //This item is not currently checked
			selectedItems.add(catalog);
			size += catalogSizeFloat;
			numFiles += catalogObjects;
			Log.d("JoeTest", "numFiles: " + numFiles);
			checked.setImageResource(settingsRef.getCheckbox_Selected());
		}
		else{
			selectedItems.remove(catalog);
			size -= catalogSizeFloat;
			numFiles -= catalogObjects;
			Log.d("JoeTest", "numFiles: " + numFiles);
			checked.setImageResource(settingsRef.getCheckbox_Unselected());
		}
	}
	
	private float extractCatalogSize(String text){
		float retVal = 0.0F;
		String[] parsed = text.split(" ");
		String methodSize = parsed[parsed.length -2]; //get the second-to-last element
		
		retVal = Float.parseFloat(methodSize);
		
		return retVal;
	}
	
	private int extractNumObjects(String text){
		int retVal = 0;
		String[] parsed = text.split(" ");
		String methodNumber = parsed[0]; //first element will have the number of objects
		
		retVal = Integer.parseInt(methodNumber);
		
		return retVal;
	}

	/**
	 * Helper method to dim out the background and make the list view unclickable in preparation to display a modal
	 */
	protected void prepForModal()
	{
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.manage_catalogs_tab_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(false);
		listView.setEnabled(false);
		submitButton.setEnabled(false);
		blackOutLayer.setVisibility(View.VISIBLE);
	}
	
	protected void tearDownModal(){
		RelativeLayout blackOutLayer = (RelativeLayout)findViewById(R.id.settings_fog);
		RelativeLayout mainBackLayer = (RelativeLayout)findViewById(R.id.manage_catalogs_tab_main);
		ListView listView = getListView();
		
		mainBackLayer.setEnabled(true);
		listView.setEnabled(true);
		submitButton.setEnabled(true);
		blackOutLayer.setVisibility(View.INVISIBLE);
		alertModal.setVisibility(View.INVISIBLE);
		progressLayout.setVisibility(View.GONE);
	}
    
    protected final Button.OnClickListener dismissAlert = new Button.OnClickListener() {
		public void onClick(View view){
			tearDownModal();
        }
    };
    
    protected final Button.OnClickListener dismissSuccess = new Button.OnClickListener() {
		public void onClick(View view){
			tearDownModal();
			
			//restart the activity
			Intent intent = new Intent(ManageCatalogsTabParent.this, AddCatalogsScreen.class);
	        startActivity(intent);
	        finish();
        }
    };
    
    /**
     * Take our existing alert modal and modify the layout to provide a progress indicator
     */
    protected void prepProgressModal(){
    	Log.d("JoeTest", "prepProgressModal called");
    	prepareImageArray();
    	alertText.setVisibility(View.GONE);
    	alertOk.setVisibility(View.GONE);
    	alertCancel.setVisibility(View.GONE);
    	alertModal.setVisibility(View.GONE);
    	progressImage.setImageResource(progressImages.get(0));
    	progressImage.setVisibility(View.VISIBLE);
    	progressMessage.setVisibility(View.VISIBLE);
    	progressLayout.setVisibility(View.VISIBLE);
    }
    
    Handler prepProgressModalHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		prepProgressModal();
    	}
    };
    
    private void prepareImageArray(){
    	if (settingsRef.getSessionMode() == SessionMode.normal){
    		progressImages.add(R.drawable.progress_normal_01);
    		progressImages.add(R.drawable.progress_normal_02);
    		progressImages.add(R.drawable.progress_normal_03);
    		progressImages.add(R.drawable.progress_normal_04);
    		progressImages.add(R.drawable.progress_normal_05);
    		progressImages.add(R.drawable.progress_normal_06);
    		progressImages.add(R.drawable.progress_normal_07);
    		progressImages.add(R.drawable.progress_normal_08);
    	}
    	else{
    		progressImages.add(R.drawable.progress_night_01);
    		progressImages.add(R.drawable.progress_night_02);
    		progressImages.add(R.drawable.progress_night_03);
    		progressImages.add(R.drawable.progress_night_04);
    		progressImages.add(R.drawable.progress_night_05);
    		progressImages.add(R.drawable.progress_night_06);
    		progressImages.add(R.drawable.progress_night_07);
    		progressImages.add(R.drawable.progress_night_08);
    	}
    	imageIterator = progressImages.listIterator();
    }
    
    Handler uiUpdateHandler_Download = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		setDownloadProgressText(msg.getData().getInt("current"), msg.getData().getInt("filesToDownLoad"));
    	}
    };
    
    Handler uiUpdateHandler_Remove = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		setRemoveProgressText(msg.getData().getInt("current"), msg.getData().getInt("filesToDownLoad"));
    	}
    };
    
    Handler ProgressImageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		advanceProgressImage();
    	}
    };
    
    public void advanceProgressImage(){
    	if(!imageIterator.hasNext()){
    		imageIterator = progressImages.listIterator();
    	}
    	progressImage.setImageResource(imageIterator.next());
    }
    
    public void setDownloadProgressText(int current, int total){
    	progressMessage.setText("Downloading File " + current + " of " + total + ".");
    }
    
    public void setRemoveProgressText(int current, int total){
    	progressMessage.setText("Removing File " + current + " of " + total + ".");
    }
    
    public void showFailureMessage(){
		progressLayout.setVisibility(View.GONE);
		alertModal.setVisibility(View.VISIBLE);
		alertText.setText(failureMessage);
		alertOk.setOnClickListener(dismissAlert);
		alertText.setVisibility(View.VISIBLE);
		alertOk.setVisibility(View.VISIBLE);
    }
    
    Handler failureMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		showFailureMessage();
    	}
    };
    
    public void showSuccessMessage(){
    	progressLayout.setVisibility(View.GONE);
		alertModal.setVisibility(View.VISIBLE);
		alertText.setText("Success");
		alertOk.setOnClickListener(dismissSuccess);
		alertText.setVisibility(View.VISIBLE);
		alertOk.setVisibility(View.VISIBLE);
    }
    
    Handler successMessageHandler = new Handler(){
    	@Override
    	public void handleMessage (Message msg){
    		showSuccessMessage();
    	}
    };
    
    protected class ProgressIndicator implements Runnable{

		public void run() {
	    	Log.d("JoeTest", "Progress Image Update thread started");
			while (keepRunningProgressUpdate){
				ProgressImageHandler.sendMessage(new Message());
				try{
					Thread.sleep(100);
				}
				catch (InterruptedException ex){
					
				}
			}
	    	Log.d("JoeTest", "Progress Image Update thread ended");
		}
    }
    
    //////////////////////////////////////
    // Catalog List Inflation Utilities //
    //////////////////////////////////////
	
	static class Catalog{
		String name;
		String size;
		String count;
		
		Catalog(String catalogName, String catalogSize, String objectCount){
			name = catalogName;
			size = catalogSize;
			count = objectCount;
		}		
	}
	
	class CatalogAdapter extends ArrayAdapter<Catalog>{
		
		int listLayout;
		
		CatalogAdapter(Context context, int listLayout, ArrayList<Catalog> list){
			super(context, listLayout, R.id.catalog_name, list);
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
		private TextView description = null;
		private ImageView checked = null;
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
		
		TextView getDescription(){
			if (description == null){
				description = (TextView)row.findViewById(R.id.catalog_description);
			}
			return description;
		}
		
		ImageView getChecked(){
			if (checked == null){
				checked = (ImageView)row.findViewById(R.id.checkbox);
			}
			return checked;
		}
		
		void populateFrom(Catalog catalog){
			getName().setText(catalog.name);
			getDescription().setText(catalog.count + " Objects | " + catalog.size);
			getChecked().setImageResource(settingsRef.getCheckbox_Unselected());
		}
	}

}
