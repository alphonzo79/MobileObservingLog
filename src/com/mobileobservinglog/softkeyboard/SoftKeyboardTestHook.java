package com.mobileobservinglog.softkeyboard;

import android.app.Activity;
import android.widget.EditText;

public class SoftKeyboardTestHook extends SoftKeyboard{

	public SoftKeyboardTestHook(Activity context, EditText textView, TargetInputType input) {
		super(context, textView, input);
	}

	public void testInsertText(char character){
		insertText(character);
	}
	
	public void testHandleBackspace(){
		handleBackspace();
	}
}
