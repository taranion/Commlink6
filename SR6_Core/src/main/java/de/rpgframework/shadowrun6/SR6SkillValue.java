/**
 * 
 */
package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Root;

import de.rpgframework.shadowrun.AShadowrunSkillValue;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@Root(name = "skillval")
public class SR6SkillValue extends AShadowrunSkillValue<SR6Skill> {

	public final static int LANGLEVEL_EXISTING = 1;
	public final static int LANGLEVEL_SPECIALIST = 2;
	public final static int LANGLEVEL_EXPERT   = 3;
	public final static int LANGLEVEL_NATIVE   = 4;

	//-------------------------------------------------------------------
	public SR6SkillValue() {
	}

	//-------------------------------------------------------------------
	public SR6SkillValue(SR6Skill skill, int val) {
		super(skill,val);
		if (skill==null) throw new NullPointerException();
	}

	//-------------------------------------------------------------------
	public SR6SkillValue(SR6SkillValue toClone) {
		super(toClone);
		if (resolved==null) throw new NullPointerException();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ref+"(distr="+value+")";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectedValue#getModifyable()
	 */
	@Override
	public SR6Skill getModifyable() {
		return (SR6Skill) resolved;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.AShadowrunSkillValue#getSkill()
	 */
	@Override
	public SR6Skill getSkill() {
		if (resolved!=null)			
			return (SR6Skill) resolved;
		resolved = ShadowrunReference.resolve(ShadowrunReference.SKILL, ref);
		return (SR6Skill) resolved;
	}

}
