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
				unprocessed.add(_mod);
			}

			// Set character back to zero
			//			logger.log(Level.DEBUG, "1. Calculate derived attributes");
			AttributeValue<ShadowrunAttribute> val = null;


			calculateMonitorPhysical();
			calculateMonitorStun();
			calculateInitiativePhysical();
			calculateInitiativeMatrixAR();
			calculateInitiativeAstral();
			calculateInitiativeMatrixVRCold();
			calculateInitiativeMatrixVRHot();

			calculateAttackRatingPhysical();
			calculateAttackRatingAstral();
			calculateAttackRatingMatrix();

			calculateDefenseRatingPhysical();
			calculateDefenseRatingMatrix();
			calculateDefenseRatingAstral();

			calculateDefensePoolPhysical();
			calculateDefensePoolCombatDirect();
			calculateDefensePoolCombatIndirect();
			calculateDefensePoolAstral();
			calculateDefensePoolMatrix();

			calculateResistMatrixDamage();
			calculateResistToxin();

			/*
			 * Drain
			 */
			val = model.getAttribute(ShadowrunAttribute.RESIST_DRAIN);
			val.setDistributed(0);
			if (model.getTradition()!=null) {
				addNaturalModifier(val, model.getTradition().getTraditionAttribute());
				addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			}
			logger.log(Level.DEBUG, " Resist Drain = "+val.getModifiedValue());

			/*
			 * Composure
			 */
			val = model.getAttribute(ShadowrunAttribute.COMPOSURE);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.CHARISMA);
			logger.log(Level.DEBUG, " Composure = "+val.getModifiedValue());

			/*
			 * Judge intentions
			 */
			val = model.getAttribute(ShadowrunAttribute.JUDGE_INTENTIONS);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Judge Intentions = "+val.getModifiedValue());

			/*
			 * lifting/carrying
			 */
			val = model.getAttribute(ShadowrunAttribute.LIFT_CARRY);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
			logger.log(Level.DEBUG, " Lift/Carry = "+val.getModifiedValue());

			/*
			 * memory
			 */
			val = model.getAttribute(ShadowrunAttribute.MEMORY);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.LOGIC);
			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
			logger.log(Level.DEBUG, " Memory = "+val.getModifiedValue());

			/*
			 * Damage Resistance
			 */
			val = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			logger.log(Level.DEBUG, "Damage Resistance = "+val);

			/*
			 * Damage overflow
			 */
			val = model.getAttribute(ShadowrunAttribute.DAMAGE_OVERFLOW);
			val.setDistributed(0);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			addNaturalModifier(val, ShadowrunAttribute.BODY);
			logger.log(Level.DEBUG, " Damage overflow = "+val.getModifiedValue());

		} finally {
			logger.log(Level.TRACE,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, int value, Object source) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), value, source);
		valMod.setSet(ValueType.NATURAL);
		val.addModification( valMod );
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, ShadowrunAttribute attr) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), model.getAttribute(attr).getModifiedValue(), attr);
		valMod.setSet(ValueType.NATURAL);
		val.addModification( valMod );
	}

