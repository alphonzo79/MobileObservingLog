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

import java.util.ArrayList;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;

import com.mobileobservinglog.ObservingLogActivityParent;
import com.mobileobservinglog.strategies.NumberPickerDriver;

/**
 * @author Joe Rowley
 *
 */
public class NumberPickerDriverTest extends SingleLaunchActivityTestCase<ObservingLogActivityParent>{
	
	//Class Under Test
	com.mobileobservinglog.strategies.NumberPickerDriver mCut = null;
	ObservingLogActivityParent mAut = null;
	Instrumentation mInstrumentation = null;
	ArrayList<String> possibleValues;
	
	public NumberPickerDriverTest(){
		super("com.mobileobservinglog", ObservingLogActivityParent.class);
		setupArrayList();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
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
	
	private void setupArrayList(){
		possibleValues = new ArrayList<String>();
		for (int i = 1; i <= 10; i++){
			possibleValues.add(String.valueOf(i));
		}
	}
	
	/**
	 *test method for {@link com.mobileobservinglog.strategies.NumberPickerDriver#upButton()}.
	 * 
	 * Simple test on the up button. We'll push it a couple times and make sure it works as expected
	 */
	public void testUpButtonSimple() {
		mCut = new NumberPickerDriver(possibleValues, "2", mAut) {			
			@Override
			public boolean save() {
				return false;
			}
		};
		mCut.upButton();
		assertEquals("The number picker did not increment as expected", "3", mCut.getCurrentValue());
		mCut.upButton();
		assertEquals("The number picker did not increment as expected", "4", mCut.getCurrentValue());
	}
	
	/** 
	 *test method for {@link com.mobileobservinglog.strategies.NumberPickerDriver#upButton()}.
	 * 
	 * Bounds test on the up button. We'll start at 10, push it and make sure it works as expected
	 */
	public void testUpButtonBounds() {
		mCut = new NumberPickerDriver(possibleValues, "10", mAut) {			
			@Override
			public boolean save() {
				return false;
			}
		};
		mCut.upButton();
		assertEquals("The number picker did not increment as expected", "1", mCut.getCurrentValue());
	}
	
	/**
	 * Test method for {@link com.mobileobservinglog.strategies.NumberPickerDriver#downButton()}.
	 * 
	 * Simple test on the down button. We'll push it a couple times and make sure it works as expected
	 */
	public void testDownButtonSimple() {
		mCut = new NumberPickerDriver(possibleValues, "4", mAut) {			
			@Override
			public boolean save() {
				return false;
			}
		};
		mCut.downButton();
		assertEquals("The number picker did not increment as expected", "3", mCut.getCurrentValue());
		mCut.downButton();
		assertEquals("The number picker did not increment as expected", "2", mCut.getCurrentValue());
	}
	
	/**
	 * Test method for {@link com.mobileobservinglog.strategies.NumberPickerDriver#downButton()}.
	 * 
	 * Bounds test on the down button. We'll start at 1, push it and make sure it works as expected
	 */
	public void testDownButtonBounds() {
		mCut = new NumberPickerDriver(possibleValues, "1", mAut) {			
			@Override
			public boolean save() {
				return false;
			}
		};
		mCut.downButton();
		assertEquals("The number picker did not increment as expected", "10", mCut.getCurrentValue());
	}
	
	/**
	 * Test method for {@link com.mobileobservinglog.strategies.NumberPickerDriver#downButton()}.
	 * 
	 * Change of direction test on up and down buttons. This is necessary because without extra logic, changing directions requires two presses
	 * to change the current value because of the way the iterator works
	 */
	public void testChangeDirection() {
		mCut = new NumberPickerDriver(possibleValues, "4", mAut) {			
			@Override
			public boolean save() {
				return false;
			}
		};
		mCut.downButton();
		assertEquals("The number picker did not increment as expected", "3", mCut.getCurrentValue());
		mCut.upButton();
		assertEquals("The number picker did not increment as expected", "4", mCut.getCurrentValue());
		mCut.downButton();
		assertEquals("The number picker did not increment as expected", "3", mCut.getCurrentValue());
	}
}
