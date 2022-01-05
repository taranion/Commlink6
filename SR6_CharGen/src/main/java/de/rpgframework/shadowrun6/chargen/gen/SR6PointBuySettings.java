package de.rpgframework.shadowrun6.chargen.gen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6PointBuySettings {

	public PowerLevel variant;
	public int characterPoints;
	public int cpBoughtSpecial;
	public int cpBoughtAttrib;
	public int cpToSkills;
	public int cpToResources;
	/** How points and karma is spent on attribute */
	public Map<ShadowrunAttribute, PerAttributePoints> perAttrib;
	public Map<String, PerSkillPoints> perSkill;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6PointBuySettings() {
		perAttrib = new LinkedHashMap<>();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			perAttrib.put(key, new PerAttributePoints());
		}
		perSkill = new LinkedHashMap<>();
		for (SR6Skill key : Shadowrun6Core.getItemList(SR6Skill.class)) {
			perSkill.put(key.getId(), new PerSkillPoints());
		}
	}

	//-------------------------------------------------------------------
	public String toAttributeString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nCharacter Points remaining: "+characterPoints);
		buf.append("\nCP converted to special attributes: "+cpBoughtSpecial);
		buf.append("\nCP converted to regular attributes: "+cpBoughtAttrib);
		for (Entry<ShadowrunAttribute,PerAttributePoints> ent : perAttrib.entrySet()) {
			buf.append(String.format("\n%10s : %s", ent.getKey(), ent.getValue().toString()));
		}
		return buf.toString();
	}
	//-------------------------------------------------------------------
	public String toSkillString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nCharacter Points remaining: "+characterPoints);
		buf.append("\nCP converted to skills: "+cpToSkills);
		for (Entry<String,PerSkillPoints> ent : perSkill.entrySet()) {
			if (ent.getValue().getSum()>0)
				buf.append(String.format("\n%10s : %s", ent.getKey(), ent.getValue().toString()));
		}
		return buf.toString();
	}
}
