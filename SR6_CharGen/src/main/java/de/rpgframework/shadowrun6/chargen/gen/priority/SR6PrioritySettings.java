package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger.Level;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityOption;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.IPrioritySettings;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author Stefan Prelle
 *
 */
public class SR6PrioritySettings extends CommonSR6GeneratorSettings implements IPrioritySettings {

	/** Selected priorities */
	public Map<PriorityType, Priority> priorities;
	/** How points and karma is spent on attribute */
	public Map<ShadowrunAttribute, PerAttributePoints> perAttrib;

	public transient Map<PriorityType, List<PriorityOption>> options;

	/** The total number of points to split between spells and PP */
	public int mysticAdeptMaxPoints;

	/** Modifier to apply to customization karma */
	public int karmaMod;
	public Map<String, PerSkillPoints> perSkill;

	//-------------------------------------------------------------------
	public SR6PrioritySettings() {
		priorities = new LinkedHashMap<PriorityType, Priority>();
		perAttrib = new LinkedHashMap<>();
		options   = new LinkedHashMap<PriorityType, List<PriorityOption>>();

		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			perAttrib.put(key, new PerAttributePoints());
		}
		perSkill = new LinkedHashMap<>();
		priorities.put(PriorityType.METATYPE, Priority.C);
		priorities.put(PriorityType.ATTRIBUTE, Priority.A);
		priorities.put(PriorityType.MAGIC, Priority.E);
		priorities.put(PriorityType.SKILLS, Priority.B);
		priorities.put(PriorityType.RESOURCES, Priority.D);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IPrioritySettings#perAttrib()
	 */
	@Override
	public Map<ShadowrunAttribute, PerAttributePoints> perAttrib() { return perAttrib; }
	public Map<PriorityType, Priority> priorities() { return priorities; }

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
		System.getLogger("de.rpgframework.shadowrun6.chargen.gen.skill").log(Level.INFO, "SR6PrioritySettings.put("+id+", "+per+" )");
		perSkill.put(id, per);
		System.getLogger("de.rpgframework.shadowrun6.chargen.gen.skill").log(Level.INFO, "Afterwards "+perSkill.keySet());
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IPrioritySettings#setPriorityOptions(de.rpgframework.shadowrun.PriorityType, java.util.List)
	 */
	@Override
	public void setPriorityOptions(PriorityType type, List<PriorityOption> options) {
		if (options!=null)
			this.options.put(type, options);
		else
			this.options.remove(type);
	}

}
