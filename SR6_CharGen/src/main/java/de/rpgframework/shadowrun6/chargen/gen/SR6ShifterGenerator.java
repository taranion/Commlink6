package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.NumericalValue;
import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * 
 */
public class SR6ShifterGenerator extends ControllerImpl<Quality> implements
		ComplexDataItemController<Quality, QualityValue>, NumericalValueController<Quality, QualityValue> {
	
	private QualityValue shifterQual;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6ShifterGenerator(SR6CharacterGenerator parent) {
		super(parent);
		shifterQual = new QualityValue(Shadowrun6Core.getItem(SR6Quality.class, "shifter"), 0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		
		if (getModel().getBodytype()!=BodyType.SHAPESHIFTER) {
			// Remove an eventually existing shifter property
			if (getModel().hasQuality("shifter")) {
				getModel().removeQuality( getModel().getQuality("shifter"));
			}
		} else {
			if (!getModel().hasQuality("shifter")) {
				getModel().addQuality(shifterQual);
			}
			
		}
		return unprocessed;
	}

	@Override
	public List<Quality> getAvailable() {
		logger.log(Level.WARNING, "getAvailable()");
		return List.of();
	}

	@Override
	public List<QualityValue> getSelected() {
		logger.log(Level.WARNING, "getSelected()");
		return List.of();
	}

	@Override
	public RecommendationState getRecommendationState(Quality value) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public RecommendationState getRecommendationState(QualityValue value) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public List<Choice> getChoicesToDecide(Quality value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeDeselected(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deselect(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getSelectionCost(Quality data, Decision... decisions) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(QualityValue value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Possible canBeIncreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeDecreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<QualityValue> increase(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<QualityValue> decrease(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

}
