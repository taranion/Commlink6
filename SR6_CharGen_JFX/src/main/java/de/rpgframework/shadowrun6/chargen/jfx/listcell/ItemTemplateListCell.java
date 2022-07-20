package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.CommonEquipmentController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class ItemTemplateListCell extends ComplexDataItemListCell<ItemTemplate> {
	
	private VBox bxDetails;
	private CarryMode carry;
	
	//-------------------------------------------------------------------
	public ItemTemplateListCell(Supplier<ComplexDataItemController<ItemTemplate, ? extends ComplexDataItemValue<ItemTemplate>>> controlProv, CarryMode carry) {
		super(controlProv, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		this.carry = carry;
	}


	//-------------------------------------------------------------------
	public void updateItem(ItemTemplate item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			lbName.setText(item.getName());
			lbSource.setText(String.join(", ", GenericCore.getBestPageReferenceShortNames(item, Locale.getDefault())));
			if (controlProv!=null && controlProv.get()!=null) {
				Possible poss = ((CommonEquipmentController)controlProv.get()).canBeSelected(item, null, carry);
				lbName.setDisable(!poss.get());
				lbSource.setStyle(poss.get()?"":"-fx-text-fill: highlight");
				setUserData(!poss.get());
			}
		}
	}
}
