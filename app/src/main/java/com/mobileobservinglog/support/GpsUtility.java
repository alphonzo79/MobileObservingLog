/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsUtility {
	LocationManager locationManager;
	Location lastKnownLocation;
	Context context;
	
	public GpsUtility(Context context) {
		this.context = context;
	}
	
	public void setUpLocationService(){
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	lastKnownLocation = location;
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	public String getString(Location location) {
		String retVal = "";
		
		double longitude = lastKnownLocation.getLongitude();
		double latitude = lastKnownLocation.getLatitude();
		String northSouth = "";
		String eastWest = "";
		
		if(longitude > 0){
			eastWest = "E";
		}
		else{
			eastWest = "W";
		}
		
		if(latitude > 0){
			northSouth = "N";
		}
		else{
			northSouth = "S";
		}
		
		try{
    		String longitudeString = formatCoordinate(Location.convert(longitude, Location.FORMAT_SECONDS));
    		String lattitudeString = formatCoordinate(Location.convert(latitude, Location.FORMAT_SECONDS));
    		String formatedLocation = String.format("%s %s, %s %s", lattitudeString, northSouth, longitudeString, eastWest);
    		retVal = formatedLocation;
		}
		catch(SecurityException e){
		}
		catch(IllegalArgumentException e){
		}
		return retVal;
	}
    
    public String formatCoordinate(String rawString){
    	//The coordinate may be given in the format DD:DD:DD.DDDD
    	//We want to convert it into DD\u00B0DD'DD.DD"
    	String retVal;
    	String[] parsed = rawString.split(":");
    	if(parsed.length == 3){
    		String[] thirdParsed = parsed[2].split("\\.");
    		if(thirdParsed.length > 1 && thirdParsed[1].length() > 2){
    			thirdParsed[1] = thirdParsed[1].substring(0, 2);
    			parsed[2] = String.format("%s.%s", thirdParsed[0], thirdParsed[1]);
    		}
    		
    		if(parsed[0].charAt(0) == '-'){
    			parsed[0] = parsed[0].substring(1);
    		}
    		
    		retVal = String.format("%s\u00B0 %s' %s\"", parsed[0], parsed[1], parsed[2]);
    	}
    	else{
    		retVal = rawString;
    	}
    	return retVal;
    }
    
    public Location getLocation() {
    	try{
    		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		if(lastKnownLocation == null){
    			tryCourseLocation();
    		}
		}
		catch(SecurityException e){
			tryCourseLocation();
		}
		catch(IllegalArgumentException e){
			tryCourseLocation();
		}
    	return lastKnownLocation;
    }
    
    public Location tryCourseLocation(){
    	try{
    		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		catch(SecurityException e){
			lastKnownLocation = null;
		}
		catch(IllegalArgumentException e){
			lastKnownLocation = null;
		}
    	return lastKnownLocation;
    }
}
