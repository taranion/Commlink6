package org.prelle.shadowrun6;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.OneAttributeSkill;
import de.rpgframework.shadowrun.ASkill;
import de.rpgframework.shadowrun.SkillType;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="skill")
public class Skill extends ASkill implements OneAttributeSkill<SR6Attribute> {

	@org.prelle.simplepersist.Attribute(required=true)
	private SR6Attribute attr;
	@org.prelle.simplepersist.Attribute(name="untr",required=true)
	private boolean useUntrained;
	@org.prelle.simplepersist.Attribute(name="tospec")
	private boolean toSpecify;

	//-------------------------------------------------------------------
	/**
	 */
	public Skill() {
		type = SkillType.PHYSICAL;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public Skill(String id, SkillType type, SR6Attribute attrib) {
		this();
		this.id = id;
		this.type = type;
		this.attr = attrib;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

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
	 * @see de.rpgframework.genericrpg.data.OneAttributeSkill#getAttribute()
	 */
	@Override
	public SR6Attribute getAttribute() {
		return attr;
	}

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

}
