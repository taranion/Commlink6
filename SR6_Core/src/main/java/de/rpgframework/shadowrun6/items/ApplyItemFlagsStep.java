package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;

/**
 * @author prelle
 *
 */
public class ApplyItemFlagsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		if (model.hasFlag(SR6ItemFlag.ALCHEMICAL_PREPARATION)) {
			applyALCHEMICAL_PREPARATION((CarriedItem<ItemTemplate>) model);
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

	//-------------------------------------------------------------------
	private void applyALCHEMICAL_PREPARATION(CarriedItem<ItemTemplate> model) {
		System.err.println("Apply ALCHEMICAL_PREPARATION");
		Decision decPotency = model.getDecision(ItemTemplate.UUID_RATING);
		Decision decMagic   = model.getDecisionByRef("MAGIC");
		int potency = Math.max(0, (decPotency!=null)?(decPotency.getValueAsInt()-4) : 0);
		int magic   = Math.max(0, (decMagic!=null)?(decMagic.getValueAsInt()-4) : 0);

		boolean extended    = "extended".equals( model.getVariantID() );
		int basePrice = model.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		int price = basePrice + (potency*100) + (magic*50);
		if (extended)
			price *=4;
		logger.log(Level.INFO, "ALCHEMICAL_PREPARATION: base price={0}  potency={1}  magic={2}  extend={3}  --> {4}", basePrice, potency, magic, extended, price);
		model.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, price));
	}

}
