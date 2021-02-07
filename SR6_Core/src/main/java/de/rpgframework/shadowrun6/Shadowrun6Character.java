package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Root;

import de.rpgframework.character.Gender;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;

/**
 * @author prelle
 *
 */
@Root(name="sr6char")
public class Shadowrun6Character extends ShadowrunCharacter<SR6Skill, SR6SkillValue> implements RuleSpecificCharacterObject<ShadowrunAttribute> {

	//-------------------------------------------------------------------
	public Shadowrun6Character() {
		gender = Gender.MALE;
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

}
