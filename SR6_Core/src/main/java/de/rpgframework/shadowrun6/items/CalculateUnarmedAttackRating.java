package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateUnarmedAttackRating implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public CalculateUnarmedAttackRating() {
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

		Shadowrun6Character charac2 = (Shadowrun6Character) charac;
		if (model.getUuid()==ItemTemplate.UUID_UNARMED) {
			// Base attack rating is REA + STR
			AttributeValue<ShadowrunAttribute> rea = charac2.getAttribute(ShadowrunAttribute.REACTION);
			AttributeValue<ShadowrunAttribute> str = charac2.getAttribute(ShadowrunAttribute.STRENGTH);
			ValueModification reaMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), rea.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.REACTION);
			ValueModification strMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), str.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
			reaMod.setOrigin(Origin.OUTSIDE);
			strMod.setOrigin(Origin.OUTSIDE);

			ItemAttributeObjectValue<SR6ItemAttribute> unarmedAR = model.getAsObject(SR6ItemAttribute.ATTACK_RATING);
			unarmedAR.addIncomingModification(reaMod);
			unarmedAR.addIncomingModification(strMod);
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
