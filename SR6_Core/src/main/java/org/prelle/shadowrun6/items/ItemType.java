/**
 *
 */
package org.prelle.shadowrun6.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author prelle
 *
 */
public enum ItemType {

	ACCESSORY(
			ItemSubType.ACCESSORY, 
			ItemSubType.MODIFICATION,
//			ItemSubType.VISION_ENHANCEMENT,
			ItemSubType.AUDIO_ENHANCEMENT,
			ItemSubType.SENSOR,
			ItemSubType.COMMLINK,
			ItemSubType.CYBERDECK,
			ItemSubType.CYBER_LIMB_ENHANCEMENT,
			ItemSubType.CYBER_LIMB_ACCESSORY,
			ItemSubType.VEHICLE_ACCESSORY,
			ItemSubType.BASIC_PROGRAM,
			ItemSubType.HACKING_PROGRAM,
			ItemSubType.AUTOSOFT,
			ItemSubType.SKILLSOFT
			),
	ARMOR(ItemSubType.ARMOR_BODY, ItemSubType.ARMOR_HELMET, ItemSubType.ARMOR_SHIELD),
	ARMOR_ADDITION,
	BIOWARE(
//			ItemSubType.BIOWARE_COSMETICS, 
			ItemSubType.BIOWARE_STANDARD, 
			ItemSubType.BIOWARE_CULTURED,
			ItemSubType.BIOWARE_IMPLANT_WEAPON
//			ItemSubType.SYMBIONTS
			),
	CYBERWARE(
//			ItemSubType.CYBER_COSMETICS,
			ItemSubType.CYBER_HEADWARE,
			ItemSubType.CYBER_EYEWARE,
			ItemSubType.CYBER_BODYWARE,
			ItemSubType.CYBER_EARWARE,
			ItemSubType.CYBER_IMPLANT_WEAPON,
			ItemSubType.CYBER_LIMBS,
			ItemSubType.COMMLINK,
			ItemSubType.CYBERDECK
			),
	TOOLS(
			ItemSubType.TOOLS
			),
	ELECTRONICS(
			ItemSubType.COMMLINK,
			ItemSubType.CYBERDECK,
			ItemSubType.ELECTRONIC_ACCESSORIES,
			ItemSubType.RIGGER_CONSOLE,
			ItemSubType.RFID,
			ItemSubType.COMMUNICATION,
			ItemSubType.ID_CREDIT,
			ItemSubType.IMAGING,
			ItemSubType.OPTICAL,
			ItemSubType.AUDIO,
			ItemSubType.SENSOR_HOUSING,
			ItemSubType.SECURITY,
			ItemSubType.BREAKING,
			ItemSubType.TAC_NET
			),
	NANOWARE(
			ItemSubType.NANOWARE_HARD,
			ItemSubType.NANOWARE_SOFT,
			ItemSubType.NANO_CYBERWARE,
			ItemSubType.NANOTECH_EQUIPMENT
			),
	GENETICS(
			ItemSubType.GENOM_CHANGES,
			ItemSubType.EXOTIC_METAGENETICS,
			ItemSubType.TRANSGENETICS,
			ItemSubType.MICRO_ENVIRONADAPT,
			ItemSubType.COMPLEMENTAL_GENETICS
			),
	WEAPON_CLOSE_COMBAT(
			ItemSubType.BLADES,
			ItemSubType.CLUBS,
			ItemSubType.WHIPS,
			ItemSubType.UNARMED,
			ItemSubType.OTHER_CLOSE
			),
	WEAPON_RANGED(
			ItemSubType.BOWS,
			ItemSubType.CROSSBOWS,
			ItemSubType.THROWING
			),
	WEAPON_FIREARMS(
			ItemSubType.TASERS,
			ItemSubType.HOLDOUTS,
			ItemSubType.PISTOLS_LIGHT,
			ItemSubType.MACHINE_PISTOLS,
			ItemSubType.PISTOLS_HEAVY,
			ItemSubType.SUBMACHINE_GUNS,
			ItemSubType.SHOTGUNS,
			ItemSubType.RIFLE_ASSAULT,
			ItemSubType.RIFLE_HUNTING,
			ItemSubType.RIFLE_SNIPER,
			ItemSubType.LMG,
			ItemSubType.MMG,
			ItemSubType.HMG,
			ItemSubType.ASSAULT_CANNON
			),
	WEAPON_SPECIAL(
			ItemSubType.LAUNCHERS,
			ItemSubType.THROWERS,
			ItemSubType.OTHER_SPECIAL
			),
	// START: For backward compatibility
	WEAPON(
			ItemSubType.CLOSE_COMBAT,
			ItemSubType.PROJECTILES,
			ItemSubType.TASERS,
			ItemSubType.HOLDOUTS,
			ItemSubType.PISTOLS_LIGHT,
			ItemSubType.MACHINE_PISTOLS,
			ItemSubType.PISTOLS_HEAVY,
			ItemSubType.SUBMACHINE_GUNS,
			ItemSubType.SHOTGUNS,
			ItemSubType.RIFLES,
			ItemSubType.MACHINE_GUNS,
			ItemSubType.SPECIAL_WEAPONS,
			ItemSubType.LAUNCHERS
			),
	// END: For backward compatibility
	AMMUNITION(
			ItemSubType.AMMUNITION,
			ItemSubType.ROCKETS,
			ItemSubType.MISSILES,
			ItemSubType.EXPLOSIVES,
			ItemSubType.GRENADES
			),
	CHEMICALS(
			ItemSubType.INDUSTRIAL_CHEMICALS,
			ItemSubType.TOXINS,
			ItemSubType.DRUGS,
			ItemSubType.BTL
			),
	SURVIVAL(
			ItemSubType.SURVIVAL_GEAR,
			ItemSubType.GRAPPLE_GUN
			),
	BIOLOGY(
			ItemSubType.BIOTECH,
			ItemSubType.SLAP_PATCHES
			),
	VEHICLES(
			ItemSubType.BIKES,
			ItemSubType.CARS,
			ItemSubType.TRUCKS,
			ItemSubType.BOATS,
			ItemSubType.SUBMARINES,
			ItemSubType.FIXED_WING,
			ItemSubType.ROTORCRAFT,
			ItemSubType.VTOL,
			ItemSubType.WALKER
			),
	DRONES(
			ItemSubType.MICRODRONES,
			ItemSubType.MINIDRONES,
			ItemSubType.SMALL_DRONES,
			ItemSubType.MEDIUM_DRONES,
			ItemSubType.LARGE_DRONES
			),
	MAGICAL(
//			ItemSubType.FOCI_ENCHANTING,
//			ItemSubType.FOCI_METAMAGIC,
//			ItemSubType.FOCI_POWER,
//			ItemSubType.FOCI_QI,
//			ItemSubType.FOCI_SPELL,
//			ItemSubType.FOCI_SPIRIT,
//			ItemSubType.FOCI_WEAPON,
//			ItemSubType.MAGICAL_FORMULA,
			ItemSubType.MAGICAL_SUPPLIES
			),
    ;

