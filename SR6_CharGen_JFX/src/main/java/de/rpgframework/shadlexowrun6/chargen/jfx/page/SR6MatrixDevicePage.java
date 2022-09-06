package de.rpgframework.shadlexowrun6.chargen.jfx.page;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.section.IconSection;
import de.rpgframework.shadowrun.ShadowrunAction.Category;
import de.rpgframework.shadowrun.chargen.jfx.pages.AMatrixDevicePage;
import de.rpgframework.shadowrun.chargen.jfx.section.PersonaSection;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.geometry.VPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6MatrixDevicePage extends AMatrixDevicePage<ItemTemplate, ItemHook, AvailableSlot> {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private final static Logger logger = System.getLogger(SR6MatrixDevicePage.class.getPackageName());
	
	private VBox bxColumn1;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDevice;
	private VBox bxColumn2;

	//-------------------------------------------------------------------
	public SR6MatrixDevicePage() {
		super();
		initColumn1();
		initColumn2();
		initActions();
		initPrivateLayout();
		refresh();
	}

	//-------------------------------------------------------------------
	private void initColumn1() {
		cbDevice = new ChoiceBox<>();
		cbDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			@Override
			public String toString(CarriedItem<ItemTemplate> item) {
				return (item==null)?"?": item.getNameWithoutRating(Locale.getDefault());
			}
			@Override
			public CarriedItem<ItemTemplate> fromString(String arg0) { return null;}
		});
		//cbDevice.getItems().add(selectedItem);
		cbDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> deviceChanged(n));
		
		Label hdDevice = new Label(ResourceI18N.get(RES, "page.matrix.heading.device"));
		hdDevice.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox bxDevice = new HBox(10, hdDevice, cbDevice);
		
		// Active Programs
		secPrograms = new IconSection<ItemTemplate, CarriedItem<ItemTemplate>>(item -> resolveIcon(item), ResourceI18N.get(RES, "page.matrix.section.activePrograms"));			
		secPrograms.setOnAddAction(ev -> {});
		((IconSection<ItemTemplate, CarriedItem<ItemTemplate>>)secPrograms).getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			description.setData(n);
		});

		((IconSection<ItemTemplate, CarriedItem<ItemTemplate>>)secPrograms).showHelpForProperty().addListener( (ov,o,n) -> description.setData(n));
		
		Predicate<ItemTemplate> selectFilter = null;
		Predicate<CarriedItem<ItemTemplate>> showFilter = null;
		secAccessories = new GearSection(ResourceI18N.get(RES, "page.matrix.section.accessories"), CarryMode.EMBEDDED, selectFilter, showFilter);
		
		bxColumn1 = new VBox(20, bxDevice, secPrograms, secAccessories);
		//bxColumn1.setPrefWidth(400);
		secAccessories.showHelpForProperty().addListener( (ov,o,n) -> {
			description.setData(n);
		});
	}

	//-------------------------------------------------------------------
	private void initColumn2() {
		Label hdDefRating = new Label(ResourceI18N.get(RES, "page.matrix.section.persona.defenseRating"));
		Label hdDefPool   = new Label(ResourceI18N.get(RES, "page.matrix.section.persona.defensePool"));
		GridPane ruleSpec = new GridPane();
		ruleSpec.setHgap(10);
		ruleSpec.setVgap(5);
		ruleSpec.add(hdDefRating, 0, 0);
		ruleSpec.add(hdDefPool  , 0, 1);
		
		secPersona = new PersonaSection(ResourceI18N.get(RES, "page.matrix.section.persona"));
		secPersona.setRuleSpecificNode(ruleSpec);
		
		bxColumn2 = new VBox(20, secPersona);		
	}
	
	private void initActions() {
		secActions.setAll( 
				Shadowrun6Core.getItemList(Shadowrun6Action.class)
				.stream()
				.filter( act -> act.getCategory()==Category.MATRIX)
				.sorted(new Comparator<Shadowrun6Action>() {
					public int compare(Shadowrun6Action o1, Shadowrun6Action o2) {
						return o1.getName().compareTo(o2.getName());
					}
				})
				.collect(Collectors.toList())
				);		
	}

	//-------------------------------------------------------------------
	private void initPrivateLayout() {
//		FlexGridPane.setMinWidth(bxColumn1, 5);
//		FlexGridPane.setMinHeight(bxColumn1, 10);
//		
//		FlexGridPane.setMinWidth(ivDeepDive, 5);
//		FlexGridPane.setMinHeight(ivDeepDive, 10);
//		
//		FlexGridPane.setMinWidth(bxColumn2, 5);
//		FlexGridPane.setMinHeight(bxColumn2, 10);
//		
//		FlexGridPane flex = new FlexGridPane();
//		flex.getChildren().addAll(bxColumn1, ivDeepDive, bxColumn2);
//		content.setContent(flex);
		grid.add(bxColumn1, 0, 0, 1,2);
		grid.add(ivDeepDive, 1, 0);
		grid.add(bxColumn2, 2, 0);
		grid.add(secActions, 1, 1, 2,1);
		GridPane.setValignment(ivDeepDive, VPos.TOP);
		
		//HBox inflex = new HBox(20, bxColumn1, ivDeepDive, bxColumn2);
		content.setContent(grid);
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
		((GearSection)secAccessories).updateController(ctrl);
		if (cbDevice.getValue()==null && !matrixDevices.isEmpty())
			cbDevice.setValue(matrixDevices.get(0));
//		secDevices.updateController(ctrl);
//		secSoftware.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	private void deviceChanged(CarriedItem<ItemTemplate> device) {
		logger.log(Level.DEBUG, "Device changed to "+device);
		
		AvailableSlot slot = device.getSlot(ItemHook.SOFTWARE);
		logger.log(Level.DEBUG, "Slot = "+slot);
		
		if (slot!=null) {
			secPrograms.setSlots((int)slot.getCapacity());
			logger.log(Level.WARNING, "Embedded in slot = "+slot.getAllEmbeddedItems());
			secPrograms.getItems().setAll(slot.getAllEmbeddedItems());
		} else
			secPrograms.setSlots(0);
		
		AvailableSlot accessories = device.getSlot(ItemHook.ELECTRONIC_ACCESSORY);
		if (accessories!=null) {
			secAccessories.setData( accessories.getAllEmbeddedItems());
			secAccessories.setVisible(true);
		} else {
			// No external accessories
			secAccessories.setData(new ArrayList<>());
			secAccessories.setVisible(false);
		}
		
	}

	//--------------------------------------------------------------------
	public void refresh()  {
		logger.log(Level.INFO, "refresh");
		secPrograms.refresh();
		secAccessories.refresh();
		secPersona.refresh();
		
		secActions.setAll( 
				Shadowrun6Core.getItemList(Shadowrun6Action.class)
				.stream()
				.filter( act -> act.getCategory()==Category.MATRIX)
				.sorted(new Comparator<Shadowrun6Action>() {
					public int compare(Shadowrun6Action o1, Shadowrun6Action o2) {
						return o1.getName().compareTo(o2.getName());
					}
				})
				.collect(Collectors.toList())
				);
	}

	//-------------------------------------------------------------------
	private Image resolveIcon(ItemTemplate item) {
		String file = "icons/"+item.getId()+".png";
		logger.log(Level.INFO, "Resolve "+file);
		InputStream is = Shadowrun6DataPlugin.class.getResourceAsStream(file);
		logger.log(Level.INFO, "Stream = "+is);
		if (is==null) {
			logger.log(Level.ERROR, "Missing icon for program: "+file);
			return null;
		} else {
			return new Image(is);
		}
	}
	
}
