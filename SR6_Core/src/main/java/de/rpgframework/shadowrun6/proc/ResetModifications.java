package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Movement;
import de.rpgframework.shadowrun.Movement.MovementType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class ResetModifications implements ProcessingStep {

	private final static Logger logger = System.getLogger(ResetModifications.class.getPackageName()+".reset");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public ResetModifications(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		logger.log(Level.WARNING, "ENTER process");
		model.clearEdgeModifications();
		model.clearItemModifications();
		model.clearGearDefinitions();
		model.clearForbiddenSources();
		model.getCritterPowers().clear();
		model.clearRuleFlags();

		try {
			// Attributes
			for (AttributeValue<ShadowrunAttribute> val : model.getAttributes()) {
				val.clearIncomingModifications();
			}

			// Skills
			for (SR6SkillValue val : model.getSkillValues()) {
				val.clearIncomingModifications();
			}

			// Remove all auto-added items
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				if (item.isAutoAdded()) {
					model.removeCarriedItem(item);
//				} else if (item.isDirty()) {
//					item.reset();
				}
			}

			// Reset all incoming parent modifications
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				item.clearModificationsFromCharacter();
			}

			// Remove all auto-added SINs
			for (SIN sin : new ArrayList<>(model.getSINs())) {
				if (sin.getInjectedBy()!=null) {
					model.removeSIN(sin);
				}
			}

			// Remove all auto-added License
			for (LicenseValue tmp : new ArrayList<>(model.getLicenses())) {
				if (tmp.getInjectedBy()!=null) {
					model.removeLicense(tmp);
				}
			}

			// Remove all auto-qualities or quality levels
			for (QualityValue val : new ArrayList<>(model.getQualities())) {
				boolean remove = val.isRemoveOnReset();
				val.clearIncomingModifications();
				if (remove) {
					logger.log(Level.WARNING, "Remove quality "+val);
					model.removeQuality(val);
				}
			}

			// Remove all auto-qualities or quality levels
			model.clearAutoAdeptPower();
			for (AdeptPowerValue val : new ArrayList<>(model.getAdeptPowers())) {
				val.reset();
				val.clearIncomingModifications();
			}

			// Remove all auto-metaechoes
			for (MetamagicOrEchoValue val : new ArrayList<>(model.getMetamagicOrEchoes())) {
				boolean remove = val.isAutoAdded();
				val.clearIncomingModifications();
				if (remove) {
					logger.log(Level.DEBUG, "Remove metaecho "+val);
					model.removeMetamagicOrEcho(val);
				}
			}

			// Prepare minimal body modifications
			model.clearBodyForms();
			BodyForm body = new BodyForm(BodyType.METAHUMAN);
			body.addMovement(new Movement(MovementType.GROUND,10,15,1));
			body.addMovement(new Movement(MovementType.WATER,3,3,1));
			model.addBodyForm(body);

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
