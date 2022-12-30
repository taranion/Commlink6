package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Optional;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class AddUpPricesStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 */
	public AddUpPricesStep() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		ItemAttributeNumericalValue<SR6ItemAttribute> attrib = model.getAsValue(SR6ItemAttribute.PRICE);
		if (attrib==null) {
			logger.log(Level.WARNING, "Item {0} has no price", model.getKey());
			return new OperationResult<List<Modification>>(unprocessed);
		}

		// Add prices of accessories
		for (CarriedItem<? extends PieceOfGear> carried : model.getAccessories()) {
			ItemAttributeNumericalValue<SR6ItemAttribute> aVal = carried.getAsValue(SR6ItemAttribute.PRICE);
			if (aVal==null) {
				logger.log(Level.ERROR, "No attribute PRICE set for item "+carried.getKey()+"/"+carried.getUuid());
				return new OperationResult<List<Modification>>();
			}
			int cost = aVal.getModifiedValue();
			logger.log(Level.INFO, "Increase cost by {0} from {1}", cost, carried.getKey());
			attrib.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), cost) );
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
