package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateProgramSlots implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public CalculateProgramSlots() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {

		AvailableSlot slot = (AvailableSlot) model.getSlot(ItemHook.SOFTWARE);
		if (model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE)==null)
			return new OperationResult<List<Modification>>(unprocessed);
		ItemSubType subtype = model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		switch (subtype) {
		case COMMLINK:
		case CYBERDECK:
		case RIGGER_CONSOLE:
		case TAC_NET:
			if (slot==null) {
				slot = new AvailableSlot(ItemHook.SOFTWARE, 99);
				model.addSlot(slot);
			}
			break;
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}
}
