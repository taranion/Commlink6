package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Locale;
import java.util.function.Supplier;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class ItemTemplateListCell extends ComplexDataItemListCell<ItemTemplate> {
	
	private VBox bxDetails;
	
	//-------------------------------------------------------------------
	public ItemTemplateListCell(Supplier<ComplexDataItemController<ItemTemplate, ? extends ComplexDataItemValue<ItemTemplate>>> controlProv) {
		super(controlProv, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
	}


	//-------------------------------------------------------------------
	public void updateItem(ItemTemplate item, boolean empty) {
		super.updateItem(item, empty);
	}
}
