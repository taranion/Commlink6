/**
 * 
 */
package de.rpgframework.shadowrun6;

import java.util.List;

import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.CheckInfluence;
import de.rpgframework.genericrpg.modification.CheckModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.AShadowrunSkillValue;
import de.rpgframework.shadowrun6.modifications.ShadowrunCheckInfluence;
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableNumericalValue#getModifier(de.rpgframework.genericrpg.ValueType)
	 */
	@Override
	public int getModifier(ValueType... typeArray) {
		int count = 0;
		int countEquip = 0;
		int countMagic = 0;
		int countUncapped = 0;
		List<ValueType> types = List.of(typeArray);
		for (Modification mod : getIncomingModifications()) {
			if (mod instanceof CheckModification cMod && cMod.getWhat()!=ShadowrunCheckInfluence.DICE)
				continue;
			if (mod instanceof ValueModification vMod) {
				if (types.contains( vMod.getSet() )) {
					if (vMod.isIgnoreLimit()) {
						countUncapped += vMod.getValue();
					} else {
						count += vMod.getValue();
					}
				}
			}
		}

		if (modifierCap>0) {
			count += Math.min(countMagic, modifierCap);
			count += Math.min(countEquip, modifierCap);
		} else {
			count += countMagic;
			count += countEquip;
		}
		count += countUncapped;

		return count;
	}

}
