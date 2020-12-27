package de.rpgframework.shadowrun6.items;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

import org.prelle.simplepersist.EnumValue;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.Shadowrun6Core;

public enum FireMode {
	@EnumValue("SA")
	SEMI_AUTOMATIC("SA"),
	@EnumValue("SS")
	SINGLE_SHOT("SS"),
	@EnumValue("BF")
	BURST_FIRE("BF"),
	@EnumValue("FA")
	FULL_AUTO("FA")
	;
	
	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(Shadowrun6Core.class, Locale.GERMAN, Locale.ENGLISH);

	String val;

	//-------------------------------------------------------------------
	private FireMode(String val) {
		this.val = val;
	}
	
	//-------------------------------------------------------------------
	public String getValue() {return val;}
	
	//-------------------------------------------------------------------
	public static FireMode getByValue(Locale loc, String val) {
		for (FireMode mode : FireMode.values()) {
			if (mode.getValue().equalsIgnoreCase(val))
				return mode;
			if (mode.getName(loc).equalsIgnoreCase(val))
				return mode;
		}
		throw new NoSuchElementException(val);
	}
	
	//-------------------------------------------------------------------
	public String getName(Locale locale) {
        try {
        	return ResourceI18N.get(RES, locale, "firemode."+this.name().toLowerCase());
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+RES.getBaseBundleName());
			return e.getKey();
		}
        
 	}
}