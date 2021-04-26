package de.rpgframework.shadowrun6.items;

import java.util.Locale;
import java.util.MissingResourceException;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.IUsageMode;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public enum SR6UsageMode implements IUsageMode {
	
	/** Carried*/
	NORMAL,
	IMPLANTED,
	EMBEDDED,
	/** Added to an weapon mount */
	MOUNTED,
	;
	
	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(Shadowrun6Core.class, "core", Locale.ENGLISH, Locale.GERMAN);

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IUsageMode#getName(java.util.Locale)
	 */
	@Override
	public String getName(Locale locale) {
        try {
        	return ResourceI18N.get(RES, locale, "usagemode."+this.name().toLowerCase());
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+RES.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IUsageMode#getName()
	 */
	@Override
	public String getName() {
		return getName(Locale.getDefault());
	}

}
