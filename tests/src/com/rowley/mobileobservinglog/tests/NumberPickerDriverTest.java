/**
 * 
 */
package com.rowley.mobileobservinglog.tests;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;

import com.rowley.mobileobservinglog.ObservingLogActivity;
import com.rowley.strategies.NumberPickerDriver;

/**
 * @author Joe Rowley
 *
 */
public class NumberPickerDriverTest extends SingleLaunchActivityTestCase<ObservingLogActivity>{
	
	//Class Under Test
	com.rowley.strategies.NumberPickerDriver mCut = null;
	ObservingLogActivity mAut = null;
	Instrumentation mInstrumentation = null;
	ArrayList<String> possibleValues;
	
	public NumberPickerDriverTest(){
		super("com.rowley.mobileobservinglog", ObservingLogActivity.class);
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
	 * Test method for {@link com.rowley.strategies.NumberPickerDriver#upButton()}.
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
	 * Test method for {@link com.rowley.strategies.NumberPickerDriver#upButton()}.
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
	 * Test method for {@link com.rowley.strategies.NumberPickerDriver#downButton()}.
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
	 * Test method for {@link com.rowley.strategies.NumberPickerDriver#downButton()}.
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
	 * Test method for {@link com.rowley.strategies.NumberPickerDriver#downButton()}.
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
