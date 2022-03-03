package de.rpgframework.shadowrun6.chargen.gen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6SkillValue;

/**
 * @author Stefan Prelle
 *
 */
public class SR6PrioritySettings extends APrioritySettings {

	public int mysticAdeptMaxPoints;
	public int mysticAdeptPowerPoints;
	/**
	 * Karma points converted to Nuyen
	 */
	int usedKarma;
	
	/** Modifier to apply to customization karma */
	public int karmaMod;
	public Map<SR6SkillValue, PerSkillPoints> perSkill;

	//-------------------------------------------------------------------
	public SR6PrioritySettings() {
		perSkill = new LinkedHashMap<>();
	}
	
	//-------------------------------------------------------------------
	public String toSkillString() {
		StringBuffer buf = new StringBuffer();
		for (Entry<SR6SkillValue,PerSkillPoints> ent : perSkill.entrySet()) {
			if (ent.getValue().getSum()>0)
				buf.append(String.format("\n%10s : %s", ent.getKey().getSkill(), ent.getValue().toString()));
		}
		return buf.toString();
	}
	
}
