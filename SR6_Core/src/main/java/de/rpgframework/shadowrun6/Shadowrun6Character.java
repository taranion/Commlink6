package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Root;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.shadowrun.ShadowrunCharacter;

/**
 * @author prelle
 *
 */
@Root(name="sr6char")
public class Shadowrun6Character extends ShadowrunCharacter<SR6Skill, SR6SkillValue> {

	//-------------------------------------------------------------------
	public Shadowrun6Character() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RuleSpecificCharacterObject#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SHADOWRUN6;
	}

}
