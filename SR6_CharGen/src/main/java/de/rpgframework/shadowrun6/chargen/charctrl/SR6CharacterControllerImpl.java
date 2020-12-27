package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * A base class for leveller and generator
 * @author Stefan
 *
 */
public abstract class SR6CharacterControllerImpl extends CharacterControllerImpl<Shadowrun6Character	>
		implements SR6CharacterController {
	
	protected SR6SkillController skills;
	
	//-------------------------------------------------------------------
	public SR6CharacterControllerImpl() {
		createPartialController();
	}
	
	//-------------------------------------------------------------------
	/**
	 * Called by constructor to setup partial controllers 
	 */
	abstract protected void createPartialController();

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.SR6CharacterController.chargen.charctrl.SpliMoCharacterController#getSkillController()
	 */
	@Override
	public SR6SkillController getSkillController() {
		return skills;
	}

}
