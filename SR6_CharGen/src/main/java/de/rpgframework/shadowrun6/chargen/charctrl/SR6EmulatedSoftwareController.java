package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;

/**
 *
 */
public class SR6EmulatedSoftwareController extends ControllerImpl<CarriedItem<ItemTemplate>> {

	//-------------------------------------------------------------------
	protected SR6EmulatedSoftwareController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public List<CarriedItem<ItemTemplate>> getAvailable() {
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		for (CarriedItem<ItemTemplate> item : getModel().getCarriedItems(ItemType.SOFTWARE)) {
			// Has the software been bought
			if (item.isAutoAdded())
				continue;
			// Has the software already been absorbed
			if (item.hasFlag(SR6ItemFlag.ABSORBED))
				continue;

			ret.add(item);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public List<CarriedItem<ItemTemplate>> getSelected() {
		logger.log(Level.DEBUG, "getSelected()");
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		for (CarriedItem<ItemTemplate> item : getModel().getCarriedItems(ItemType.SOFTWARE)) {
			// Has the software been bought
			if (item.isAutoAdded())
				continue;
			// Has the software already been absorbed
			if (!item.hasFlag(SR6ItemFlag.ABSORBED))
				continue;

			ret.add(item);
		}

		// Add virtual items by complex forms
		for (ComplexFormValue val : getModel().getComplexForms()) {
			if (!"emulate".equals(val.getKey()))
				continue;
			Decision dec = val.getDecision(UUID.fromString("d6fad551-128e-4376-a397-95f7aa543ace"));
			if (dec==null) {
				logger.log(Level.ERROR, "Emulate complex form without correct decision UUID");
				continue;
			}
			ItemTemplate prog = Shadowrun6Core.getItem(ItemTemplate.class, dec.getValue());
			if (prog!=null) {
				CarriedItem<ItemTemplate> carried = SR6GearTool.buildItem(prog, CarryMode.EMBEDDED, getModel(), false).get();
				carried.setInjectedBy(val);
				ret.add(carried);
			} else {
				logger.log(Level.ERROR, "Emulate complex form with unknown program {0}", dec.getValue());
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

}
