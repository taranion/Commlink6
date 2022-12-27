package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.List;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;

/**
 * @author prelle
 *
 */
public interface ISR6EquipmentController extends IEquipmentController<ItemTemplate, ItemHook, SR6ItemEnhancement> {

	//-------------------------------------------------------------------
	public List<ItemTemplate> getAvailable(CarryMode mode, ItemType...types);
		
	public ComplexDataItemController<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>> getItemEnhancementController(CarriedItem<ItemTemplate> toModify);
	
}
