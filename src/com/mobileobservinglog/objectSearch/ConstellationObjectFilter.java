/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.objectSearch;

import java.util.Set;

public class ConstellationObjectFilter extends AbstractObjectFilter {
	public ConstellationObjectFilter() {
		title = "Constellation";
		multiSelect = true;
	}

	@Override
	public String getSqlString() {
		String retVal = "";
		
		if(filters.containsValue(true)) {
			retVal = retVal.concat("constellation IN (");
			
			Set<String> keys = filters.keySet();
			String inParens = "";
			for(String key : keys) {
				if(filters.get(key)) {
					if(inParens.length() != 0) {
						inParens = inParens.concat(", ");
					}
					inParens = inParens.concat("'" + key + "'");
				}
			}
			
			retVal = retVal.concat(inParens + ")");
		}
		
		return retVal;
	}

	@Override
	protected void resetValues() {
		for(ConstellationFilterTypes type : ConstellationFilterTypes.values()) {
			filters.put(type.toString(), false);
		}
	}

	public enum ConstellationFilterTypes {
		ANDROMEDA("Andromeda"), ANTLIA("Antlia"), APUS("Apus"), AQUARIUS("Aquarius"), AQUILA("Aquila"), ARA("Ara"), ARIES("Aries"), AURIGA("Auriga"), 
		BOOTES("Bootes"), CAELUM("Caelum"), CAMELOPARDALIS("Camelopardalis"), CANCER("Cancer"), CANES_VENATICI("Canes Venatici"), CANIS_MAJOR("Canis Major"), 
		CANIS_MINOR("Canis Minor"), CAPRICORNUS("Capricornus"), CARINA("Carina"), CASSIOPEIA("Cassiopeia"), CENTAURUS("Centaurus"), CEPHUS("Cepheus"), 
		CETUS("Cetus"), CHAMAELEON("Chamaeleon"), CIRCINUS("Circinus"), COLUMBA("Columba"), COMA_BERENICES("Coma Berenices"), CORONA_AUS("Corona Australis"), 
		CORONA_BOR("Corona Borealis"), CORVUS("Corvus"), CRATER("Crater"), CRUX("Crux"), CYGNUS("Cygnus"), DELPHINUS("Delphinus"), DORADO("Dorado"), DRACO("Draco"), 
		EQUULEUS("Equuleus"), ERIDANUS("Eridanus"), FORNAX("Fornax"), GEMINI("Gemini"), GRUS("Grus"), HERCULES("Hercules"), HOROLOGIUM("Horologium"), HYDRA("Hydra"), 
		HYDRUS("Hydrus"), INDUS("Indus"), LACERTA("Lacerta"), LEO("Leo"), LEO_MINOR("Leo Minor"), LEPUS("Lepus"), LIBRA("Libra"), LUPUS("Lupus"), LYNX("Lynx"), 
		LYRA("Lyra"), MENSA("Mensa"), MICROSCOPIUM("Microscopium"), MONOCEROS("Monoceros"), MUSCA("Musca"), NORMA("Norma"), OCTANS("Octans"), OPHIUCUS("Ophiucus"), 
		ORION("Orion"), PAVO("Pavo"), PEGASUS("Pegasus"), PERSEUS("Perseus"), PHOENIX("Phoenix"), PICTOR("Pictor"), PISCES("Pisces"), PISCES_AUS("Pisces Austrinus"), 
		PUPPIS("Puppis"), PYXIS("Pyxis"), RETICULUM("Reticulum"), SAGITTA("Sagitta"), SAGITTARIUS("Sagittarius"), SCORPIUS("Scorpius"), SCULPTOR("Sculptor"), 
		SCUTUM("Scutum"), SERPENS("Serpens"), SEXTANS("Sextans"), TAURUS("Taurus"), TELESCOPIUM("Telescopium"), TRIANGULUM("Triangulum"), 
		TRIANGULUM_AUS("Triangulum Australe"), TUCANA("Tucana"), URSA_MAJOR("Ursa Major"), URSA_MINOR("Ursa Minor"), VELA("Vela"), VIRGO("Virgo"), VOLANS("Volans"), 
		VULPECULA("Vulpecula");  
		
		private String filterText;
		
		ConstellationFilterTypes(String text) {
			this.filterText = text;
		}
		
		public String toString() {
			return filterText;
		}
	}
}
