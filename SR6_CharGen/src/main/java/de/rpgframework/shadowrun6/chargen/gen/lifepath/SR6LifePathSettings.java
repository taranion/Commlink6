package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author prelle
 *
 */
public class SR6LifePathSettings extends CommonSR6GeneratorSettings {

	private List<LifepathModuleValue> modules;

	private String nativeLanguage;
	private String bornQual1, bornQual2;

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

	//-------------------------------------------------------------------
	public Quality getBornQuality1() {
		if (bornQual1 == null) return null;
		return Shadowrun6Core.getItem(Quality.class, bornQual1);
	}

	//-------------------------------------------------------------------
	public void setBornQuality1(Quality value) {
		if (value==null) this.bornQual1=null;
		else this.bornQual1 = value.getId();
	}

	//-------------------------------------------------------------------
	public Quality getBornQuality2() {
		if (bornQual2 == null) return null;
		return Shadowrun6Core.getItem(Quality.class, bornQual2);
	}

	//-------------------------------------------------------------------
	public void setBornQuality2(Quality value) {
		if (value==null) this.bornQual2=null;
		else this.bornQual2 = value.getId();
	}

	//-------------------------------------------------------------------
	public String getNativeLanguage() {
		return nativeLanguage;
	}

	//-------------------------------------------------------------------
	public void setNativeLanguage(String value) {
		this.nativeLanguage = value;
	}
}
