package de.rpgframework.shadowrun6.vehicle;

import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author Stefan
 *
 */
public enum DroneAction {

	EVADE,
	PERCEPTION,
	CRACKING,
	PILOT,
	STEALTH,
	WEAPON,
	DEFENSE_RATING
	;
	public String getName() {
		return Shadowrun6Core.getI18nResources().getString("droneaction."+name().toLowerCase());
	}
	
}
