package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.items.AugmentationQuality;

/**
 * Take the base essence and nuyen cost and multiply with augmentation grade factor
 * @author prelle
 *
 */
public class HandleAugmentationGradeStep implements CarriedItemProcessor {
	
	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 */
	public HandleAugmentationGradeStep() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model, List<Modification> unprocessed) {
			System.err.println("HandleAugmentationGradeStep "+model.getKey());
			ItemTemplate templ = (ItemTemplate) model.getResolved();
			SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) model.getVariant();
			if (templ.hasFlag(ItemTemplate.FLAG_AUGMENTATION) || (variant!=null && variant.hasFlag(ItemTemplate.FLAG_AUGMENTATION))) {
				// This is an item to work on
				AugmentationQuality quality = AugmentationQuality.STANDARD;
				Decision dec = model.getDecision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY);
				logger.log(Level.ERROR, "Found {0} in {1}", dec.getValue(), model.getKey());
				model.setAttribute(SR6ItemAttribute.QUALITY, new ItemAttributeObjectValue<>(SR6ItemAttribute.QUALITY, quality));
				
				ItemAttributeDefinition def = templ.getAttribute(SR6ItemAttribute.ESSENCECOST);
				System.exit(1);
			}
		
		return new OperationResult<List<Modification>>(unprocessed);
	}

}
