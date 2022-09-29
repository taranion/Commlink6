package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.RecommendingController;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * @author prelle
 *
 */
public abstract class CommonSR6CharacterGenerator extends SR6CharacterControllerImpl
		implements SR6CharacterGenerator {

	protected static final Logger logger = System.getLogger(CommonSR6CharacterGenerator.class.getPackageName());

	protected IMetatypeController meta;
	protected MagicOrResonanceController magicReso;

	// -------------------------------------------------------------------
	protected CommonSR6CharacterGenerator() {
//		updateEffectiveRules();
	}

	//-------------------------------------------------------------------
	public CommonSR6CharacterGenerator(Shadowrun6Character model, CharacterHandle handle, Class<?> charGenSettingsClazz) {
		super(model, handle, charGenSettingsClazz);
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
//		updateEffectiveRules();
		createPartialController();
	}

	// --------------------------------------------------------------------
	protected abstract void setupProcessChain();

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#setModel(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void setModel(Shadowrun6Character model, CharacterHandle handle) {
		super.model = model;
		super.handle= handle;
		if (model.getMetatype() == null)
			model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
		setupProcessChain();
		runProcessors();
	}
	
//	// -------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void start(Shadowrun6Character model) {
//		super.model = model;
//		if (model.getMetatype() == null)
//			model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
//		setupProcessChain();
//		runProcessors();
//	}
//
//	// -------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void continueCreation(Shadowrun6Character model) {
//		super.model = model;
//		setupProcessChain();
//		runProcessors();
//	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		for (ToDoElement todo : getToDos()) {
			if (todo.getSeverity()==Severity.STOPPER)
				return false;
		}
		return true;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		logger.log(Level.WARNING, "TODO: finish");

	}

	//-------------------------------------------------------------------
	public <T> RecommendingController<T> getRecommendingControllerFor(T item) {
//		if (item instanceof ShadowrunAttribute)
//			return getAttributeController();
		return null;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMetatypeController()
	 */
	@Override
	public IMetatypeController getMetatypeController() {
		return meta;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMagicOrResonanceController()
	 */
	@Override
	public IMagicOrResonanceController getMagicOrResonanceController() {
		return magicReso;
	}

}
