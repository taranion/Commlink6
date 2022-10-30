package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonRitualController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6PriorityRitualGenerator extends CommonRitualController implements IRitualController {
	
	private int freeSpells;

	//-------------------------------------------------------------------
	public SR6PriorityRitualGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getFreeSpells() {
		return freeSpells;
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
		
		if (freeSpells<1) {
			boolean karmaAllowed =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
			if (karmaAllowed && getModel().getKarmaFree()>=5) {
				return Possible.TRUE;
			}
			
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);
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
	public float getSelectionCost(Ritual data) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Ritual data) {
		return String.valueOf(getSelectionCostString(data));
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
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells() && model.hasCharGenSettings(SR6PrioritySettings.class)) {				
				SR6PrioritySettings settings = getModel().getCharGenSettings(SR6PrioritySettings.class);
				if (model.getMagicOrResonanceType().usesPowers()) {
					// Mystic adept
					freeSpells = (settings.mysticAdeptMaxPoints - settings.getMagicForPP()) *2;
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
				boolean karmaAllowed =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
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
