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

import com.mobileobservinglog.R;
import com.mobileobservinglog.R.id;
import com.mobileobservinglog.support.database.PersonalInfoDAO;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonalInfoScreen extends ActivityBase {

	//gather resources
	LinearLayout body;
	
	Button editButton;
	TextView fullNameField;
	TextView addressField;
	TextView phoneField;
	TextView emailField;
	TextView clubField;
	
	String nameText;
	String addressText;
	String phoneText;
	String emailText;
	String clubText;
	
	@Override
    public void onCreate(Bundle icicle) {
		Log.d("JoeDebug", "PersonalInfo onCreate. Current session mode is " + settingsRef.getSessionMode());
        super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getPersonalInfoLayout());
        body = (LinearLayout)findViewById(R.id.personal_info_root); 
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
		Log.d("JoeDebug", "PersonalInfo onResume. Current session mode is " + settingsRef.getSessionMode());
        super.onResume();
        setLayout();
    }
	
    //Used by the Toggle Mode menu item method in ActivityBase. Reset the layout and force the redraw
	@Override
	public void setLayout(){
		setContentView(settingsRef.getPersonalInfoLayout());
		super.setLayout();
		findFields();
		findButtonSetListener();
		populateFields();
		body.postInvalidate();
	}
	
	private void findFields(){
		fullNameField = (TextView)findViewById(R.id.personal_info_top_label);
		addressField = (TextView)findViewById(R.id.view_personal_info_address);
		phoneField = (TextView)findViewById(R.id.view_personal_info_phone);
		emailField = (TextView)findViewById(id.view_personal_info_email);
		clubField = (TextView)findViewById(R.id.view_personal_info_club);
	}
	
	private void findButtonSetListener(){
		editButton = (Button)findViewById(R.id.view_personal_info_edit);
		editButton.setOnClickListener(editPersonalInfo);
	}
	
	private void populateFields(){
		PersonalInfoDAO db = new PersonalInfoDAO(this);
		Cursor personalInfo = db.getPersonalInfo();
		personalInfo.moveToFirst();
		
		nameText = personalInfo.getString(1);
		addressText = personalInfo.getString(2);
		phoneText = personalInfo.getString(3);
		emailText = personalInfo.getString(4);
		clubText = personalInfo.getString(5);
		
		if(!nameText.equals("")) {
			fullNameField.setText(nameText);
		} else {
			fullNameField.setText("Press \"Edit Information\" to change");
		}
		if(!addressText.equals("")) {
			addressField.setText(addressText);
		} else {
			addressField.setText("Press \"Edit Information\" to change");
		}
		if(!phoneText.equals("")) {
			phoneField.setText(phoneText);
		} else {
			phoneField.setText("Press \"Edit Information\" to change");
		}
		if(!emailText.equals("")) {
			emailField.setText(emailText);
		} else {
			emailField.setText("Press \"Edit Information\" to change");
		}
		if(!clubText.equals("")) {
			clubField.setText(clubText);
		} else {
			clubField.setText("Press \"Edit Information\" to change");
		}
	}
    
    protected final Button.OnClickListener editPersonalInfo = new Button.OnClickListener() {
		public void onClick(View view){
			//start new intent
			Intent intent = new Intent(PersonalInfoScreen.this.getApplication(), EditPersonalInfoScreen.class);
	        startActivity(intent);
        }
    };
}
