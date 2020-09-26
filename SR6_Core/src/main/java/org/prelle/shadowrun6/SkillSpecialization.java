/**
 * 
 */
package org.prelle.shadowrun6;

import java.text.Collator;
import java.util.List;

import org.prelle.shadowrun6.items.ItemSubType;
import org.prelle.shadowrun6.persist.ItemSubTypesConverter;
//import org.prelle.shadowrun6.items.ItemTemplate;
//import org.prelle.shadowrun6.persist.ItemSubTypesConverter;
import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;

/**
 * @author prelle
 *
 */
public class SkillSpecialization extends DataItem implements Comparable<SkillSpecialization> {
	
	public static SkillSpecialization NONE = new SkillSpecialization();

	@Attribute
	private String id;
	@Attribute(required=false)
	private transient Skill skill;
	@Attribute(required=false)
	@AttribConvert(ItemSubTypesConverter.class)
	private List<ItemSubType> subtypes;
	
//	private transient ItemTemplate exoticItem;

	//-------------------------------------------------------------------
	public SkillSpecialization() {
		
	}

	//-------------------------------------------------------------------
	public SkillSpecialization(Skill skill, String id) {
		this.skill = skill;
		this.id    = id;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.shadowrun5.BasePluginData#getPageI18NKey()
//	 */
//	@Override
//	public String getPageI18NKey() {
//		if (skill!=null)
//			return "skill."+skill.getId()+"."+id+".page";
//		return "missing.skill.for."+id;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.shadowrun5.BasePluginData#getHelpI18NKey()
//	 */
//	@Override
//	public String getHelpI18NKey() {
//		if (skill!=null)
//			return "skill."+skill.getId()+"."+id+".desc";
//		return "missing.skill.for."+id;
//	}
//
//	//-------------------------------------------------------------------
//	public String getName() {
//		if (exoticItem!=null)
//			return exoticItem.getName();
//		
//		String key = "missing.skill.for."+id.toLowerCase();
//		if (skill!=null)
//			key = "skill."+skill.getId()+"."+id.toLowerCase(); 
//		
//		return Resource.get(i18n, key);
//	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof SkillSpecialization) {
			SkillSpecialization other = (SkillSpecialization)o;
			if (id==null) return other.getId()==null;
			return id.equals(other.getId());
		}
		return false;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return skill+"/"+id;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SkillSpecialization other) {
		return Collator.getInstance().compare(this.getName(), other.getName());
	}

//	//--------------------------------------------------------------------
//	/**
//	 * @return the exoticItem
//	 */
//	public ItemTemplate getExoticItem() {
//		return exoticItem;
//	}
//
//	//--------------------------------------------------------------------
//	/**
//	 * @param exoticItem the exoticItem to set
//	 */
//	public void setExoticItem(ItemTemplate exoticItem) {
//		this.exoticItem = exoticItem;
//	}

	//-------------------------------------------------------------------
	/**
	 * @return the subtypes
	 */
	public List<ItemSubType> getSubtypes() {
		return subtypes;
	}

}
