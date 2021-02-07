package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class QualityGenerator extends ControllerImpl<Quality> implements IQualityController {

	//-------------------------------------------------------------------
	public QualityGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.ComplexDataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(Quality value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(QualityValue value) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public boolean canBeIncreased(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increase(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeDecreased(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decrease(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Choice> getChoicesToDecide(Quality value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canBeSelected(Quality value, Decision... decisions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public QualityValue select(Quality value, Decision... decisions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canBeDeselected(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deselect(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

}
