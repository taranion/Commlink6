package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.ISpellGenerator;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;

/**
 * @author prelle
 *
 */
public class SR6KarmaSpellGenerator extends ControllerImpl<SR6Spell> implements SR6SpellController, ISpellGenerator<SR6Spell> {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".spells");
	private static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	private int maxSpells;

	//-------------------------------------------------------------------
	protected SR6KarmaSpellGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.ISpellGenerator#usesFreeSpells()
	 */
	public boolean usesFreeSpells() { return false; }

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.ISpellGenerator#getFreeSpells()
	 */
	@Override
	public int getFreeSpells() {
		return getMaxFree() - parent.getModel().getSpells().size() - parent.getModel().getRituals().size();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.ISpellGenerator#getMaxFree()
	 */
	@Override
	public int getMaxFree() {
		return maxSpells;
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

		if ( (getModel().getSpells().size()+getModel().getRituals().size())>=maxSpells) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_MAX_SPELLS, maxSpells);
		}

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

			Shadowrun6Character model = getModel();
			maxSpells = 0;

			SR6KarmaSettings settings = parent.getModel().getCharGenSettings(SR6KarmaSettings.class);
			settings.spells=0;
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells()) {
				int magic = model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
				maxSpells= magic*2;
			}
			logger.log(Level.INFO, "May buy up to {0} spells", maxSpells);

			// Pay Karma
			int karmaNeeded = model.getSpells().size()*5;
			logger.log(Level.INFO, "Pay {0} Karma for spells", karmaNeeded);
			settings.spells = karmaNeeded;
			model.setKarmaFree( model.getKarmaFree() - karmaNeeded);
			model.setKarmaInvested( model.getKarmaInvested() - karmaNeeded);

			// Ensure not enough spells
			if ((model.getSpells().size() + model.getRituals().size())>maxSpells) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.TODO_SPELLS_TOO_MANY));
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
