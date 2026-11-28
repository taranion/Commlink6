package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.WorldType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.AccessoriesSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.ActiveProgramsSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6PersonaSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SoftwareLibrarySection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemSubTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.fxml.FXML;

/**
 * Controller for MatrixPage.fxml.
 */
public class MatrixPageController {

	private final static Logger logger = System.getLogger(MatrixPageController.class.getPackageName());

	@FXML private SR6PersonaSection secPersona;
	@FXML private GearSection secDevices;
	@FXML private AccessoriesSection secAccessories;
	@FXML private ActiveProgramsSection secPrograms;
	@FXML private SoftwareLibrarySection secSoftware;
	@FXML private CombatSection secCombat;

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		// Devices
		Predicate<ItemTemplate> selectFilter = new ItemSubTypeFilter(CarryMode.CARRIED, ItemSubType.matrixDevices());
		if (secDevices != null) {
			secDevices.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.matrix.section.devices"));
			secDevices.setFilter(CarryMode.CARRIED, selectFilter, ItemUtil.MATRIXDEVICES_FILTER);
		}

		// Programs
		if (secPrograms != null) {
			Predicate<ItemTemplate> programFilter = (c) -> c.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() == ItemType.SOFTWARE;
			secPrograms.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.matrix.section.activePrograms"));
			secPrograms.setFilter(CarryMode.EMBEDDED, programFilter, null);
		}

		// Accessories
		if (secAccessories != null) {
			Predicate<ItemTemplate> accSelect = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY);
			Predicate<CarriedItem<ItemTemplate>> accShow = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY);
			secAccessories.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.matrix.section.accessories"));
			secAccessories.setFilter(CarryMode.EMBEDDED, accSelect, accShow);
		}

		if (secCombat != null) {
			secCombat.setWorldType(WorldType.MATRIX);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		if (secPersona != null) secPersona.updateController(ctrl);
		if (secDevices != null) secDevices.updateController(ctrl);
		if (secAccessories != null) secAccessories.updateController(ctrl);
		if (secPrograms != null) secPrograms.updateController(ctrl);
		if (secSoftware != null) secSoftware.updateController(ctrl);
		if (secCombat != null && ctrl != null && ctrl.getModel() != null) secCombat.setData(ctrl.getModel());
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secPersona != null) secPersona.refresh();
		if (secDevices != null) secDevices.refresh();
		if (secAccessories != null) secAccessories.refresh();
		if (secPrograms != null) secPrograms.refresh();
		if (secSoftware != null) secSoftware.refresh();
		if (secCombat != null && control != null && control.getModel() != null) secCombat.setData(control.getModel());
	}

}
