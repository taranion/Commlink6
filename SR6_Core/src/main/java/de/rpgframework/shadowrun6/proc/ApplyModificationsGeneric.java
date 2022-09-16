package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
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
		if (tmp instanceof DataItemModification) {
			DataItemModification mod = (DataItemModification)tmp;
			switch ((ShadowrunReference) tmp.getReferenceType()) {
			case ADEPT_POWER: return applyAdeptPower(model, mod);
			case ATTRIBUTE  : return applyAttribute(model, (ValueModification) mod);
			case QUALITY    : return applyQuality(model, mod);
			case RULE       : return applyRule(model, mod);
			default:
				logger.log(Level.WARNING, "Don't know how to apply "+tmp.getReferenceType()+" of "+tmp);
				System.err.println("ApplyModificationsGeneric: Don't know how to apply "+tmp.getReferenceType()+" of "+tmp);
				System.exit(1);
			}
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
				if (tmp.getApplyTo()==ApplyTo.CHARACTER || tmp.getReferenceType()==ShadowrunReference.QUALITY) {
					applyModification(model, tmp);
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
		ShadowrunAttribute item = mod.getReferenceType().resolve(mod.getKey());
		AttributeValue<ShadowrunAttribute> value = model.getAttribute(item);
		if (item == null) {
			logger.log(Level.ERROR, "Cannot apply modification " + mod + " - no such attribute {0}", mod.getKey());
		}
		if (value == null) {
		}

		value.addModification(mod);
		logger.log(Level.DEBUG, "Added {0} to attribute {1}", mod.getValue(), item);

		return true;
	}

	// -------------------------------------------------------------------
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
