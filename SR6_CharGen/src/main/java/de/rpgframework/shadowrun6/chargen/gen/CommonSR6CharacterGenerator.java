package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.genericrpg.chargen.RecommendingController;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
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
public abstract class CommonSR6CharacterGenerator extends CharacterControllerImpl<ShadowrunAttribute,Shadowrun6Character>
		implements SR6CharacterGenerator {

	protected static final Logger logger = System.getLogger(CommonSR6CharacterGenerator.class.getPackageName());

	protected IMetatypeController meta;
	protected MagicOrResonanceController magicReso;
	protected CommonAttributeGenerator attributes;
	protected SR6SkillGenerator skill;
	protected IQualityController qualities;

	// -------------------------------------------------------------------
	protected CommonSR6CharacterGenerator() {
		updateEffectiveRules();
	}

	//-------------------------------------------------------------------
	public CommonSR6CharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		this();
		this.model = model;
		this.handle = handle;
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
		// TODO Auto-generated method stub
		logger.log(Level.DEBUG, "TODO: canBeFinished");
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

	// -------------------------------------------------------------------
	protected void updateEffectiveRules() {
		logger.log(Level.DEBUG, "ENTER updateEffectiveRules for "+this);
//		try {
//			throw new RuntimeException("Trace");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			effectiveRules.clear();
			// Use hardcoded defaults first
			for (Rule rule : Shadowrun6Rules.values()) {
				RuleValue rVal = new RuleValue(rule);
				effectiveRules.put(rule, rVal);
				logger.log(Level.INFO, "start with "+rVal);
			}

			// Apply settings from character
			if (model != null) {
				for (RuleConfiguration rc : new ArrayList<>(model.getRuleValues())) {
					Rule rule = Shadowrun6Rules.getRule(rc.getRuleId());
					// clean up unknown rule settings
					if (rule == null) {
						model.getRuleValues().remove(rc);
					} else {
						// Overwrite default with setting from char
						RuleValue rv = getRule(rule);
						rv.setValue(rule.parseValue(rc.getValueString()));
						logger.log(Level.INFO, "stored in character: "+rv);
					}
				}

				// Now check for chosen rule interpretation
				// If necessary, overwrite
				if (model.getStrictness() != null) {
					RuleInterpretation inter = Shadowrun6Core.getItem(RuleInterpretation.class, model.getStrictness());
					if (inter == null) {
						logger.log(Level.ERROR, "Character uses an unknown rule interpretation: " + model.getStrictness());
					} else {
						for (RuleConfiguration set : inter.getRules()) {
							Rule rule = Shadowrun6Rules.getRule(set.getRuleId());
							RuleValue rv = getRule(rule);
							rv.setValue(rule.parseValue(set.getValueString()));
							// If set in strictness, remove from character
							model.clearRuleValue(rule);
							rv.setEditable(false);
							logger.log(Level.INFO, "by strictness: "+rv);
						}
					}
				}
			}
		} finally {
			logger.log(Level.DEBUG, "LEAVE updateEffectiveRules");
		}
	}

	//-------------------------------------------------------------------
	public <T> RecommendingController<T> getRecommendingControllerFor(T item) {
//		if (item instanceof ShadowrunAttribute)
//			return getAttributeController();
		return null;
	}

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
		return skill;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getQualityController()
	 */
	@Override
	public IQualityController getQualityController() {
		return qualities;
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
