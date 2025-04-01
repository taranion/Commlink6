package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;

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
			if (usage==null && accessory.getVariant()!=null) {
				usage = accessory.getVariant().getUsage(accessory.getCarryMode());
			}

			//logger.log(Level.INFO, indent+"accessory {0} in slot {1}", accessory.getKey(), hook);
//			OperationResult<List<Modification>> sub = GearTool.recalculate("", refType, charac, accessory, strict);
			logger.log(Level.INFO, "Usage ''{0}''", usage);
			if (usage==null) {
				logger.log(Level.INFO, "Don't know how to use '"+accessory.getKey()+"' in mode "+accessory.getCarryMode());
			}
			// Check if a resolution is necessary
			float size = 1;
			if (usage.getFormula()!=null) {
				if (usage.getFormula().isResolved()) {
					logger.log(Level.DEBUG, "Already resolved Size is {0}", usage.getSize());
					if (usage.getFormula().isFloat()) // do not use getSize, as that divides by 1.000 if value is an integer
						size = usage.getFormula().getAsFloat(); 
					else
						size = usage.getFormula().getAsInteger();
				} else {
					VariableResolver resolver = new VariableResolver(accessory, charac);
					String resolved = FormulaTool.resolve(refType, usage.getFormula(), resolver);
					if (resolved==null) {
						logger.log(Level.ERROR, "Resolved size {0} of {1} to {2}", usage.getFormula(), accessory.getKey(), resolved);
					} else {
						size = Math.round(Float.parseFloat(resolved));
						logger.log(Level.DEBUG, "Resolved Size is {0}", size);
					}
				}
			}

			if ((size - (int)size)==0)
				accessory.setAttribute(SR6ItemAttribute.SIZE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.SIZE, (int)size));
			else
				accessory.setAttribute(SR6ItemAttribute.SIZE, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.SIZE, size));
		}

//		ret.get().addAll(model.getModifications());
		return ret;
	}

}
