package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;

/**
 * @author prelle
 *
 */
public class CreateAlternatesStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(boolean, de.rpgframework.genericrpg.modification.ModifiedObjectType, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType refType, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {
		model.clearAlternates();

		if (!model.getResolved().getAlternates().isEmpty()) {
			System.err.println("CreateAlternatesStep: not implemented yet  (alternates of "+model.getKey());
			logger.log(Level.WARNING, "CreateAlternatesStep: not implemented yet  (alternates of {0}= {1})", model.getKey(), model.getResolved().getAlternates());
		}

		if (model.hasAutoFlag(SR6ItemFlag.MELEE_HARDENING_ALTERNATE)) {
			CarriedItem<ItemTemplate> asMelee = new CarriedItem<ItemTemplate>();
			asMelee.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT.name()));
			asMelee.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.CLUBS.name()));
			asMelee.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeObjectValue<>(SR6ItemAttribute.DAMAGE, new Damage(3, DamageType.STUN, DamageElement.REGULAR)));
			asMelee.setAttribute(SR6ItemAttribute.ATTACK_RATING, new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, new int[] {4,0,0,0,0}));
			logger.log(Level.INFO, "Add melee hardening: {0}", asMelee);
			model.addAlternates(asMelee);
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
