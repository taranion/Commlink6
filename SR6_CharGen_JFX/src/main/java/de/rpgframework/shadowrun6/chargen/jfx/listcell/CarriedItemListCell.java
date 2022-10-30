package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;

import org.prelle.javafx.ApplicationScreen;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.FlipControl;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.MainScreen;
import org.prelle.javafx.Page;
import org.prelle.javafx.ScreenManagerProvider;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.OperationMode;
import de.rpgframework.genericrpg.items.OperationModeOption;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditCarriedItemDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CarriedItemListCell extends ComplexDataItemValueListCell<ItemTemplate, CarriedItem<ItemTemplate>> {
	
	private SR6CharacterController charCtrl;
	
	private Label lbValue;
	
	private FlipControl flip;

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
		bxActions.getChildren().add(lbValue);
		
		flip = new FlipControl(Orientation.HORIZONTAL, true);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem<ItemTemplate> item, boolean empty) {
		super.updateItem(item, empty);
		bxCenter.getChildren().retainAll(line1, sep, bxActions);
		
		if (item!=null) {
			flip.getItems().setAll(layout);
			flip.setVisibleIndex(0);
			
			name.setText(item.getNameWithRating());
			btnEdit.setVisible(true);
			btnEdit.setManaged(true);
			Node data = ItemUtilJFX.getItemInfoNode(item, charCtrl, false);
			if (data!=null) {
				bxCenter.getChildren().add(bxCenter.getChildren().indexOf(bxActions), data);
				data.getStyleClass().add("item-info-node");
			}
			
			lbValue.setText("\u00A5"+item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
			
			ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>> charGen = controlProvider.get();
			Possible removeable = charGen.canBeDeselected(item);
			if (!removeable.get() && !item.getModifications().isEmpty()) {
				lblLock.setTooltip(new Tooltip(Shadowrun6Tools.getModificationSourceString(item.getModifications().get(0).getSource())));
			}
			
			// Modes
			if (item.getOperationModes().isEmpty()) {
				line2.getChildren().clear();
				setGraphic(layout);
			} else {
				Label lbWarning = new Label("Modeswitcher - Not functional yet");
				GridPane back = new GridPane();
				int y=0;
				for (OperationModeOption modeOpt : item.getOperationModes()) {
					CarriedItem<?> src = modeOpt.getSource();
					Label lbSrc = new Label(src.getNameWithoutRating());
					ChoiceBox<OperationMode> cbMode = new ChoiceBox<>();
					cbMode.getItems().addAll(modeOpt.getModes());
					back.add(lbSrc , 0, y);
					back.add(cbMode, 1, y);
					y++;
				}
				//line2.getChildren().setAll(cbMode);
				flip.getItems().add(back);
				setGraphic(flip);
			}
			
		}
	}

	//-------------------------------------------------------------------
	@Override
	protected void editClicked(CarriedItem<ItemTemplate> ref) {
		logger.log(Level.WARNING, "TODO: editClicked for "+getClass());
		
		EditCarriedItemDialog content = new EditCarriedItemDialog(charCtrl, ref, new ScreenManagerProvider() {
			
			@Override
			public MainScreen getScreenManager() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		content.refresh();
		content.setAppLayout(FlexibleApplication.getInstance().getAppLayout());
		FlexibleApplication.getInstance().openScreen(new ApplicationScreen(content));
	}
}
