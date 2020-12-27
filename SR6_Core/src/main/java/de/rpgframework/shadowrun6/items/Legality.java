package de.rpgframework.shadowrun6.items;

import java.util.Locale;
import java.util.MissingResourceException;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.Shadowrun6Core;

public enum Legality {
	LEGAL,
	RESTRICTED,
	FORBIDDEN
	;
	
	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(Shadowrun6Core.class, "core", Locale.GERMAN, Locale.ENGLISH);

	//-------------------------------------------------------------------
	public String getShortCode(Locale locale) {
		if (this==LEGAL)
			return "";
        try {
        	return ResourceI18N.get(RES, locale, "legality."+this.name().toLowerCase());
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+RES.getBaseBundleName());
			return e.getKey();
		}
	}
	
	//-------------------------------------------------------------------
	public static Legality valueOfShortCode(String val) {
		if (val.equalsIgnoreCase("f") || val.equalsIgnoreCase("v") || val.equalsIgnoreCase("i"))
			return Legality.FORBIDDEN;
		if (val.equalsIgnoreCase("r") || val.equalsIgnoreCase("e") || val.equalsIgnoreCase("l"))
			return Legality.RESTRICTED;
		for (Legality tmp : Legality.values())
			if (tmp.getShortCode(Locale.getDefault()).equalsIgnoreCase(val))
				return tmp;
		throw new IllegalArgumentException(val);
	}
	
}

