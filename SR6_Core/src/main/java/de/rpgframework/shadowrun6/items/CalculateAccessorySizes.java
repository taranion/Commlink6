package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.Hook;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
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
public class CalculateAccessorySizes implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public CalculateAccessorySizes() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType refType, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		OperationResult<List<Modification>> ret = new OperationResult<>(unprocessed);

		for (CarriedItem<? extends PieceOfGear> accessoryR : model.getAccessories()) {
			CarriedItem<ItemTemplate> accessory = (CarriedItem<ItemTemplate>) accessoryR;
			Usage usage = accessory.getResolved().getUsage(accessory.getCarryMode());

			//logger.log(Level.INFO, indent+"accessory {0} in slot {1}", accessory.getKey(), hook);
			OperationResult<List<Modification>> sub = GearTool.recalculate("", refType, charac, accessory, strict);
			logger.log(Level.INFO, "Usage ''{0}''", usage);
			// Check if a resolution is necessary
			float size = 1;
			if (usage.getFormula()!=null) {
				if (usage.getFormula().isResolved()) {
					logger.log(Level.INFO, "Already resolved Size is {0}", size);
					size = usage.getSize();
				} else {
					VariableResolver resolver = new VariableResolver(accessory, charac);
					String resolved = FormulaTool.resolve(refType, usage.getFormula(), resolver);
					logger.log(Level.INFO, "Resolved {0}", resolved);
					size = Math.round(Float.parseFloat(resolved));
					logger.log(Level.INFO, "Resolved Size is {0}", size);
				}
			}

			accessory.setAttribute(SR6ItemAttribute.SIZE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.SIZE, (int)size));
		}

//		ret.get().addAll(model.getModifications());
		return ret;
	}

}
