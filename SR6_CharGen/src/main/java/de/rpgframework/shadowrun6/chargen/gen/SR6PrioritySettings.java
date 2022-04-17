package de.rpgframework.shadowrun6.chargen.gen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;

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
	public Map<String, PerSkillPoints> perSkill;

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
		for (Entry<String,PerSkillPoints> ent : perSkill.entrySet()) {
			if (ent.getValue().getSum()>0)
				buf.append(String.format("\n%10s : %s", Shadowrun6Core.getSkill(ent.getKey()), ent.getValue().toString()));
		}
//		for (Entry<UUID,PerSkillPoints> ent : perKnowledgeSkill.entrySet()) {
//			if (ent.getValue().getSum()>0)
//				buf.append(String.format("\n%10s : %s", Shadowrun6Core.getSkill(ent.getKey()), ent.getValue().toString()));
//		}
		return buf.toString();
	}
	
	//-------------------------------------------------------------------
	public void put(SR6SkillValue sVal, PerSkillPoints per) {
		SR6Skill skill = sVal.getModifyable();
		String id = skill.getId();
		if (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE) {
			id+="/"+sVal.getUuid();
		} 
		perSkill.put(id, per);
	}
	
	//-------------------------------------------------------------------
	public PerSkillPoints get(SR6SkillValue sVal) {
		SR6Skill skill = sVal.getModifyable();
		String id = skill.getId();
		if (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE) {
			id+="/"+sVal.getUuid();
		} 
		return perSkill.get(id);
	}

	//-------------------------------------------------------------------
	public void remove(SR6SkillValue sVal) {
		SR6Skill skill = sVal.getModifyable();
		String id = skill.getId();
		if (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE) {
			id+="/"+sVal.getUuid();
		} 
		perSkill.remove(id);
	}
	
}
