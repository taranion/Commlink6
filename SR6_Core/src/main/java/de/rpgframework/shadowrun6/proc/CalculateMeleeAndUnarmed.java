package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateMeleeAndUnarmed implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateMeleeAndUnarmed.class.getPackageName()+".derived");
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public CalculateMeleeAndUnarmed(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		List<Rule> rules = new ArrayList<Rule>();
		logger.log(Level.DEBUG, "START: process");
		try {

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
			
			/*
			 * Add strength to attack rating of all melee weapons (Errata 09.2021)
			 */
			if (model.getRuleValueAsBoolean(Shadowrun6Rules.ADD_STRENGTH_TO_MELEE_AR)) {
				int[] strengthAR = new int[] { model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue(), 0, 0,
						0, 0 };
//				ValueModification iMod = new ValueModification(
//						ShadowrunReference.ITEM_ATTRIBUTE,
//						SR6ItemAttribute.ATTACK_RATING.name(), 
//						strengthAR,
//						ShadowrunAttribute.STRENGTH);
//				SR6Skill melee = Shadowrun6Core.getSkill("close_combat");
//				SkillSpecialization<SR6Skill> unarmed = melee.getSpecialization("unarmed");
//				for (CarriedItem<ItemTemplate> item : model.getCarriedItems(ItemType.weaponTypes())) {
//					SR6Skill skill = item.getAsObject(SR6ItemAttribute.SKILL).getValue();
//					SkillSpecialization<SR6Skill> spec = item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getValue();
//					if (skill==melee && spec != unarmed) {
//						item.getAsObject(SR6ItemAttribute.ATTACK_RATING).addModification(iMod);
//						logger.log(Level.DEBUG, "Added " + iMod + " to " + item);
//					}
//				}
			}
			/*
			 * Unarmed attacks
			 */
			int attRat = model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue()+ model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue();
//			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING));
//			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifications());
//			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue());
//			if (model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue()>0) {
//				logger.log(Level.INFO, "Add "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue()+" to unarmed attack rating");
//				attRat += model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue(); 
//			}
			
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				if (item.getKey().startsWith("unarmed")) {
//					item.setAttributeOverride(SR6ItemAttribute.ATTACK_RATING, new int[] {attRat,0,0,0,0});
//					// Apply eventually unarmed AR modifiers
//					ItemAttributeObjectValue oVal = item.getAsObject(ItemAttribute.ATTACK_RATING);
//					for (Modification mod : model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifications()) {
//						AttributeModification amod = (ShadowrunAttributeModification)mod;
//						if (amod.getValue()!=0) {
//							logger.log(Level.INFO, "Apply unarmed attack rating modification "+amod);
//							ItemAttributeModification nMod = new ItemAttributeModification(ItemAttribute.ATTACK_RATING, new int[] {amod.getValue(),0,0,0,0});
//							nMod.setSource(amod.getSource());
//							item.addAutoModification(nMod);
//							oVal.addModification(nMod);
//						}
//					}
//					
//					
//					// Damage is calculated later on the fly by method ShadowrunTools.getWeaponDamage
//					Damage dmg = (Damage) item.getAsValue(ItemAttribute.DAMAGE);
//					dmg.setType(unarmedDamageType);
//					if (unarmedDamageType!=Type.STUN)
//						logger.log(Level.INFO, "  Set damage type to "+unarmedDamageType);
//					// Apply MELEE_DAMAGE
//					for (Modification mod : model.getAttribute(ShadowrunAttribute.MELEE_DAMAGE).getModifications()) {
//						ItemAttributeModification itemMod = new ItemAttributeModification(ItemAttribute.DAMAGE, ((ShadowrunAttributeModification)mod).getValue());
//						itemMod.setSource(mod.getSource());
//						logger.log(Level.DEBUG, "  add to unarmed damage: "+itemMod);
//						dmg.addModification(itemMod);
//					}
//					
//					logger.log(Level.DEBUG, "Set Unarmed attack with attack rating "+Arrays.toString((int[])item.getAsObject(ItemAttribute.ATTACK_RATING).getModifiedValue())+" and current damage "+dmg);
				}
			}

		} finally {
			logger.log(Level.TRACE,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, ShadowrunAttribute attr) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), model.getAttribute(attr).getModifiedValue(), attr);
		valMod.setSource(attr);
		val.addModification( valMod );
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), item.getAsValue(attr).getModifiedValue(), attr);
		valMod.setSource(attr);
		val.addModification( valMod );
	}

}
