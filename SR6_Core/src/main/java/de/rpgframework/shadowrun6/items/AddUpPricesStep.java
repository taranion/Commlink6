package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
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
	@SuppressWarnings("unchecked")
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		ItemAttributeNumericalValue<SR6ItemAttribute> attrib = model.getAsValue(SR6ItemAttribute.PRICE);
		if (attrib==null) {
			if (!"software_library".equals(model.getKey()) && !"unarmed".equals(model.getKey())) {
				logger.log(Level.WARNING, "Item {0} has no price", model.getKey());
			}
			return new OperationResult<List<Modification>>(unprocessed);
		}

		if (model.hasFlag(SR6ItemFlag.CUSTOM_FITTED)) {
			/* The custom fitting process modifies the base limb’s cost by 7,000 nuyen for
			 * each point of the user’s Strength and Agility that is higher than 2. */
			int plus = Math.max(0, charac.getAttribute(ShadowrunAttribute.AGILITY).getDistributed()-2)*7000;
			plus += Math.max(0, charac.getAttribute(ShadowrunAttribute.STRENGTH).getDistributed()-2)*7000;
			attrib.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), plus, SR6ItemFlag.CUSTOM_FITTED));
		}
		if (model.hasFlag(SR6ItemFlag.CHEAP_KNOCK_OFF)) {
			int value = attrib.getDistributed()/2;
			attrib.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), -value, SR6ItemFlag.CHEAP_KNOCK_OFF));
		}

		// Add prices of accessories
		for (CarriedItem<? extends PieceOfGear> carried : model.getAccessories()) {
			if (carried.isAutoAdded() || carried.hasFlag(SR6ItemFlag.AUTO_ADDED)) {
				logger.log(Level.INFO, "Ignore cost of auto-added {0}", carried.getKey());
				continue;
			}
			ItemAttributeNumericalValue<SR6ItemAttribute> aVal = carried.getAsValue(SR6ItemAttribute.PRICE);
			if (aVal==null) {
				logger.log(Level.ERROR, "No attribute PRICE set for item "+carried.getKey()+"/"+carried.getUuid());
				return new OperationResult<List<Modification>>();
			}
			int cost = aVal.getModifiedValue();
			logger.log(Level.INFO, "Increase cost by {0} from {1}", cost, carried.getKey());
			attrib.setDistributed( attrib.getDistributed() + cost);
//			ValueModification valMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), cost);
//			valMod.setSource("item:"+carried.getKey());
//			valMod.setOrigin(Origin.CHILDREN);
//			attrib.addIncomingModification(valMod );
		}
//		logger.log(Level.INFO, "Price before multiply {0}", model.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
//
//		if (model.getCount()>1) {
//			int total = attrib.getModifiedValue() * (model.getCount()-1);
//			ValueModification valMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), total, "+"+(model.getCount()-1)+"x");
//			valMod.setOrigin(Origin.OUTSIDE);
//			attrib.addIncomingModification(valMod);
//		}
//		logger.log(Level.INFO, "Price after multiply {0}", model.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
