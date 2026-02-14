package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
public class ReAddUserItemsInFactoryItems implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public ReAddUserItemsInFactoryItems() {
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {

		for (CarriedItem<?> userItem : model.getAccessoriesInFact()) {
			logger.log(Level.INFO, "Re-adding user item {0} to {1}", userItem, model);
			CarriedItem<ItemTemplate> toAdd = (CarriedItem<ItemTemplate>) userItem;
			
			SR6GearTool.recalculate("", charac, toAdd);
			
			UUID key = userItem.getInFactoryItem();
			Optional<CarriedItem<ItemTemplate>> addTo = Optional.empty();
			// If a UUID is given, try to find the item with that UUID in the accessories of the model. 
			// If no UUID is given, try to find the parent by the slot
			if (key!=null) {
				addTo = model.getEffectiveAccessories().stream()
					.map(acc -> (CarriedItem<ItemTemplate>) acc)
					.filter(acc -> key.equals(acc.getUuid()))
					.findFirst();
			} else {
				addTo = findParentBySlot(model, userItem);
				if (addTo.isEmpty()) {
					logger.log(Level.WARNING, "Could not find parent item for user item {0} in {1} by slot {2}", userItem, model, userItem.getUsedSlot());
				}
			}
			if (addTo.isPresent()) {
				logger.log(Level.INFO, "Adding to {0}", addTo);
				addTo.get().addAccessory(toAdd, userItem.getUsedSlot());
			} else {
				logger.log(Level.WARNING, "Could not find item with key {0} in {1} to add user item {2}", key, model, userItem);
			}
		}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

	//-------------------------------------------------------------------
	private static Optional<CarriedItem<ItemTemplate>> findParentBySlot(CarriedItem<?> model, CarriedItem<?> userItem) {
		Optional<CarriedItem<ItemTemplate>> ret = model.getEffectiveAccessories().stream()
				.map(acc -> (CarriedItem<ItemTemplate>) acc)
				.filter(acc -> userItem.getUsedSlot().equals(acc.getUsedSlot()))
				.findFirst();
		if (ret.isEmpty()) {
			// Check accessories for a slot
			for (CarriedItem<?> acc : model.getEffectiveAccessories()) {
				Optional<CarriedItem<ItemTemplate>> found = findParentBySlot((CarriedItem<?>) acc, userItem);
				if (found.isPresent()) {
					return found;
				}
			}
		}
		
		return ret;
	}
}
