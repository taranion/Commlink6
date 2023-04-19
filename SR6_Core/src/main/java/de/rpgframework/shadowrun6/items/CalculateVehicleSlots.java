package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateVehicleSlots implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public CalculateVehicleSlots() {
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
		logger.log(Level.DEBUG, "Modification slots {0}",model.getAsObject(SR6ItemAttribute.VEHICLE_MODSLOTS));
		if (model.getAsObject(SR6ItemAttribute.VEHICLE_MODSLOTS)!=null) {
			int[] base = model.getAsObject(SR6ItemAttribute.VEHICLE_MODSLOTS).getValue();
			int[] changes = model.getModificationSlotChanges();
			if (changes==null) {
				changes=new int[] {0,0,0};
				model.setModificationSlotChanges(changes);
			}

			ItemAttributeNumericalValue<SR6ItemAttribute> chassis = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.MODSLOTS_CHASSIS, base[0]);
			ItemAttributeNumericalValue<SR6ItemAttribute> electro = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.MODSLOTS_ELECTRONICS, base[1]);
			ItemAttributeNumericalValue<SR6ItemAttribute> powertr = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.MODSLOTS_POWERTRAIN, base[2]);
			// Electronics / Powertrain
			addModifications(chassis, electro, changes[0]);
			addModifications(electro, powertr, changes[1]);
			addModifications(powertr, chassis, changes[2]);

			model.setAttribute(SR6ItemAttribute.MODSLOTS_CHASSIS, chassis);
			model.setAttribute(SR6ItemAttribute.MODSLOTS_ELECTRONICS, electro);
			model.setAttribute(SR6ItemAttribute.MODSLOTS_POWERTRAIN, powertr);
			logger.log(Level.DEBUG, "Chassis: {0}   Electro: {1}   Powertr: {2}", chassis, electro, powertr);

			// Now create slots
			ensureSlotSize(model, ItemHook.VEHICLE_CHASSIS, chassis.getModifiedValue());
			ensureSlotSize(model, ItemHook.VEHICLE_ELECTRONICS, electro.getModifiedValue());
			ensureSlotSize(model, ItemHook.VEHICLE_POWERTRAIN, powertr.getModifiedValue());
		}
		return new OperationResult<List<Modification>>(unprocessed);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void ensureSlotSize(CarriedItem<?> model, ItemHook hook, int size) {
		logger.log(Level.WARNING, "Ensure slot {0} has capacity {1}", hook, size);
		AvailableSlot slot = ((CarriedItem<ItemTemplate>)model).getSlot(hook);
		if (slot==null) {
			if (size==0) return;
			slot = new AvailableSlot(hook, size);
			model.addSlot(slot);
		} else {
			if (size==0) model.removeSlot(hook);
			else
				slot.setCapacity(size);
		}
	}

	//-------------------------------------------------------------------
	private void addModifications(ItemAttributeNumericalValue<SR6ItemAttribute> a1, ItemAttributeNumericalValue<SR6ItemAttribute> a2, int val) {
		if (val!=0) {
			if (val<0) {
				logger.log(Level.DEBUG, "Transfer {0} points from {1} to {2}", Math.abs(val), a2.getModifyable(), a1.getModifyable());
				// Transfer 2:1 points from A2 to A1
				a2.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a2.getModifyable().name(), val, a1.getModifyable()));
				a1.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a1.getModifyable().name(), -val/2, a2.getModifyable()));
			} else {
				logger.log(Level.DEBUG, "Transfer {0} points from {1} to {2}", val, a1.getModifyable(), a2.getModifyable());
				// Transfer 2:1 points from A1 to A2
				a1.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a1.getModifyable().name(), -val, a2.getModifyable()));
				a2.addModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a2.getModifyable().name(), val/2, a1.getModifyable()));
			}
		}
	}
}
