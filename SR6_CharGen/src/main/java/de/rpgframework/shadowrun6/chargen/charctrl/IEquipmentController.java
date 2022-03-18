package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public interface IEquipmentController extends 
	ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>>, 
	NumericalValueController<ItemTemplate, CarriedItem<ItemTemplate>> {

	
}
