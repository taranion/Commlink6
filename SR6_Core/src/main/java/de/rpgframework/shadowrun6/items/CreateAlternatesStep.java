package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.UUID;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AlternateUsage;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CopyResolvedAttributesStep;
import de.rpgframework.genericrpg.items.Formula;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class CreateAlternatesStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	final static CopyResolvedAttributesStep copyAttribs = new CopyResolvedAttributesStep();

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(boolean, de.rpgframework.genericrpg.modification.ModifiedObjectType, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType refType, Lifeform charac, CarriedItem<?> modelR, List<Modification> unprocessed) {
		CarriedItem<ItemTemplate> model = (CarriedItem<ItemTemplate>) modelR;
		model.clearAlternates();

		if (!model.getResolved().getAlternates().isEmpty()) {
			List<SR6AlternateUsage> alts = model.getResolved().getAlternates();
			for (SR6AlternateUsage alt : alts) {
				logger.log(Level.WARNING, "Create alternate usage for {0} as {1}/{2}", new Object[] { model, alt.getType(), alt.getSubtype() });
				CarriedItem<ItemTemplate> alternate = new CarriedItem<ItemTemplate>(model);
				alternate.setInjectedBy(model);
				alternate.setParent(model);
				alternate.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMTYPE, alt.getType()));
				alternate.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMSUBTYPE, alt.getSubtype()));
				for (ItemAttributeDefinition val : alt.getAttributes()) {
					SR6ItemAttribute attrib = (SR6ItemAttribute) val.getModifyable();
					if (attrib==SR6ItemAttribute.ESSENCECOST)
						continue;
					if (attrib==SR6ItemAttribute.PRICE) {
						alternate.setAttribute(val.getModifyable(), new ItemAttributeNumericalValue(attrib, 0));
						continue;
					}
					Formula form = val.getFormula();
					if (form.isResolved()) {
						if (form.isInteger()) {
							alternate.setAttribute(val.getModifyable(), new ItemAttributeNumericalValue(attrib, form.getAsInteger()));
						} else if (form.isFloat()) {
							alternate.setAttribute(val.getModifyable(), new ItemAttributeFloatValue(attrib, form.getAsFloat()));
						} else {
							alternate.setAttribute(val.getModifyable(), new ItemAttributeObjectValue(attrib, form.getValue()));
						}
					} else {
						logger.log(Level.ERROR, "Formula for {2} in alternate {0} of {1} not resolved", alternate.getKey(), model.getKey(), attrib);
					}
				}
				model.addAlternates(alternate);
				logger.log(Level.INFO,"added alternate {0} with {1}", alternate, alternate.getAttributes());
			}

		}

		if (model.hasAutoFlag(SR6ItemFlag.MELEE_HARDENING_ALTERNATE)) {
			CarriedItem<ItemTemplate> asMelee = new CarriedItem<ItemTemplate>();
			asMelee.setResolved(model.getResolved());
			asMelee.setInjectedBy(Shadowrun6Core.getItem(SR6ItemEnhancement.class, "melee_hardening"));
			asMelee.setParent(model);
			if (model.getCustomName()!=null)
				asMelee.setCustomName(model.getCustomName());
			//asMelee.setCustomName(model.getNameWithoutRating());
			asMelee.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT));
			asMelee.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.CLUBS));
			asMelee.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeObjectValue<>(SR6ItemAttribute.DAMAGE, new Damage(3, DamageType.STUN, DamageElement.REGULAR)));
			asMelee.setAttribute(SR6ItemAttribute.ATTACK_RATING, new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, new int[] {4,0,0,0,0}));
			asMelee.setAttribute(SR6ItemAttribute.SKILL, new ItemAttributeObjectValue<>(SR6ItemAttribute.SKILL, Shadowrun6Core.getSkill("close_combat")));
			asMelee.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, new ItemAttributeObjectValue<>(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat").getSpecialization("clubs")));
			logger.log(Level.INFO, "Add melee hardening: {0}", asMelee);
			model.addAlternates(asMelee);
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
