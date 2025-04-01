package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
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
			if (changes==null || changes.length!=3) {
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

		calculateCargoFactor((Shadowrun6Character) charac, model);
		calculateHardpoints((Shadowrun6Character) charac, model);
		return new OperationResult<List<Modification>>(unprocessed);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private AvailableSlot ensureSlotSize(CarriedItem<?> model, ItemHook hook, float size) {
		logger.log(Level.DEBUG, "Ensure slot {0} has capacity {1}", hook, size);
		AvailableSlot slot = ((CarriedItem<ItemTemplate>)model).getSlot(hook);
		if (slot==null) {
			if (size==0) return null;
			slot = new AvailableSlot(hook, size);
			model.addSlot(slot);
		} else {
			if (size==0) model.removeSlot(hook);
			else
				slot.setCapacity(size);
		}
		return slot;
	}

	//-------------------------------------------------------------------
	private void addModifications(ItemAttributeNumericalValue<SR6ItemAttribute> a1, ItemAttributeNumericalValue<SR6ItemAttribute> a2, int val) {
		if (val!=0) {
			if (val<0) {
				logger.log(Level.DEBUG, "Transfer {0} points from {1} to {2}", Math.abs(val), a2.getModifyable(), a1.getModifyable());
				// Transfer 2:1 points from A2 to A1
				a2.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a2.getModifyable().name(), val, a1.getModifyable()));
				a1.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a1.getModifyable().name(), -val/2, a2.getModifyable()));
			} else {
				logger.log(Level.DEBUG, "Transfer {0} points from {1} to {2}", val, a1.getModifyable(), a2.getModifyable());
				// Transfer 2:1 points from A1 to A2
				a1.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a1.getModifyable().name(), -val, a2.getModifyable()));
				a2.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, a2.getModifyable().name(), val/2, a1.getModifyable()));
			}
		}
	}

	//-------------------------------------------------------------------
	private void calculateCargoFactor(Shadowrun6Character charac, CarriedItem<?> model) {
		ItemAttributeNumericalValue<SR6ItemAttribute> seating = model.getAsValue(SR6ItemAttribute.SEATS);
		int seats = 0;
		if (seating!=null) seats = seating.getDistributed();
		if (seats==0)
			return;

		// Is CF in addition to seating or is seating substracted from cargo factor (substract 1) ?
		boolean substractSeats = false;
		if (charac!=null) {
			RuleConfiguration config = charac.getRuleValue(Shadowrun6Rules.CARGOFACTOR_IS_WITHOUT_SEATS);
			substractSeats = Shadowrun6Rules.CARGOFACTOR_IS_WITHOUT_SEATS.getDefaultAsBooleanValue();
			if (config!=null)
				substractSeats = Boolean.parseBoolean( config.getValueString() );
		}

		int cf = seats;
		ItemSubType subtype = model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		if (subtype==ItemSubType.BIKES) {
			cf = (int) Math.round(cf * (substractSeats?0.25:1.25));
		} else {
			cf = (int) Math.round(cf * (substractSeats?0.5:1.5));
		}
		if (subtype==ItemSubType.TRUCKS) {
			cf += 4;
		}
		logger.log(Level.INFO, "{0}: CF is {1}   (substractSeats={2}, subtype={3})", model.getKey(), cf, substractSeats, subtype);

		ensureSlotSize(model, ItemHook.VEHICLE_CF, cf);
	}

	//-------------------------------------------------------------------
	private void calculateHardpoints(Shadowrun6Character charac, CarriedItem<?> model) {
		if (model.getAsObject(SR6ItemAttribute.ITEMTYPE)==null) return;
		ItemType type = model.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		ItemSubType subtype = model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		ItemAttributeNumericalValue<SR6ItemAttribute> bodyA = model.getAsValue(SR6ItemAttribute.BODY);
		int body = 0;
		if (bodyA!=null) body = bodyA.getDistributed();
		if (body==0)
			return;

		float hardpoints = 0;
		switch (type) {
		case VEHICLES:
		case DRONE_LARGE:
		case DRONE_MEDIUM:

			// Use unmodified BODY / 3
			hardpoints = (int)(body /3.0);
			break;
		case DRONE_SMALL:
			if (body>3)
				hardpoints=0.5f;
			break;
		default:
			// No extra hardpoints
			return;
		}

		// Add hardpoints from mods in vehicle_chassis
		for (CarriedItem<ItemTemplate> acc : ((CarriedItem<ItemTemplate>)model).getAccessories()) {
			hardpoints += (float)acc.getResolved().getOutgoingModifications().stream()
				    .filter(mod -> mod instanceof ValueModification)
				    .filter(mod -> ((ValueModification)mod).getReferenceType()==ShadowrunReference.HOOK) 
				    .filter(mod -> ((ValueModification)mod).getResolvedKey()==ItemHook.VEHICLE_HARDPOINT)
				    .mapToDouble(mod -> ((ValueModification)mod).getValueAsDouble())
				    .sum();
				}
		
		if (hardpoints==0)
			return;

		AvailableSlot slot = ensureSlotSize(model, ItemHook.VEHICLE_HARDPOINT, hardpoints);
		if (type==ItemType.DRONE_SMALL || type==ItemType.DRONE_MINI) {
			if (slot!=null) {
				slot.setMaxSizePerItem(0.5);
			}
		}
	}
}
