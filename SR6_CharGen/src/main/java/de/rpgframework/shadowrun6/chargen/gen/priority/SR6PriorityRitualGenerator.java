package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.IRitualGenerator;
import de.rpgframework.shadowrun.chargen.gen.ISpellGenerator;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonRitualController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6PriorityRitualGenerator extends CommonRitualController implements IRitualGenerator {

	private int freeLeft;
	private int maxSpellsAndRituals;

	//-------------------------------------------------------------------
	public SR6PriorityRitualGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IRitualGenerator#usesFreeRituals()
	 */
	@Override
	public boolean usesFreeRituals() {
		return true;
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
				return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		if (freeLeft<1) {
			boolean karmaAllowed =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
			if (karmaAllowed && getModel().getKarmaFree()>=5) {
				return Possible.TRUE;
			}

			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);
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
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		if (value.isAutoAdded()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_AUTO_ADDED);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(Ritual data, Decision... decisions) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Ritual data) {
		return "0";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<RitualValue> select(Ritual value, Decision... decisions) {
		OperationResult<RitualValue> ret = super.select(value, decisions);
		parent.runProcessors();
		return ret;
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
			freeLeft = ((ISpellGenerator<?>)parent.getSpellController()).getFreeSpells();
			maxSpellsAndRituals = ((ISpellGenerator<?>)parent.getSpellController()).getMaxFree();

			Shadowrun6Character model = getModel();

			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} remaining free spells", freeLeft);

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
