package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;

/**
 * Take the base essence and nuyen cost and multiply with augmentation grade factor
 * @author prelle
 *
 */
public class DetermineStandardEssenceStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	public DetermineStandardEssenceStep() {
		// Instantiated from SR6GearTool
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model, List<Modification> unprocessed) {
			ItemTemplate templ = (ItemTemplate) model.getResolved();
			SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) model.getVariant();
			if (templ.hasFlag(SR6ItemFlag.AUGMENTATION.name()) || (variant!=null && variant.hasFlag(SR6ItemFlag.AUGMENTATION.name()))) {
				float essence = 0.0f;
				// Ask for regular essence definition first
				ItemAttributeDefinition iDef = templ.getAttribute(SR6ItemAttribute.ESSENCECOST);
				if (iDef!=null) {
					essence = iDef.getFormula().getAsFloat();
				}
				// Overwrite with value from variant
				if (variant!=null) {
					iDef = variant.getAttribute(SR6ItemAttribute.ESSENCECOST);
					if (iDef!=null) {
						essence = iDef.getFormula().getAsFloat();
					}
				}
				// Overwrite with usage
				Usage usage = templ.getUsage(model.getCarryMode());
				if (usage==null && variant!=null)
					usage = variant.getUsage(model.getCarryMode());
				if (usage==null) {
					logger.log(Level.WARNING, "No usage {0} for item {1}", model.getCarryMode(), model.getTemplateID()+"/"+model.getVariantID());
					OperationResult<List<Modification>> ret = new OperationResult<List<Modification>>(unprocessed);
					ret.addMessage(new ToDoElement(Severity.WARNING, "No usage "+model.getCarryMode()+" for item "+model.getTemplateID()+"/"+model.getVariantID()));
					return ret;
				}
				// IMPLANTED uses capacity as essence
				if (usage.getMode()==CarryMode.IMPLANTED) {
					if (usage.getFormula()!=null && usage.getSize()!=0.0f)
						essence = usage.getSize();
				}

				ItemAttributeFloatValue<SR6ItemAttribute> val = new ItemAttributeFloatValue<>(SR6ItemAttribute.ESSENCECOST);
				val.setDistributed(essence);
				model.setAttribute(SR6ItemAttribute.ESSENCECOST, val);
				logger.log(Level.DEBUG, "Set ESSENCECOST to "+val.getDistributed());
			}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
