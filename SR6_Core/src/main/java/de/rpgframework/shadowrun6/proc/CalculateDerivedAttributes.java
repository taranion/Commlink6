package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author stefa
 *
 */
public class CalculateDerivedAttributes implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateDerivedAttributes.class.getPackageName());
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public CalculateDerivedAttributes(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		List<Rule> rules = new ArrayList<Rule>();
		logger.log(Level.DEBUG, "START: process");
		try {
			DamageType unarmedDamageType = DamageType.STUN;
			// Apply attribute modifications
			for (Modification _mod : previous) {
				
//				if (_mod instanceof AttributeModification) {
//					AttributeModification mod = (ShadowrunAttributeModification)_mod;
//					if (mod.getType()==ModificationValueType.MAX) {
//						unprocessed.add(mod);
//					} else {
//						AttributeValue val = model.getAttribute(mod.getAttribute());
//						logger.log(Level.INFO, "  Add "+mod+" from "+mod.getSource());
//						val.addModification(mod);
//					}
//				} else if (_mod instanceof ItemAttributeModification) {
//					ItemAttributeModification mod = (ItemAttributeModification)_mod;
//					for (CarriedItem item : model.getItems(true)) {
//						logger.trace("check "+mod+" on "+item);
//						logger.trace("item = "+item.getUsedAsType()+"   "+item.getItem().getNonAccessoryType()+"/"+item.getItem().getSubtype(null)+"   slot="+item.getSlot());
//						if (mod.getType()!=null && mod.getType()!=item.getUsedAsType())
//							continue;
//						if (mod.getSubtype()!=null && mod.getSubtype()!=item.getUsedAsSubType())
//							continue;
//						logger.log(Level.INFO, "Apply "+mod+" to "+item);
//						if (item.getModifications().size()>30) {
//							logger.fatal("STOP HERE - too many modifications");
//							System.exit(1);
//						}
//						item.addAutoModification(mod);
//					}
//				} else if (_mod instanceof DamageTypeModification) {
//					DamageTypeModification mod = (DamageTypeModification)_mod;
//					logger.log(Level.INFO, "  Set unarmed damage to "+mod.getType()+" from "+mod.getSource());
//					unarmedDamageType = mod.getType();
//				} else if (_mod instanceof SpecialRuleModification) {
//					SpecialRuleModification mod = (SpecialRuleModification)_mod;
//					switch (mod.getRule()) {
//					case CHARISMATIC_DEFENSE:
//						rules.add(mod.getRule());
//						break;
//					default:
//						unprocessed.add(_mod);
//					}
//				} else
//					unprocessed.add(_mod);
			}

			// Set character back to zero
			//			logger.log(Level.DEBUG, "1. Calculate derived attributes");
			AttributeValue val = null;

			/*
			 * Physical condition monitor
			 */
			int phy = Math.round(model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue()/2.0f) + 8;
			val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
			val.setDistributed(phy);
//			val.clearModifications();
			logger.log(Level.DEBUG, " Monitor Physical = "+val.getModifiedValue()+"    modifier="+val.getModifier());

			/*
			 * Stun condition monitor
			 */
			int stun = Math.round(model.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue()/2.0f) + 8;
			val = model.getAttribute(ShadowrunAttribute.STUN_MONITOR);
			val.setDistributed(stun);
//			val.clearModifications();
			logger.log(Level.DEBUG, " Monitor Stun     = "+val.getModifiedValue());

			/*
			 * Physical initiative
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_PHYSICAL);
			val.setDistributed(0);
//			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.REACTION);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " INI Physical = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL).getModifiedValue()+" d6");
			// Minor actions (Physical)
			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION);
			val.setDistributed(1);
			addNaturalModifier(val,ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL);
			logger.log(Level.DEBUG, "              = "+val.getDisplayString()+"   "+val.getModifications());

			/*
			 * astral initiative
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_ASTRAL);
			val.setDistributed(0);
//			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.LOGIC);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Base INI Astral = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL).getModifiedValue()+" d6");
			// Minor actions (Astral)
			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION_ASTRAL);
			val.setDistributed(1);
			addNaturalModifier(val, ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
			logger.log(Level.DEBUG, "                 = "+val.getDisplayString()+"   "+val.getModifications());

			/*
			 * matrix initiative (AR)
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX);
			val.setDistributed(0);
			val.clearModifications();
			CarriedItem bestDF = Shadowrun6Tools.getBestMatrixDF(model);
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancers
				addNaturalModifier(val,ShadowrunAttribute.LOGIC);
				addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//			} else if (bestDF!=null) {
//				// With commlink
//				val.addModification(new AttributeModification(ModificationValueType.NATURAL, SR6ItemAttribute.INITIATIVE_MATRIX, model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue(), ModificationType.RELATIVE, Attribute.REACTION));
//				val.addModification(new AttributeModification(ModificationValueType.NATURAL, SR6ItemAttribute.INITIATIVE_MATRIX, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue(), ModificationType.RELATIVE, Attribute.INTUITION));
			} 
			logger.log(Level.DEBUG, " Base INI Matrix = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue()+" d6");
			// Minor actions (Matrix)
//			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION_MATRIX);
//			val.setDistributed(1 + model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue());
//			logger.log(Level.DEBUG, "                 = "+val.getDisplayString()+"   "+val.getModifications());

			/*
			 * matrix initiative (VR, cold sim)
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD);
			val.setDistributed(0);
			val.clearModifications();
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancers
				addNaturalModifier(val,ShadowrunAttribute.LOGIC);
				addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//			} else if (bestDF!=null) {
//				addNaturalModifier(val, bestDF.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue(), ModificationType.RELATIVE, SR6ItemAttribute.DATA_PROCESSING));
//				val.addModification(new AttributeModification(ValueType.NATURAL, ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue(), ModificationType.RELATIVE, Attribute.INTUITION));
			} 
//			logger.log(Level.DEBUG, " Base INI Matrix VR = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD).getModifiedValue()+" d6");

			/*
			 * matrix initiative (VR, cold sim)
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT);
			val.setDistributed(0);
			val.clearModifications();
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancers
				addNaturalModifier(val,ShadowrunAttribute.LOGIC);
				addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//			} else if (bestDF!=null) {
//				val.addModification(new AttributeModification(ModificationValueType.NATURAL, Attribute.INITIATIVE_MATRIX_VR_HOT, bestDF.getAsValue(ItemAttribute.DATA_PROCESSING).getModifiedValue(), ModificationType.RELATIVE, ItemAttribute.DATA_PROCESSING));
//				val.addModification(new AttributeModification(ModificationValueType.NATURAL, Attribute.INITIATIVE_MATRIX_VR_HOT, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue(), ModificationType.RELATIVE, Attribute.INTUITION));
			} 
//			logger.log(Level.DEBUG, " Base INI Matrix VR Hot = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT).getModifiedValue()+" d6");

			/*
			 * Defensive
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSIVE_POOL);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.REACTION);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Defensive pool = "+val.getModifiedValue());

			/*
			 * Defensive Combat Direct
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSIVE_POOL_COMBAT_DIRECT);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Defensive pool (Direct) = "+val.getModifiedValue());

			/*
			 * Defensive Combat Indirect
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSIVE_POOL_COMBAT_INDIRECT);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.REACTION);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			logger.log(Level.DEBUG, " Defensive pool (Indirect) = "+val.getModifiedValue());

			/*
			 * Defensive Pool against toxin damage
			 */
			val = model.getAttribute(ShadowrunAttribute.RESIST_TOXIN);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			logger.log(Level.DEBUG, " Defensive pool (Toxins) = "+val.getModifiedValue());

			/*
			 * 
			 */
			
			/*
			 * Drain
			 */
			val = model.getAttribute(ShadowrunAttribute.RESIST_DRAIN);
			val.setDistributed(0);
			if (model.getTradition()!=null) {
				addNaturalModifier(val, model.getTradition().getDrainAttribute1());
				addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			}
			logger.log(Level.DEBUG, " Defensive pool (Drain) = "+val.getModifiedValue());

//			/*
//			 * Dodge
//			 */
//			val = model.getAttribute(ShadowrunAttribute.DODGE);
//			val.setDistributed(0);
//			val.addModification(new AttributeModification(ModificationValueType.NATURAL, Attribute.DODGE, model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue(), ModificationType.RELATIVE, Attribute.REACTION));
//			val.addModification(new AttributeModification(ModificationValueType.NATURAL, Attribute.DODGE, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue(), ModificationType.RELATIVE, Attribute.INTUITION));
//			if (model.getSkillValue(ShadowrunCore.getSkill("athletics"))!=null)
//				val.addModification(new AttributeModification(ModificationValueType.NATURAL, Attribute.DODGE, model.getSkillValue(ShadowrunCore.getSkill("athletics")).getModifiedValue(), ModificationType.RELATIVE, ShadowrunCore.getSkill("athletics")));
//			logger.log(Level.DEBUG, " Dodge = "+val.getModifiedValue());

			/*
			 * Composure
			 */
			val = model.getAttribute(ShadowrunAttribute.COMPOSURE);
			val.setDistributed(0);
			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.CHARISMA);
			logger.log(Level.DEBUG, " Composure = "+val.getModifiedValue());

			/*
			 * Judge intentions
			 */
			val = model.getAttribute(ShadowrunAttribute.JUDGE_INTENTIONS);
			val.setDistributed(0);
			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Judge Intentions = "+val.getModifiedValue());

			/*
			 * lifting/carrying
			 */
			val = model.getAttribute(ShadowrunAttribute.LIFT_CARRY);
			val.setDistributed(0);
			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			logger.log(Level.DEBUG, " Lift/Carry = "+val.getModifiedValue());

			/*
			 * memory
			 */
			val = model.getAttribute(ShadowrunAttribute.MEMORY);
			val.setDistributed(0);
			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.LOGIC);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Memory = "+val.getModifiedValue());

			/*
			 * Damage Resistance
			 */
			val = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE);
			val.setDistributed(0);
