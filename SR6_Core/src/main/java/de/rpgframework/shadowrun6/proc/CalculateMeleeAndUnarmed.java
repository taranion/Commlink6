package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateMeleeAndUnarmed implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateMeleeAndUnarmed.class.getPackageName()+".derived");

	private Shadowrun6Character model;
	private RuleController ruleCtrl;

	private ItemTemplate unarmedDef;

	//-------------------------------------------------------------------
	/**
	 */
	public CalculateMeleeAndUnarmed(Shadowrun6Character model) {
		this.model = model;
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());

		unarmedDef = new ItemTemplate();
		unarmedDef.setId("unarmed");
		unarmedDef.setAttribute(SR6ItemAttribute.DAMAGE, new Damage(2, DamageType.STUN, DamageElement.REGULAR));
		int[] attRat = new int[5];
		//ItemAttributeObjectValue<SR6ItemAttribute> iaVal = new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, attRat);
		unarmedDef.setAttribute(SR6ItemAttribute.ATTACK_RATING, attRat);
		unarmedDef.setAttribute(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT);
		unarmedDef.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.UNARMED);
		unarmedDef.setAttribute(SR6ItemAttribute.SKILL, Shadowrun6Core.getSkill("close_combat"));
		unarmedDef.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed"));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "START: process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		// Introduce a virtual "Unarmed" item
		CarriedItem<ItemTemplate> unarmed = model.getCarriedItem(ItemTemplate.UUID_UNARMED);
		if (unarmed==null) {
			unarmed = new CarriedItem<>(unarmedDef, null, CarryMode.VIRTUAL);
			unarmed.setUuid(ItemTemplate.UUID_UNARMED);
			unarmed.setCustomName(Shadowrun6Core.getI18nResources().getString("weapon.unarmed"));

			// Base attack rating is REA + STR
			AttributeValue<ShadowrunAttribute> rea = model.getAttribute(ShadowrunAttribute.REACTION);
			AttributeValue<ShadowrunAttribute> str = model.getAttribute(ShadowrunAttribute.STRENGTH);
			ValueModification reaMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), rea.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.REACTION);
			ValueModification strMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), str.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
			ItemAttributeObjectValue<SR6ItemAttribute> unarmedAR = new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, new int[] {0,0,0,0,0});
			unarmedAR.addModification(reaMod);
			unarmedAR.addModification(strMod);
			unarmed.setAttribute(SR6ItemAttribute.ATTACK_RATING, unarmedAR);
			unarmed.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeObjectValue<>(SR6ItemAttribute.DAMAGE, new Damage(2, DamageType.STUN, DamageElement.REGULAR)));
			unarmed.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, 0));
			unarmed.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT));
			unarmed.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<>(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.UNARMED));
			unarmed.setAttribute(SR6ItemAttribute.SKILL, new ItemAttributeObjectValue<>(SR6ItemAttribute.SKILL, Shadowrun6Core.getSkill("close_combat")));
			unarmed.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, new ItemAttributeObjectValue<>(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed")));
			unarmed.setInjectedBy("CORE");

			model.addVirtualCarriedItem(unarmed);
			logger.log(Level.INFO, "Add natural weapon 'unarmed'");
			unarmed.setResolved(unarmedDef);
		} else {
			if (unarmed.getInjectedBy()==null) {
				logger.log(Level.WARNING, "Found an 'unarmed' item in regular inventory - remove it");
				model.removeCarriedItem(unarmed);
			}
		}
		applyGlobalItemModificatios(model, unarmed);
		checkUnarmedIsPhysical(model, unarmed);
		SR6GearTool.recalculate("", model, unarmed);

		// Prepare modifications to add
		ValueModification strDMGBonus = null;
		ValueModification strARMod = null;

		// Rule: High Strength adds to damage (6WC 150)
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.STRENGTH);
		if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.HIGH_STRENGTH_ADDS_DAMAGE) && aVal.getModifiedValue()>6) {
			int plus = (aVal.getModifiedValue()>=10)?2:1;
			strDMGBonus = new ValueModification(
					ShadowrunReference.ITEM_ATTRIBUTE,
					SR6ItemAttribute.DAMAGE.name(),
					plus,
					Shadowrun6Rules.HIGH_STRENGTH_ADDS_DAMAGE);
		}

		// Rule: Add Strength to close combat attack rating for unarmed/melee weapons (CRB Seattle Edition)
		if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.ADD_STRENGTH_TO_MELEE_AR)) {
			strARMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
			strARMod.setSet(ValueType.NATURAL);
		}

		// Now walk all melee weapons
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems(ItemType.WEAPON_CLOSE_COMBAT)) {
			// Remove eventually existing STRENGTH mod
			for (Modification tmpRaw : item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifications()) {
				ValueModification tmp = (ValueModification) tmpRaw;
				if (tmp.getSource().equals(ShadowrunAttribute.STRENGTH)) {
					item.getAsObject(SR6ItemAttribute.ATTACK_RATING).removeModification(tmp);
				}
			}

			if (strARMod!=null && item.getUuid()!=ItemTemplate.UUID_UNARMED) {
				item.getAsObject(SR6ItemAttribute.ATTACK_RATING).addModification(strARMod);
				logger.log(Level.TRACE, "Add {0} to attack rating for {1}", strARMod, item.getKey());
			}
			ItemSubType subtype = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
			switch (subtype) {
			case BLADES:
			case CLUBS:
			case UNARMED:
				if (strDMGBonus!=null) {
					item.getAsObject(SR6ItemAttribute.DAMAGE).addModification(strDMGBonus);
					logger.log(Level.ERROR, "Add {0} to damage for {1}", strDMGBonus, item.getKey());
				}
			}
		}

			/*
			 * Modify all weapons with attack rating
			 */
			// Store modifications for attack rating
			List<Object> sourcesUsed = new ArrayList<>();
