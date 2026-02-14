/**
 *
 */
package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public enum ItemType {

	VIRTUAL,
	ACCESSORY(
			ItemSubType.ACCESSORY,
			ItemSubType.MODIFICATION,
			ItemSubType.VISION_ENHANCEMENT,
			ItemSubType.AUDIO_ENHANCEMENT,
			ItemSubType.SENSOR,
			ItemSubType.COMMLINK,
			ItemSubType.CYBERDECK,
			ItemSubType.CYBER_LIMB_ENHANCEMENT,
			ItemSubType.CYBER_LIMB_ACCESSORY,
			ItemSubType.INSTRUMENT_ACCESSORY,
			ItemSubType.VEHICLE_ACCESSORY
			),
	ARMOR(
			ItemSubType.ARMOR_BODY,
			ItemSubType.ARMOR_SOCIAL,
			ItemSubType.ARMOR_CLOTHES,
			ItemSubType.ARMOR_HELMET,
			ItemSubType.ARMOR_SHIELD
			),
	ARMOR_ADDITION,
	BIOWARE(
			ItemSubType.BIOWARE_STANDARD,
			ItemSubType.BIOWARE_DERMAL,
			ItemSubType.BIOWARE_CULTURED,
			ItemSubType.BIOSENSE,
			ItemSubType.BIOWARE_WEAPON,
			ItemSubType.SYMBIONTS,
			ItemSubType.BIOSCULPT
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
			ItemSubType.CYBERDECK,
			ItemSubType.CYBERNETIC_RESTRAINT,
			ItemSubType.COSMETIC
			),
	CODEMODS(
			ItemSubType.ATTRIBUTE_CODEMOD,
			ItemSubType.CORE_CODEMOD,
			ItemSubType.PROCESSOR,
			ItemSubType.IO,
			ItemSubType.DIGITAL_WEAPON
			),
	TOOLS(
			ItemSubType.TOOLS,
			ItemSubType.SPARE_PARTS
			),
	ELECTRONICS(
			ItemSubType.COMMLINK,
			ItemSubType.CYBERDECK,
			ItemSubType.DATATERM,
			ItemSubType.CYBERTERM,
			ItemSubType.RIGGER_CONSOLE,
			ItemSubType.ELECTRONIC_ACCESSORIES,
			ItemSubType.RFID,
			ItemSubType.COMMUNICATION,
			ItemSubType.ID_CREDIT,
			ItemSubType.IMAGING,
			ItemSubType.OPTICAL,
			ItemSubType.AUDIO,
			ItemSubType.SENSOR_HOUSING,
			ItemSubType.SECURITY,
			ItemSubType.BREAKING,
			ItemSubType.TAC_NET,
			ItemSubType.INSTRUMENT,
			ItemSubType.BTLS
			),
	/** Hack&Slash custom cyberdecks */
	CYBERDECK(
			ItemSubType.CORE,
			ItemSubType.CASES,
			ItemSubType.CASE_MODS,
			ItemSubType.CORE_OPTIONAL
			),
	SOFTWARE(
			ItemSubType.AUTOSOFT,
			ItemSubType.BASIC_PROGRAM,
			ItemSubType.HACKING_PROGRAM,
			ItemSubType.RIGGER_PROGRAM,
			ItemSubType.SKILLSOFT,
			ItemSubType.TAC_NET,
			ItemSubType.ESOFT,
			ItemSubType.OTHER_PROGRAMS
			),
	NANOWARE(
			ItemSubType.NANITES_COSMETIC,
			ItemSubType.NANITES_THERAPEUTIC,
			ItemSubType.NANITES_BIOAMP,
			ItemSubType.NANITES_UTILITIES,
			ItemSubType.NANITES_TRANSIENT,
			ItemSubType.NANO_CYBERWARE,
			ItemSubType.NANOTECH_KIT
			),
	GENEWARE(
			ItemSubType.THERAPEUTIC,
			ItemSubType.AUGMENTICS,
			ItemSubType.COMPLEMENTARY_GENETIC_MODS,
			ItemSubType.TRANSGENICS,
			ItemSubType.TRANSGENIC_BIOWARE
			),
	PACK(
			ItemSubType.PACK_COMPLETE,
			ItemSubType.PACK_WEAPON,
			ItemSubType.PACK_OTHER,
			ItemSubType.PACK_AUGMENT,
			ItemSubType.PACK_VEHICLE
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
			ItemSubType.DMSO,
			ItemSubType.DART,
			ItemSubType.OTHER_SPECIAL
			),
	AMMUNITION(
			ItemSubType.AMMUNITION,
			ItemSubType.BOWS,
			ItemSubType.CROSSBOWS,
			ItemSubType.BALLISTAS,
			ItemSubType.ROCKETS,
			ItemSubType.EXPLOSIVES,
			ItemSubType.GRENADES
			),
	CHEMICALS(
			ItemSubType.INDUSTRIAL_CHEMICALS,
			ItemSubType.TOXINS,
			ItemSubType.DRUGS,
			ItemSubType.PERFUME
			),
	SURVIVAL(
			ItemSubType.SURVIVAL_GEAR,
			ItemSubType.WINTER_GEAR,
			ItemSubType.GRAPPLE_GUN
			),
	BIOLOGY(
			ItemSubType.BIOTECH,
			ItemSubType.SLAP_PATCHES
			),
	VEHICLES(
			ItemSubType.BIKES,
			ItemSubType.ATVS,
			ItemSubType.CARS,
			ItemSubType.TRUCKS,
			ItemSubType.VANS,
			ItemSubType.BUS,
			ItemSubType.TRACKED,
			ItemSubType.SPECIAL_VEHICLES,
			ItemSubType.HOVERCRAFT,
			ItemSubType.PWC,
			ItemSubType.BOATS,
			ItemSubType.SHIPS,
			ItemSubType.SUBMARINES,
			ItemSubType.AIRSHIP,
			ItemSubType.FIXED_WING,
			ItemSubType.ROTORCRAFT,
			ItemSubType.VTOL,
			ItemSubType.LAV,
			ItemSubType.LTAV,
			ItemSubType.GRAV,
			ItemSubType.SPACECRAFT,
			ItemSubType.WALKER,
			ItemSubType.MOD_TRAILER
			),
	DRONE_MICRO(
			ItemSubType.GROUND,
			ItemSubType.AIR,
			ItemSubType.AQUATIC
			),
	DRONE_MINI(
			ItemSubType.GROUND,
			ItemSubType.AIR,
			ItemSubType.AQUATIC
			),
	DRONE_SMALL(
			ItemSubType.GROUND,
			ItemSubType.AIR,
			ItemSubType.AQUATIC,
			ItemSubType.ANTHRO
			),
	DRONE_MEDIUM(
			ItemSubType.GROUND,
			ItemSubType.AIR,
			ItemSubType.AQUATIC,
			ItemSubType.ANTHRO
			),
	DRONE_LARGE(
			ItemSubType.GROUND,
			ItemSubType.AIR,
			ItemSubType.AQUATIC,
			ItemSubType.ANTHRO
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
			ItemSubType.ALCHEMICAL,
			ItemSubType.MAGICAL_SUPPLIES
			),
	// Vehicle Modifications
		MOD_CHASSIS(
				ItemSubType.MOD_LAYOUT,
				ItemSubType.MOD_CORE,
				ItemSubType.MOD_HANDLING,
				ItemSubType.MOD_INTERIOR,
				ItemSubType.MOD_CARGO
				),
		MOD_SKIN(
				ItemSubType.MOD_FORM,
				ItemSubType.MOD_ARMOR,
				ItemSubType.MOD_ELEMENTS
				),
		MOD_POWER(
				ItemSubType.MOD_ACC,
				ItemSubType.MOD_EFFICIENCY,
				ItemSubType.MOD_SPEED,
				ItemSubType.MOD_PROPULSION
				),
		MOD_ELEC(
				ItemSubType.MOD_COAT,
				ItemSubType.MOD_MISC,
				ItemSubType.MOD_PILOT
				),
		MOD_HARD(
				ItemSubType.MOD_MOUNT,
				ItemSubType.MOD_RACK,
				ItemSubType.MOD_OTHERS
				),
    ;

	private ItemSubType[] subTypes;

	private ItemType(ItemSubType...data) {
		this.subTypes = data;
	}

//    public String getName() {
//        return Resource.get(ShadowrunCore.getI18nResources(),"itemtype."+name().toLowerCase());
//    }

	//-------------------------------------------------------------------
	public String getName() {
		return Shadowrun6Core.getI18nResources().getString("itemtype."+this.name().toLowerCase());
	}

	//-------------------------------------------------------------------
	public String getName(Locale loc) {
		return Shadowrun6Core.getI18nResources().getString("itemtype."+this.name().toLowerCase(), loc);
	}

    public ItemSubType[] getSubTypes() {
    	return subTypes;
    }

    public static ItemType[] bodytechTypes() {
    	return new ItemType[]{CYBERWARE,BIOWARE, NANOWARE, GENEWARE};
    }

    public static ItemType[] gearTypes() {
    	return new ItemType[]{ARMOR,ELECTRONICS,BIOLOGY,CHEMICALS,SURVIVAL,AMMUNITION,TOOLS,NANOWARE};
    }

    public static ItemType[] vehicleTypes() {
    	return new ItemType[]{VEHICLES,DRONE_MICRO,DRONE_MINI,DRONE_MEDIUM,DRONE_SMALL,DRONE_LARGE};
    }

    public static ItemType[] droneTypes() {
    	return new ItemType[]{DRONE_MICRO,DRONE_MINI,DRONE_MEDIUM,DRONE_SMALL,DRONE_LARGE};
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
    public static boolean isWeapon(CarriedItem<ItemTemplate> item) {
    	return getWeaponTypes().contains(item.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue());
    }
    public static boolean isWeapon(ItemTemplate item) {
    	return getWeaponTypes().contains(item.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
    }
    public static boolean isVehicle(ItemType type) {
    	return Arrays.asList(vehicleTypes()).contains(type) || Arrays.asList(droneTypes()).contains(type);
    }
    public static boolean isDrone(ItemType type) {
    	return List.of(droneTypes()).contains(type);
    }

    public static List<ItemSubType> getWeaponSubTypes() {
    	List<ItemSubType> ret = new ArrayList<ItemSubType>();
    	getWeaponTypes().forEach(type -> ret.addAll(Arrays.asList(type.getSubTypes())));
    	return ret;
    }
}
