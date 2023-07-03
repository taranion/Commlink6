package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;

/**
 * @author prelle
 *
 */
public class AddMissingStandardSlots implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public AddMissingStandardSlots() {
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		if (model.getAsObject(SR6ItemAttribute.ITEMTYPE)==null)
			return new OperationResult<List<Modification>>(unprocessed);

		ItemType type = model.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		ItemSubType subtype = model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		if (type==ItemType.WEAPON_FIREARMS) {
			if (model.getSlot(ItemHook.FIREARMS_EXTERNAL)==null) {
				logger.log(Level.DEBUG, "Add FIREARMS_EXTERNAL slot to {0}", model.getKey());
				AvailableSlot extern = new AvailableSlot(ItemHook.FIREARMS_EXTERNAL,99);
				model.addSlot(extern);
			}
		}

		if (type==ItemType.ELECTRONICS) {
			switch (subtype) {
			case CYBERDECK:
			case COMMLINK:
			case DATATERM:
			case CYBERTERM:
			case RIGGER_CONSOLE:
			case TAC_NET:
				if (model.getSlot(ItemHook.ELECTRONIC_ACCESSORY)==null) {
					logger.log(Level.DEBUG, "Add ELECTRONIC_ACCESSORY slot to {0}", model.getKey());
					AvailableSlot extern = new AvailableSlot(ItemHook.ELECTRONIC_ACCESSORY,99);
					model.addSlot(extern);
				}
				break;
			}
		}

//		if (ItemType.isVehicle(type)) {
//			logger.log(Level.WARNING, "TODO: add vehicle slots to {0}", type);
//		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
