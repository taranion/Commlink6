package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator;

/**
 * A base class for leveller and generator
 * @author Stefan
 *
 */
public abstract class SR6CharacterControllerImpl extends CharacterControllerImpl<ShadowrunAttribute,Shadowrun6Character	>
		implements SR6CharacterController {
	
	protected IAttributeController attributes;
	protected SR6SkillController skills;
	protected IQualityController qualities;
	protected IComplexFormController complex;
	protected IContactController contacts;
	protected IAdeptPowerController adeptPowers;
	protected SR6SpellController spells;
	protected IEquipmentController equipment;
	
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getAttributeController()
	 */
	@Override
	public IAttributeController getAttributeController() {
		return attributes;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getSkillController()
	 */
	@Override
	public SR6SkillController getSkillController() {
		return skills;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getQualityController()
	 */
	@Override
	public IQualityController getQualityController() {
		return qualities;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getComplexFormController()
	 */
	@Override
	public IComplexFormController getComplexFormController() {
		return complex;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getContactController()
	 */
	@Override
	public IContactController getContactController() {
		return contacts;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getAdeptPowerController()
	 */
	@Override
	public IAdeptPowerController getAdeptPowerController() {
		return adeptPowers;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getSpellController()
	 */
	@Override
	public SR6SpellController getSpellController() {
		return spells;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getEquipmentController()
	 */
	@Override
	public IEquipmentController getEquipmentController() {
		return equipment;
	}

}
