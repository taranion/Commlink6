package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.classification.Gender;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;

/**
 * @author prelle
 *
 */
@Root(name="sr6char")
public class Shadowrun6Character extends ShadowrunCharacter<SR6Skill, SR6SkillValue> implements RuleSpecificCharacterObject<ShadowrunAttribute> {

	@Element
	private int heat;
	
	//-------------------------------------------------------------------
	public Shadowrun6Character() {
		gender = Gender.MALE;
		
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValuesPlusEdge()) {
			attributes.add(new AttributeValue<ShadowrunAttribute>(key, 1));
		}
		attributes.add(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.MAGIC, 0));
		attributes.add(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.RESONANCE, 0));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RuleSpecificCharacterObject#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SHADOWRUN6;
	}
	
	//-------------------------------------------------------------------
	public SR6MetaType getMetatype() {
		return Shadowrun6Core.getItem(SR6MetaType.class, metatype);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RuleSpecificCharacterObject#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		SR6MetaType meta = getMetatype();
				
		String p1 = (meta!=null)?meta.getName():"?";
		return p1;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the heat
	 */
	public int getHeat() {
		return heat;
	}

	//-------------------------------------------------------------------
	/**
	 * @param heat the heat to set
	 */
	public void setHeat(int heat) {
		this.heat = Math.max(0,heat);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ShadowrunCharacter#getMagicOrResonanceType()
	 */
	@Override
	public MagicOrResonanceType getMagicOrResonanceType() {
		return Shadowrun6Core.getItem(MagicOrResonanceType.class, magicOrResonance);
	}

}
