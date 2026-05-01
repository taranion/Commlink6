package de.rpgframework.shadowrun6.items;

import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 *
 */
public enum WeaponSize {

	/** (for hidden_arm_slide) = TASERS, HOLDOUTS, PISTOLS_LIGHT, OTHER_SPECIAL */
	TINY,
	/** (for all holsters) = PISTOLS_HEAVY, MACHINE_PISTOLS, DMSO, DART */
	SMALL,
	/** (for weapon_mount_small) = SUBMACHINE_GUNS */
	MEDIUM,
	/** (for weapon_mount_standard) = SHOTGUNS, RIFLE_ASSAULT, RIFLE_HUNTING, RIFLE_SNIPER */
	LARGE,
	/** (for weapon_mount_heavy) = LMG, MMG, HMG, ASSAULT_CANNON, LAUNCHERS, THROWERS, BALLISTAS, CANNON */
	BIG,
	/** (for Deadly Arts military weapons with size >0) = VEHICLE_MILITARY */
	MILITARY,
	/** (only for buildings) = WEAPON_SPECIAL - OTHER_SPECIAL - guizhen_2_hall_of_tears */
	BUILDING
	;

	//-------------------------------------------------------------------
	public String getName() {
		return Shadowrun6Core.getI18nResources().getString("weaponsize."+this.name().toLowerCase());
	}

}
