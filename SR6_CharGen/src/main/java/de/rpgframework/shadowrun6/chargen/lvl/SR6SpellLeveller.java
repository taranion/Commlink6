package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6SpellLeveller extends ControllerImpl<SR6Spell> implements SR6SpellController {

	//-------------------------------------------------------------------
	protected SR6SpellLeveller(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<SR6Spell> getAvailable() {
		List<SR6Spell> ret = new ArrayList<>(Shadowrun6Core.getSpells());
		for (SpellValue<? extends ASpell> tmp : getModel().getSpells()) {
			ret.remove(tmp.getModifyable());
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<SpellValue<SR6Spell>> getSelected() {
		List<SpellValue<SR6Spell>> ret = new ArrayList<>();
		getModel().getSpells().forEach( sp -> ret.add((SpellValue<SR6Spell>) sp));
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6Spell value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(SpellValue<SR6Spell> value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(SR6Spell value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(SR6Spell value, Decision... decisions) {
		// Ensure character is caster and has sorcery
		if (!getModel().getMagicOrResonanceType().usesSpells())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SPELLCASTER);
		if (getModel().getSkillValue("sorcery")==null || getModel().getSkillValue("sorcery").getModifiedValue()==0)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SPELLCASTER);

		// Ensure spell has not been selected yet
		for (SpellValue<SR6Spell> tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		if (getModel().getKarmaFree()<5)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, 5);

		if (getModel().getNuyen()<getNuyenCost(value))
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, getNuyenCost(value));

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<SpellValue<SR6Spell>> select(SR6Spell value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", value, Arrays.toString(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select a spell which cannot be selected: {0}",poss);
				return new OperationResult<>(poss);
			}

			SpellValue<SR6Spell> toAdd = new SpellValue<SR6Spell>(value);
			for (Decision dec : decisions) {
				toAdd.addDecision(dec);
			}
			getModel().addSpell(toAdd);
			logger.log(Level.INFO, "Added spell {0}", toAdd);

			// Pay Karma
			int cost = 5;
			getModel().setKarmaFree(getModel().getKarmaFree()-cost);
			getModel().setKarmaInvested(getModel().getKarmaInvested()+cost);

			// Record in history
			DataItemModification mod = new DataItemModification(ShadowrunReference.SPELL, value.getId());
			mod.setExpCost(cost);
			mod.setDate(new Date());
			getModel().addToHistory(mod);

			// Pay Money
			int nuyen = getNuyenCost(value);
			getModel().setNuyen( getModel().getNuyen() - nuyen );

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
	public Possible canBeDeselected(SpellValue<SR6Spell> value) {
		if (!getSelected().contains(value)) {
			return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		if (value.isAutoAdded()) {
			return new Possible(IRejectReasons.IMPOSS_AUTO_ADDED);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	private DataItemModification getHistoryEntryFor(SpellValue<SR6Spell> value) {
		for (DataItemModification mod : getModel().getHistory()) {
			if (mod.getReferenceType()==ShadowrunReference.SPELL && mod.getKey().equals(value.getKey()))
				return mod;
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(SpellValue<SR6Spell> value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.getRequireDecisions()) {
				logger.log(Level.WARNING, "Trying to select a spell which cannot be selected: {0}",poss);
				return false;
			}

			getModel().removeSpell(value);
			logger.log(Level.INFO, "Removed spell {0}", value);

			// Remove from history
			DataItemModification mod = getHistoryEntryFor(value);
			if (mod!=null) {
				getModel().removeFromHistory(mod);
			}

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
	public float getSelectionCost(SR6Spell data, Decision... decisions) {
		return 5;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(SR6Spell data) {
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

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
