package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author prelle
 *
 */
public class SR6LifePathSettings extends CommonSR6GeneratorSettings {

	private List<LifepathModuleValue> modules;

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
		modules = new ArrayList<>();
//		perAttrib = new LinkedHashMap<>();
//		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
//			perAttrib.put(key, new PerAttributePoints());
//		}
//		perSkill = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	public List<LifepathModuleValue> getModules() {
		return modules;
	}

	//-------------------------------------------------------------------
	public void addModule(LifepathModuleValue module) {
		modules.add(module);
	}

	//-------------------------------------------------------------------
	public void removeModule(LifepathModuleValue module) {
		modules.remove(module);
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
