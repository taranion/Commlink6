package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.EmulatedProgramsSection;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class EmulatedProgramCell extends ComplexDataItemValueListCell<ItemTemplate, CarriedItem<ItemTemplate>> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EmulatedProgramsSection.class.getPackageName()+".Section");

	private SR6CharacterController charCtrl;

	private Label lbSource;

	//-------------------------------------------------------------------
	public EmulatedProgramCell(SR6CharacterController control) {
		super(() -> control.getEquipmentController());
		this.charCtrl = control;
		name.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		super.initLayout();

		bxCenter.getChildren().remove(tiles);
		lbSource = new Label();
		line2.getChildren().add(lbSource);

		// Spacing
		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(buf, Priority.SOMETIMES);
		bxActions.getChildren().add(buf);

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
		bxCenter.getChildren().retainAll(line1, line2);

		if (item != null) {
			name.setText(item.getNameWithRating());
			btnEdit.setVisible(false);
			btnEdit.setManaged(false);

			if (item.getInjectedBy()!=null) {
				if (item.getInjectedBy() instanceof ComplexFormValue) {
					lbSource.setText("  "+RES.getString("section.emulated_programs.cell.src_cform"));
				} else if (item.getInjectedBy()==null)
					lbSource.setText("  "+RES.getString("section.emulated_programs.cell.src_echo"));
				else
					lbSource.setText("  "+String.valueOf(item.getInjectedBy()));
			} else {
				lbSource.setText(null);
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
		logger.log(Level.WARNING, "editClicked for "+getClass());
	}
}
