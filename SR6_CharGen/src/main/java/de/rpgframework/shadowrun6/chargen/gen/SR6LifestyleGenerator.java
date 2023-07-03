package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6LifestyleController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6LifestyleGenerator extends ControllerImpl<LifestyleQuality> implements SR6LifestyleController {

	protected final static Logger logger = System.getLogger(SR6LifestyleGenerator.class.getPackageName()+".lifestyle");

	protected Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6LifestyleGenerator(SR6CharacterController parent) {
		super(parent);
		model = parent.getModel();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ILifestyleController#getLifestyleCost(de.rpgframework.shadowrun.Lifestyle)
	 */
	@Override
	public int getLifestyleCost(SR6Lifestyle lifestyle) {
		return getNuyenForLP(lifestyle.getLifestylePoints()) * lifestyle.getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<LifestyleQuality> getAvailable() {
		List<LifestyleQuality> list = Shadowrun6Core.getItemList(LifestyleQuality.class);
		list.remove(Shadowrun6Core.getItem(LifestyleQuality.class, "z_zone"));
		list.remove(Shadowrun6Core.getItem(LifestyleQuality.class, "aaa_zone"));
		return list;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<SR6Lifestyle> getSelected() {
		return model.getLifestyles();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(LifestyleQuality value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6Lifestyle value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(LifestyleQuality value) {
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#areRequirementsMet(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public Possible areRequirementsMet(LifestyleQuality value) {
		Possible poss = Shadowrun6Tools.areRequirementsMet(getModel(), value);
		if (!poss.get())
			return poss;

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(LifestyleQuality value, Decision... decisions) {
		Possible poss = areRequirementsMet(value);
		if (!poss.get())
			return poss;

		int cost = value.getCost();
		if (model.getNuyen()<cost) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN);
		}
		return GenericRPGTools.areAllDecisionsPresent(value, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<SR6Lifestyle> select(LifestyleQuality value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0})", value);
		try {
			Possible possible = canBeSelected(value, decisions);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to select a lifestyle that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<SR6Lifestyle>(possible, false);
			}

			SR6Lifestyle selected = new SR6Lifestyle(value);
			selected.setDistributed(1);
			logger.log(Level.INFO, "{0} has Level = {1}", value, value.hasLevel());
			for (Decision dec : decisions) {
				selected.addDecision(dec);
			}

			model.addLifestyle(selected);
			logger.log(Level.INFO, "Add lifestyle '" + value.getId());

			parent.runProcessors();
			return new OperationResult<SR6Lifestyle>(selected);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(SR6Lifestyle value) {
		if (!model.getLifestyles().contains(value)) {
			return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(SR6Lifestyle value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible possible = canBeDeselected(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to deselect a lifestyle that cannot be deselected: {0}",possible.getI18NKey());
				return false;
			}

			value.setDistributed(0);
			model.removeLifestyle(value);
			logger.log(Level.INFO, "Remove lifestyle ''{0}''", value.getNameWithRating());

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(LifestyleQuality data) {
		return data.getCost();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(LifestyleQuality data) {
		return String.valueOf(data.getCost());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return List.of();
	}

	@Override
	public void roll() {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	private static int getNuyenForLP(int lp) {
		logger.log(Level.DEBUG, "Get for "+lp);
		if (lp<=1) return 0;
		if (lp<=6) return lp*100;
		if (lp<=12) return 500 + (lp-6)*250;
		if (lp<=19) return 2000 + (lp-12)*500;
		if (lp<=27) return 5000 + (lp-19)*1000;
		switch (lp) {
		case 28: return 15000;
		case 29: return 20000;
		case 30: return 30000;
		case 31: return 50000;
		case 32: return 75000;
		case 33: return 80000;
		case 34: return 90000;
		case 35: return 95000;
		case 36: return 100000;
		default:
			return 100000 + (lp-36)*25000;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Remove all lifestyles that have been added by modifications
	 */
	private void cleanUp() {
		todos.clear();
		for (SR6Lifestyle life : new ArrayList<>(getModel().getLifestyles())) {
			if (life.isAutoAdded()) {
				getModel().removeLifestyle(life);
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();

		try {
			cleanUp();
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.LIFESTYLE) {
					DataItemModification mod = (DataItemModification)tmp;
					LifestyleQuality qual = mod.getResolvedKey();
					int months = 1;
					if (mod instanceof ValueModification)
						months = Math.max(1,((ValueModification)mod).getValue());
					SR6Lifestyle life = new SR6Lifestyle(qual);
					life.setDistributed(months);
					life.addModification(mod);
					logger.log(Level.DEBUG, "Add lifestyle {0} for {1} months", qual, months);
					model.addLifestyle(life);
				} else
					unprocessed.add(tmp);
			}

			// Pay Nuyen for lifestyles
			for (SR6Lifestyle val : model.getLifestyles()) {
				int lp = val.getLifestylePoints();
				if (val.isAutoAdded()) {
					logger.log(Level.DEBUG, "Don't pay for auto-added lifestyle {0}", val.getKey());
					continue;
				}
				logger.log(Level.DEBUG, "Pay {1} for lifestyle {1}", val.getNameWithRating(), getLifestyleCost(val));
				model.setNuyen( model.getNuyen() - getLifestyleCost(val));
			}

			// Make sure there is at laast one lifestyle
			if (model.getLifestyles().isEmpty()) {
				logger.log(Level.DEBUG, "No lifestyle found");
				todos.add(new ToDoElement(Severity.WARNING, IRejectReasons.IMPOSS_NO_LIFESTYLE));
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	@Override
	public boolean canBeIncreased(SR6Lifestyle value) {
		return parent.getModel().getNuyen() >= value.getCostPerMonth();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#increase(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean increase(SR6Lifestyle value) {
		if (!canBeDecreased(value)) {
			logger.log(Level.ERROR, "Trying to increase lifestyle {0} which is not allowed", value);
			return false;
		}

		value.setDistributed( value.getDistributed() +1 );
		logger.log(Level.INFO, "Increase paid months for lifestyle {0} to {1}", value, value.getDistributed());

		parent.runProcessors();

		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#canBeDecreased(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean canBeDecreased(SR6Lifestyle value) {
		return value.getDistributed()>1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#decrease(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean decrease(SR6Lifestyle value) {
		if (!canBeDecreased(value)) {
			logger.log(Level.ERROR, "Trying to decrease lifestyle {0} which is not allowed", value);
			return false;
		}

		value.setDistributed( value.getDistributed() -1 );
		logger.log(Level.INFO, "Decrease paid months for lifestyle {0} to {1}", value, value.getDistributed());

		parent.runProcessors();

		return true;
	}

}
