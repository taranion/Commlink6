package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.RiggerConsoleSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SignatureManeuverSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.fxml.FXML;

/**
 * Controller for VehiclesPage.fxml.
 */
public class VehiclesPageController {

	private final static Logger logger = System.getLogger(VehiclesPageController.class.getPackageName());

	@FXML private GearSection secVehicles;
	@FXML private GearSection secDrones;
	@FXML private RiggerConsoleSection secRigging;
	@FXML private SignatureManeuverSection secSignature;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		if (secVehicles != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.VEHICLES);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.VEHICLES);
			secVehicles.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.vehicles.section.vehicles"));
			secVehicles.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		if (secDrones != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.droneTypes());
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.droneTypes());
			secDrones.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.vehicles.section.drones"));
			secDrones.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secVehicles != null) secVehicles.updateController(ctrl);
		if (secDrones != null) secDrones.updateController(ctrl);
		if (secRigging != null) secRigging.updateController(ctrl);
		if (secSignature != null) secSignature.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secVehicles != null) secVehicles.refresh();
		if (secDrones != null) secDrones.refresh();
		if (secRigging != null) secRigging.refresh();
		if (secSignature != null) secSignature.refresh();
	}

}
