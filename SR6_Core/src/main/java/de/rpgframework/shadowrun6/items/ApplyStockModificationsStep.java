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
import de.rpgframework.genericrpg.items.Formula;
import de.rpgframework.genericrpg.items.formula.FormulaImpl;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.DataItemModification;
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
			logger.log(Level.DEBUG, "Process {0}", tmp);
			if (tmp instanceof ValueModification) {
				applyModification(indent, charac, model, (ValueModification) tmp);
			} else if (tmp instanceof DataItemModification) {
				applyModification(indent, charac, model, (DataItemModification) tmp);
			} else {
				logger.log(Level.ERROR, "Unsupported modification: " + tmp);
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
			AAvailableSlot<ItemHook,ItemTemplate> slot = new AvailableSlot(hook, mod.getValue());
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
				logger.log(Level.INFO, indent + "Add slot {0} without capacity ", hook);
				AvailableSlot slot = new AvailableSlot(hook);
				model.addSlot(slot);
			}
			return;
		case GEAR:
			model.addCharacterModification(mod);
			return;
		}
		logger.log(Level.WARNING, "ToDo: DataItemModification " + mod);
//		model.addModification(mod);
	}

}
