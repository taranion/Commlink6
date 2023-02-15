package de.rpgframework.shadowrun6.items;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.items.Hook;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author Stefan
 *
 */
public enum ItemHook implements Hook {

	MELEE_EXTERNAL,
	FIREARMS_EXTERNAL,
	RANGED_EXTERNAL,
	BARREL,
	INTERNAL, // Only used for auto-added
	SMARTGUN,
	TOP,
	UNDER,
	UNDER_WEAPON_MOUNT, // Firing squad
	WEAPON_SECURITY, // Firing squad
	SIDE_L, // Firing squad
	SIDE_R, // Firing squard
	STOCK,
	// Especially for cyberimplant weapons
	IMPLANTWEAPON_ACCESSORY,

	ELECTRONIC_ACCESSORY(true),
	IMAGING(true),
	OPTICAL(true),
	AUDIO(true),
	SENSOR_HOUSING(true),
	SENSOR_FUNCTION(true),
	ARMOR(true),
	ARMOR_REACTIVE(true),
	ARMOR_ADDITION(true),
	ARMOR_MEMS(true),
	HELMET_ACCESSORY(true),
	COMMLINK,
	CYBERDECK,
	RCC,
	MEDKIT,
	HEADWARE_IMPLANT(true),
	CYBEREYE_IMPLANT(true),
	CYBEREAR_IMPLANT(true),
	CYBERLIMB_IMPLANT(true),
	/* Mounted means: used as implant weapon here */
	IMPLANT_MELEE,
	IMPLANT_RANGED,
	IMPLANT_HOLDOUT,
	IMPLANT_PISTOL_LIGHT,
	IMPLANT_PISTOL_MACHINE,
	IMPLANT_PISTOL_HEAVY,
	IMPLANT_SMG, // Submachine guns
	IMPLANT_SHOTGUN,
	IMPLANT_RIFLES,
	IMPLANT_MACHINEGUN,
	IMPLANT_LAUNCHER,
	VEHICLE_BODY(true), // Should be HARDPOINTS
	VEHICLE_CHASSIS(true),
	VEHICLE_CF (true),
	VEHICLE_POWERTRAIN(true),
	VEHICLE_ELECTRONICS(true),
	VEHICLE_WEAPON(true),
	VEHICLE_WEAPON_SMALL(true),
	VEHICLE_WEAPON_LARGE(true),
	VEHICLE_ACCESSORY(true),
	VEHICLE_TIRES (true),
	SOFTWARE(true),
	SKILLJACK(true),
	INSTRUMENT_SLOT(true),
	INSTRUMENT_WEAPON(true),
	PROCAM_SLOT(true),
	/** For cyberdecks: number core slots = Rating*3*/
	CORE_SLOTS(true),
	/** Custom Cyberdeck case modifications */
	CASE_MODS(true),
	CUSTOM_CYBERDECK(false),
	CYBERHACK(false)
	;

	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	boolean hasCapacity;

	//-------------------------------------------------------------------
	private ItemHook() {
		hasCapacity = false;
	}

	//-------------------------------------------------------------------
	private ItemHook(boolean cap) {
		hasCapacity = cap;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.Hook#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("itemhook."+this.name().toLowerCase());
	}

	//-------------------------------------------------------------------
	public String getName(Locale loc) {
		return RES.getString("itemhook."+this.name().toLowerCase(), loc);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.Hook#hasCapacity()
	 */
	@Override
	public boolean hasCapacity() {
		return hasCapacity;
	}
}
