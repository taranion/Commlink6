package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

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
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author prelle
 *
 */
public class SR6PointBuyRitualGenerator extends CommonRitualController implements IRitualController {

	private int maxSpellsAndRituals;

	//-------------------------------------------------------------------
	public SR6PointBuyRitualGenerator(SR6CharacterController parent) {
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
	public Possible canBeSelectedCP(Ritual value, Decision... decisions) {
		// Ensure spell has not been selected yet
		for (RitualValue tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		if (!parent.getModel().hasCharGenSettings(SR6PointBuySettings.class)) {
			logger.log(Level.ERROR, "Expected SR6PointBuySettings not found");
			return Possible.FALSE;
		}

		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		if (settings.characterPoints<2)
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		if (settings.sumSpellsRituals>=maxSpellsAndRituals)
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_MAX_SPELLS);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	public Possible canBeSelectedKarma(Ritual value, Decision... decisions) {
		// Ensure spell has not been selected yet
		for (RitualValue tmp : getSelected()) {
			if (tmp.getResolved()==value)
				return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		if (parent.getModel().getKarmaFree()<5)
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);

		if (!parent.getModel().hasCharGenSettings(SR6PointBuySettings.class)) {
			logger.log(Level.ERROR, "Expected SR6PointBuySettings not found");
			return Possible.FALSE;
		}

		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		if (settings.sumSpellsRituals>=maxSpellsAndRituals)
			return new Possible(Severity.STOPPER, IRejectReasons.RES ,IRejectReasons.IMPOSS_MAX_SPELLS);

		return Possible.TRUE;
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

		Possible p1 = canBeSelectedCP(value, decisions);
		Possible p2 = canBeSelectedKarma(value, decisions);
		if (p1.get() || p2.get() )
			return Possible.TRUE;
		return p1;
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
	public float getSelectionCost(Ritual data) {
		return 2;
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
		boolean payWithCP = canBeSelectedCP(value, decisions).get();

		OperationResult<RitualValue> result = super.select(value, decisions);
		logger.log(Level.INFO, "Pay {0} with CP = {1}", value.getId(), payWithCP);
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		logger.log(Level.INFO, "Put "+result.get()+" = "+payWithCP);
		settings.perRitualPayedWithCP.put(result.get().getKey(), payWithCP);

		parent.runProcessors();
		return result;
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
			maxSpellsAndRituals = 0;

			Shadowrun6Character model = getModel();
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells()) {
				CommonSR6GeneratorSettings settings = model.getCharGenSettings(CommonSR6GeneratorSettings.class);
				maxSpellsAndRituals = ( model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue() - settings.getMagicForPP() ) *2;;
			}
			logger.log(Level.INFO, "Can pick up to {0} spells and rituals", maxSpellsAndRituals);

			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			for (RitualValue val : model.getRituals()) {
				settings.sumSpellsRituals++;
				Boolean payedWithCP = settings.perRitualPayedWithCP.get(val);
				logger.log(Level.INFO, "perRitualPayedWithCP = "+settings.perRitualPayedWithCP);
				if (payedWithCP!=null && payedWithCP) {
					settings.characterPoints -= 2;
					settings.spellsCP++;
					logger.log(Level.INFO, "Pay ritual ''{0}'' with 2 CP", val.getModifyable().getId());
				} else {
					model.setKarmaFree( model.getKarmaFree() -5 );
					settings.spellsKarma++;
					logger.log(Level.INFO, "Pay ritual ''{0}'' with 5 Karma", val.getModifyable().getId());
				}
			}

			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} of allowed {1} spells and rituals", settings.sumSpellsRituals, maxSpellsAndRituals);
			if (settings.sumSpellsRituals>maxSpellsAndRituals) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many spells bought"));
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
