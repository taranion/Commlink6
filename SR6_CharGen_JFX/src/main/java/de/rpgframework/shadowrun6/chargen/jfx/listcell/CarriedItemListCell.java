package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;
import java.util.Locale;

import org.prelle.javafx.ApplicationScreen;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.jfx.cells.ACarriedItemListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditCarriedItemDialog;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditVehicleItemDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/**
 * @author prelle
 *
 */
public class CarriedItemListCell extends ACarriedItemListCell<ItemTemplate> {

	private SR6CharacterController charCtrl;

	private Label lbEssence;

	//-------------------------------------------------------------------
	public CarriedItemListCell(SR6CharacterController control) {
		super(() -> control.getEquipmentController());
		this.charCtrl = control;
		lbEssence = new Label();
		name.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		addExtraActionLine(new Label(SR6ItemAttribute.ESSENCECOST.getShortName(Locale.getDefault())+":"));
		addExtraActionLine(lbEssence);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem<ItemTemplate> item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			// Essence
			lbEssence.setManaged(item.hasAttribute(SR6ItemAttribute.ESSENCECOST));
			lbEssence.setVisible(item.hasAttribute(SR6ItemAttribute.ESSENCECOST));
			if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST)) {
				ItemAttributeFloatValue<SR6ItemAttribute> essVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
				lbEssence.setText( essVal.getModifiedValue()+"");
				if (essVal.getIncomingModifications().size()>0) {
					//lbEssence.setText( essVal.getDistributed()+"("+essVal.getModifiedValue()+")");
					lbEssence.setTooltip(new Tooltip(Shadowrun6Tools.toExplainStringFloat(essVal.getIncomingModifications())));
					lbEssence.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
				} else {
					lbEssence.getStyleClass().remove(JavaFXConstants.STYLE_HEADING5);
				}
			}
		}
	}

	//-------------------------------------------------------------------
	@Override
	protected OperationResult<CarriedItem<ItemTemplate>> editClicked(CarriedItem<ItemTemplate> ref) {
		logger.log(Level.INFO, "editClicked for "+getClass());

		ItemType type = ref.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		EditCarriedItemDialog content = null;
		switch (type) {
		case VEHICLES:
		case DRONE_LARGE: case DRONE_MEDIUM: case DRONE_MICRO: case DRONE_MINI: case DRONE_SMALL:
			content = new EditVehicleItemDialog(charCtrl, ref);
			break;
		default:
			content = new EditCarriedItemDialog(charCtrl, ref);
		}

		content.refresh();
		content.setAppLayout(FlexibleApplication.getInstance().getAppLayout());
		FlexibleApplication.getInstance().openScreen(new ApplicationScreen(content));
		return new OperationResult<>(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.cells.ACarriedItemListCell#getSinglePrice(de.rpgframework.genericrpg.items.CarriedItem)
	 */
	@Override
	public int getSinglePrice(CarriedItem<ItemTemplate> item) {
		int count = (item.getCount()>0) ? item.getCount() : 1;
		return item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue() / count;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.cells.ACarriedItemListCell#getModificationSourceString(java.lang.Object)
	 */
	@Override
	public String getModificationSourceString(Object modSource) {
		return Shadowrun6Tools.getModificationSourceString(modSource);
	}

}
