package de.rpgframework.shadowrun6.chargen.gen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
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
		priorities.put(PriorityType.METATYPE, Priority.B);
		priorities.put(PriorityType.ATTRIBUTE, Priority.A);
		priorities.put(PriorityType.MAGIC, Priority.E);
		priorities.put(PriorityType.SKILLS, Priority.C);
		priorities.put(PriorityType.RESOURCES, Priority.D);
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
