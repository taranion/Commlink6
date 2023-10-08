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
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AmmunitionConverter;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.persist.AttackRatingArrayConverter;
import de.rpgframework.shadowrun6.persist.OnRoadOffRoadConverter;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public enum SR6ItemAttribute implements IItemAttribute {

	ACCELERATION(new OnRoadOffRoadConverter()),
	AMMUNITION(new AmmunitionConverter()),
	AMMUNITION_CLASS( new EnumConverter(AmmunitionClass.class)),
	// Vehicle Armor
	ARMOR,
	ATTACK_RATING(new AttackRatingArrayConverter()),
	/** Boolean: is this item subject to augmentation grade changes */
	AUGMENTATION,
	AVAILABILITY(new AvailabilityConverter()),
	BLAST_GZ(new WeaponDamageConverter()),
	BLAST_CLOSE(new WeaponDamageConverter()),
	BLAST_NEAR(new WeaponDamageConverter()),
	BLAST_RANGE,
	// Vehicle Body
	BODY,
	// Vehicle Cargo Factor (CF)
	CARGO,
	CAPACITY,
	CONCEALABILITY,
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
	FIREMODES( (StringValueConverter)null),
	FORCE,
	HANDLING(new OnRoadOffRoadConverter()),
//	HAS_RATING,
	/* For accessories: Where to attach it */
	HOOK( new EnumConverter(ItemHook.class)),
	ITEMTYPE( new EnumConverter(ItemType.class)),
	ITEMSUBTYPE( new EnumConverter(ItemSubType.class)),

	NANITE_VOLUME,

//	/** Maximum rating of embedded items */
	MAX_SENSOR_RATING,
	MAX_SKILLSOFT_RATING,
	MODIFICATION_SLOTS,
	MODSLOTS_CHASSIS,
	MODSLOTS_ELECTRONICS,
	MODSLOTS_POWERTRAIN,
	// Vehicle Pilot
	PILOT,
	PRICE,
	/** Used for two-step price calculations - e.g. Infostick alchemical preparation */
	PRICE2,
	//
	SOFTWARE_TYPES( new EnumConverter(SoftwareTypes.class)),
	QUALITY,
	RANGE,
	RATING,
	SEATS,
	SENSORS,
	/** The amount of capacity slots required */
	SIZE(new FloatConverter()),
	SKILL( (StringValueConverter)null),
	SKILL_SPECIALIZATION( (StringValueConverter)null),
//	SOCIAL,
	SPEED_INTERVAL(new OnRoadOffRoadConverter()),
	TOPSPEED,
	VEHICLE_TYPE( (StringValueConverter)null),
	// Unmodified modification slots. 3 element array CHASSIS, ELECTRONICS, POWERTRAIN
	VEHICLE_MODSLOTS( new IntegerArrayConverter()),
	WEAPON_SIZE( new EnumConverter(WeaponSize.class)),

	ATTACK,
	SLEAZE,
	DATA_PROCESSING,
	FIREWALL,
	CONCURRENT_PROGRAMS,
	NOISE_REDUCTION,
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
		if (mods.isEmpty())
			return (T)base;
		if (this==AVAILABILITY)
			return (T) SR6GearTool.calculateModifiedValue((Availability) base, mods);
		if (this==ATTACK_RATING)
			return (T) SR6GearTool.calculateModifiedValue((int[]) base, mods);
		if (this==DAMAGE)
			return (T) SR6GearTool.calculateModifiedValue((Damage) base, mods);
		if (this==QUALITY)
			return (T) SR6GearTool.calculateModifiedValue((AugmentationQuality) base, mods);
		if (this!=ITEMSUBTYPE && this!=ITEMTYPE)
			System.err.println("SR6ItemAttribute: Don't know how to calculate modified value for "+this+" /"+this.getClass());
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

	//-------------------------------------------------------------------
	public static SR6ItemAttribute[] matrixValues() {
		return new SR6ItemAttribute[] {ATTACK, SLEAZE, DATA_PROCESSING, FIREWALL};
	}

}
