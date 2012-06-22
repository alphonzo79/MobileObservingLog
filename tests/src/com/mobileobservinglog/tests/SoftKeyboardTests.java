/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.tests;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.widget.EditText;

import com.mobileobservinglog.AddEditEyepiece;
import com.mobileobservinglog.SettingsContainer;
import com.mobileobservinglog.SettingsContainer.SessionMode;
import com.mobileobservinglog.softkeyboard.SoftKeyboardTestHook;
import com.mobileobservinglog.softkeyboard.SoftKeyboard.TargetInputType;

public class SoftKeyboardTests extends SingleLaunchActivityTestCase<com.mobileobservinglog.AddEditEyepiece>{
	
	//Class Under Test
	SoftKeyboardTestHook mCut = null;
	AddEditEyepiece mAut = null;
	Instrumentation mInstrumentation = null;
	
	//Text field to use
	EditText textField;
	
	public SoftKeyboardTests(){
		super("com.mobileobservinglog", AddEditEyepiece.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mAut = getActivity();
		//textField = (EditText)mAut.findViewById(com.mobileobservinglog.R.id.eyepiece_type);
		textField = new EditText(mAut);
		mCut = new SoftKeyboardTestHook(mAut, textField, TargetInputType.LETTERS);
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
		Assert.assertEquals("Failed to add text at the end", "test text!", foundText);
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#insertText()}.
	 * 
	 */
	public void testInsertTextAtBeginning() {
		textField.setText("test text");
		textField.setSelection(0);
		mCut.testInsertText('!');
		String foundText = textField.getText().toString();
		Assert.assertEquals("Failed to add text at the beginning", "!test text", foundText);
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#insertText()}.
	 * 
	 */
	public void testInsertTextInMiddle() {
		textField.setText("test text");
		textField.setSelection(3);
		mCut.testInsertText('!');
		String foundText = textField.getText().toString();
		Assert.assertEquals("Failed to add text in the middle", "tes!t text", foundText);
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#insertText()}.
	 * 
	 */
	public void testInsertTextInSelection() {
		textField.setText("test text");
		textField.setSelection(3, 7);
		mCut.testInsertText('!');
		String foundText = textField.getText().toString();
		Assert.assertEquals("Failed to add text in place of a selection", "tes!xt", foundText);
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#handleBackspace()}.
	 * 
	 */
	public void testHandleBackspaceAtEnd() {
		textField.setText("test text");
		textField.setSelection(9);
		mCut.testHandleBackspace();
		String foundText = textField.getText().toString();
		String expected = "test tex";
		Assert.assertEquals("Failed to remove a character at the end. Expected: " + expected + ". Found: " + foundText, expected, foundText);
		Assert.assertEquals("The cursor is in the wrong spot", 8, textField.getSelectionStart());
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#handleBackspace()}.
	 * 
	 */
	public void testHandleBackspaceAtBeginning() {
		textField.setText("test text");
		textField.setSelection(0);
		mCut.testHandleBackspace();
		String foundText = textField.getText().toString();
		String expected = "test text";
		Assert.assertEquals("Failed to handle the backspace at the beginning. Expected: " + expected + ". Found: " + foundText, expected, foundText);
		Assert.assertEquals("The cursor is in the wrong spot", 0, textField.getSelectionStart());
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#handleBackspace()}.
	 * 
	 */
	public void testHandleBackspaceInMiddle() {
		textField.setText("test text");
		textField.setSelection(5);
		mCut.testHandleBackspace();
		String foundText = textField.getText().toString();
		String expected = "testtext";
		Assert.assertEquals("Failed to delete text in the middle. Expected: " + expected + ". Found: " + foundText, expected, foundText);
		Assert.assertEquals("The cursor is in the wrong spot", 4, textField.getSelectionStart());
	}

	/**
	 * Test method for {@link com.mobileobservinglog.softkeyboard.SoftKeyboard#handleBackspace()}.
	 * 
	 */
	public void testHandleBackspaceWithSelection() {
		textField.setText("test text");
		textField.setSelection(2, 7);
		mCut.testHandleBackspace();
		String foundText = textField.getText().toString();
		String expected = "text";
		Assert.assertEquals("Failed to delete a chunk of text. Expected: " + expected + ". Found: " + foundText, expected, foundText);
		Assert.assertEquals("The cursor is in the wrong spot", 2, textField.getSelectionStart());
	}
}
