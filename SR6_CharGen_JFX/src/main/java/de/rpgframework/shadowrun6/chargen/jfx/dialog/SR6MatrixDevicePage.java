package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Section;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.section.IconSection;
import de.rpgframework.shadowrun.chargen.jfx.pages.AMatrixDevicePage;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6MatrixDevicePage extends AMatrixDevicePage<ItemTemplate, ItemHook, AvailableSlot> {

	private final static Logger logger = System.getLogger(SR6MatrixDevicePage.class.getPackageName());
	
	private VBox bxColumn1;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDevice;
	private VBox bxColumn2;

	//-------------------------------------------------------------------
	public SR6MatrixDevicePage() {
		super();
		initColumn1();
		initColumn2();
		initPrivateLayout();
	}

	//-------------------------------------------------------------------
	private void initColumn1() {
		cbDevice = new ChoiceBox<>();
		//cbDevice.getItems().add(selectedItem);
		
		Label hdDevice = new Label("Device");
		hdDevice.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox bxDevice = new HBox(10, hdDevice, cbDevice);
		
		// Active Programs
		Function<ItemTemplate, Image> iconResolver = null;
		secPrograms = new IconSection<ItemTemplate>(iconResolver, ResourceI18N.get(UI, "heading.activePrograms"), new Label("Empty"));
		
		Predicate<ItemTemplate> selectFilter = null;
		Predicate<CarriedItem<ItemTemplate>> showFilter = null;
		secAccessories = new GearSection(ResourceI18N.get(UI, "heading.accessories"), CarryMode.EMBEDDED, selectFilter, showFilter);
		
		bxColumn1 = new VBox(20, bxDevice, secPrograms, secAccessories);
	}

	//-------------------------------------------------------------------
	private void initColumn2() {
		secPersona = new Section("Persona", new Label("Content"));
		
		bxColumn2 = new VBox(20, secPersona);
	}

	//-------------------------------------------------------------------
	private void initPrivateLayout() {
		FlexGridPane.setMinWidth(bxColumn1, 5);
		FlexGridPane.setMinHeight(bxColumn1, 10);
		
		FlexGridPane.setMinWidth(ivDeepDive, 5);
		FlexGridPane.setMinHeight(ivDeepDive, 10);
		
		FlexGridPane.setMinWidth(bxColumn2, 5);
		FlexGridPane.setMinHeight(bxColumn2, 10);
		
		//flex.getChildren().addAll(bxColumn1, ivDeepDive, bxColumn2);
		
		HBox inflex = new HBox(20, bxColumn1, ivDeepDive, bxColumn2);
		content.setContent(inflex);
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		super.setController(ctrl);

		List<CarriedItem<ItemTemplate>> matrixDevices = ctrl.getModel().getCarriedItems()
			.stream()
			.filter(ci -> ci.hasFlag(ItemTemplate.FLAG_MATRIX_DEVICE))
			.collect(Collectors.toList());
		cbDevice.getItems().setAll(matrixDevices);
//		secAccessories.updateController(ctrl);
//		secDevices.updateController(ctrl);
//		secSoftware.updateController(ctrl);
		refresh();
	}
}
