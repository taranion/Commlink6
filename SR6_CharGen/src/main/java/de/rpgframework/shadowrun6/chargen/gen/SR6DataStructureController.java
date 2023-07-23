package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.IDataStructureController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.items.ItemTemplate;

public class SR6DataStructureController extends ControllerImpl<DataStructure> implements IDataStructureController {

	private int remain;
	private int maxPool;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6DataStructureController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<DataStructure> getAvailable() {
		List<DataStructure> avail = new ArrayList<>();
		for (DataStructure tmp : Shadowrun6Core.getItemList(DataStructure.class)) {
			if (getSelected().stream().anyMatch(dsv -> dsv.getResolved()==tmp))
				continue;
			avail.add(tmp);
		}

		return avail;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<DataStructureValue> getSelected() {
		return getModel().getDataStructures();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(DataStructure value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(DataStructureValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(DataStructure value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(DataStructure value, Decision... decisions) {
		// Ensure all decisions are present
		Possible poss = GenericRPGTools.areAllDecisionsPresent(value, decisions);
		if (!poss.get() || poss.getState()==State.DECISIONS_MISSING)
			return poss;

		Shadowrun6Character model = getModel();

		/*
		 * General rules: (CRB 154)
		 * - You can’t bond more foci than your Magic attribute,
		 * - no focus Force may exceed your magic Attribute (new in Seattle Edition)
		 * - and the maximum Force of all your bonded foci
		 *   can’t exceed your Magic x 5.
		 */

		int resonance = model.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
		// Can't bond more foci than MAG
		if (model.getDataStructures().size()>=resonance) {
			// No. foci larger than magic attribute
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_TOO_MANY_DATASTRCTURES);
		}

		// Determine rating
		Decision decForce = GenericRPGTools.getDecision(ItemTemplate.UUID_RATING, decisions);
		int force = (decForce!=null)?Integer.parseInt(decForce.getValue()):1;
		// No focus may exceed MAG attribute
		if (force>resonance) {
			// No. foci larger than magic attribute
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_RATING_EXCEEDS_RESONANCE, resonance);
		}

		// maximum force
		if (force>remain) {
			// Sum of force of all foci exceeds Magic*5
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_SUM_RATINGS_EXCEEDS_MAX, remain);
		}

		// Enough Karma for bonding
		if ( force>model.getKarmaFree()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, force);
		}

		// Enough Nuyen for bonding
		if ( (force*5000)>model.getNuyen()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, force*5000, model.getNuyen());
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<DataStructureValue> select(DataStructure value, Decision... decisions) {
		Possible poss = canBeSelected(value, decisions);
		if (!poss.get() || poss.getState()==State.DECISIONS_MISSING) {
			logger.log(Level.ERROR, "Trying to select focus, but "+poss);
			return new OperationResult<>(poss);
		}

		Decision decForce = GenericRPGTools.getDecision(ItemTemplate.UUID_RATING, decisions);
		int force = Integer.parseInt(decForce.getValue());

		DataStructureValue val = new DataStructureValue(value, force);
		for (Decision dec : decisions) {
			val.addDecision(dec);
		}
		getModel().addDataStructure(val);
		logger.log(Level.INFO, "Selected data structure "+val);

		// Pay Karma
		getModel().setKarmaFree( getModel().getKarmaFree() - force );
		if (!(getCharacterController() instanceof SR6CharacterGenerator)) {
			getModel().setKarmaInvested( getModel().getKarmaInvested() + force );
		}
		getModel().setNuyen( getModel().getNuyen() - force*5000 );

		parent.runProcessors();
		return new OperationResult<DataStructureValue>(val);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(DataStructureValue value) {
		if (getModel().getDataStructures().contains(value)) {
			return Possible.TRUE;
		}
		return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(DataStructureValue value) {
		Possible poss = canBeDeselected(value);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to deselect data structure, but "+poss);
			return false;
		}

		getModel().removeDataStructure(value);
		logger.log(Level.INFO,"Deselected data structure "+value);

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(DataStructure data) {
		return 1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(DataStructure data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IDataStructureController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return remain;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		Shadowrun6Character model = getModel();

		int resonance = model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue(ValueType.NATURAL);
		maxPool = resonance * 5;
		remain = maxPool;

		for (DataStructureValue ds : getModel().getDataStructures()) {
			int level = ds.getDistributed();
			remain -= level;
			int nuyen = level*5000;
			logger.log(Level.INFO, "Pay {0} and {1} karma nuyen for {2}", nuyen, level, ds.getKey());
			// Pay money
			model.setNuyen( model.getNuyen() - nuyen );
			// Pay Karma
			model.setKarmaFree( model.getKarmaFree() - level );
		}

		return unprocessed;
	}

}
