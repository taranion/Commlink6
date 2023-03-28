package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
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

		CarriedItem<ItemTemplate> unarmed = model.getCarriedItem(ItemTemplate.UUID_UNARMED);
		if (unarmed==null) {
			unarmed = new CarriedItem<>(unarmedDef, null, CarryMode.VIRTUAL);
			unarmed.setUuid(ItemTemplate.UUID_UNARMED);
			model.addCarriedItem(unarmed);
			unarmed.setCustomName(Shadowrun6Core.getI18nResources().getString("weapon.unarmed"));
			unarmed.setAttribute(SR6ItemAttribute.DAMAGE, new Damage(2, DamageType.STUN, DamageElement.REGULAR));
			unarmed.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, 0));
			logger.log(Level.INFO, "Add natural weapon");
		}
		SR6GearTool.recalculate("", model, unarmed);

		//------Attack Rating-----------

		try {
			// Reaction
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.REACTION);
			ValueModification reaMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.REACTION);
			reaMod.setSet(ValueType.NATURAL);
			unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING).addModification(reaMod);
			// Strength
			if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.ADD_STRENGTH_TO_MELEE_AR)) {
				aVal = model.getAttribute(ShadowrunAttribute.STRENGTH);
				reaMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
				reaMod.setSet(ValueType.NATURAL);
				unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING).addModification(reaMod);
			}

			//logger.log(Level.DEBUG, "Base AR = "+unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING));
			logger.log(Level.DEBUG, "Base AR = "+Arrays.toString((int[])unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue()));

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

			logger.log(Level.ERROR, "CloseCombat = "+model.getCarriedItems(ItemType.WEAPON_CLOSE_COMBAT));

		} finally {
			logger.log(Level.TRACE,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
