package de.rpgframework.shadowrun6.chargen.gen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;

/**
 * @author prelle
 *
 */
public abstract class CommonSR6CharacterGenerator extends CharacterControllerImpl<Shadowrun6Character> implements SR6CharacterGenerator {

	protected static final Logger logger = LoggerFactory.getLogger("shadowrun6.gen.proc");

	protected IMetatypeController meta;
	protected SR6SkillGenerator  skill;
	protected IQualityController  qualities;

	//-------------------------------------------------------------------
	protected CommonSR6CharacterGenerator() {
	}

	//--------------------------------------------------------------------
	protected void setupProcessChain() {
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void start(Shadowrun6Character model) {
		super.model = model;
		if (model.getMetatype()==null)
			model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
		setupProcessChain();
		runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model) {
		super.model = model;
		setupProcessChain();
		runProcessors();
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		// TODO Auto-generated method stub
		logger.warn("TODO: canBeFinished");
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		logger.warn("TODO: finish");
	
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getSkillController()
	 */
	@Override
	public SR6SkillController getSkillController() {
		return skill;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getQualityController()
	 */
	@Override
	public IQualityController getQualityController() {
		return qualities;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getMetatypeController()
	 */
	@Override
	public IMetatypeController getMetatypeController() {
		return meta;
	}

}
