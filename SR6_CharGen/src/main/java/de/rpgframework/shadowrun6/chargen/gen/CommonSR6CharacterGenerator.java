package de.rpgframework.shadowrun6.chargen.gen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.chargen.charctrl.MetatypeController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;

/**
 * @author prelle
 *
 */
public abstract class CommonSR6CharacterGenerator extends CharacterControllerImpl<Shadowrun6Character> implements SR6CharacterGenerator {

	protected static final Logger logger = LogManager.getLogger("shadowrun6.gen.proc");

	protected MetatypeController meta;
	protected SR6SkillGenerator  skill;

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
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ShadowrunCharacterGenerator#getMetatypeController()
	 */
	@Override
	public MetatypeController getMetatypeController() {
		return meta;
	}

}
