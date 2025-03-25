package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
import de.rpgframework.shadowrun.chargen.charctrl.ICritterPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IFocusController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IPANController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun.chargen.charctrl.SINController;
import de.rpgframework.shadowrun6.Shadowrun6Character;

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
	protected SR6ContactController contacts;
	protected IAdeptPowerController adeptPowers;
	protected SR6SpellController spells;
	protected IRitualController rituals;
	protected IMetamagicOrEchoController metaEcho;
	protected ISR6EquipmentController equipment;
	protected SINController sins;
	protected SR6LifestyleController lifestyles;
	protected IPANController pan;
	protected IFocusController foci;
	protected IQualityPathController qPaths;
	protected IMartialArtsController martial;
	protected ICritterPowerController critter;
	protected SR6DrakeController drake;
	protected SR6AnimalismController animalism;
	protected IDataStructureController dataStructures;

	//-------------------------------------------------------------------
	public SR6CharacterControllerImpl() {
	}

	//-------------------------------------------------------------------
	public SR6CharacterControllerImpl(Shadowrun6Character model, CharacterHandle handle, Class<?> charGenSettingsClazz) {
		super(model,handle);
		model.readCharGenSettings(charGenSettingsClazz);
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getRitualController()
	 */
	@Override
	public IRitualController getRitualController() {
		return rituals;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getMetamagicOrEchoController()
	 */
	@Override
	public IMetamagicOrEchoController getMetamagicOrEchoController() {
		return metaEcho;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getEquipmentController()
	 */
	@Override
	public ISR6EquipmentController getEquipmentController() {
		return equipment;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getSINController()
	 */
	@Override
	public SINController getSINController() {
		return sins;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getPANController()
	 */
	public IPANController getPANController() {
		return pan;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getLifestyleController()
	 */
	@Override
	public SR6LifestyleController getLifestyleController() {
		return lifestyles;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getFocusController()
	 */
	@Override
	public IFocusController getFocusController() {
		return foci;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getQualityPathController()
	 */
	@Override
	public IQualityPathController getQualityPathController() {
		return qPaths;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getMartialArtsController()
	 */
	@Override
	public IMartialArtsController getMartialArtsController() {
		return martial;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getCritterPowerController()
	 */
	@Override
	public ICritterPowerController getCritterPowerController() {
		return critter;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getDrakeController()
	 */
	@Override
	public SR6DrakeController getDrakeController() {
		return drake;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getDataStructureController()
	 */
	@Override
	public IDataStructureController getDataStructureController() {
		return dataStructures;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getAnimalismController()
	 */
	@Override
	public SR6AnimalismController getAnimalismController() {
		return animalism;
	}

}
