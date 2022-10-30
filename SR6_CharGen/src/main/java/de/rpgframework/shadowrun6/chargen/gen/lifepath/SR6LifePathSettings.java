package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author prelle
 *
 */
public class SR6LifePathSettings extends CommonSR6GeneratorSettings {

//	public int characterPoints;
//	public int cpBoughtSpecial;
//	public int cpBoughtAttrib;
//	public int cpToSkills;
//	public int cpToResources;
//	/** How points and karma is spent on attribute */
//	public Map<ShadowrunAttribute, PerAttributePoints> perAttrib;
//	public Map<SR6SkillValue, PerSkillPoints> perSkill;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6LifePathSettings() {
//		perAttrib = new LinkedHashMap<>();
//		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
//			perAttrib.put(key, new PerAttributePoints());
//		}
//		perSkill = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	public String toAttributeString() {
		StringBuffer buf = new StringBuffer();
//		buf.append("\nCharacter Points remaining: "+characterPoints);
//		buf.append("\nCP converted to special attributes: "+cpBoughtSpecial);
//		buf.append("\nCP converted to regular attributes: "+cpBoughtAttrib);
//		for (Entry<ShadowrunAttribute,PerAttributePoints> ent : perAttrib.entrySet()) {
//			buf.append(String.format("\n%10s : %s", ent.getKey(), ent.getValue().toString()));
//		}
		return buf.toString();
	}
	
	//-------------------------------------------------------------------
	public String toSkillString() {
		StringBuffer buf = new StringBuffer();
//		buf.append("\nCharacter Points remaining: "+characterPoints);
//		buf.append("\nCP converted to skills: "+cpToSkills);
//		for (Entry<SR6SkillValue,PerSkillPoints> ent : perSkill.entrySet()) {
//			if (ent.getValue().getSum()>0)
//				buf.append(String.format("\n%10s : %s", ent.getKey().getSkill(), ent.getValue().toString()));
//		}
		return buf.toString();
	}
}
