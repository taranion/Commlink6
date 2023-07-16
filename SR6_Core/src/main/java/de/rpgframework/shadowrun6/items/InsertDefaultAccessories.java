package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class InsertDefaultAccessories implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public InsertDefaultAccessories() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		// Add "Anti-Theft" to all vehicles with a pilot rating
		ItemAttributeNumericalValue<SR6ItemAttribute> pilotVal = model.getAsValue(SR6ItemAttribute.PILOT);
		if (pilotVal!=null && pilotVal.getModifiedValue()>0) {
			if (model.getAccessory("anti_theft",null)==null) {
				ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "anti_theft");
				CarriedItem access = new CarriedItem<ItemTemplate>(item, item.getVariant("rating1"), CarryMode.EMBEDDED);
				SR6GearTool.recalculate("", charac, access);
				access.setInjectedBy("DEFAULT");
				access.addFlag(SR6ItemFlag.AUTO_ADDED);
				model.addAccessory(access, ItemHook.VEHICLE_ELECTRONICS);
			}
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}
}
