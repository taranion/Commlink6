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
	PRIMARY,
	/** 
	 * For things which's primary job isn't to be a weapon, but
	 * can be used as such. 
	 */
	WEAPON,
	/**
	 * Item duplicates as drone or vehicle
	 */
	VEHICLE,
	;
	
	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

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
