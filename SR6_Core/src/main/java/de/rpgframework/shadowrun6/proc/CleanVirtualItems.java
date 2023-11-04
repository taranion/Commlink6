package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.UUID;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class CleanVirtualItems implements ProcessingStep {

	private final static Logger logger = System.getLogger(CleanVirtualItems.class.getPackageName()+".reset");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public CleanVirtualItems(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {

		logger.log(Level.INFO, "Virtual items: "+model.getVirtualCarriedItems());

		/*
		 * Only keep virtual software library and unarmed - reset those, remove others
		 */
		boolean missingUnarmed = true;
		boolean missingSoftware= true;
		for (CarriedItem<ItemTemplate> item : model.getVirtualCarriedItems()) {
			UUID uuid = item.getUuid();
			if (uuid==null)
				logger.log(Level.ERROR, "No UUID for item {0}", item);
			if (uuid.equals(ItemTemplate.UUID_UNARMED)) {
				logger.log(Level.INFO, "Reset UNARMED "+item.getAttributeRaw(SR6ItemAttribute.DAMAGE));
				item.clearModificationsFromCharacter();
				missingUnarmed = false;
			} else if (uuid.equals(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE)) {
				missingSoftware = false;
			} else {
				model.removeVirtualCarriedItem(item);
			}
		}

		// Eventually add missing unarmed
		if (missingUnarmed) {
			OperationResult<CarriedItem<ItemTemplate>> res = SR6GearTool.buildItem(ItemUtil.UNARMED_ITEM, CarryMode.VIRTUAL, null, model, true);
			CarriedItem<ItemTemplate> unarmed = res.get();
			unarmed.setInjectedBy("CORE");
			unarmed.setUuid(ItemTemplate.UUID_UNARMED);
			logger.log(Level.WARNING, "Inject UNARMED");
			model.addVirtualCarriedItem(unarmed);
		}

		// Eventually add missing unused software library
		if (missingSoftware) {
			logger.log(Level.WARNING, "Inject SOFTWARE_LIBRARY");
			model.addVirtualCarriedItem(createSoftwareLibrary());
		}


		return unprocessed;
	}

	//-------------------------------------------------------------------
	private CarriedItem<ItemTemplate> createSoftwareLibrary() {
		CarriedItem<ItemTemplate> SOFTWARE_LIBRARY;
		SOFTWARE_LIBRARY =  new CarriedItem<ItemTemplate>(ItemUtil.SOFTWARE_LIBRARY_ITEM, null, CarryMode.VIRTUAL);
		SOFTWARE_LIBRARY.addSlot(new AvailableSlot(ItemHook.SOFTWARE, 99));
		SOFTWARE_LIBRARY.setUuid(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE);
		SOFTWARE_LIBRARY.setAttribute(SR6ItemAttribute.PRICE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.PRICE, 0));
		SOFTWARE_LIBRARY.setInjectedBy("CORE");
		SOFTWARE_LIBRARY.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.ITEMTYPE, ItemType.ELECTRONICS));
		SOFTWARE_LIBRARY.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.TOOLS));
		SR6GearTool.recalculate("", null, model, SOFTWARE_LIBRARY, false);
		return SOFTWARE_LIBRARY;
	}
}
