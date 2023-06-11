package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Movement;
import de.rpgframework.shadowrun.Movement.MovementType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;

/**
 * @author prelle
 *
 */
public class ResetModifications implements ProcessingStep {

	private final static Logger logger = System.getLogger(ResetModifications.class.getPackageName());

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

		model.clearEdgeModifications();
		model.clearItemModifications();
		model.clearGearDefinitions();

		try {
			// Attributes
			for (AttributeValue<ShadowrunAttribute> val : model.getAttributes()) {
				val.clearModifications();
			}

			// Skills
			for (SR6SkillValue val : model.getSkillValues()) {
				val.clearModifications();
			}

			// Remove all auto-added items
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				if (item.isAutoAdded() || ItemTemplate.UUID_UNARMED.equals(item.getUuid())) {
					model.removeCarriedItem(item);
				}
			}

			// Remove all auto-qualities or quality levels
			for (QualityValue val : new ArrayList<>(model.getQualities())) {
				boolean remove = val.isRemoveOnReset();
				val.clearModifications();
				if (remove) {
					logger.log(Level.DEBUG, "Remove quality "+val);
					model.removeQuality(val);
				}
			}

			// Remove all auto-metaechoes
			for (MetamagicOrEchoValue val : new ArrayList<>(model.getMetamagicOrEchoes())) {
				boolean remove = val.isAutoAdded();
				val.clearModifications();
				if (remove) {
					logger.log(Level.DEBUG, "Remove metaecho "+val);
					model.removeMetamagicOrEcho(val);
				}
			}


			// Ensure there is a device for unused software
			CarriedItem<ItemTemplate> item = model.getSoftwareLibrary();
			if (item==null) {
				item = SR6GearTool.buildItem(ItemUtil.SOFTWARE_LIBRARY_ITEM, CarryMode.CARRIED, null, false).get();
				//item = new CarriedItem<ItemTemplate>(ItemUtil.SOFTWARE_LIBRARY_ITEM, null, CarryMode.CARRIED);
				item.setUuid(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE);
				model.setSoftwareLibrary(item);
				SR6GearTool.recalculate("", model, item);
				//item.addSlot(new AvailableSlot(ItemHook.SOFTWARE, 99));
				if (item.getSlot(ItemHook.SOFTWARE)==null) {
					logger.log(Level.ERROR, "Missing software slot");
					System.exit(1);
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
