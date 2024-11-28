package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonRitualController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;

/**
 * @author prelle
 *
 */
public class SR6KarmaRitualGenerator extends CommonRitualController implements IRitualController {

	private static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	private int maxSpellsAndRituals;

	//-------------------------------------------------------------------
	public SR6KarmaRitualGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IRitualGenerator#usesFreeRituals()
	 */
	@Override
	public boolean usesFreeRituals() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.ISpellGenerator#getFreeSpells()
	 */
	@Override
	public int getFreeRituals() {
		return getMaxFree() - parent.getModel().getSpells().size() - parent.getModel().getRituals().size();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.ISpellGenerator#getMaxFree()
	 */
	@Override
	public int getMaxFree() {
		return maxSpellsAndRituals;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Ritual value, Decision... decisions) {
		// Ensure spell has not been selected yet
		for (RitualValue tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		if ( (getModel().getSpells().size()+getModel().getRituals().size())>=maxSpellsAndRituals) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_MAX_SPELLS, maxSpellsAndRituals);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(RitualValue value) {
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
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(Ritual data, Decision... decisions) {
		return 5;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Ritual data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<RitualValue> select(Ritual value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", value, Arrays.toString(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select a spell which cannot be selected: {0}",poss);
				return new OperationResult<>(poss);
			}

			RitualValue toAdd = new RitualValue(value);
			for (Decision dec : decisions) {
				toAdd.addDecision(dec);
			}

			getModel().addRitual(toAdd);
			logger.log(Level.INFO, "Added ritual {0}", toAdd);

			parent.runProcessors();

			return new OperationResult<>(poss);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1})", value, Arrays.toString(decisions));
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
			maxSpellsAndRituals = 0;

			SR6KarmaSettings settings = parent.getModel().getCharGenSettings(SR6KarmaSettings.class);
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells()) {
				int magic = model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
				maxSpellsAndRituals= magic*2;
			}
			logger.log(Level.INFO, "May buy up to {0} spells and rituals", maxSpellsAndRituals);

			// Pay Karma
			int karmaNeeded = model.getRituals().size()*5;
			logger.log(Level.INFO, "Pay {0} Karma for rituals", karmaNeeded);
			settings.spells += karmaNeeded;
			model.setKarmaFree( model.getKarmaFree() - karmaNeeded);
			model.setKarmaInvested( model.getKarmaInvested() - karmaNeeded);

			// Ensure not overbought spells
			if ((model.getSpells().size() + model.getRituals().size())>maxSpellsAndRituals) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.TODO_SPELLS_TOO_MANY));
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
