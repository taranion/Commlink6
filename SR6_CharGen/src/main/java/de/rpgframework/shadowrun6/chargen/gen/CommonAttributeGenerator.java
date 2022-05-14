package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.chargen.ai.Recommender;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;

/**
 * @author Stefan Prelle
 *
 */
public abstract class CommonAttributeGenerator extends ControllerImpl<ShadowrunAttribute> implements IAttributeController {
	
	protected List<ShadowrunAttribute> allowedAdjust = new ArrayList<>();

	//-------------------------------------------------------------------
	/**
	 */
	public CommonAttributeGenerator(SpliMoCharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getMaximumValue(ShadowrunAttribute key) {
		int max = parent.getModel().getAttribute(key).getMaximum();
		return (max>0)?max:6;
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
		int max = (value.getMaximum()!=0)?value.getMaximum():6;
		return new Possible(value.getDistributed()<max);
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
