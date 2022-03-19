package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author Stefan Prelle
 *
 */
public interface SR6CharacterController extends IShadowrunCharacterController<SR6Skill,SR6SkillValue,Shadowrun6Character> {

	public SR6SkillController getSkillController();

	public IEquipmentController getEquipmentController();
	
}
