package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.chargen.ai.Recommender;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author Stefan Prelle
 *
 */
public abstract class CommonAttributeGenerator extends ControllerImpl<ShadowrunAttribute> implements IAttributeController {

	//-------------------------------------------------------------------
	/**
	 */
	public CommonAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		if (value.getModifyable()==ShadowrunAttribute.MAGIC && ( parent.getModel().getMagicOrResonanceType()==null || !parent.getModel().getMagicOrResonanceType().usesMagic()))
			return false;
		if (value.getModifyable()==ShadowrunAttribute.RESONANCE && ( parent.getModel().getMagicOrResonanceType()==null || !parent.getModel().getMagicOrResonanceType().usesResonance()))
			return false;
		int max = (value.getMaximum()!=0)?value.getMaximum():6;
		return value.getDistributed()<max;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		return value.getModifiedValue()>1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean increase(AttributeValue<ShadowrunAttribute> value) {
		if (!canBeIncreased(value)) {
			logger.warn("Tried increasing "+value+" although not possible");
			return false;
		}
		
		value.setDistributed(value.getDistributed()+1);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean decrease(AttributeValue<ShadowrunAttribute> value) {
		if (!canBeDecreased(value)) {
			logger.warn("Tried decreasing "+value+" although not possible");
			return false;
		}
		
		value.setDistributed(value.getDistributed()-1);
		return true;
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
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl#roll()
	 */
	@Override
	public void roll() {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAttributeController#isRacialAttribute(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean isRacialAttribute(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

}
