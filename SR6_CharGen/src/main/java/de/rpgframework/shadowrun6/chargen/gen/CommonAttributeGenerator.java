package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
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
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean increase(AttributeValue<ShadowrunAttribute> value) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean decrease(AttributeValue<ShadowrunAttribute> value) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(ShadowrunAttribute item) {
		return RecommendationState.NEUTRAL;
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
