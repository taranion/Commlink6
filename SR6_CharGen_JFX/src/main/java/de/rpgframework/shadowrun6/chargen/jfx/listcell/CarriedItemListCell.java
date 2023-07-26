package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;

import org.prelle.javafx.ApplicationScreen;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditCarriedItemDialog;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditVehicleItemDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class CarriedItemListCell extends ComplexDataItemValueListCell<ItemTemplate, CarriedItem<ItemTemplate>> {

	private SR6CharacterController charCtrl;

	private Label lbValue;
	private Label lbEssence;

	//-------------------------------------------------------------------
	public CarriedItemListCell(SR6CharacterController control) {
		super(() -> control.getEquipmentController());
		this.charCtrl = control;
		name.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		super.initLayout();

		lbValue = new Label();
		lbEssence = new Label();
		bxActions.getChildren().add(lbValue);
		bxCenter.getChildren().remove(tiles);

		// Spacing
		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(buf, Priority.SOMETIMES);
		bxActions.getChildren().add(buf);

		bxActions.getChildren().add(lbEssence);

		// Put Increase/decrease buttons in actions line and make them smaller
		tiles.setStyle("-fx-padding: -6px 0 0 0;");
		lblVal.getStyleClass().remove(JavaFXConstants.STYLE_HEADING2);
		bxActions.getChildren().add(tiles);

		layout.setStyle("-fx-max-width: 22em");
		layout.setAlignment(Pos.TOP_CENTER);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem<ItemTemplate> item, boolean empty) {
		super.updateItem(item, empty);
		bxCenter.getChildren().retainAll(line1, sep, bxActions);

		if (item != null) {
			name.setText(item.getNameWithRating());
			btnEdit.setVisible(true);
			btnEdit.setManaged(true);
			Node data = ItemUtilJFX.getItemInfoNode(item, charCtrl, false);
			if (data != null) {
				data.setStyle("-fx-max-width: 20.5em");
				bxCenter.getChildren().add(bxCenter.getChildren().indexOf(bxActions), data);
				data.getStyleClass().add("item-info-node");
			}

			int price = item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue() * (item.getCount()>1?item.getCount():1);
			lbValue.setText("\u00A5" + price);
			// Essence
			lbEssence.setManaged(item.hasAttribute(SR6ItemAttribute.ESSENCECOST));
			lbEssence.setVisible(item.hasAttribute(SR6ItemAttribute.ESSENCECOST));
			if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST)) {
				lbEssence.setText( item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue()+"");
			}
			// Countable
			if (item.getResolved().isCountable()) {
				lblVal.setText(String.valueOf(item.getCount()));
				tiles.setVisible(true);
				tiles.setManaged(true);
				btnDec.setDisable(!((ISR6EquipmentController) charCtrl.getEquipmentController()).canBeDecreased(item).get());
				btnInc.setDisable(!((ISR6EquipmentController) charCtrl.getEquipmentController()).canBeIncreased(item).get());
//				line2.getChildren().setAll(tiles);
			} else {
				tiles.setVisible(false);
				tiles.setManaged(false);
//				line2.getChildren().clear();
			}

			ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>> charGen = controlProvider.get();
			Possible removeable = charGen.canBeDeselected(item);
			if (!removeable.get() && !item.getModifications().isEmpty()) {
				lblLock.setTooltip(new Tooltip(Shadowrun6Tools.getModificationSourceString(item.getModifications().get(0).getSource())));
			}

			setGraphic(layout);
		}
	}

	//-------------------------------------------------------------------
	@Override
	protected void editClicked(CarriedItem<ItemTemplate> ref) {
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
	}
}
