package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyGlobalItemModificationsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	private final static List<ItemSubType> meleeTypes = List.of(ItemSubType.BLADES, ItemSubType.CLUBS, ItemSubType.UNARMED);

	// -------------------------------------------------------------------
	public ApplyGlobalItemModificationsStep() {
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {

		ItemSubType subtype = (model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE)!=null)?	model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue():null;
		boolean isMelee = (subtype!=null)?meleeTypes.contains(subtype):false;

		if (charac instanceof Shadowrun6Character) {
			CarriedItem<ItemTemplate> model2 = (CarriedItem<ItemTemplate>) model;

			for (Modification tmp : ((Shadowrun6Character)charac).getItemModifications()) {
//				logger.log(Level.ERROR, "Check if I need apply "+tmp+" to "+model2);
				if (model.getUuid()==ItemTemplate.UUID_UNARMED && tmp.getApplyTo()==ApplyTo.UNARMED) {
					logger.log(Level.INFO, "Apply global mod {0} to {1}", tmp, model);
					model.addModificationFromCharacter((ValueModification) tmp);
				} else if (tmp.getApplyTo()==ApplyTo.MELEE && isMelee) {
					model.addModificationFromCharacter((ValueModification) tmp);
				} else if (tmp.getApplyTo()==ApplyTo.CHARACTER && tmp.getReferenceType()==ShadowrunReference.ACTION) {
				} else {
					logger.log(Level.WARNING, "Don't know how to deal with "+tmp);
				}

			}
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
