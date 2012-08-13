package com.mobileobservinglog.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class ObjectIndexFilter {
	private ObjectIndexFilter(){
		clearFilter();
	}
	//TODO filterIsSet = true on all setFilter....
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
		searchString = "";
		resetLogged();
		resetType();
		resetMag();
		resetConstellation();
		resetSeason();
		resetCatalog();
	}
	private void setFilter(){
		filterIsSet = true;
	}	
	public boolean isSet(){
		return filterIsSet;
	}
	
	public String getFullFilterString(){
		String retVal = "";
		if(!isSet()){
			retVal = "None";
		}
		else{
			String[] allStrings = new String[]{getLoggedFilterString(), getTypeFilterString(), getMagFilterString(), getConstellationFilterString(), 
					getSearchString(), getCatalogFilterString(), getSearchString()};
			for(String filterString : allStrings){
				if(!filterString.equals("All")){
					if(retVal.length() > 0){
						retVal = retVal.concat(", ");
					}
					retVal = retVal.concat(filterString);
				}
			}
		}
		return retVal;
	}
	
	//filter criteria
	private String searchString;
	public void setSearchString(String searchString){
		this.searchString = searchString;
		setFilter();
	}
	public String getSearchString(){
		return searchString;
	}
	
	private HashMap<String, Boolean> logged;
	private void resetLogged(){
		logged = new HashMap<String, Boolean>();
		logged.put("All", true);
		logged.put("Logged", false);
		logged.put("Not Logged", false);
	}
	public void setLoggedFilter(String key, boolean value){
		if(key.equals("All") && value){
			resetLogged();
		}
		else{
			logged.put(key, value);
			logged.put("All", false);	
		}
	}
	public String getLoggedFilterString(){
		return formatString(logged);
	}
	public boolean getLoggedFilterValue(String key){
		return logged.get(key);
	}
	
	private HashMap<String, Boolean> type;
	private void resetType(){
		type = new HashMap<String, Boolean>();
		type.put("All", true);
		type.put("Globular Cluster", false);
		type.put("Open Cluster", false);
		type.put("Galaxy", false);
		type.put("Reflection Nebula", false);
		type.put("Emmission Nebula", false);
		type.put("Planetary Nebula", false);
		type.put("Dark Nebula", false);
		type.put("Binary Star", false);
		type.put("Milky Way Patch", false);
		type.put("Supernova Remnant", false);
		type.put("Asterism", false);
	}
	public void setTypeFilter(String key, boolean value){
		if(key.equals("All") && value){
			resetType();
		}
		else{
			type.put(key, value);
			type.put("All", false);	
		}
	}
	public String getTypeFilterString(){
		return formatString(type);
	}
	public boolean getTypeFilterValue(String key){
		return type.get(key);
	}
	
	//this one is single-select
	private HashMap<String, Boolean> magnitude;
	private void resetMag(){
		magnitude = new HashMap<String, Boolean>();
		magnitude.put("All", true);
		magnitude.put("1", false);
		magnitude.put("2", false);
		magnitude.put("3", false);
		magnitude.put("4", false);
		magnitude.put("5", false);
		magnitude.put("6", false);
		magnitude.put("7", false);
		magnitude.put("8", false);
		magnitude.put("9", false);
		magnitude.put("10", false);
		magnitude.put("11", false);
		magnitude.put("12", false);
		magnitude.put("13", false);
	}
	public void setMagFilter(String key, boolean value){
		resetMag();
		magnitude.put("All", false);
		magnitude.put(key, value);
	}
	public String getMagFilterString(){
		if(magnitude.get("All")){
			return "All";
		}
		else{
			return String.format("%s and brighter", formatString(magnitude));
		}
	}public boolean getMagFilterValue(String key){
		return magnitude.get(key);
	}
	
	public HashMap<String, Boolean> constellation;
	private void resetConstellation(){
		constellation = new HashMap<String, Boolean>();
		constellation.put("All", true);
		constellation.put("Andromeda", false);
		constellation.put("Antlia", false);
		constellation.put("Apus", false);
		constellation.put("Aquarius", false);
		constellation.put("Aquila", false);
		constellation.put("Ara", false);
		constellation.put("Aries", false);
		constellation.put("Auriga", false);
		constellation.put("Bootes", false);
		constellation.put("Caelum", false);
		constellation.put("Camelopardalis", false);
		constellation.put("Cancer", false);
		constellation.put("Canes Venatici", false);
		constellation.put("Canis Major", false);
		constellation.put("Canis Minor", false);
		constellation.put("Capricornus", false);
		constellation.put("Carina", false);
		constellation.put("Cassiopeia", false);
		constellation.put("Centaurus", false);
		constellation.put("Cepheus", false);
		constellation.put("Cetus", false);
		constellation.put("Chamaleon", false);
		constellation.put("Circinus", false);
		constellation.put("Columba", false);
		constellation.put("Coma Berenices", false);
		constellation.put("Corona Australis", false);
		constellation.put("Corona Borealis", false);
		constellation.put("Corvus", false);
		constellation.put("Crater", false);
		constellation.put("Crux", false);
		constellation.put("Cygnus", false);
		constellation.put("Delphinus", false);
		constellation.put("Dorado", false);
		constellation.put("Draco", false);
		constellation.put("Equuleus", false);
		constellation.put("Eridanus", false);
		constellation.put("Fornax", false);
		constellation.put("Gemini", false);
		constellation.put("Grus", false);
		constellation.put("Hercules", false);
		constellation.put("Horologium", false);
		constellation.put("Hydra", false);
		constellation.put("Hydrus", false);
		constellation.put("Indus", false);
		constellation.put("Lacerta", false);
		constellation.put("Leo", false);
		constellation.put("Leo Minor", false);
		constellation.put("Lepus", false);
		constellation.put("Libra", false);
		constellation.put("Lupus", false);
		constellation.put("Lynx", false);
		constellation.put("Lyra", false);
		constellation.put("Mensa", false);
		constellation.put("Microscopium", false);
		constellation.put("Monoceros", false);
		constellation.put("Musca", false);
		constellation.put("Norma", false);
		constellation.put("Octans", false);
		constellation.put("Ophiucus", false);
		constellation.put("Orion", false);
		constellation.put("Pavo", false);
		constellation.put("Pegasus", false);
		constellation.put("Perseus", false);
		constellation.put("Phoenix", false);
		constellation.put("Pictor", false);
		constellation.put("Pisces", false);
		constellation.put("Pisces Austrinus", false);
		constellation.put("Puppis", false);
		constellation.put("Pyxis", false);
		constellation.put("Reticulum", false);
		constellation.put("Sagitta", false);
		constellation.put("Sagittarius", false);
		constellation.put("Scorpius", false);
		constellation.put("Sculptor", false);
		constellation.put("Scutum", false);
		constellation.put("Serpens", false);
		constellation.put("Sextans", false);
		constellation.put("Taurus", false);
		constellation.put("Telescopium", false);
		constellation.put("Triangulum", false);
		constellation.put("Triangulum Australe", false);
		constellation.put("Tucana", false);
		constellation.put("Ursa Major", false);
		constellation.put("Ursa Minor", false);
		constellation.put("Vela", false);
		constellation.put("Virgo", false);
		constellation.put("Volans", false);
		constellation.put("Vulpecula", false);
	}
	public void setConstellationFilter(String key, boolean value){
		if(key.equals("All") && value){
			resetConstellation();
		}
		else{
			constellation.put(key, value);
			constellation.put("All", false);	
		}
	}
	public String getConstellationFilterString(){
		return formatString(constellation);
	}
	public boolean getConstellationFilterValue(String key){
		return constellation.get(key);
	}
	
	public HashMap<String, Boolean> season;
	private void resetSeason(){
		season = new HashMap<String, Boolean>();
		season.put("All", true);
		season.put("Winter", false);
		season.put("Spring", false);
		season.put("Summer", false);
		season.put("Fall", false);
	}
	public void setSeasonFilter(String key, boolean value){
		if(key.equals("All") && value){
			resetSeason();
		}
		else{
			season.put(key, value);
			season.put("All", false);
		}
	}
	public String getSeasonFilterString(){
		return formatString(season);
	}
	public boolean getSeasonFilterValue(String key){
		return season.get(key);
	}
	
	private HashMap<String, Boolean> catalog;
	public void resetCatalog(){
		catalog = new HashMap<String, Boolean>();
		catalog.put("All", true);
		catalog.put("Messier Catalog", false);
	}
	public void setCatalogFilter(String key, boolean value){
		if(key.equals("All") && value){
			resetCatalog();
		}
		else{
			season.put(key, value);
			season.put("All", false);
		}
	}
	public String getCatalogFilterString(){
		return formatString(catalog);
	}
	public boolean getCatalogFilterValue(String key){
		return catalog.get(key);
	}
	
	private String formatString(HashMap<String, Boolean> mapToOutput){
		Iterator<Entry<String, Boolean>> it = mapToOutput.entrySet().iterator();
		String retVal = "";
		while(it.hasNext()){
			Entry<String, Boolean> thisEntry = (Entry<String, Boolean>) it.next();
			if(thisEntry.getValue()){
				if(retVal.length() > 0){
					retVal = retVal.concat(", ");
				}
				retVal = retVal.concat(thisEntry.getKey());
			}			
		}
		return retVal;
	}
}
