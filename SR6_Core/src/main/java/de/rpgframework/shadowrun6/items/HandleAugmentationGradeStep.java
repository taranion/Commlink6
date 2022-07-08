package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.ApplyableValueModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Take the base essence and nuyen cost and multiply with augmentation grade factor
 * @author prelle
 *
 */
public class HandleAugmentationGradeStep implements CarriedItemProcessor {
	
	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	public HandleAugmentationGradeStep() {
		// Instantiated from SR6GearTool
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model, List<Modification> unprocessed) {
			ItemTemplate templ = (ItemTemplate) model.getResolved();
			SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) model.getVariant();
			if (templ.hasFlag(ItemTemplate.FLAG_AUGMENTATION) || (variant!=null && variant.hasFlag(ItemTemplate.FLAG_AUGMENTATION))) {
				// This is an item to work on
				AugmentationQuality quality = AugmentationQuality.STANDARD;
				Decision dec = model.getDecision(ItemTemplate.UUID_AUGMENTATION_QUALITY);
				if (dec==null) {
					logger.log(Level.WARNING, "Item {0}/{1} misses decision for CHOICE_AUGMENTATION_QUALITY {2}", model.getUuid(), model.getKey(), ItemTemplate.UUID_AUGMENTATION_QUALITY);
					OperationResult<List<Modification>> ret = new OperationResult<List<Modification>>(unprocessed);
					ret.addMessage(new ToDoElement(Severity.WARNING, "Missing choice for AUGMENTATION_QUALITY"));
					return ret;
				}
				logger.log(Level.ERROR, "Found {0} in {1}", dec.getValue(), model.getKey());
				quality = AugmentationQuality.valueOf(dec.getValue());
				model.setAttribute(SR6ItemAttribute.QUALITY, new ItemAttributeObjectValue<>(SR6ItemAttribute.QUALITY, quality));
				
				ItemAttributeFloatValue<SR6ItemAttribute> essenceAttr = model.getAsFloat(SR6ItemAttribute.ESSENCECOST);
				ItemAttributeNumericalValue<SR6ItemAttribute> priceAttr = model.getAsValue(SR6ItemAttribute.PRICE);
				ItemAttributeObjectValue<SR6ItemAttribute> availAttr = model.getAsObject(SR6ItemAttribute.AVAILABILITY);
				switch (quality) {
				case USED:
//					model.setAttribute(SR6ItemAttribute.ESSENCECOST, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.ESSENCECOST, def.getFormula().getAsFloat()*1.1f));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), priceAttr.getDistributed()/2));
//					model.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, def.getFormula().getAsFloat()*0.5f));
//					model.setAttribute(SR6ItemAttribute.AVAILABILITY, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, def.getFormula().getAsFloat()*0.5f));
					break;
				case BETA:
					essenceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), Math.round(essenceAttr.getDistributed()*-300)));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), Math.round(priceAttr.getDistributed()*1.5f)));
//					model.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, def.getFormula().getAsFloat()*0.5f));
//					model.setAttribute(SR6ItemAttribute.AVAILABILITY, new ItemAttributeFloatValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, def.getFormula().getAsFloat()*0.5f));
					break;
				}
//				System.exit(1);
			}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

}
