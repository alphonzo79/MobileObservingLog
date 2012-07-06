package com.mobileobservinglog.support;

public class ObjectIndexFilter {
	private ObjectIndexFilter(){
		clearFilter();
	}
	
	private static ObjectIndexFilter ref;
	public static synchronized ObjectIndexFilter getReference(){
		if(ref == null){
			ref = new ObjectIndexFilter();
		}
		return ref;
	}
	
	private boolean filterIsSet;
	
	public void clearFilter(){
		filterIsSet = false;
	}
	
	public boolean isSet(){
		return filterIsSet;
	}
}
