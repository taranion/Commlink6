package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.EmulatedProgramsSection;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.Parent;
import javafx.scene.control.Label;

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
		lbSource = new Label();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem<ItemTemplate> item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
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
		}
	}

	//-------------------------------------------------------------------
	protected Parent getContentNode(CarriedItem<ItemTemplate> ref) {
		return lbSource;
	}

}