//	//-------------------------------------------------------------------
//	static void addNaturalModifier(Shadowrun6Character model, AttributeValue<ShadowrunAttribute> val, ShadowrunAttribute attr) {
//		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), model.getAttribute(attr).getModifiedValue(), attr);
//		valMod.setSet(ValueType.NATURAL);
//		valMod.setSource(attr);
//		val.addModification( valMod );
//	}

	//-------------------------------------------------------------------
	static void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		if (item==null) return;
		if (!item.hasAttribute(attr)) {
			logger.log(Level.ERROR, "Item {0} does not have attribute {1}", item.getKey(), attr);
			return;
		}
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), item.getAsValue(attr).getModifiedValue(), attr);
		valMod.setSet(ValueType.NATURAL);
		valMod.setSource(item);
		val.addModification( valMod );
	}

	//-------------------------------------------------------------------
	private void calculateMonitorPhysical() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		val.setDistributed(8);
		addNaturalModifier(val, Math.round(model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue()/2.0f), ShadowrunAttribute.BODY.getName()+"/2");
		logger.log(Level.DEBUG, " Monitor Physical = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateMonitorStun() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.STUN_MONITOR);
		val.setDistributed(8);
		addNaturalModifier(val, Math.round(model.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue()/2.0f), ShadowrunAttribute.WILLPOWER.getName()+"/2");
		logger.log(Level.DEBUG, " Monitor Stun     = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateInitiativePhysical() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.INITIATIVE_PHYSICAL);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.REACTION);
		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		logger.log(Level.DEBUG, " INI Physical Base = "+val.getModifiedValue());
		// Initiave Dice (Physical)
		val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL);
		val.setDistributed(1); // Base value without modifiers
		logger.log(Level.DEBUG, "              = "+val.getDisplayString()+"   "+val.getModifications());
		logger.log(Level.DEBUG, " INI Physical D6  = "+val.getModifiedValue());

		// Minor actions
		AttributeValue<ShadowrunAttribute> val2 = model.getAttribute(ShadowrunAttribute.MINOR_ACTION);
		val2.setDistributed(1);
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL.name(), val.getModifiedValue(), ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL);
		valMod.setSet(ValueType.NATURAL);
		val2.addModification( valMod );
		logger.log(Level.DEBUG, "              = "+val2.getDisplayString()+"   "+val2.getModifications());
	}

	//-------------------------------------------------------------------
	private void calculateInitiativeAstral() {
		AttributeValue<ShadowrunAttribute> 			val = model.getAttribute(ShadowrunAttribute.INITIATIVE_ASTRAL);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.LOGIC);
		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		logger.log(Level.DEBUG, " Base INI Astral = "+val.getModifiedValue());
		// Initiave Dice (Astral)
		val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
		val.setDistributed(2); // Base value without modifiers
		logger.log(Level.DEBUG, "              = "+val.getModifiedValue());
		// Minor actions (Astral)
		val = model.getAttribute(ShadowrunAttribute.MINOR_ACTION_ASTRAL);
		val.setDistributed(1);
		addNaturalModifier(val,ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
		logger.log(Level.DEBUG, "                 = "+val.getModifiedValue());

	}

	//-------------------------------------------------------------------
	private void calculateInitiativeMatrixAR() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX);
		val.setDistributed(0);
		CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getPrimaryMatrixDF(model);
		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
			// Technomancers
			addNaturalModifier(val,ShadowrunAttribute.LOGIC);
			addNaturalModifier(val,ShadowrunAttribute.INTUITION);
		} else if (bestDF!=null) {
			// With commlink
			addNaturalModifier(val,ShadowrunAttribute.REACTION);
			addNaturalModifier(val,ShadowrunAttribute.INTUITION);
		}
		model.getPersona().setAttribute(val);
		logger.log(Level.DEBUG, " INI Matrix Base  "+val.getModifiedValue());

		// Initiave Dice (Physical)
		val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX);
		val.setDistributed(1); // Base value without modifiers

		model.getPersona().setAttribute(val);
		logger.log(Level.DEBUG, " INI Matrix D6    "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateInitiativeMatrixVRCold() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD);
		val.setDistributed(0);

		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		val.addModifications(model.getPersona().getDataProcessing().getModifications());

//		CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getPrimaryMatrixDF(model);
//		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
//			// Technomancers
//			addNaturalModifier(val,ShadowrunAttribute.LOGIC);
//			addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//		} else if (bestDF!=null) {
//			// With commlink
//			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
//			addNaturalModifier(val, bestDF, SR6ItemAttribute.DATA_PROCESSING);
//		}
		model.getPersona().setAttribute(val);
		logger.log(Level.DEBUG, " INI Matrix VR Base  "+val.getModifiedValue());

		// Initiave Dice (Physical)
		val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD);
		val.setDistributed(1); // Base value without modifiers
		val.addModification( new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), 1, "VR Cold Sim") );
		model.getPersona().setAttribute(val);
		logger.log(Level.DEBUG, " INI Matrix VR D6    "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateInitiativeMatrixVRHot() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT);
		val.setDistributed(0);

		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		val.addModifications(model.getPersona().getDataProcessing().getModifications());
