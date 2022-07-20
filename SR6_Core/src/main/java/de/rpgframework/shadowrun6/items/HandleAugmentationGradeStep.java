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
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.modification.ApplyableValueModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.items.Availability;
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
				logger.log(Level.DEBUG, "Found {0} in {1}", dec.getValue(), model.getKey());
				quality = AugmentationQuality.valueOf(dec.getValue());
				model.setAttribute(SR6ItemAttribute.QUALITY, new ItemAttributeObjectValue<>(SR6ItemAttribute.QUALITY, quality));
				
				ItemAttributeFloatValue<SR6ItemAttribute> essenceAttr = model.getAsFloat(SR6ItemAttribute.ESSENCECOST);
				ItemAttributeNumericalValue<SR6ItemAttribute> priceAttr = model.getAsValue(SR6ItemAttribute.PRICE);
				ItemAttributeObjectValue<SR6ItemAttribute> availAttr = model.getAsObject(SR6ItemAttribute.AVAILABILITY);
				switch (quality) {
				case USED:
					if (essenceAttr!=null)
					essenceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), Math.round(essenceAttr.getDistributed()*100), quality));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), -priceAttr.getDistributed()/2, quality));
					availAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.AVAILABILITY.name(), -1, quality));
					break;
				case ALPHA:
					if (essenceAttr!=null)
					essenceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), Math.round(essenceAttr.getDistributed()*-200), quality));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), Math.round(priceAttr.getDistributed()*0.2f), quality));
					availAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.AVAILABILITY.name(), +1, quality));
					break;
				case BETA:
					if (essenceAttr!=null)
					essenceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), Math.round(essenceAttr.getDistributed()*-300f), quality));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), Math.round(priceAttr.getDistributed()*0.5f), quality));
					availAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.AVAILABILITY.name(), +2, quality));
					break;
				case DELTA:
					if (essenceAttr!=null)
					essenceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), Math.round(essenceAttr.getDistributed()*-500), quality));
					priceAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), Math.round(priceAttr.getDistributed()*1.5f), quality));
					availAttr.addModification( new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.AVAILABILITY.name(), +3, quality));
					break;
				}
//				System.exit(1);
			}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

}
