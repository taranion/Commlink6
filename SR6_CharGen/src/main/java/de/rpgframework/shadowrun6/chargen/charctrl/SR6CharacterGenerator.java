package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.charctrl.ShadowrunCharacterGenerator;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public interface SR6CharacterGenerator extends SR6CharacterController, ShadowrunCharacterGenerator<SR6Skill,SR6SkillValue, Shadowrun6Character> {

}
