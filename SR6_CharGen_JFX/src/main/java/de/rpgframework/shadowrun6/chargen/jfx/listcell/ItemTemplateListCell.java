package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Locale;
import java.util.function.Supplier;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class ItemTemplateListCell extends ComplexDataItemListCell<ItemTemplate> {
	
	private CarryMode carry;
	private CarriedItem<ItemTemplate> container;
	private ItemHook hook;
	
	//-------------------------------------------------------------------
	public ItemTemplateListCell(Supplier<ComplexDataItemController<ItemTemplate, ? extends ComplexDataItemValue<ItemTemplate>>> controlProv, CarryMode carry) {
		super(controlProv, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		this.carry = carry;
		layout.setStyle("-fx-max-width: 21.5em");
	}
	
	//-------------------------------------------------------------------
	public ItemTemplateListCell(Supplier<ComplexDataItemController<ItemTemplate, ? extends ComplexDataItemValue<ItemTemplate>>> controlProv, CarriedItem<ItemTemplate> container, ItemHook hook) {
		super(controlProv, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		this.carry = CarryMode.EMBEDDED;
		this.container = container;
		this.hook      = hook;
		layout.setStyle("-fx-max-width: 21.5em");
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
				Possible poss = null;
				if (CarryMode.EMBEDDED==carry) {
					poss = ((ISR6EquipmentController)controlProv.get()).canBeEmbedded(container, hook, item, null);
				} else {
					String variant = null;
					// If the carry mode does not exist in the default item, check all variants
					if (item.getUsage(carry)==null) {
						SR6PieceOfGearVariant var = (SR6PieceOfGearVariant) item.getVariant(carry);
						if (var!=null)
							variant = var.getId();
					}
					poss = ((ISR6EquipmentController)controlProv.get()).canBeSelected(item, variant, carry);
				}
				lbName.setDisable(!poss.get());
				lbSource.setStyle(poss.get()?"":"-fx-text-fill: highlight");
				lbSource.setWrapText(true);
				if (!poss.get()) {
					lbSource.setText(poss.toString());
				}
				setUserData(!poss.get());
			}
		}
	}
}
