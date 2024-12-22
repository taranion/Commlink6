package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6ComplexFormLeveller extends ControllerImpl<ComplexForm> implements IComplexFormController {

	protected static Logger logger = System.getLogger(SR6ComplexFormLeveller.class.getPackageName()+".cplxform");

	public final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6ComplexFormLeveller(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<ComplexForm> getAvailable() {
		List<ComplexForm> ret = new ArrayList<>();
		ret.addAll( Shadowrun6Core.getItemList(ComplexForm.class).stream()
			.filter(cf -> cf.isMultipleSelectable() || getModel().getComplexForm(cf.getId())==null)
			.collect(Collectors.toList())
		);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<ComplexFormValue> getSelected() {
		return getModel().getComplexForms();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(ComplexForm value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(ComplexFormValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(ComplexForm value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(ComplexForm value, Decision... decisions) {
		// Ensure spell has not been selected yet
		for (ComplexFormValue tmp : getSelected()) {
			if (tmp.getResolved()==value && !value.isMultipleSelectable())
				return new Possible(IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		List<Choice> requiredChoices = value.getChoices();
		for (Decision dec : decisions) {
			logger.log(Level.INFO, "Decision "+dec);
			if (dec==null) continue;
			Choice choice = value.getChoice( dec.getChoiceUUID() );
			// If we found
			if (choice!=null) requiredChoices.remove(choice);
		}

		// If there are decisions open, don't allow selection
		if (!requiredChoices.isEmpty()) {
			// Convert open decisions into names or at least identifiers
			List<String> names = new ArrayList<>();
			requiredChoices.forEach(c -> names.add(
					(c.getChooseFrom()==ShadowrunReference.SUBSELECT)?value.getChoiceName(c, Locale.getDefault()):String.valueOf(c.getChooseFrom())));
			return new Possible(Severity.WARNING, RES, SR6RejectReasons.IMPOSS_MISSING_DECISIONS,names);
		}

//		if (freeSpells<1)
//			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<ComplexFormValue> select(ComplexForm value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", value, Arrays.toString(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.getRequireDecisions()) {
				logger.log(Level.WARNING, "Trying to select a complex form which cannot be selected: {0}",poss);
				return new OperationResult<>(poss);
			}

			ComplexFormValue toAdd = new ComplexFormValue(value);
			for (Decision dec : decisions) {
				toAdd.addDecision(dec);
			}

			getModel().addComplexForm(toAdd);
			logger.log(Level.INFO, "Added complex form {0}", toAdd);

			// Pay Karma
			int cost = 5;
			getModel().setKarmaFree(getModel().getKarmaFree()-cost);
			getModel().setKarmaInvested(getModel().getKarmaInvested()+cost);

			// Record in history
			DataItemModification mod = new DataItemModification(ShadowrunReference.COMPLEX_FORM, value.getId());
			mod.setExpCost(cost);
			mod.setDate(new Date());
			getModel().addToHistory(mod);

			parent.runProcessors();

			return new OperationResult<>(poss);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1})", value, Arrays.toString(decisions));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(ComplexFormValue value) {
		if (!getSelected().contains(value)) {
			return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		if (value.isAutoAdded()) {
			return new Possible(IRejectReasons.IMPOSS_AUTO_ADDED);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(ComplexFormValue value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select a complex form which cannot be selected: {0}",poss);
				return false;
			}

			getModel().removeComplexForm(value);
			logger.log(Level.INFO, "Removed complex form {0}", value);

			parent.runProcessors();

			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(ComplexForm data, Decision... decisions) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(ComplexForm data) {
		return String.valueOf(getSelectionCost(data));
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

//			for (SpellValue val : model.getAdeptPowers()) {
//				// Apply modifications
//				unprocessed.addAll(val.getModifications());
//			}
//
//			// Summary and eventually warn
//			logger.log(Level.INFO, "Have {0} remaining power points", freePoints);
//			if (freePoints>0) {
//				todos.add(new ToDoElement(Severity.WARNING, "Unused power points"));
//			} else if (freePoints<0) {
//				todos.add(new ToDoElement(Severity.STOPPER, "Too many power points used"));
//			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
