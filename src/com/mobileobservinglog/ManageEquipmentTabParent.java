package com.mobileobservinglog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileobservinglog.R;

public class ManageEquipmentTabParent extends ActivityBase {

	//joint member variables
	Button addEquipmentButton;
	RelativeLayout alertModal;
	TextView alertText;
	Button alertEdit;
	Button alertDelete;
	Button alertCancel;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
	
	//For this screen, the tabs layout was causing problems with our regular toggle mode handling. So instead on this screen we will simply relaunch the activity,
	//then kill the current instance so a back press will not take us back to the other mode.
	@Override
	protected void toggleMode(){
		super.toggleMode();
		Intent intent = new Intent(this.getApplication(), ManageEquipmentScreen.class);
        startActivity(intent);
        finish();
	}
	
	protected void findModalElements(){
		alertModal = (RelativeLayout)findViewById(R.id.alert_modal);
        alertText = (TextView)findViewById(R.id.alert_main_text);
        alertEdit = (Button)findViewById(R.id.alert_ok_button);
        alertDelete = (Button)findViewById(R.id.alert_cancel_button);
        alertCancel = (Button)findViewById(R.id.alert_extra_button);
	}
}
