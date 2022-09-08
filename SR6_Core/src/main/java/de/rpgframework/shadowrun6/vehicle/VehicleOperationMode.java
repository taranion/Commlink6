package de.rpgframework.shadowrun6.vehicle;

import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author Stefan
 *
 */
public enum VehicleOperationMode {
	
	DRIVING,
	RCC,
	JUMPED_IN,
	AUTONOMOUS
	;
	
	public String getName() {
		return Shadowrun6Core.getI18nResources().getString("vehiclemode."+name().toLowerCase());
	}
}
