package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6AttributeLeveller extends ControllerImpl<ShadowrunAttribute> implements IAttributeController {

	private final static Logger logger = System.getLogger(CharacterControllerImpl.class.getPackageName() + ".attrib");

	private List<ShadowrunAttribute> metatypeAttribute;

	// -------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6AttributeLeveller(SR6CharacterController parent) {
		super(parent);
		metatypeAttribute = new ArrayList<>();
	}
	//-------------------------------------------------------------------
	private ValueModification getHighestModification(ShadowrunAttribute key) {
		ValueModification ret = null;
		for (Modification mod : getModel().getHistory()) {
			if (!(mod instanceof ValueModification))
				continue;
			ValueModification amod = (ValueModification)mod;
			if (mod.getSource()!=this && mod.getSource()!=null)
				continue;
			if (amod.getResolvedKey()!=key)
				continue;
			if (amod.getExpCost()<0)
				continue;
			if (ret==null || amod.getExpCost()>ret.getExpCost())
				ret = amod;
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		// Calculate required Karma
		int required = (value.getDistributed() + 1) * 5;
		if (getModel().getKarmaFree() < required)
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);

		// May not be at maximum yet
		int maximum = (value.getMaximum()==0)?6:value.getMaximum();
		if (value.getDistributed() >= maximum) {
			return new Possible(IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
		}

		return Possible.TRUE;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		ValueModification toUndo = getHighestModification(value.getModifyable());
		if (toUndo!=null && value.getStart()<value.getDistributed()) {
			if (toUndo.getSource()!=null)
				// Has been increased this session
				return Possible.TRUE;
			// Has been increased in career mode
			if ( getModel().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER) ) {
				return Possible.TRUE;
			} else {
				return new Possible(IRejectReasons.IMPOSS_PREVIOUS_SESSION);
			}
		} else {
			// Has been increased in generation mode
			if ( !getModel().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN) ) {
				return new Possible(IRejectReasons.IMPOSS_UNDO_CHARGEN);
			}
			if (value.getDistributed()<2) {
				return new Possible(IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
			}
		}
		return Possible.TRUE;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
		logger.log(Level.TRACE, "ENTER increase({0})", value.getModifyable());
		try {
			ShadowrunAttribute key = value.getModifyable();

			Possible poss = canBeIncreased(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Tried to increase attribute {0} but its not allowed: {1}", value,
						poss.toString());
				return new OperationResult<>(poss);
			}

			// Calculate required Karma
			int karma = (value.getDistributed() + 1) * 5;

			// Increase attribute
			value.setDistributed(value.getDistributed() + 1);
			logger.log(Level.INFO, "Increase {0} to {1} for {2} Karma", key, value.getDistributed(), karma);
			// Pay Karma
			Shadowrun6Character model = getModel();
			model.setKarmaFree(model.getKarmaFree() - karma);
			model.setKarmaInvested(model.getKarmaInvested() + karma);

			// Grant power point for adept
			if (key == ShadowrunAttribute.MAGIC && model.getMagicOrResonanceType().usesPowers()) {
				boolean raisePP = model.getRuleValueAsBoolean(Shadowrun6Rules.MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP);
				// There may be a house rule that mystic adepts need to buy PP (instead of
				// getting them free)
				if (model.getMagicOrResonanceType().paysPowers() && !raisePP) {
					logger.log(Level.WARNING,
							"House rule prevents adding an free power point for increased magic for mystic adepts");
				} else {
					AttributeValue<ShadowrunAttribute> pp = model.getAttribute(ShadowrunAttribute.POWER_POINTS);
					pp.setDistributed(pp.getDistributed() + 1);
					logger.log(Level.INFO, "Increase power points because MAGIC is increased");
				}
			}

			// Add to history
			ValueModification mod = new ValueModification(ShadowrunReference.ATTRIBUTE, value.getModifyable().name(),
					value.getDistributed(), this);
			mod.setExpCost(karma);
			mod.setDate(new Date(System.currentTimeMillis()));
			model.addToHistory(mod);

			parent.runProcessors();

			return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({0})", value.getModifyable());
		}
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
		logger.log(Level.TRACE, "ENTER decrease({0})", value.getModifyable());
		try {
			ShadowrunAttribute key = value.getModifyable();

			Possible poss = canBeDecreased(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Tried to decrease attribute {0} but its not allowed: {1}", value,
						poss.toString());
				return new OperationResult<>(poss);
			}

			// Calculate granted Karma
			int karma = value.getDistributed()* 5;

			// Increase attribute
			value.setDistributed(value.getDistributed() - 1);
			logger.log(Level.INFO, "Decrease {0} to {1} for {2} Karma", key, value.getDistributed(), karma);
			// Grant Karma
			Shadowrun6Character model = getModel();
			model.setKarmaFree(model.getKarmaFree() + karma);
			model.setKarmaInvested(model.getKarmaInvested() - karma);

			// Remove power point for adept
			if (key == ShadowrunAttribute.MAGIC && model.getMagicOrResonanceType().usesPowers()) {
				boolean raisePP = model.getRuleValueAsBoolean(Shadowrun6Rules.MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP);
				// There may be a house rule that mystic adepts need to buy PP (instead of
				// getting them free)
				if (model.getMagicOrResonanceType().paysPowers() && !raisePP) {
					logger.log(Level.WARNING,
							"House rule prevents adding an free power point for increased magic for mystic adepts");
				} else {
					AttributeValue<ShadowrunAttribute> pp = model.getAttribute(ShadowrunAttribute.POWER_POINTS);
					pp.setDistributed(pp.getDistributed() - 1);
					logger.log(Level.INFO, "Decrease power points because MAGIC is increased");
				}
			}

			// Add to history
			ValueModification mod = getHighestModification(key);
			model.removeFromHistory(mod);

			parent.runProcessors();

			return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({0})", value.getModifyable());
		}
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(ShadowrunAttribute item) {
		return RecommendationState.NEUTRAL;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAttributeController#isRacialAttribute(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean isRacialAttribute(ShadowrunAttribute key) {
		return metatypeAttribute.contains(key);
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		metatypeAttribute.clear();
		todos.clear();
		for (Modification _mod : previous) {
			if (_mod.getReferenceType() == ShadowrunReference.ATTRIBUTE) {
				ValueModification mod = (ValueModification) _mod;
				ShadowrunAttribute key = mod.getResolvedKey();
				AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
				logger.log(Level.DEBUG, "Add modification {0} to {1}", mod, key);
				val.addModification(mod);
				if (mod.getSet() == ValueType.MAX && mod.getValue() > 6)
					metatypeAttribute.add(key);
			} else
				unprocessed.add(_mod);
		}
		logger.log(Level.WARNING, "  Metatype attributes are: " + metatypeAttribute);

		return unprocessed;
	}

}
