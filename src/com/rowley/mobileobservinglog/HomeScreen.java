package com.rowley.mobileobservinglog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeScreen extends ActivityBase implements View.OnClickListener{

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.homescreen);
        TextView footerText = (TextView)findViewById(R.id.initialFooter);
        footerText.setText("Home Screen -- Yay \r\n\r\n" + getSessionMode().toString());
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
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
