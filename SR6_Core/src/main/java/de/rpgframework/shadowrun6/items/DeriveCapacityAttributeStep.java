package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.Hook;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * If no capacity is set and there is exactly one slot that has a capacity, use
 * this for the attribute.
 * 
 * @author prelle
 *
 */
public class DeriveCapacityAttributeStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public DeriveCapacityAttributeStep() {
		// Instantiated from SR6GearTool
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model, List<Modification> unprocessed) {
		ItemAttributeNumericalValue<SR6ItemAttribute> capVal = model.getAsValue(SR6ItemAttribute.CAPACITY);
		List<AAvailableSlot<? extends Hook>> slotsWithCap = model.getSlots().stream().filter(s -> s.getHook().hasCapacity()).collect(Collectors.toUnmodifiableList());
		if (capVal==null && slotsWithCap.size()==1) {
			int cap = Math.round(slotsWithCap.get(0).getCapacity());
			logger.log(Level.DEBUG, "set CAPACITY to {0}",cap);
			capVal = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.CAPACITY, cap);
			model.setAttribute(SR6ItemAttribute.CAPACITY, capVal);
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
