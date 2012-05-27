package com.mobileobservinglog.tests;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.widget.EditText;

import com.mobileobservinglog.ObservingLogActivity;
import com.mobileobservinglog.SettingsContainer;
import com.mobileobservinglog.SettingsContainer.SessionMode;
import com.mobileobservinglog.softkeyboard.SoftKeyboardTestHook;

public class SoftKeyboardTests extends SingleLaunchActivityTestCase<com.mobileobservinglog.ObservingLogActivity>{
	
	//Class Under Test
	SoftKeyboardTestHook mCut = null;
	ObservingLogActivity mAut = null;
	Instrumentation mInstrumentation = null;
	
	//Text field to use
	EditText textField;
	
	public SoftKeyboardTests(){
		super("com.mobileobservinglog", ObservingLogActivity.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		textField = new EditText(mAut);
		mCut = new SoftKeyboardTestHook(mAut, textField);
		mAut = getActivity();
		mInstrumentation = getInstrumentation();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		mCut = null;
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#insertText()}.
	 * 
	 */
	public void testInsertTextAtEnd() {
		textField.setText("test text");
		textField.setSelection(9);
		mCut.testInsertText('!');
		String foundText = textField.getText().toString();
		Assert.assertEquals("test text!", foundText);
	}

}
