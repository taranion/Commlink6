package de.rpgframework.shadowrun6.chargen.gen;

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
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;

/**
 * @author prelle
 *
 */
public class SR6PrioritySpellGenerator extends ControllerImpl<SR6Spell> implements SR6SpellController {
	
	private int freeSpells;

	//-------------------------------------------------------------------
	protected SR6PrioritySpellGenerator(SpliMoCharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getFreeSpells() {
		return freeSpells;
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
	@SuppressWarnings("unchecked")
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
		// Ensure spell has not been selected yet
		for (SpellValue<SR6Spell> tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}
		
		if (freeSpells<1) {
			boolean karmaAllowed = getModel().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
			if (karmaAllowed && getModel().getKarmaFree()>=5) {
				return Possible.TRUE;
			}
			
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);
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
	public float getSelectionCost(SR6Spell data) {
		// TODO Auto-generated method stub
		return 0;
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
			freeSpells = 0;
			
			Shadowrun6Character model = getModel();
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells()) {				
				SR6PrioritySettings settings = getModel().getCharGenSettings(SR6PrioritySettings.class);
				if (model.getMagicOrResonanceType().usesPowers()) {
					// Mystic adept
					freeSpells = (settings.mysticAdeptMaxPoints - settings.mysticAdeptPowerPoints) *2;
				} else {
					freeSpells = settings.perAttrib.get(ShadowrunAttribute.MAGIC).base * 2;
				}
				logger.log(Level.INFO, "Have {0} free spells", freeSpells);
			}
			
			int byKarma = 0;
			for (SpellValue<? extends ASpell> val : model.getSpells()) {
				if (freeSpells>0)
					freeSpells--;
				else {
					byKarma++;
					model.setKarmaFree( model.getKarmaFree() -5 );
					logger.log(Level.INFO, "Pay spell ''{0}'' with 5 Karma", val.getModifyable().getId());
				}
			}
			
			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} remaining free spells", freeSpells);
			if (freeSpells>0) {
				todos.add(new ToDoElement(Severity.WARNING, "Unused spells"));
			} else if (byKarma>0) {
				boolean karmaAllowed = getModel().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
				if (!karmaAllowed) {
					todos.add(new ToDoElement(Severity.STOPPER, "Too many spells bought"));
				}
			}
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
