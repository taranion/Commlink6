package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class VehiclePage extends Page {

	private final static Logger logger = System.getLogger(VehiclePage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private GearSection secVehicles;
	private GearSection secDrones;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public VehiclePage() {
		super(ResourceI18N.get(RES, "page.vehicles.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initDrones();
		initVehicles();
	}
	
	//-------------------------------------------------------------------
	private void initVehicles() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.VEHICLES); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.VEHICLES); 
		secVehicles = new GearSection(
				ResourceI18N.get(RES, "page.vehicles.section.vehicles"), selectFilter, showFilter
				);
		secVehicles.setId("vehicles");
		secVehicles.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secVehicles, 4);
		FlexGridPane.setMinHeight(secVehicles, 6);
		FlexGridPane.setMediumWidth(secVehicles, 6);
		FlexGridPane.setMediumHeight(secVehicles, 9);
		FlexGridPane.setMaxWidth(secVehicles, 6);
		FlexGridPane.setMaxHeight(secVehicles, 12);
	}
	
	//-------------------------------------------------------------------
	private void initDrones() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.droneTypes()); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.droneTypes()); 
		secDrones = new GearSection(
				ResourceI18N.get(RES, "page.vehicles.section.drones"), selectFilter, showFilter
				);
		secDrones.setId("drones");
		secDrones.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secDrones, 4);
		FlexGridPane.setMinHeight(secDrones, 6);
		FlexGridPane.setMediumWidth(secDrones, 6);
		FlexGridPane.setMediumHeight(secDrones, 9);
		FlexGridPane.setMaxWidth(secDrones, 6);
		FlexGridPane.setMaxHeight(secDrones, 12);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secVehicles,secDrones);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secDrones.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secVehicles.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

//	//-------------------------------------------------------------------
//	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
//		logger.log(Level.INFO, "Show description "+n);
//		if (n==null) {
//			layout.setOptional(null);
//		} else {
//			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
//			layout.setTitle(n.getModifyable().getName());
//		}
//	}

	//-------------------------------------------------------------------
	private void showDescription(CarriedItem<ItemTemplate> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new CarriedItemDescriptionPane( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), ctrl, n));
			layout.setTitle(n.getModifyable().getName());
		}
	}

//	//-------------------------------------------------------------------
//	private void showDescription(ComplexDataItem n) {
//		logger.log(Level.INFO, "Show description "+n);
//		if (n==null) {
//			layout.setOptional(null);
//		} else {
//			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n));
//			layout.setTitle(n.getName());
//		}
//	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		this.ctrl = ctrl;
		secVehicles.updateController(ctrl);
		secDrones.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secVehicles.refresh();
		secDrones.refresh();
	}

}
