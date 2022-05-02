package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
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
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyStockModificationsStep implements CarriedItemProcessor {
	
	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 */
	public ApplyStockModificationsStep() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {
		for (Modification tmp : model.getModifyable().getModifications()) {
			if (tmp.getApplyTo()==ApplyTo.CHARACTER) {
				unprocessed.add(tmp);
				if (logger.isLoggable(Level.DEBUG)) logger.log(Level.DEBUG, indent+"found modification for character: "+tmp);
			} else {
				if (tmp instanceof ValueModification) {
					applyModification(indent, charac, model, (ValueModification) tmp);
				} else if (tmp instanceof DataItemModification) {
					applyModification(indent, charac, model, (DataItemModification) tmp);
				} else {
					logger.log(Level.ERROR, "Unsupported modification: "+tmp);
				}
			}
			
		}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

	//-------------------------------------------------------------------
	private void applyModification(String indent, Lifeform charac, CarriedItem<?> model, ValueModification mod) {
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case HOOK:
			ItemHook hook = mod.getResolvedKey();
			Formula form = mod.getFormula();
			logger.log(Level.INFO, indent+"form="+form+" / "+form.isResolved());
			AAvailableSlot<ItemHook> slot = null;
			if (!form.isResolved()) {
				VariableResolver resolver = new VariableResolver(model, charac);

				String foo = FormulaTool.resolve(SR6ItemAttribute.CAPACITY, (FormulaImpl) form, resolver);
				logger.log(Level.INFO, indent+"foo="+foo);
				int capacity = Integer.parseInt(foo);
				slot = new AvailableSlot(hook, capacity);
			} else {
				slot = new AvailableSlot(hook, mod.getValue());
			}
			model.addSlot(slot);
			logger.log(Level.INFO, indent+"Added slot {0} with capacity {1}", hook, slot.getCapacity());
			return;
		}
		logger.log(Level.WARNING, "ToDo: ValueModification "+mod);
		model.addModification(mod);
	}

	//-------------------------------------------------------------------
	private void applyModification(String indent, Lifeform charac, CarriedItem<?> model, DataItemModification mod) {
		logger.log(Level.WARNING, "ToDo: DataItemModification "+mod);
	}

}
