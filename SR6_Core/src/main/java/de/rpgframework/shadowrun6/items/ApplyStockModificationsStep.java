package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.Formula;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyStockModificationsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public ApplyStockModificationsStep() {
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(String indent, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		
		// Read all modifications that are meant for this item		
		for (Modification tmp : model.getModifications()) {
			try {
				logger.log(Level.DEBUG, "Process {0}", tmp);
				if (tmp instanceof ValueModification) {
					applyModification(indent, charac, model, (ValueModification) tmp);
				} else if (tmp instanceof EmbedModification) {
					embedModification(indent, charac, model, (EmbedModification) tmp);
				} else if (tmp instanceof DataItemModification) {
					applyModification(indent, charac, model, (DataItemModification) tmp);
				} else {
					logger.log(Level.ERROR, "Unsupported modification: " + tmp);
				}
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error processing "+tmp+" from "+tmp.getSource(),e);
				System.exit(1);
			}

		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

	// -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void applyModification(String indent, Lifeform charac, CarriedItem<?> model, ValueModification mod) {
		if (mod.getApplyTo() == ApplyTo.CHARACTER) {
			logger.log(Level.WARNING, "Ignore for now " + mod);
			model.addCharacterModification(mod);
			return;
		}
		
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case HOOK:
			ItemHook hook = mod.getResolvedKey();
			Formula form = mod.getFormula();
			AAvailableSlot<ItemHook,ItemTemplate> slot = null;
			if (mod.getRawValue()==null) {
				logger.log(Level.WARNING, "No value in Hook modification from "+mod.getSource()+" - assume 1");
				slot = new AvailableSlot(hook,1);
			} else {
				slot = new AvailableSlot(hook, mod.getValue());
			}
			model.addSlot(slot);
			logger.log(Level.INFO, indent+"Added slot {0} with capacity {1}", hook, slot.getCapacity());
			return;
		case ITEM_ATTRIBUTE:
			logger.log(Level.INFO, "Found modification " + mod);
			model.addModification(mod);
			return;
		default:
			logger.log(Level.WARNING, "Don't know how to deal with:" + mod);
			return;
		}
	}

	// -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void applyModification(String indent, Lifeform charac, CarriedItem<?> model, DataItemModification mod) {
		if (mod.getApplyTo() == ApplyTo.CHARACTER || mod.getApplyTo() == ApplyTo.UNARMED) {
			model.addCharacterModification(mod);
			logger.log(Level.WARNING, "Ignore for now " + mod);
			return;
		}

		Decision[] decs = new Decision[mod.getDecisions().size()];
		decs = mod.getDecisions().toArray(decs);
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case HOOK:
			ItemHook hook = mod.getResolvedKey();
			if (hook==ItemHook.SOFTWARE && model.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS)!=null) {
				logger.log(Level.INFO, indent + "Add slot {0} and take capacity from CONCURRENT_PROGRAMS ", hook);
				AvailableSlot slot = new AvailableSlot(hook, model.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS).getDistributed());
				model.addSlot(slot);
			} else {
				if (mod.isRemove()) {
					logger.log(Level.INFO, indent + "Remove slot {0} from {1}", hook, mod.getSource());
					model.removeSlot(hook);
				} else {
				logger.log(Level.INFO, indent + "Add slot {0} without capacity from {1}", hook, mod.getSource());
				AvailableSlot slot = (hook.hasCapacity)?(new AvailableSlot(hook)):(new AvailableSlot(hook));
				model.addSlot(slot);
				}
			}
			return;
		case GEAR:
			model.addCharacterModification(mod);
			return;
		}
		logger.log(Level.WARNING, "ToDo: DataItemModification " + mod);
//		model.addModification(mod);
	}

	// -------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void embedModification(String indent, Lifeform charac, CarriedItem<?> model, EmbedModification mod) {
		if (mod.getApplyTo() == ApplyTo.CHARACTER || mod.getApplyTo() == ApplyTo.UNARMED) {
			model.addCharacterModification(mod);
			logger.log(Level.WARNING, "Ignore for now " + mod);
			return;
		}

		Decision[] decs = new Decision[mod.getDecisions().size()];
		decs = mod.getDecisions().toArray(decs);
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case GEAR:
			ItemTemplate templ = mod.getReferenceType().resolve(mod.getKey());
			ItemHook hook = mod.getHook();
			logger.log(Level.INFO, "Add instanceof {0} into hook {1} of {2}", mod.getKey(), hook, model.getKey());
			OperationResult<CarriedItem<ItemTemplate>> carriedR = SR6GearTool.buildItem(templ, CarryMode.EMBEDDED, charac, false, decs);
			if (carriedR.hasError()) {
				logger.log(Level.ERROR, "Error embedding {0} into hook {1} of {2}: {3}", mod.getKey(), hook, model.getKey(),carriedR.getError());
				return;
			}
			CarriedItem accessory = carriedR.get();
			accessory.setInjectedBy(mod.getSource());
			accessory.addModification(mod);
			//if (mod.isIncludedInStats())
			// Check if AvailableSlot already exists - if not, create one
			AvailableSlot slot = (AvailableSlot) model.getSlot(hook);
			if (slot==null) {
				if (hook.hasCapacity) {
					logger.log(Level.ERROR, "Item {0} has an <embed> for a not existing slot {1}, but cannot auto-create slot since no capacity is given", mod.getKey(), hook);
				}
				slot = new AvailableSlot(hook);
				model.addSlot(slot);
			}
			slot.addEmbeddedItem(accessory);
			return;
		}
		logger.log(Level.WARNING, "ToDo: EmbedModification " + mod);
//		model.addModification(mod);
	}

}
