package de.rpgframework.shadowrun6.items;

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
public class SR6ModeDetailsStep implements CarriedItemProcessor {

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.modification.ModifiedObjectType, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType refType, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		OperationResult<List<Modification>> ret = new OperationResult<>(unprocessed);
		if (model.getAsObject(SR6ItemAttribute.ITEMTYPE)==null)
			return ret;
		ItemType type = model.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		switch (type) {
		case WEAPON_CLOSE_COMBAT:
		case WEAPON_FIREARMS:
		case WEAPON_VEHICLE:
		case WEAPON_RANGED:
		case WEAPON_SPECIAL:
//			model.addOperationMode(ItemTemplate.MODE_WIRELESS);
			break;
		default:
		}

		return ret;
	}

}
