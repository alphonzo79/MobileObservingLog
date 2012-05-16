package com.mobileobservinglog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class AddEditEyepiece extends ActivityBase {
	RelativeLayout lowerCase;
    RelativeLayout upperCase;
    RelativeLayout numbers;
    
    @Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        customizeBrightness.setDimButtons(settingsRef.getButtonBrightness());
		
        //setup the layout
        setContentView(settingsRef.getAddEditEyepieceLayout());
        FrameLayout body = (FrameLayout)findViewById(R.id.keyboard_root);
        FrameLayout keyboardRoot = (FrameLayout)findViewById(R.id.keyboard_root);
        lowerCase = (RelativeLayout)findViewById(R.id.lower_case_letters);
        upperCase = (RelativeLayout)findViewById(R.id.upper_case_letters);
        numbers = (RelativeLayout)findViewById(R.id.numbers_and_symbols);
        
        Button lowerCaseShift = (Button)findViewById(R.id.shift);
        lowerCaseShift.setOnClickListener(showUpperCase);
        Button upperCaseShift = (Button)findViewById(R.id.uc_shift);
        upperCaseShift.setOnClickListener(showLowerCase);
        Button numbersSymbols = (Button)findViewById(R.id.numbers);
        numbersSymbols.setOnClickListener(showNumbers);
        Button lowerCaseNumbers = (Button)findViewById(R.id.uc_numbers);
        lowerCaseNumbers.setOnClickListener(showNumbers);
        Button letters = (Button)findViewById(R.id.letters);
        letters.setOnClickListener(showLetters);
        
        keyboardRoot.setVisibility(View.VISIBLE);
        lowerCase.setVisibility(View.VISIBLE);
	}

    protected final Button.OnClickListener showUpperCase = new Button.OnClickListener(){
    	public void onClick(View view){
    		lowerCase.setVisibility(View.INVISIBLE);
    		upperCase.setVisibility(View.VISIBLE);
    	}
    };

    protected final Button.OnClickListener showLowerCase = new Button.OnClickListener(){
    	public void onClick(View view){
    		upperCase.setVisibility(View.INVISIBLE);
    		lowerCase.setVisibility(View.VISIBLE);
    	}
    };

    protected final Button.OnClickListener showNumbers = new Button.OnClickListener(){
    	public void onClick(View view){
    		lowerCase.setVisibility(View.INVISIBLE);
    		upperCase.setVisibility(View.INVISIBLE);
    		numbers.setVisibility(View.VISIBLE);
    	}
    };

    protected final Button.OnClickListener showLetters = new Button.OnClickListener(){
    	public void onClick(View view){
    		lowerCase.setVisibility(View.VISIBLE);
    		numbers.setVisibility(View.INVISIBLE);
    	}
    };
}