//			for (Modification mod : model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifications()) {
//				if (sourcesUsed.contains(mod.getSource())) {
//					logger.log(Level.INFO, "Ignoring the AR modification from the same source appearing a second time");
//					continue;
//				}
//				AttributeModification amod = (ShadowrunAttributeModification)mod;
//				if (amod.getAttackRating()!=null) {
//					logger.log(Level.INFO, "Apply global attack rating modification "+amod);
//					sourcesUsed.add(mod.getSource());
//					for (CarriedItem item : model.getItemsRecursive(true, ItemType.weaponTypes())) {
//						ItemAttributeModification iMod = new ItemAttributeModification(ItemAttribute.ATTACK_RATING, amod.getAttackRating());
//						iMod.setSource(amod.getSource());
//						item.getAttribute(ItemAttribute.ATTACK_RATING).addModification(iMod);
//						logger.log(Level.DEBUG, "Added "+iMod+" to "+item);
//					}
//				}
//			}
		return unprocessed;
	}

	private void clearOldItemModificatios(Shadowrun6Character model, CarriedItem<ItemTemplate> unarmed) {
		for (ItemAttributeValue<IItemAttribute> val : unarmed.getAttributes()) {
			val.clearModifications();
		}
	}

	private void applyGlobalItemModificatios(Shadowrun6Character model, CarriedItem<ItemTemplate> unarmed) {
		clearOldItemModificatios(model, unarmed);

		for (Modification mod : model.getItemModifications()) {
			if (mod.getApplyTo()==ApplyTo.UNARMED) {
				ValueModification vMod = (ValueModification)mod;
				SR6ItemAttribute attr = vMod.getResolvedKey();
				unarmed.getAttributeRaw(attr).addModification(vMod);
				logger.log(Level.INFO, "Add modification {0} to UNARMED = {1}", vMod, unarmed.getAttributeRaw(attr));
			}
		}
	}

	private void checkUnarmedIsPhysical(Shadowrun6Character model, CarriedItem<ItemTemplate> unarmed) {
		Damage dmg = unarmed.getAsObject(SR6ItemAttribute.DAMAGE).getValue();
		if (dmg.getType()==DamageType.STUN && model.hasRuleFlag(SR6RuleFlag.UNARMED_DAMAGE_IS_PHYSICAL)) {
			logger.log(Level.INFO, "Apply UNARMED_DAMAGE_IS_PHYSICAL");
			dmg.setType(DamageType.PHYSICAL);
		}
	}

}
