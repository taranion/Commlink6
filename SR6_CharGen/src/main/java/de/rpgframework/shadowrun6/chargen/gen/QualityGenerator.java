package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyQualityModifications;

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
	public Possible canBeSelected(Quality value, Decision... decisions) {
		if (value.getKarmaCost()>6)
			return new Possible("Not enough Karma");
		return Possible.TRUE;
	}

	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeDeselected(QualityValue value) {
		// TODO Auto-generated method stub
		return Possible.FALSE;
	}

	@Override
	public boolean deselect(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();

		try {
			SR6PrioritySettings prioSettings = getModel().getCharGenSettings(SR6PrioritySettings.class);
			// Reset
//			reset();

			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.QUALITY) {
					ApplyQualityModifications.applyModification(getModel(), tmp);
					logger.log(Level.DEBUG, "Consume "+tmp);
				} else {
					unprocessed.add(tmp);
				}
			}
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	@Override
	public List<Quality> getAvailable() {
		logger.log(Level.WARNING,"ToDo: getAvailable()");
		return Shadowrun6Core.getItemList(Quality.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityValue> getSelected() {
		logger.log(Level.WARNING,"ToDo: getSelected()");
		return getCharacterController().getModel().getQualities();
	}

}
