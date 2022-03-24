package de.rpgframework.shadowrun6.items;

import java.util.Locale;
import java.util.MissingResourceException;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.IVariantMode;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * Decision made when adding the item to the character.
 * @author prelle
 */
public enum SR6VariantMode implements IVariantMode {
	
	/** Carried in your hands or whatever the default is for such an item */
	NORMAL,
	/** Installed on a weapon mount in a drone or vehicle */
	MOUNTED,
	/** Installed as an accessory in another item */
	EMBEDDED,
	/** Directly implanted in your body */
	BODYWARE,
	/** Any kind of alternative or secondary usage */
	ALTERNATE
	;
	
	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IUsageMode#getName(java.util.Locale)
	 */
	@Override
	public String getName(Locale locale) {
        try {
        	return ResourceI18N.get(RES, locale, "equipmode."+this.name().toLowerCase());
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
