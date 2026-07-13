package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonRitualController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

public class SR6LifePathRitualGenerator extends CommonRitualController implements IRitualController {

	private int maxFree;

	//-------------------------------------------------------------------
	public SR6LifePathRitualGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	@Override
	public boolean usesFreeRituals() {
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public int getFreeRituals() {
		return getMaxFree() - getModel().getSpells().size() - getModel().getRituals().size();
	}

	//-------------------------------------------------------------------
	@Override
	public int getMaxFree() {
		return maxFree;
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeSelected(Ritual value, Decision... decisions) {
		if (getModel().getMagicOrResonanceType()==null || !getModel().getMagicOrResonanceType().usesSpells())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SPELLCASTER);

		for (RitualValue tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}
		if (getFreeRituals()<1)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	@Override
	public OperationResult<RitualValue> select(Ritual value, Decision... decisions) {
		OperationResult<RitualValue> result = super.select(value, decisions);
		if (result.wasSuccessful())
			parent.runProcessors();
		return result;
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeDeselected(RitualValue value) {
		if (!getSelected().contains(value))
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_PRESENT);
		if (value.isAutoAdded())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_AUTO_ADDED);
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	@Override
	public float getSelectionCost(Ritual data, Decision... decisions) {
		return 0;
	}

	//-------------------------------------------------------------------
	@Override
	public String getSelectionCostString(Ritual data) {
		return "0";
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);
		try {
			todos.clear();
			maxFree = 0;
			if (getModel().getMagicOrResonanceType()!=null && getModel().getMagicOrResonanceType().usesSpells())
				maxFree = getModel().getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue() * 2;

			int free = maxFree;
			for (SpellValue<? extends ASpell> val : getModel().getSpells()) {
				free--;
			}
			for (RitualValue val : getModel().getRituals()) {
				free--;
			}
			if (free<0)
				todos.add(new de.rpgframework.genericrpg.ToDoElement(Severity.STOPPER, "Too many spells or rituals selected"));

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}
}