//			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			logger.log(Level.DEBUG, "Damage Resistance = "+val);
			
			/*
			 * Damage overflow
			 */
			val = model.getAttribute(ShadowrunAttribute.DAMAGE_OVERFLOW);
			val.setDistributed(0);
			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			logger.log(Level.DEBUG, " Damage overflow = "+val.getModifiedValue());

			/*
			 * Alternate cyberlimb attributes
			 */
			val = model.getAttribute(ShadowrunAttribute.AGILITY);
			//logger.log(Level.DEBUG, " Agility alternate = "+val.getAlternateValue());
			val = model.getAttribute(ShadowrunAttribute.STRENGTH);
			//logger.log(Level.DEBUG, " Strength alternate = "+val.getAlternateValue());

			/*
			 * Defense rating
			 * First of all find the highest normal armor
			 */
			CarriedItem<ItemTemplate> bestArmor = null;
//			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
//				if (!item.hasAttribute(ItemAttribute.ARMOR) || item.isType(ItemType.VEHICLES) || item.isType(Arrays.asList(ItemType.droneTypes())))
//					continue;
//				item.setIgnoredForCalculations(true);
//				// If no previous selection or armor is better, use it
//				if (bestArmor==null || item.getAsValue(ItemAttribute.ARMOR).getModifiedValue()> bestArmor.getAsValue(ItemAttribute.ARMOR).getModifiedValue() )
//					bestArmor = item;
//				// Gear pieces that add armor are also allowed
//				if (item.getItem().getArmorData()!=null && item.getItem().getArmorData().addsToMain())
//					item.setIgnoredForCalculations(false);
//				logger.log(Level.DEBUG, "*  "+item.getNameWithRating()+" \t"+item.getAsValue(ItemAttribute.ARMOR).getModifiedValue()+": ignored="+item.isIgnoredForCalculations());
//			}
//			if (bestArmor!=null)
//				bestArmor.setIgnoredForCalculations(false);
//			for (CarriedItem item : model.getItems(false)) {
//				if (!item.hasAttribute(ItemAttribute.ARMOR) || item.isType(ItemType.VEHICLES) || item.isType(Arrays.asList(ItemType.droneTypes())))
//					continue;
//				item.setPrimary(bestArmor==item);
//			}


			int defRating = model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue();
			// Power Plays "Charismatic Defense"
