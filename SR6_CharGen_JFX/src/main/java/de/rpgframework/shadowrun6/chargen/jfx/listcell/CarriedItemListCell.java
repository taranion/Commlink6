package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.function.Supplier;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class CarriedItemListCell extends ComplexDataItemValueListCell<ItemTemplate, CarriedItem<ItemTemplate>> {

	//-------------------------------------------------------------------
	public CarriedItemListCell(Supplier<ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>>> ctrlProv) {
		super(ctrlProv);
		// TODO Auto-generated constructor stub
	}

}
