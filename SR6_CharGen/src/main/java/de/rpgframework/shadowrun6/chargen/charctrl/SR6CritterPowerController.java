package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.chargen.charctrl.ICritterPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author prelle
 *
 */
public class SR6CritterPowerController extends ControllerImpl<CritterPower> implements ICritterPowerController {

	protected static Logger logger = System.getLogger(SR6CritterPowerController.class.getPackageName()+".adept");

	protected final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	protected float freePoints;

	//-------------------------------------------------------------------
	public SR6CritterPowerController(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<CritterPower> getAvailable() {
		return Shadowrun6Core.getItemList(CritterPower.class).stream()
			.filter(p -> parent.showDataItem(p))
			.filter(p -> !getModel().hasCritterPower(p.getId()) )
			.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<CritterPowerValue> getSelected() {
		return getModel().getCritterPowers();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(CritterPower value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(CritterPowerValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(CritterPower value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(CritterPower value, Decision... decisions) {
		// Check if all choices have been made and all requirements are fulfilled
		Possible poss = Shadowrun6Tools.checkDecisionsAndRequirements(getModel(), value, decisions);
		if (!poss.get())
			return poss;

		// Ensure enough power points are present
//		float cost = value.getCostForLevel(1);
//		if (cost>freePoints) {
//			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_NOT_ENOUGH_PPOINTS);
//		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CritterPowerValue> select(CritterPower data, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", data, List.of(decisions));
		try {
			Possible poss = canBeSelected(data, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select ''{0}'' which cannot be selected: {1}", data.getId(), poss);
				return new OperationResult<>(poss);
			}

			CritterPowerValue value = new CritterPowerValue(data, data.hasLevel()?1:0);
			getModel().addCritterPower(value);
			logger.log(Level.INFO, "Selected ''{0}''", value);

			parent.runProcessors();

			return new OperationResult<CritterPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1})", data, List.of(decisions));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(CritterPowerValue value) {
		// Power must exist in model
		if (!getModel().getCritterPowers().contains(value))
			return new Possible(Severity.INFO, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_PRESENT);

		// Power may not be auto-added
		if (value.isAutoAdded())
			return new Possible(Severity.INFO, IRejectReasons.RES, IRejectReasons.IMPOSS_AUTO_ADDED);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CritterPowerValue value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to deselect ''{0}'' which cannot be selected: {1}", value.getKey(), poss);
				return false;
			}

			getModel().removeCritterPower(value);
			logger.log(Level.INFO, "Deselected ''{0}''", value);

			parent.runProcessors();

			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(CritterPowerValue value) {
		CritterPower item = value.getModifyable();
		// Only valif the power has levels at all
		if (!item.hasLevel()) {
			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}
		// Ensure not already at limit
//		if (item.getMaxLevel()!=0 && item.getMaxLevel()<=value.getModifiedValue()) {
//			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
//		}

		// Determine the difference in power cost and ensure enough power points are present
//		float costBefore = item.getCostForLevel(value.getDistributed());
//		float costAfter = item.getCostForLevel(value.getDistributed()+1);
//		float cost = costAfter - costBefore;
//		if (cost>freePoints) {
//			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_NOT_ENOUGH_PPOINTS);
//		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(CritterPowerValue value) {
		CritterPower item = value.getModifyable();
		// Only valif the power has levels at all
		if (!item.hasLevel()) {
			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}
		// Ensure not already at limit
		if (value.getDistributed()==0) {
			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CritterPowerValue> increase(CritterPowerValue value) {
		logger.log(Level.TRACE, "ENTER increase({0})", value);
		try {
			Possible poss = canBeIncreased(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to increase ''{0}'' which cannot be increased: {1}", value.getModifyable().getId(), poss);
				return new OperationResult<>(poss);
			}

			value.setDistributed( value.getDistributed() +1);
			logger.log(Level.INFO, "Increased ''{0}'' to {1}", value, value.getDistributed());

			parent.runProcessors();

			return new OperationResult<CritterPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CritterPowerValue> decrease(CritterPowerValue value) {
		logger.log(Level.TRACE, "ENTER decrease({0})", value);
		try {
			Possible poss = canBeDecreased(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to decrease ''{0}'' which cannot be decreased: {1}", value.getModifyable().getId(), poss);
				return new OperationResult<>(poss);
			}

			value.setDistributed( value.getDistributed() -1);
			logger.log(Level.INFO, "Decreased ''{0}'' to {1}", value, value.getDistributed());

			parent.runProcessors();

			return new OperationResult<CritterPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			todos.clear();

			Shadowrun6Character model = getModel();

//			for (CritterPowerValue val : model.getCritterPowers()) {
//				// Apply modifications
//				unprocessed.addAll(val.getModifications());
//			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(CritterPower data, Decision... decisions) {
		return  0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(CritterPower data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(CritterPowerValue value) {
		return value.getModifiedValue();
	}

}
