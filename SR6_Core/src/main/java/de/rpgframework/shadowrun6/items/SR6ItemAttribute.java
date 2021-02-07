package de.rpgframework.shadowrun6.items;

import java.util.Locale;
import java.util.MissingResourceException;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public enum SR6ItemAttribute implements IItemAttribute {
	
//	ACCELERATION,
//	AMMUNITION,
//	ARMOR,
	ATTACK_RATING,
	AVAILABILITY,
//	BODY,
//	CAPACITY,
//	CONCEALABILITY,
	DAMAGE,
	RATING,
//	DAMAGE_REDUCTION,
//	DEFENSE_RATING,
//	DEVICE_RATING,
//	ESSENCECOST,
//	HANDLING,
//	HAS_RATING,
//	MODE,
//	/** Maximum rating of embedded items */
//	MAX_SENSOR_RATING,
//	MODIFICATION_SLOTS,
//	PILOT,
	PRICE,
//	QUALITY,
//	SEATS,
//	SENSORS,
//	SKILL,
//	SKILL_SPECIALIZATION,
//	SOCIAL,
//	SPEED_INTERVAL,
//	SPEED,
//
//	ATTACK,
//	SLEAZE,
//	DATA_PROCESSING,
//	FIREWALL,
//	CONCURRENT_PROGRAMS,
	;
	
	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(Shadowrun6Core.class, "core", Locale.ENGLISH, Locale.GERMAN);

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IItemAttribute#getName(java.util.Locale)
	 */
	@Override
	public String getName(Locale locale) {
        try {
        	return ResourceI18N.get(RES, locale, "itemattribute."+this.name().toLowerCase());
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+RES.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IItemAttribute#getName()
	 */
	@Override
	public String getName() {
		return getName(Locale.getDefault());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IItemAttribute#getShortName(java.util.Locale)
	 */
	@Override
	public String getShortName(Locale locale) {
        try {
        	return ResourceI18N.get(RES, locale, "itemattribute."+this.name().toLowerCase()+".short");
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+RES.getBaseBundleName());
			return e.getKey();
		}
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IItemAttribute#resolve(java.lang.String)
	 */
	@Override
	public <T> T resolve(String key) {
//		try {
//			if (converter!=null)
//				return (T) converter.read(key);
//		} catch (Exception e1) {
//			throw new IllegalArgumentException(e1);
//		}
		try {
			return (T)Integer.valueOf(key);
		} catch (NumberFormatException e) {
			return (T)key;
		}
	}
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IItemAttribute#toString(java.lang.Object)
	 */
	@Override
	public <E extends Object> String toString(E value) {
//		if (converter!=null) {
//			try {
//				return converter.write(bla);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		// TODO Auto-generated method stub
		return String.valueOf(value);
	}

}
