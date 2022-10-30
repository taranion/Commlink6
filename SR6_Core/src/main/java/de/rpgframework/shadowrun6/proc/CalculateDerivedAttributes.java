package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateDerivedAttributes implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateDerivedAttributes.class.getPackageName()+".derived");
	
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
			int baseDamage = 3;
			// Apply attribute modifications
			for (Modification _mod : previous) {
				ShadowrunReference ref = (ShadowrunReference) _mod.getReferenceType();
				if (ref==null) continue;
				String key = ((DataItemModification)_mod).getKey();
				switch (ref) {
				case RULE:
					SR6RuleFlag flag = ref.resolve(key);
					switch (flag) {
					case UNARMED_DAMAGE_IS_PHYSICAL:
						logger.log(Level.DEBUG, "Consume {0}", _mod);
						unarmedDamageType = DamageType.PHYSICAL;
						continue;
					}
					break;
				}
				
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
					unprocessed.add(_mod);
			}

			// Set character back to zero
			//			logger.log(Level.DEBUG, "1. Calculate derived attributes");
			AttributeValue<ShadowrunAttribute> val = null;

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
			// Initiave Dice (Physical)
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL);
			val.setDistributed(1); // Base value without modifiers
			logger.log(Level.DEBUG, "              = "+val.getDisplayString()+"   "+val.getModifications());
			// Minor actions (Physical)
			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION);
			val.setDistributed(1);
			addNaturalModifier(val,ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL);

			/*
			 * astral initiative
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_ASTRAL);
			val.setDistributed(0);
//			val.clearModifications();
			addNaturalModifier(val, ShadowrunAttribute.LOGIC);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Base INI Astral = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL).getModifiedValue()+" d6");
			// Initiave Dice (Astral)
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
			val.setDistributed(2); // Base value without modifiers
			logger.log(Level.DEBUG, "              = "+val.getDisplayString()+"   "+val.getModifications());
			// Minor actions (Astral)
			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION_ASTRAL);
			val.setDistributed(1);
			addNaturalModifier(val,ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
			logger.log(Level.ERROR, "                 = "+val.getDisplayString()+"   "+val.getModifications());

			/*
			 * matrix initiative (AR)
			 */
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX);
			val.setDistributed(0);
			val.clearModifications();
			CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getBestMatrixDF(model);
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancers
				addNaturalModifier(val,ShadowrunAttribute.LOGIC);
				addNaturalModifier(val,ShadowrunAttribute.INTUITION);
			} else if (bestDF!=null) {
				// With commlink
				addNaturalModifier(val,ShadowrunAttribute.REACTION);
				addNaturalModifier(val,ShadowrunAttribute.INTUITION);
			} 
			logger.log(Level.DEBUG, " Base INI Matrix = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue()+" d6");
			// Initiave Dice (Matrix Cold)
			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD);
			val.setDistributed(2); // Base value without modifiers
			logger.log(Level.DEBUG, "              = "+val.getDisplayString()+"   "+val.getModifications());
			// Minor actions (Matrix)
//			val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION_);
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
			} else if (bestDF!=null) {
				addNaturalModifier(val, bestDF, SR6ItemAttribute.DATA_PROCESSING);
				addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			} 
			logger.log(Level.DEBUG, " Base INI Matrix VR = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD).getModifiedValue()+" d6");

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
			} else if (bestDF!=null) {
				addNaturalModifier(val, bestDF, SR6ItemAttribute.DATA_PROCESSING);
				addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			} 
			logger.log(Level.DEBUG, " Base INI Matrix VR Hot = "+val.getDisplayString()+" + "+model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT).getModifiedValue()+" d6");

			/*
			 * Defensive
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_PHYSICAL);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.REACTION);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Defensive pool = "+val.getModifiedValue());

			/*
			 * Defensive Combat Direct
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_DIRECT);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Defensive pool (Direct) = "+val.getModifiedValue());

			/*
			 * Defensive Combat Indirect
			 */
			val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_INDIRECT);
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
			logger.log(Level.DEBUG, "           = "+val.getModifications());

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
			Shadowrun6Tools.flagItemWithHighestAttribute(model, SR6ItemAttribute.DEFENSE_PHYSICAL, SR6ItemFlag.IGNORE_FOR_CALCULATIONS, false);
//			CarriedItem<ItemTemplate> bestArmor = null;
//			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
//				if (!item.hasAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL))
//					continue;
//				item.setAutoFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS, true);
//				// If no previous selection or armor is better, use it
//				if (bestArmor==null || item.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue()> bestArmor.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue() )
//					bestArmor = item;
////				// Gear pieces that add armor are also allowed
////				if (item.getResolved().getArmorData()!=null && item.getItem().getArmorData().addsToMain())
////					item.setIgnoredForCalculations(false);
//				logger.log(Level.DEBUG, "*  "+item.getNameWithRating()+" \t"+item.getAsValue(ItemAttribute.ARMOR).getModifiedValue()+": ignored="+item.isIgnoredForCalculations());
//			}
//			if (bestArmor!=null)
//				bestArmor.setAutoFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS, false);
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

			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				if (!item.hasAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL))
					continue;
				ItemAttributeNumericalValue<SR6ItemAttribute> armorAtt = item.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL);
				defRating += armorAtt.getModifiedValue();
				logger.log(Level.DEBUG, "  Add Defensive rating = "+armorAtt.getModifiedValue()+" from "+item.getNameWithRating());
			}
			val = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL);
			logger.log(Level.INFO, "Defensive rating = "+defRating+" = "+val);
//			if (logger.isTraceEnabled()) {
//				for (Modification mod : val.getModifications()) {
//					logger.trace("#### "+mod+"  from "+mod.getSource());
//				}
//			}
			val.setDistributed(defRating);
		} finally {
			logger.log(Level.TRACE,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, ShadowrunAttribute attr) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), model.getAttribute(attr).getModifiedValue(), attr);
		valMod.setSet(ValueType.NATURAL);
		valMod.setSource(attr);
		val.addModification( valMod );
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), item.getAsValue(attr).getModifiedValue(), attr);
		valMod.setSet(ValueType.NATURAL);
		valMod.setSource(attr);
		val.addModification( valMod );
	}

}
