package de.rpgframework.shadowrun6;

import java.util.List;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.shadowrun.AShadowrunSkill;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="skill")
public class SR6Skill extends AShadowrunSkill {

	@org.prelle.simplepersist.Attribute(name="untr",required=true)
	private boolean useUntrained;
	@org.prelle.simplepersist.Attribute(name="tospec")
	private boolean toSpecify;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6Skill() {
		type = SkillType.PHYSICAL;
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SR6Skill(String id, SkillType type, ShadowrunAttribute attrib) {
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
	 * @see de.rpgframework.genericrpg.data.ISkill#getSpecializations()
	 */
	public List<SkillSpecialization<?>> getSpecializations() {
		return specializations;
	}

}