	private ItemSubType[] subTypes;

	private ItemType(ItemSubType...data) {
		this.subTypes = data;
	}

//    public String getName() {
//        return Resource.get(ShadowrunCore.getI18nResources(),"itemtype."+name().toLowerCase());
//    }

    public ItemSubType[] getSubTypes() {
    	return subTypes;
    }

    public static ItemType[] bodytechTypes() {
    	return new ItemType[]{CYBERWARE,BIOWARE, NANOWARE, GENETICS};
    }

    public static ItemType[] gearTypes() {
    	return new ItemType[]{ARMOR,ELECTRONICS,BIOLOGY,CHEMICALS,SURVIVAL,AMMUNITION,TOOLS};
    }

    public static ItemType[] vehicleTypes() {
    	return new ItemType[]{VEHICLES,DRONES};
    }

    public static ItemType[] weaponTypes() {
    	return new ItemType[]{WEAPON_CLOSE_COMBAT, WEAPON_RANGED, WEAPON_FIREARMS, WEAPON_SPECIAL};
    }

    public static List<ItemType> getWeaponTypes() {
    	return Arrays.asList(weaponTypes());
    }

    public static boolean isWeapon(ItemType type) {
    	return getWeaponTypes().contains(type);
    }

    public static List<ItemSubType> getWeaponSubTypes() {
    	List<ItemSubType> ret = new ArrayList<ItemSubType>();
    	getWeaponTypes().forEach(type -> ret.addAll(Arrays.asList(type.getSubTypes())));
    	return ret;
    }
}
