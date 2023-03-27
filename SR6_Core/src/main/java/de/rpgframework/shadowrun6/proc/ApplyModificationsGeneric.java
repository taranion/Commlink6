package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.CheckModification;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyModificationsGeneric implements ProcessingStep {

	private final static Logger logger = System.getLogger(ApplyModificationsGeneric.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public ApplyModificationsGeneric(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @return TRUE when modification was processed
	 */
	public static boolean applyModification(Shadowrun6Character model, Modification tmp) {
		try {
			if (tmp instanceof DataItemModification) {
				DataItemModification mod = (DataItemModification)tmp;
				switch ((ShadowrunReference) tmp.getReferenceType()) {
				case ADEPT_POWER: return applyAdeptPower(model, mod);
				case ATTRIBUTE  : return applyAttribute(model, (ValueModification) mod);
				case CRITTER_POWER: return applyCritterPower(model, mod);
				case GEAR       : return applyGear(model, mod);
				case LIFESTYLE  : return applyLifestyle(model, mod);
				case QUALITY    : return applyQuality(model, mod);
				case RULE       : return applyRule(model, mod);
				case SKILL		: return applySkill(model, (ValueModification) mod);
				case ITEM_ATTRIBUTE:
				case ACTION:
					model.addItemModification(mod); return true;
				default:
					logger.log(Level.WARNING, "Don't know how to apply "+tmp.getReferenceType()+" of "+tmp);
					System.err.println("ApplyModificationsGeneric: Don't know how to apply "+tmp.getReferenceType()+" of "+tmp);
				}
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, "Error applying "+tmp+" from "+tmp.getSource(),e);
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();

		try {
			// Walk modifications for creation points
			for (Modification tmp : previous) {
				logger.log(Level.DEBUG, "process "+tmp);
				if (tmp instanceof AllowModification) {
					unprocessed.add(tmp);
				} else if (tmp.getApplyTo()==ApplyTo.CHARACTER || tmp.getApplyTo()==ApplyTo.UNARMED
						|| tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE
						|| tmp.getReferenceType()==ShadowrunReference.SKILL
						|| tmp.getReferenceType()==ShadowrunReference.QUALITY
						|| tmp.getReferenceType()==ShadowrunReference.CRITTER_POWER
						) {
					if (!applyModification(model, tmp)) {
						unprocessed.add(tmp);
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	// -------------------------------------------------------------------
	private static boolean applyAdeptPower(Shadowrun6Character model, DataItemModification mod) {
		AdeptPower item = Shadowrun6Core.getItem(AdeptPower.class, mod.getKey());
		AdeptPowerValue value = model.getAdeptPower(mod.getKey());
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such quality {0}", mod.getKey());
		}
		if (value == null) {
			value = new AdeptPowerValue(item, 0);
			// Handle decisions
			for (Decision dec : mod.getDecisions()) {
				value.addDecision(dec);
				logger.log(Level.DEBUG, "Add decision {0} to adept power {1}", dec, item);
			}

			model.addAdeptPower(value);
			logger.log(Level.DEBUG, "Add adept power {0} to character", item);
		}
		// Mark as auto-added
		value.addModification(mod);

		if (item.hasLevel()) {
			logger.log(Level.DEBUG, " Level is now distr={0}   mod={1} = " + value.getNameWithoutRating(), value.getDistributed(),
					value.getModifier());
			logger.log(Level.DEBUG, "  result=" + value);
		}
		return true;
	}

	// -------------------------------------------------------------------
	private static boolean applyAttribute(Shadowrun6Character model, ValueModification mod) {
		ShadowrunAttribute item = null;
		if ("CHOICE".equals(mod.getKey())) {
			UUID uuid = mod.getConnectedChoice();
			Decision dec = mod.getDecision(uuid);
			item = mod.getReferenceType().resolve(dec.getValue());
		} else {
			item = mod.getReferenceType().resolve(mod.getKey());
		}
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such attribute {0}", mod.getKey());
		}
		AttributeValue<ShadowrunAttribute> value = model.getAttribute(item);
		if (mod.getApplyTo()==ApplyTo.DRAKE) {
			BodyForm drake = model.getBodyForm(BodyType.DRAKE);
			if (drake==null) {
				logger.log(Level.ERROR, "Should apply {0} to drake body, but there is no such body");
				return false;
			}
			value = drake.getAttributeValue(item);

		}
		if (value == null) {
		}
		if (mod.getSet()==ValueType.MAX)
			return false;


		value.addModification(mod);
		if (!(mod instanceof CheckModification)) {
			logger.log(Level.INFO, "Added {0} to attribute {1} ({2}) from {3}", mod.getValue(), item, mod.getSet(), mod.getSource());
		}

		return true;
	}

	// -------------------------------------------------------------------
	private static boolean applySkill(Shadowrun6Character model, ValueModification mod) {
		SR6Skill item = mod.getReferenceType().resolve(mod.getKey());
		SR6SkillValue value = model.getSkillValue(item);
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such skill {0}", mod.getKey());
		}
		if (value == null) {
			logger.log(Level.ERROR, "applySkill for skill unset: "+mod.getKey());
			return false;
		}

		value.addModification(mod);
		logger.log(Level.INFO, "Added {0} to skill {1} ({2}) from {3}", mod.getValue(), item, mod.getSet(), mod.getSource());

		return true;
	}

	// -------------------------------------------------------------------
	private static boolean applyGear(Shadowrun6Character model, DataItemModification mod) {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, mod.getKey());
		SR6PieceOfGearVariant variant = null;
		if (mod.getVariant()!=null) {
			variant = (SR6PieceOfGearVariant) item.getVariant(mod.getVariant());
		}
		CarryMode carry = CarryMode.CARRIED;
		if (!item.getUsages().isEmpty()) {
			carry = item.getUsages().get(0).getMode();
		}
		if (variant!=null && !variant.getUsages().isEmpty()) {
			carry = variant.getUsages().get(0).getMode();
		}
		Decision[] dec = mod.getDecisions().toArray(new Decision[mod.getDecisions().size()]);
		OperationResult<CarriedItem<ItemTemplate>> result = SR6GearTool.buildItem(item, carry, variant, model, false, dec);
		if (result.hasError()) {
			logger.log(Level.ERROR, "Failed creating {0}/{1}/{2}: {3}", mod.getKey(), mod.getVariant(), carry, result.getError());
			return false;
		}
		result.get().setInjectedBy(mod.getSource());
		result.get().addModification(mod);

		logger.log(Level.DEBUG, "Put item in inventory: {0}   (from {1})", result.get(), mod.getSource());
		model.addCarriedItem(result.get());
		return true;
	}

	//-------------------------------------------------------------------
	private static boolean applyLifestyle(Shadowrun6Character model, DataItemModification mod) {
		LifestyleQuality item = Shadowrun6Core.getItem(LifestyleQuality.class, mod.getKey());
//		UUID uuidToSet = mod.getId();
//		if (uuidToSet==null) {
//			logger.log(Level.ERROR, "When injecting lifestyles, the modification should have an id='UUID' attribute (from {0})", mod.getSource());
//			return false;
//		}
//		SR6Lifestyle value = model.getLifestyle(uuidToSet);
//		if (value == null) {
		SR6Lifestyle
			value = new SR6Lifestyle(item);
			if (mod instanceof ValueModification) {
				value.setDistributed( ((ValueModification)mod).getValue() );
			} else
				value.setDistributed(1);
			// Handle decisions
			for (Decision dec : mod.getDecisions()) {
				value.addDecision(dec);
				logger.log(Level.DEBUG, "Add decision {0} to lifestyle {1}", dec, item);
			}
			value.setInjectedBy(mod.getSource());
			value.setUuid(UUID.randomUUID());

			logger.log(Level.WARNING, "Inject lifestyle: {0}   (from {1})", value, mod.getSource());
			model.addLifestyle(value);
			logger.log(Level.DEBUG, "Add lifestyle {0} to character", item);
//		}
		// Mark as auto-added
		value.addModification(mod);
		return true;
	}

	//-------------------------------------------------------------------
	private static boolean applyQuality(Shadowrun6Character model, DataItemModification mod) {
		Quality item = Shadowrun6Core.getItem(Quality.class, mod.getKey());
		QualityValue value = model.getQuality(mod.getKey());
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such quality {0}", mod.getKey());
		}

		if (value == null) {
			value = new QualityValue(item, 0);
			// Handle decisions
			for (Decision dec : mod.getDecisions()) {
				value.addDecision(dec);
				logger.log(Level.DEBUG, "Add decision {0} to quality {1}", dec, item);
			}

			model.addQuality(value);
			logger.log(Level.DEBUG, "Add quality {0} to character", item);
		}
		// Mark as auto-added
		value.addModification(mod);

		if (item.hasLevel()) {
			logger.log(Level.DEBUG, " Level is now distr={0}   mod={1} = " + value.getName(), value.getDistributed(),
					value.getModifier());
			logger.log(Level.DEBUG, "  result=" + value);
		}
		return true;
	}

	//-------------------------------------------------------------------
	private static boolean applyCritterPower(Shadowrun6Character model, DataItemModification mod) {
		CritterPower item = Shadowrun6Core.getItem(CritterPower.class, mod.getKey());
		CritterPowerValue value = model.getCritterPower(mod.getKey());
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such quality {0}", mod.getKey());
		}
		if (value == null) {
			value = new CritterPowerValue(item, 0);
			// Handle decisions
			for (Decision dec : mod.getDecisions()) {
				value.addDecision(dec);
				logger.log(Level.DEBUG, "Add decision {0} to critter power {1}", dec, item);
			}

			if (mod.getApplyTo()==ApplyTo.DRAKE) {
				logger.log(Level.INFO, "Add critter power {0} to drake body", item);
				model.getBodyForm(BodyType.DRAKE).addCritterPower(value);

			} else {
				model.addCritterPower(value);
				logger.log(Level.INFO, "Add critter power {0} to character", item);
			}
		}
		// Mark as auto-added
		value.addModification(mod);

		if (item.hasLevel()) {
			logger.log(Level.INFO, " Level is now distr={0}   mod={1} = " + value.getName(), value.getDistributed(),
					value.getModifier());
			logger.log(Level.DEBUG, "  result=" + value);
		}
		return true;
	}

	// -------------------------------------------------------------------
	private static boolean applyRule(Shadowrun6Character model, DataItemModification mod) {
		SR6RuleFlag item = SR6RuleFlag.valueOf(mod.getKey());
		//Rule item = Shadowrun6Rules.getRule(mod.getKey());
		if (item==null) {
			logger.log(Level.ERROR, "No such rule {0} - source {1}", mod.getKey(), mod.getSource());
			System.exit(1);
		}
		if (mod.isRemove()) {
			model.clearRuleFlag(item);
			logger.log(Level.DEBUG, "Clear rule {0} from character", item);
		} else {
			model.addRuleFlag(item);
			logger.log(Level.DEBUG, "Set rule {0} to character", item);
		}
		return true;
	}

}
