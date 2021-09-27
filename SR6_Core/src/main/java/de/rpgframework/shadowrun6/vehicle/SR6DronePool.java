package de.rpgframework.shadowrun6.vehicle;

import de.rpgframework.shadowrun.vehicle.DronePool;

/**
 * Pools or values dependent on the VehicleOperationMode
 * @author prelle
 *
 */
public enum SR6DronePool implements DronePool<SR6DronePool> {

	EVADE,
	PERCEPTION,
	CRACKING,
	PILOT,
	STEALTH,
	WEAPON,
	DEFENSE_RATING;

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.vehicle.DronePool#getValidValues()
	 */
	@Override
	public SR6DronePool[] getValidValues() {
		return values();
	}
	
}
