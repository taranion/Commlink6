package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6ResolveTemplatesStep implements CarriedItemProcessor {
	
	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {
		
		boolean changed = false;
		ItemTemplate resolved = (ItemTemplate) model.getResolved();
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) model.getVariant();
		if (resolved == null) {
			resolved = Shadowrun6Core.getItem(ItemTemplate.class, model.getKey());
			if (resolved == null) {
				logger.log(Level.ERROR, indent+"Item {0} refers to unknown item template ''{1}''", model.getUuid(),
						model.getKey());
				System.exit(1);
				return new OperationResult<>();
			}
			changed = true;
		}
		if (model.getVariantID()!=null && model.getVariant()==null) {
			variant = (SR6PieceOfGearVariant) resolved.getVariant(model.getVariantID());
			if (variant==null) {
				logger.log(Level.ERROR, indent+"Item {0} refers to unknown variant ''{1}'' of template {2}", model.getUuid(),
						model.getVariantID(), model.getKey());
				System.exit(1);
				return new OperationResult<>();
			}
			changed = true;
		}
		
		// Depth first 
		// Since setResolved() triggers recalculation, resolve children first
		for (CarriedItem<ItemTemplate> child : ((CarriedItem<ItemTemplate>)model).getAccessories()) {
			OperationResult<List<Modification>> sub = this.process(indent+"", charac, child, unprocessed);
		}
		
		if (changed) {
			logger.log(Level.WARNING, "Resolve "+model.getKey()+"/"+model.getVariantID()+" to "+resolved+"/"+variant);
			if (variant!=null) {
				((CarriedItem<ItemTemplate>)model).setResolved(resolved, variant);
			} else {
				((CarriedItem<ItemTemplate>)model).setResolved(resolved);
			}
		}
		
		return new OperationResult<> (unprocessed);
	}

}
