package de.rpgframework.shadowrun6.chargen.gen;

import java.util.HashMap;
import java.util.Map;

import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;

/**
 * @author Stefan Prelle
 *
 */
public class SR6PrioritySettings extends APrioritySettings {

	public PriorityVariant variant;
	
	public int mysticAdeptMaxPoints;
	public int mysticAdeptPowerPoints;
	/**
	 * Karma points converted to Nuyen
	 */
	int usedKarma;
	
	/** Modifier to apply to customization karma */
	public int karmaMod;
	
}
