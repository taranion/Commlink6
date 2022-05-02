package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AccessoryInSlot;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.ApplyableValueModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
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
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {
		for (AccessoryInSlot tmp : model.getAccessories()) {
			CarriedItem<ItemTemplate> carried = tmp.getItem();
			int cost = carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			logger.log(Level.INFO, "Increase cost by {0} from {1}", cost, carried.getKey());
			ItemAttributeNumericalValue<SR6ItemAttribute> attrib = model.getAsValue(SR6ItemAttribute.PRICE);
			attrib.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), cost) );
			
		}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

}
