package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
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
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

}
