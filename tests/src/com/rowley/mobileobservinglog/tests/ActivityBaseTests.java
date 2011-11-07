package com.rowley.mobileobservinglog.tests;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import com.rowley.mobileobservinglog.ActivityBase;

public class ActivityBaseTests extends ActivityInstrumentationTestCase2<ActivityBase>{

	Activity mAut = null;
	
	public ActivityBaseTests(){
		super("com.rowley.mobileobservinglog", ActivityBase.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		mAut = this.getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	public void testDisplayMenu(){
		
	}
}
