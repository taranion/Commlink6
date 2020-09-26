/**
 *
 */
package org.prelle.shadowrun6;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="skill")
public class Skill extends DataItem implements Comparable<Skill> {

	public enum SkillType {
		COMBAT,
		PHYSICAL,
		SOCIAL,
		MAGIC,
		RESONANCE,
		TECHNICAL,
		VEHICLE,
		ACTION,
		LANGUAGE,
		KNOWLEDGE,
		NOT_SET,
		;

//		public String getName() {
//			return ShadowrunCore.getI18nResources().getString("skill.type."+name().toLowerCase());
//		}
		public static SkillType[] regularValues() {
			return new SkillType[]{COMBAT, PHYSICAL, SOCIAL, MAGIC, RESONANCE, TECHNICAL, VEHICLE};
		}
		public static SkillType[] individualValues() {
			return new SkillType[]{LANGUAGE, KNOWLEDGE};
		}
	}

	@org.prelle.simplepersist.Attribute(required=true)
	private Attribute attr;
	@org.prelle.simplepersist.Attribute(required=true)
	private SkillType type;
	@org.prelle.simplepersist.Attribute(name="untr",required=true)
	private boolean useUntrained;
	@org.prelle.simplepersist.Attribute(name="restrict")
	private boolean restricted;
	@ElementList(entry="skillspec",type=SkillSpecialization.class,inline=true)
	private List<SkillSpecialization> specializations;
	@org.prelle.simplepersist.Attribute(name="tospec")
	private boolean toSpecify;

	//-------------------------------------------------------------------
	/**
	 */
	public Skill() {
		type = SkillType.PHYSICAL;
		specializations = new ArrayList<SkillSpecialization>();
	}

	//-------------------------------------------------------------------
	/**
	 */
	public Skill(String id, SkillType type, Attribute attr1) {
		this();
		this.id = id;
		this.type = type;
		this.attr = attr1;
	}

//	//--------------------------------------------------------------------
//	/**
//	 * @see org.prelle.ubiquity.BasePluginData#getPageI18NKey()
//	 */
//	@Override
//	public String getPageI18NKey() {
//		return "skill."+id+".page";
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
//	 */
//	@Override
//	public String getHelpI18NKey() {
//		return "skill."+id+".desc";
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.shadowrun5.BasePluginData#getName()
//	 */
//	@Override
//	public String getName() {
//		return Resource.get(i18n,"skill."+id);
//	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

//	//-------------------------------------------------------------------
//	public String dump() {
//		return ShadowrunCore.getI18nResources().getString("skill."+id)+"  ("+attr+")  "+type+" ... "+specializations;
//	}
//
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun5.BasePluginData#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the attr1
	 */
	public Attribute getAttribute1() {
		return attr;
	}

	//-------------------------------------------------------------------
	/**
	 * @param attr1 the attr1 to set
	 */
	public void setAttribute1(Attribute attr1) {
		this.attr = attr1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Skill o) {
//		int foo = type.compareTo(o.getType());
//		if (foo!=0)
//			return foo;
		return Collator.getInstance().compare(getName(), o.getName());

	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public SkillType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(SkillType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public List<SkillSpecialization> getSpecializations() {
		return specializations;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the masterships
	 */
	public SkillSpecialization getSpecialization(String id) {
		for (SkillSpecialization tmp : specializations)
			if (tmp.getId().equals(id))
				return tmp;
		return null;
	}

	//-------------------------------------------------------------------
	public void addSpecialization(SkillSpecialization spec) {
		specializations.add(spec);
		spec.setSkill(this);
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.SelectableItem#getTypeId()
//	 */
//	@Override
//	public String getTypeId() {
//		return "skill";
//	}

	//-------------------------------------------------------------------
	/**
	 * @return the useUntrained
	 */
	public boolean isUseUntrained() {
		return useUntrained;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the toSpecify
	 */
	public boolean requiresSpecialization() {
		return toSpecify;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the toSpecify
	 */
	public boolean isToSpecify() {
		return toSpecify;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the restricted
	 */
	public boolean isRestricted() {
		return restricted;
	}

}
