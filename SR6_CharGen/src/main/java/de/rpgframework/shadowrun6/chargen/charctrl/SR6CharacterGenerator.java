package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.SR6ShifterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.BornThisWayGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.ChildhoodGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.EarlyAdultGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathModuleGenerator;

/**
 * @author prelle
 *
 */
public interface SR6CharacterGenerator extends SR6CharacterController, IShadowrunCharacterGenerator<SR6Skill,SR6SkillValue, SR6Spell, Shadowrun6Character> {

	public final static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(SR6CharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);;

	default public BornThisWayGenerator getBornThisWayGenerator() {
		return null;
	}

	default public ChildhoodGenerator getChildhoodGenerator() {
		return null;
	}

	default public EarlyAdultGenerator getEarlyAdultGenerator() {
		return null;
	}

	default public SR6LifePathModuleGenerator getLifePathModuleGenerator() {
		return null;
	}

	public SR6ShifterGenerator getShifterGenerator() ;

}
