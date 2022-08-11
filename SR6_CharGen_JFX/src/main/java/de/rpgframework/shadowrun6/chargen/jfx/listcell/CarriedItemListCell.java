package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;

import org.prelle.javafx.ApplicationScreen;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.MainScreen;
import org.prelle.javafx.Page;
import org.prelle.javafx.ScreenManagerProvider;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditCarriedItemDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class CarriedItemListCell extends ComplexDataItemValueListCell<ItemTemplate, CarriedItem<ItemTemplate>> {
	
	private SR6CharacterController charCtrl;
	
	private Label lbValue;

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
			Node data = ItemUtilJFX.getItemInfoNode(item, charCtrl, false);
			if (data!=null)
				bxCenter.getChildren().add(bxCenter.getChildren().indexOf(bxActions), data);
			
			lbValue.setText("\u00A5"+item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
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
