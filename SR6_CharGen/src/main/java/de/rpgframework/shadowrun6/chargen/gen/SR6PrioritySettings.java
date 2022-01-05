package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;

/**
 * @author Stefan Prelle
 *
 */
public class SR6PrioritySettings extends APrioritySettings {

	public PowerLevel variant;
	
	public int mysticAdeptMaxPoints;
	public int mysticAdeptPowerPoints;
	/**
	 * Karma points converted to Nuyen
	 */
	int usedKarma;
	
	/** Modifier to apply to customization karma */
	public int karmaMod;
	
}
