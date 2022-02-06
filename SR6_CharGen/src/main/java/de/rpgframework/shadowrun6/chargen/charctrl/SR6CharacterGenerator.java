package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public interface SR6CharacterGenerator extends SR6CharacterController, IShadowrunCharacterGenerator<SR6Skill,SR6SkillValue, Shadowrun6Character> {

	public final static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(SR6CharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);;
	
	public final static String IMPOSS_NOT_ENOUGH_KARMA = "impossible.notEnoughKarma";
	public final static String IMPOSS_MISSING_DECISIONS = "impossible.missingDecisions";
	
}
