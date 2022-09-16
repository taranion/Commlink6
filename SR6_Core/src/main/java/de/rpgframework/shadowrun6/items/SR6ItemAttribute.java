package de.rpgframework.shadowrun6.items;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.persist.EnumConverter;
import de.rpgframework.genericrpg.persist.FloatConverter;
import de.rpgframework.genericrpg.persist.IntegerArrayConverter;
import de.rpgframework.genericrpg.persist.IntegerConverter;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public enum SR6ItemAttribute implements IItemAttribute {
	
	ACCELERATION,
	AMMUNITION,
	// Vehicle Armor
	ARMOR,
	ATTACK_RATING(new IntegerArrayConverter()),
	/** Boolean: is this item subject to augmentation grade changes */
	AUGMENTATION,
	AVAILABILITY(new AvailabilityConverter()),
	// Vehicle Body
	BODY,
	// Vehicle Cargo Factor (CF)
	CARGO,
	CAPACITY,
//	CONCEALABILITY,
	DAMAGE(new WeaponDamageConverter()),
	DEFENSE_MATRIX,
	// Defense Rating against physical attacks
	DEFENSE_PHYSICAL,
	// Defense Rating against social attacks
	DEFENSE_SOCIAL,
	DAMAGE_REDUCTION,
//	DEFENSE_RATING,
	DEVICE_RATING,
	ESSENCECOST(new FloatConverter()),
	FIREMODES,
	FORCE,
	HANDLING,
//	HAS_RATING,
	/* For accessories: Where to attach it */
	HOOK( new EnumConverter(ItemHook.class)),
	ITEMTYPE( new EnumConverter(ItemType.class)),
	ITEMSUBTYPE( new EnumConverter(ItemSubType.class)),
	
//	/** Maximum rating of embedded items */
	MAX_SENSOR_RATING,
	MAX_SKILLSOFT_RATING,
//	MODIFICATION_SLOTS,
	// Vehicle Pilot
	PILOT,
	PRICE,
	// 
	SOFTWARE_TYPES( new EnumConverter(SoftwareTypes.class)),
	QUALITY,
	RANGE,
	RATING,
	SEATS,
	SENSORS,
	/** The amount of capacity slots required */
	SIZE,
	SKILL,
	SKILL_SPECIALIZATION,
//	SOCIAL,
	SPEED_INTERVAL,
	TOPSPEED,
	VEHICLE_TYPE,

	ATTACK,
	SLEAZE,
	DATA_PROCESSING,
	FIREWALL,
	CONCURRENT_PROGRAMS,
	;
	
	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	private StringValueConverter<? extends Object> converter;

	//-------------------------------------------------------------------
	private SR6ItemAttribute() {
		// Having a default int converter breaks ENUMs
		converter = new IntegerConverter();
	}

	//-------------------------------------------------------------------
	private SR6ItemAttribute(StringValueConverter<? extends Object> conv) {
		converter = conv;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IItemAttribute#calculateModifiedValue(java.lang.Object, java.util.List)
	 */
	public <T> T calculateModifiedValue(Object base, List<Modification> mods) {
		if (this==AVAILABILITY)
			return (T) SR6GearTool.calculateModifiedValue((Availability) base, mods);
		if (this!=ITEMSUBTYPE && this!=ITEMTYPE)
			System.err.println("SR6ItemAttribute: Don't know how to calculate modified value for "+this);
		return (T)base;
	}

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
	public String getShortName() {
		return getShortName(Locale.getDefault());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IItemAttribute#resolve(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SR6ItemAttribute resolve(String key) {
		return SR6ItemAttribute.valueOf(key);
	}
	
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.data.IItemAttribute#resolve(java.lang.String)
//	 */
//	@Override
//	public <T> T resolve(String key) {
//		LogManager.getLogger("shadowrun6.core").debug("Resolve "+key);
//		try {
//			if (converter!=null)
//				return (T) converter.read(key);
//		} catch (Exception e1) {
//			throw new IllegalArgumentException(e1);
//		}
//		try {
//			return (T)Integer.valueOf(key);
//		} catch (NumberFormatException e) {
//			return (T)key;
//		}
//	}
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.data.IItemAttribute#toString(java.lang.Object)
//	 */
//	@Override
//	public <E extends Object> String toString(E value) {
//		if (converter!=null) {
//			try {
//				return ((StringValueConverter<E>)converter).write(value);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		// TODO Auto-generated method stub
//		return String.valueOf(value);
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IItemAttribute#getConverter()
	 */
	@Override
	public <T> StringValueConverter<T> getConverter() {
		return (StringValueConverter<T>) converter;
	}

}
