package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6AdeptPowerController extends ControllerImpl<AdeptPower> implements IAdeptPowerController {

	protected static Logger logger = System.getLogger(SR6AdeptPowerController.class.getPackageName()+".adept");

	protected final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	protected float freePoints;

	//-------------------------------------------------------------------
	public SR6AdeptPowerController(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<AdeptPower> getAvailable() {
		return Shadowrun6Core.getItemList(AdeptPower.class).stream()
			.filter(p -> parent.showDataItem(p))
			.filter(p -> !getModel().hasAdeptPower(p.getId()) || p.isMulti())
			.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<AdeptPowerValue> getSelected() {
		return getModel().getAdeptPowers();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(AdeptPower value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(AdeptPowerValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(AdeptPower value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(AdeptPower value, Decision... decisions) {
		// Ensure enough power points are present
		float cost = value.getCostForLevel(1);
		if (cost>freePoints) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_PPOINTS);
		}

		// Check if all choices have been made and all requirements are fulfilled
		return Shadowrun6Tools.checkDecisionsAndRequirements(getModel(), value, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<AdeptPowerValue> select(AdeptPower data, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", data, List.of(decisions));
		try {
			Possible poss = canBeSelected(data, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select ''{0}'' which cannot be selected: {1}", data.getId(), poss);
				return new OperationResult<>(poss);
			}

			AdeptPowerValue value = new AdeptPowerValue(data, data.hasLevel()?1:0);
			getModel().addAdeptPower(value);
			for (Decision dec : decisions) {
				if (dec.getChoiceUUID().equals(ItemTemplate.UUID_RATING)) {
					value.setDistributed( dec.getValueAsInt());
				} else {
					value.addDecision(dec);
				}
			}
			logger.log(Level.INFO, "Selected ''{0}''", value.getNameWithRating());

			// Record in history
			DataItemModification mod = new DataItemModification(ShadowrunReference.ADEPT_POWER, data.getId());
			getModel().addToHistory(mod);

			parent.runProcessors();

			return new OperationResult<AdeptPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1})", data, List.of(decisions));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(AdeptPowerValue value) {
		// Power must exist in model
		if (!getModel().getAdeptPowers().contains(value))
			return new Possible(Severity.INFO, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_PRESENT);

		// Power may not be auto-added
		if (value.isAutoAdded())
			return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_AUTO_ADDED);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(AdeptPowerValue value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to deselect ''{0}'' which cannot be selected: {1}", value.getKey(), poss);
				return false;
			}

			getModel().removeAdeptPower(value);
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
	public Possible canBeIncreased(AdeptPowerValue value) {
		AdeptPower item = value.getModifyable();
		// Only valif the power has levels at all
		if (!item.hasLevel()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}
		// Ensure not already at limit
		if (item.getMaxLevel()!=0 && item.getMaxLevel()<=value.getModifiedValue()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
		}

		// Determine the difference in power cost and ensure enough power points are present
		float costBefore = item.getCostForLevel(value.getDistributed());
		float costAfter = item.getCostForLevel(value.getDistributed()+1);
		float cost = costAfter - costBefore;
		if (cost>freePoints) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_PPOINTS);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(AdeptPowerValue value) {
		AdeptPower item = value.getModifyable();
		// Only valif the power has levels at all
		if (!item.hasLevel()) {
			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}
		// Ensure not already at limit
		if (item.getMaxLevel()!=0 && value.getDistributed()==0) {
			return new Possible(Severity.STOPPER, RES, IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AdeptPowerValue> increase(AdeptPowerValue value) {
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

			return new OperationResult<AdeptPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AdeptPowerValue> decrease(AdeptPowerValue value) {
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

			return new OperationResult<AdeptPowerValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({0})", value);
		}
	}

	//-------------------------------------------------------------------
	protected int determineMaxFreePoints() {
		Shadowrun6Character model = getModel();
		if (model.getMagicOrResonanceType()==null || !model.getMagicOrResonanceType().usesPowers())
			return 0;

		return model.getAttribute(ShadowrunAttribute.POWER_POINTS).getModifiedValue();
	}

	//-------------------------------------------------------------------
	protected void allocatePP() {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");

		try {
			todos.clear();

			Shadowrun6Character model = getModel();
			freePoints = determineMaxFreePoints();

			/*
			 * Count invested power points
			 */
			for (AdeptPowerValue val : model.getAdeptPowers()) {
				float cost = 0;
				if (val.getModifyable().hasLevel()) {
					cost = val.getModifyable().getCostForLevel(val.getDistributed());
				} else if (val.isAutoAdded()) {
				} else {
					cost = val.getModifyable().getCost();
				}
				logger.log(Level.INFO, "Pay {0} PP for ''{1}''", cost, val.getKey());
				freePoints -= cost;
			}

			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} remaining power points", freePoints);
			if (freePoints>0) {
				todos.add(new ToDoElement(Severity.WARNING, SR6CharacterGenerator.RES, IRejectReasons.TODO_UNUSED_POWER_POINTS, freePoints));
			} else if (freePoints<0) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, IRejectReasons.TODO_TOO_MANY_POWERS, -freePoints));
			}
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
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
		logger.log(Level.INFO, "Running {0} from {1}", getClass(), parent);
		try {
			todos.clear();
			allocatePP();

			Shadowrun6Character model = getModel();

			for (AdeptPowerValue val : model.getAdeptPowers()) {
				Possible poss = GenericRPGTools.areAllDecisionsPresent(val.getResolved(), val.getDecisionArray());
				if (poss.getMostSevere()!=null) {
					todos.add(new ToDoElement(ToDoElement.Severity.STOPPER, val.getNameWithoutRating()+": "+poss.toString()));
				}
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#getMaxPowerPoints()
	 */
	@Override
	public int getMaxPowerPoints() {
		return determineMaxFreePoints();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#getUnsedPowerPoints()
	 */
	@Override
	public float getUnsedPowerPoints() {
		return freePoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(AdeptPower data, Decision... decisions) {
		return data.getCost();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(AdeptPower data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#canBuyPowerPoints()
	 */
	@Override
	public boolean canBuyPowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#canIncreasePowerPoints()
	 */
	@Override
	public boolean canIncreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#canDecreasePowerPoints()
	 */
	@Override
	public boolean canDecreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#increasePowerPoints()
	 */
	@Override
	public boolean increasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#decreasePowerPoints()
	 */
	@Override
	public boolean decreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(AdeptPowerValue value) {
		return value.getModifiedValue();
	}

}
