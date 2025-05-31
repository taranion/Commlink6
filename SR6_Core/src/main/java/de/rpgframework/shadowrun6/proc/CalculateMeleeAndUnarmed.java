package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
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

	//-------------------------------------------------------------------
	/**
	 */
	public CalculateMeleeAndUnarmed(Shadowrun6Character model) {
		this.model = model;
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());

		ItemUtil.UNARMED_ITEM.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed"));

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

		if (unarmed.getInjectedBy()==null) {
			logger.log(Level.WARNING, "Found an 'unarmed' item in regular inventory - remove it");
			model.removeCarriedItem(unarmed);
		}

		logger.log(Level.DEBUG, "Before recalc: "+unarmed.getAttributeRaw(SR6ItemAttribute.DAMAGE));
		SR6GearTool.recalculate("", model, unarmed);
		logger.log(Level.INFO, "After recalc: "+unarmed.getAttributeRaw(SR6ItemAttribute.DAMAGE));
		checkUnarmedIsPhysical(model, unarmed);

		// Prepare modifications to add
		ValueModification strDMGBonus = null;
		ValueModification strARMod = null;
		ValueModification reaARMod = null;

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


		strARMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
		strARMod.setSet(ValueType.NATURAL);
		aVal = model.getAttribute(ShadowrunAttribute.REACTION);
		reaARMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue(), ShadowrunAttribute.REACTION);
		reaARMod.setSet(ValueType.NATURAL);

		// Now walk all melee weapons
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems(ItemType.WEAPON_CLOSE_COMBAT)) {
			if (item!=unarmed) {
				item.clearModificationsFromCharacter();
			}
			ItemSubType sub = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();

//			// Remove eventually existing STRENGTH mod
//			for (Modification tmpRaw : item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getIncomingModifications()) {
//				ValueModification tmp = (ValueModification) tmpRaw;
//				if (tmp.getSource().equals(ShadowrunAttribute.STRENGTH)) {
//					item.getAsObject(SR6ItemAttribute.ATTACK_RATING).removeIncomingModification(tmp);
//				}
//			}

			// Rule: Add Strength to close combat attack rating for unarmed/melee weapons (CRB Seattle Edition)
			if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.ADD_STRENGTH_TO_MELEE_AR)) {
				if (sub==ItemSubType.WHIPS) {
					item.addModificationFromCharacter(reaARMod);
					logger.log(Level.INFO, "Add {0} to attack rating for {1}", reaARMod, item.getKey());
				} else
				if (strARMod!=null && item.getUuid()!=ItemTemplate.UUID_UNARMED) {
					item.addModificationFromCharacter(strARMod);
					logger.log(Level.INFO, "Add {0} to attack rating for {1}", strARMod, item.getKey());
				}
			}
			ItemSubType subtype = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
			switch (subtype) {
			case BLADES:
			case CLUBS:
			case UNARMED:
				if (strDMGBonus!=null) {
					item.addIncomingModification(strDMGBonus);
					logger.log(Level.DEBUG, "Add {0} to damage for {1}", strDMGBonus, item.getKey());
				}
			}
			logger.log(Level.DEBUG, "Leaving {0} with {1}", item, item.getAsObject(SR6ItemAttribute.DAMAGE));

		}
		
		// Shifter natural weapon
		if (model.getBodytype()==BodyType.SHAPESHIFTER) {
			BodyForm body = model.getBodyForm(BodyType.SHAPESHIFTER);
			if (body==null) {
				logger.log(Level.ERROR, "Character {0} is a SHAPESHIFTER, but misses the BodyForm", model.getName());
			} else {
				// Prepare modifications to add
				strDMGBonus = null;
				strARMod = null;
				reaARMod = null;

				// Rule: High Strength adds to damage (6WC 150)
				aVal = body.getAttributeValue(ShadowrunAttribute.STRENGTH);
				if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.HIGH_STRENGTH_ADDS_DAMAGE) && aVal.getModifiedValue()>6) {
					int plus = (aVal.getModifiedValue()>=10)?2:1;
					strDMGBonus = new ValueModification(
							ShadowrunReference.ITEM_ATTRIBUTE,
							SR6ItemAttribute.DAMAGE.name(),
							plus,
							Shadowrun6Rules.HIGH_STRENGTH_ADDS_DAMAGE);
				}


				strARMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue()+",0,0,0,0", ShadowrunAttribute.STRENGTH);
				strARMod.setSet(ValueType.NATURAL);
				aVal = body.getAttributeValue(ShadowrunAttribute.REACTION);
				reaARMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ATTACK_RATING.name(), aVal.getModifiedValue(), ShadowrunAttribute.REACTION);
				reaARMod.setSet(ValueType.NATURAL);
				for (CarriedItem<ItemTemplate> natural : body.getNaturalWeapons()) {
					Pool<Integer> pool = Shadowrun6Tools.getWeaponPool(model, natural);
					natural.setPool(pool);
					natural.addModificationFromCharacter(strARMod);
					natural.addModificationFromCharacter(reaARMod);
					if (ruleCtrl.getRuleValueAsBoolean(Shadowrun6Rules.ADD_STRENGTH_TO_MELEE_AR)) {
						natural.addModificationFromCharacter(strARMod);
					}
				}
			}
		}

		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void checkUnarmedIsPhysical(Shadowrun6Character model, CarriedItem<ItemTemplate> unarmed) {
		Damage dmg = unarmed.getAsObject(SR6ItemAttribute.DAMAGE).getValue();
		if (dmg.getType()==DamageType.STUN && model.hasRuleFlag(SR6RuleFlag.UNARMED_DAMAGE_IS_PHYSICAL)) {
			logger.log(Level.INFO, "Apply UNARMED_DAMAGE_IS_PHYSICAL");
			dmg.setType(DamageType.PHYSICAL);
		} else if (dmg.getType()!=DamageType.STUN && !model.hasRuleFlag(SR6RuleFlag.UNARMED_DAMAGE_IS_PHYSICAL)){
			logger.log(Level.INFO, "NOT Apply UNARMED_DAMAGE_IS_PHYSICAL");
			dmg.setType(DamageType.STUN);
		}
	}

}
