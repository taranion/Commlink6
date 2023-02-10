package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.UUID;

import de.rpgframework.shadowrun.chargen.charctrl.ISkillController;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;

public interface SR6SkillController extends ISkillController<SR6Skill, SR6SkillValue> {

	public final static UUID NATIVE_LANGUAGE = UUID.fromString("1ab9b4c2-b6d2-4d84-8c82-91920cbefe8b");
	public final static UUID DEC_LANGUAGE  = UUID.fromString("a7103ee4-31fa-435d-ac42-08f7d4d1e80c");
	public final static UUID DEC_KNOWLEDGE = UUID.fromString("89ebc659-ba06-4732-b347-6b832842a55b");

}