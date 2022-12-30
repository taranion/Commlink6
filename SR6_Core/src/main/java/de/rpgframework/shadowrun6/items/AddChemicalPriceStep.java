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
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * If the item has a decision for a chemical, add the price with the corresponding multiplier
 *
 * @author prelle
 *
 */
public class AddChemicalPriceStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public AddChemicalPriceStep() {
		// Instantiated from SR6GearTool
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model, List<Modification> unprocessed) {
		Decision dec = model.getDecision(ItemTemplate.UUID_CHEMICAL_CHOICE);
		if (dec!=null) {
			ItemTemplate chemical = Shadowrun6Core.getItem(ItemTemplate.class, dec.getValue());
			if (chemical==null) {
				logger.log(Level.ERROR, "Unknown chemical {0} referenced in {1}", dec.getValue(), model.getKey());
			} else {
				ItemSubType sub = model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
				int multiplier = 1;
				switch (sub) {
				case GRENADES: multiplier = 20; break;
				case ROCKETS : multiplier = 100; break;
				default:
				}
				int chemPrice = chemical.getAttribute(SR6ItemAttribute.PRICE).getDistributed() * multiplier;
				logger.log(Level.INFO, "Add {0} Nuyen for {1} units of chemical {2}", chemPrice, multiplier, chemical.getId());
				ItemAttributeNumericalValue<SR6ItemAttribute> attrib = model.getAsValue(SR6ItemAttribute.PRICE);
				attrib.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), chemPrice, chemical) );
			}
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