//			if (rules.contains(Rule.CHARISMATIC_DEFENSE)) {
//				defRating = model.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue();
//			}
			logger.log(Level.DEBUG, "  Base Defensive rating = "+defRating);

			for (CarriedItem item : model.getCarriedItems()) {
//				if (!item.hasAttribute(ItemAttribute.ARMOR) || item.isIgnoredForCalculations() || item.isType(ItemType.VEHICLES) || item.isType(Arrays.asList(ItemType.droneTypes())))
//					continue;
//				ItemAttributeNumericalValue armorAtt = item.getAsValue(ItemAttribute.ARMOR);
//				defRating += armorAtt.getModifiedValue();
//				logger.log(Level.DEBUG, "  Add Defensive rating = "+armorAtt.getModifiedValue()+" from "+item.getNameWithRating());
			}
			val = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL);
			logger.log(Level.INFO, "Defensive rating = "+defRating+" = "+val);
//			if (logger.isTraceEnabled()) {
//				for (Modification mod : val.getModifications()) {
//					logger.trace("#### "+mod+"  from "+mod.getSource());
//				}
//			}
			val.setDistributed(defRating);

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
//			
//			/*
//			 * Add strength to attack rating of all melee weapons (Errata 09.2021)
//			 */
//			int[] strengthAR = new int[] {model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue(),0,0,0,0};
//			ItemAttributeModification iMod = new ItemAttributeModification(ItemAttribute.ATTACK_RATING, strengthAR);
//			iMod.setSource(ShadowrunAttribute.STRENGTH);
//			Skill melee = ShadowrunCore.getSkill("close_combat");
//			SkillSpecialization unarmed = melee.getSpecialization("unarmed");
//			for (CarriedItem item : model.getItemsRecursive(true, ItemType.weaponTypes())) {
//				if (item.getItem().getWeaponData()!=null && item.getItem().getWeaponData().getSkill()==melee && item.getItem().getWeaponData().getSpecialization()!=unarmed) {
//					item.getAttribute(ItemAttribute.ATTACK_RATING).addModification(iMod);
//					logger.log(Level.DEBUG, "Added "+iMod+" to "+item);
//				}
//			}
//
//			
//			/*
//			 * Unarmed attacks
//			 */
//			int attRat = model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue()+ model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue();
////			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING));
////			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifications());
////			logger.log(Level.INFO, "AR = "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue());
////			if (model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue()>0) {
////				logger.log(Level.INFO, "Add "+model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue()+" to unarmed attack rating");
////				attRat += model.getAttribute(ShadowrunAttribute.ATTACK_RATING).getModifiedValue(); 
////			}
//			
//			for (CarriedItem item : model.getItems(true)) {
//				if (item.getItem().getId().startsWith("unarmed")) {
//					item.setAttributeOverride(ItemAttribute.ATTACK_RATING, new int[] {attRat,0,0,0,0});
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
//				}
//			}

		} finally {
			logger.log(Level.TRACE,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue val, ShadowrunAttribute attr) {
		val.addModification( new ValueModification(ShadowrunReference.ATTRIBUTE, attr.name(), model.getAttribute(attr).getModifiedValue(), attr) );
	}

}
