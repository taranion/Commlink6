package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.ISpellGenerator;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;

/**
 * Life Path grants starting spells and rituals from the final Magic value.
 */
public class SR6LifePathSpellGenerator extends ControllerImpl<SR6Spell> implements SR6SpellController, ISpellGenerator<SR6Spell> {

	private static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".lifepath.spells");

	private int freeSpells;
	private int maxFree;

	//-------------------------------------------------------------------
	public SR6LifePathSpellGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	@Override
	public boolean usesFreeSpells() {
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public int getFreeSpells() {
		return freeSpells;
	}

	//-------------------------------------------------------------------
	@Override
	public int getMaxFree() {
		return maxFree;
	}

	//-------------------------------------------------------------------
	@Override
	public List<SR6Spell> getAvailable() {
		List<SR6Spell> ret = new ArrayList<>(Shadowrun6Core.getSpells());
		for (SpellValue<? extends ASpell> tmp : getModel().getSpells()) {
			ret.remove(tmp.getModifyable());
		}
		return ret;
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public List<SpellValue<SR6Spell>> getSelected() {
		List<SpellValue<SR6Spell>> ret = new ArrayList<>();
		getModel().getSpells().forEach(sp -> ret.add((SpellValue<SR6Spell>)sp));
		return ret;
	}

	//-------------------------------------------------------------------
	@Override
	public RecommendationState getRecommendationState(SR6Spell value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	@Override
	public RecommendationState getRecommendationState(SpellValue<SR6Spell> value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Choice> getChoicesToDecide(SR6Spell value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeSelected(SR6Spell value, Decision... decisions) {
		if (getModel().getMagicOrResonanceType()==null || !getModel().getMagicOrResonanceType().usesSpells())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SPELLCASTER);
		if ((getModel().getSkillValue("sorcery")==null || getModel().getSkillValue("sorcery").getModifiedValue()==0)
				&& (getModel().getSkillValue("enchanting")==null || getModel().getSkillValue("enchanting").getModifiedValue()==0))
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SPELLCASTER);

		for (SpellValue<SR6Spell> tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}
		if (freeSpells<1)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	@Override
	public OperationResult<SpellValue<SR6Spell>> select(SR6Spell value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1})", value, Arrays.toString(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to select a spell which cannot be selected: {0}", poss);
				return new OperationResult<>(poss);
			}

			SpellValue<SR6Spell> toAdd = new SpellValue<>(value);
			for (Decision dec : decisions) {
				toAdd.addDecision(dec);
			}

			getModel().addSpell(toAdd);
			logger.log(Level.INFO, "Added spell {0}", toAdd);
			parent.runProcessors();
			return new OperationResult<>(toAdd);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1})", value, Arrays.toString(decisions));
		}
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeDeselected(SpellValue<SR6Spell> value) {
		if (!getSelected().contains(value))
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_PRESENT);
		if (value.isAutoAdded())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_AUTO_ADDED);
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean deselect(SpellValue<SR6Spell> value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to deselect a spell which cannot be deselected: {0}", poss);
				return false;
			}
			getModel().removeSpell(value);
			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	//-------------------------------------------------------------------
	@Override
	public float getSelectionCost(SR6Spell data, Decision... decisions) {
		return 0;
	}

	//-------------------------------------------------------------------
	@Override
	public String getSelectionCostString(SR6Spell data) {
		return "0";
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);
		todos.clear();
		maxFree = 0;
		freeSpells = 0;

		if (getModel().getMagicOrResonanceType()!=null && getModel().getMagicOrResonanceType().usesSpells()) {
			maxFree = getModel().getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue() * 2;
			freeSpells = maxFree;
		}

		for (SpellValue<? extends ASpell> val : getModel().getSpells()) {
			freeSpells--;
		}
		for (RitualValue val : getModel().getRituals()) {
			freeSpells--;
		}

		if (freeSpells>0) {
			todos.add(new ToDoElement(Severity.WARNING, "Unused spells or rituals"));
		} else if (freeSpells<0) {
			todos.add(new ToDoElement(Severity.STOPPER, "Too many spells or rituals selected"));
		}

		return unprocessed;
	}
}
