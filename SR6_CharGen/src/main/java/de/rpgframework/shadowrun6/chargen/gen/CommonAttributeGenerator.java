package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.chargen.ai.Recommender;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author Stefan Prelle
 *
 */
public abstract class CommonAttributeGenerator extends ControllerImpl<ShadowrunAttribute> implements IAttributeController {

	protected List<ShadowrunAttribute> allowedAdjust = new ArrayList<>();

	protected int numAttributesToMax = 1;

	//-------------------------------------------------------------------
	/**
	 */
	public CommonAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getMaximumValue(ShadowrunAttribute key) {
		int max = parent.getModel().getAttribute(key).getMaximum();
		if (max<3)
			max = 6+max;
		boolean allow = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_RAISE_ABOVE_6);
		if (!allow) {
			for (Modification mod : parent.getModel().getAttribute(key).getModifications()) {
				if (!(mod instanceof ValueModification))
					continue;
				ValueModification vMod = (ValueModification)mod;
				if (vMod.getSet()!=ValueType.MAX)
					continue;
				if (mod.getSource() instanceof MetamagicOrEcho) {
					max -= vMod.getValue();
				}
			}
		}
		//logger.log(Level.WARNING, "Attribute {0} is max {1}", key, max);
		return (max>0)?max:6;
	}

	//-------------------------------------------------------------------
	protected List<ShadowrunAttribute> getMaximizedAttributes() {
		List<ShadowrunAttribute> maxed = new ArrayList<ShadowrunAttribute>();
		Shadowrun6Character model = parent.getModel();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(key);
			if (aVal.getModifiedValue() >= getMaximumValue(key))
				maxed.add(key);
		}
		return maxed;
	}

	//-------------------------------------------------------------------
	protected boolean isAnotherAttributeAlreadyMaxed(ShadowrunAttribute key) {
		if (key.isSpecial())
			return false;

		Collection<ShadowrunAttribute> alreadyMaxed = getMaximizedAttributes();
		alreadyMaxed.remove(key);
		// Only allow to max an attribute, if there isn't one already
		if ((getModel().getAttribute(key).getModifiedValue()+1)>=getMaximumValue(key) && key.isPrimary()) {
//			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.WARNING, "Increasing "+key+" would reach maximum of "+getMaximumValue(key)+".  Is already one maxed = "+alreadyMaxed+" of "+numAttributesToMax);
			return alreadyMaxed.size()>=numAttributesToMax;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		if (value.getModifyable()==ShadowrunAttribute.MAGIC && ( parent.getModel().getMagicOrResonanceType()==null || !parent.getModel().getMagicOrResonanceType().usesMagic()))
			return Possible.FALSE;
		if (value.getModifyable()==ShadowrunAttribute.RESONANCE && ( parent.getModel().getMagicOrResonanceType()==null || !parent.getModel().getMagicOrResonanceType().usesResonance()))
			return Possible.FALSE;
		int max = getMaximumValue(value.getModifyable());
		if (value.getDistributed()>=max) {
			logger.log(Level.WARNING, "Attribute {0} is already at maximum {1}", value.getModifyable(), max);
			return new Possible(IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
		}
		if ((value.getDistributed()+1)==max) {
			if (isAnotherAttributeAlreadyMaxed(value.getModifyable())) {
				logger.log(Level.WARNING, "Attribute {0} is not at maximum {1}, but another attribute us", value.getModifyable(), max);
				return Possible.FALSE;
			}
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		return new Possible(value.getModifiedValue()>1);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(ShadowrunAttribute item) {
		Recommender recommender = getModel().getRecommender();
		// No recommender set, means no recommendation
		if (recommender==null) {
			return RecommendationState.NEUTRAL;
		}

		return recommender.getRecommendationState(item);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAttributeController#isRacialAttribute(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean isRacialAttribute(ShadowrunAttribute key) {
		return allowedAdjust.contains(key);
	}

}
