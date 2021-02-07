package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.charctrl.ISkillGenerator;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;

public interface SR6SkillGenerator extends SR6SkillController, ISkillGenerator<SR6Skill, SR6SkillValue> {

	//--------------------------------------------------------------------
//	public SR6SkillGenerator configureAllowMultipleMaxed(boolean allowed);
//	public boolean isAllowMultipleMaxed();

}