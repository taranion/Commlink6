package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.ctrl.IPANController;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SpecialRuleStep implements ProcessingStep {

	protected static Logger logger = System.getLogger(SpecialRuleStep.class.getPackageName());

	private Shadowrun6Character model;
	private Locale loc = Locale.getDefault();

	//-------------------------------------------------------------------
	public SpecialRuleStep(Shadowrun6Character model, Locale loc) {
		this.model = model;
		this.loc   = loc;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		if (model.hasRuleFlag(SR6RuleFlag.CYBER_SINGULARITY_SEEKER)) {
			applyCyberSingularitySeeker();
		}
		if (model.hasRuleFlag(SR6RuleFlag.REDLINER)) {
			applyRedliner();
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void applyCyberSingularitySeeker() {
		logger.log(Level.DEBUG, "Apply {0}",SR6RuleFlag.CYBER_SINGULARITY_SEEKER);
		List<CarriedItem<ItemTemplate>> limbs = model.getCarriedItems(item -> item.hasAutoFlag(SR6ItemFlag.CYBERLIMB));
		if (limbs==null) return;
		// The runner gains +1 Willpower for each pair of cyberlimbs (two arms or two legs) they get implanted, up to a maximum of +2 Willpower
		int bonus = Math.min(2, limbs.size()/2);
		if (bonus>0) {
			logger.log(Level.INFO, "Add +{0} WILLPOWER for CYBER_SINGULARITY_SEEKER",bonus);
			ValueModification mod = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.WILLPOWER.name(), bonus);
			mod.setSource(model.getQuality("cyber_singularity_seeker"));
			mod.setSet(ValueType.AUGMENTED);
			model.getAttribute(ShadowrunAttribute.WILLPOWER).addIncomingModification(mod);
		}
	}

	//-------------------------------------------------------------------
	private void applyRedliner() {
		logger.log(Level.INFO, "Apply {0}",SR6RuleFlag.REDLINER);
		List<CarriedItem<ItemTemplate>> limbs = model.getCarriedItems(item -> item.hasAutoFlag(SR6ItemFlag.CYBERLIMB));
		if (limbs==null) return;
		// For every pair of cyberlimbs installed (two cyberarms or two cyberlegs), your paired limbs receive an additional +1 Strength and +1 Agility, up to a maximum for +2
		int bonus = Math.min(2, limbs.size()/2);
		if (bonus>0) {
			logger.log(Level.INFO, "Add +{0} AGI/STR for REDLINER",bonus);
			ValueModification mod = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.AGILITY.name(), bonus);
			mod.setSource(model.getQuality("redliner"));
			mod.setSet(ValueType.AUGMENTED);
			model.getAttribute(ShadowrunAttribute.AGILITY).addIncomingModification(mod);
			mod = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.STRENGTH.name(), bonus);
			mod.setSource(model.getQuality("redliner"));
			mod.setSet(ValueType.AUGMENTED);
			model.getAttribute(ShadowrunAttribute.STRENGTH).addIncomingModification(mod);
		}
		// Mark items as "not overclockable"
		for (CarriedItem<ItemTemplate> limb : limbs) {
			limb.addAutoFlag(SR6ItemFlag.CANNOT_OVERCLOCK);
		}
	}

}