//		CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getPrimaryMatrixDF(model);
//		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
//			// Technomancers
//			addNaturalModifier(val,ShadowrunAttribute.LOGIC);
//			addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//		} else if (bestDF!=null) {
//			// With commlink
//			addNaturalModifier(val, ShadowrunAttribute.INTUITION);
//			addNaturalModifier(val, bestDF, SR6ItemAttribute.DATA_PROCESSING);
//		}
		model.getPersona().setAttribute(val);

		logger.log(Level.DEBUG, " INI Matrix VR Base Hot "+val.getModifiedValue());
		// Initiave Dice (Physical)
		val = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT);
		val.setDistributed(1); // Base value without modifiers
		val.addModification( new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), 2, "VR Hot Sim") );
		model.getPersona().setAttribute(val);
		logger.log(Level.DEBUG, " INI Matrix VR D6 Hot   "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateAttackRatingPhysical() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL);
		val.setDistributed(0);
		addNaturalModifier(val,ShadowrunAttribute.STRENGTH);
		addNaturalModifier(val,ShadowrunAttribute.REACTION);
		logger.log(Level.DEBUG, " Attack Rating Phyiscal = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateAttackRatingAstral() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_ASTRAL);
		val.setDistributed(0);
		addNaturalModifier(val,ShadowrunAttribute.MAGIC);
		if (model.getMagicOrResonanceType()!=null) {
			if (model.getMagicOrResonanceType().usesSpells() && model.getTradition()!=null) {
				addNaturalModifier(val,model.getTradition().getTraditionAttribute());
			} else if (model.getMagicOrResonanceType().usesPowers() ) {
				addNaturalModifier(val,ShadowrunAttribute.BODY);
			}
		}
		logger.log(Level.DEBUG, " Attack Rating Astral = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateAttackRatingMatrix() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_MATRIX);
		val.setDistributed(0);
		addNaturalModifier(val, model.getPersona().getAttack().getModifiedValue(), SR6ItemAttribute.ATTACK);
		addNaturalModifier(val, model.getPersona().getSleaze().getModifiedValue(), SR6ItemAttribute.SLEAZE);

//		CarriedItem<ItemTemplate> bestAS = Shadowrun6Tools.getPrimaryMatrixAS(model);
//		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
//			// Technomancers
//			addNaturalModifier(val,ShadowrunAttribute.CHARISMA);
//			addNaturalModifier(val,ShadowrunAttribute.INTUITION);
//		} else if (bestAS!=null) {
//			// With commlink
//			addNaturalModifier(val, bestAS, SR6ItemAttribute.ATTACK);
//			addNaturalModifier(val, bestAS, SR6ItemAttribute.SLEAZE);
//		}
		logger.log(Level.DEBUG, " Attack Rating Matrix = "+val.getModifiedValue()+ "  /  "+val.getModifications());
	}

	//-------------------------------------------------------------------
	private void calculateDefenseRatingPhysical() {
		Shadowrun6Tools.flagItemWithHighestAttribute(model, SR6ItemAttribute.DEFENSE_PHYSICAL, SR6ItemFlag.IGNORE_FOR_CALCULATIONS, false);
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL);
		val.setDistributed(0);
		// Power Plays "Charismatic Defense"
		if (model.hasRuleFlag(SR6RuleFlag.CHARISMATIC_DEFENSE)) {
			addNaturalModifier(val,ShadowrunAttribute.CHARISMA);
		} else {
			addNaturalModifier(val,ShadowrunAttribute.BODY);
		}

		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			if (!item.hasAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL))
				continue;
			if (item.hasFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS))
				continue;
			addNaturalModifier(val, item, SR6ItemAttribute.DEFENSE_PHYSICAL);
		}
		logger.log(Level.DEBUG, " Defense Rating Phyiscal = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefenseRatingMatrix() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_MATRIX);
		val.setDistributed(0);
		addNaturalModifier(val, model.getPersona().getDataProcessing().getModifiedValue(), SR6ItemAttribute.DATA_PROCESSING);
		addNaturalModifier(val, model.getPersona().getFirewall().getModifiedValue(), SR6ItemAttribute.FIREWALL);

//		CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getPrimaryMatrixDF(model);
//		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
//			// Technomancers
//			addNaturalModifier(val,ShadowrunAttribute.LOGIC);
//			addNaturalModifier(val,ShadowrunAttribute.WILLPOWER);
//		} else if (bestDF!=null) {
//			// With commlink
//			addNaturalModifier(val, bestDF, SR6ItemAttribute.DATA_PROCESSING);
//			addNaturalModifier(val, bestDF, SR6ItemAttribute.FIREWALL);
//		}
		logger.log(Level.DEBUG, " Defense Rating Matrix = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefenseRatingAstral() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_ASTRAL);
		val.setDistributed(0);
		addNaturalModifier(val,ShadowrunAttribute.INTUITION);
		logger.log(Level.DEBUG, " Defense Rating Astral = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefensePoolPhysical() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_PHYSICAL);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.REACTION);
		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		logger.log(Level.DEBUG, " Defensive pool = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefensePoolCombatDirect() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_DIRECT);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		logger.log(Level.DEBUG, " Defensive pool (Direct) = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefensePoolCombatIndirect() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_INDIRECT);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.REACTION);
		addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
		logger.log(Level.DEBUG, " Defensive pool (Direct) = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefensePoolAstral() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_ASTRAL);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.INTUITION);
		addNaturalModifier(val, ShadowrunAttribute.LOGIC);
		logger.log(Level.DEBUG, " Defensive pool Astral = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateDefensePoolMatrix() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX);
		val.setDistributed(0);
		addNaturalModifier(val, model.getPersona().getDataProcessing().getModifiedValue(), SR6ItemAttribute.DATA_PROCESSING);
		addNaturalModifier(val, model.getPersona().getFirewall().getModifiedValue(), SR6ItemAttribute.FIREWALL);
		logger.log(Level.DEBUG, " Defense Pool Matrix = "+val.getModifiedValue());

		val = model.getAttribute(ShadowrunAttribute.FULL_DEFENSE_POOL_MATRIX);
		val.setDistributed(0);
		addNaturalModifier(val, model.getPersona().getDataProcessing().getModifiedValue(), SR6ItemAttribute.DATA_PROCESSING);
		addNaturalModifier(val, model.getPersona().getFirewall().getModifiedValue(), SR6ItemAttribute.FIREWALL);
		addNaturalModifier(val, model.getPersona().getFirewall().getModifiedValue(), SR6ItemAttribute.FIREWALL);
		logger.log(Level.DEBUG, " Full Defense Pool Matrix = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateResistMatrixDamage() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_MATRIX);
		val.setDistributed(0);
		addNaturalModifier(val, model.getPersona().getFirewall().getModifiedValue(), SR6ItemAttribute.FIREWALL);
		logger.log(Level.DEBUG, " Defensive pool Matrix damafe = "+val.getModifiedValue());
	}

	//-------------------------------------------------------------------
	private void calculateResistToxin() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.RESIST_TOXIN);
		val.setDistributed(0);
		addNaturalModifier(val, ShadowrunAttribute.BODY);
		addNaturalModifier(val, ShadowrunAttribute.WILLPOWER);
		logger.log(Level.DEBUG, " Defensive pool Toxins = "+val.getModifiedValue());
	}


}
