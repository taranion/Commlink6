package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.WorldType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.MartialArtsSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.fxml.FXML;

/**
 * Controller for CombatPage.fxml.
 */
public class CombatPageController {

	private final static Logger logger = System.getLogger(CombatPageController.class.getPackageName());

	@FXML private CombatSection secCombat;
	@FXML private GearSection secRanged;
	@FXML private GearSection secMelee;
	@FXML private GearSection secArmor;
	@FXML private GearSection secAmmo;
	@FXML private MartialArtsSection secMartial;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		if (secCombat != null) {
			secCombat.setWorldType(WorldType.PHYSICAL);
		}

		if (secRanged != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_FIREARMS, ItemType.WEAPON_RANGED, ItemType.WEAPON_SPECIAL);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_FIREARMS, ItemType.WEAPON_RANGED, ItemType.WEAPON_SPECIAL);
			secRanged.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.combat.section.ranged"));
			secRanged.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		if (secMelee != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_CLOSE_COMBAT);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_CLOSE_COMBAT);
			secMelee.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.combat.section.melee"));
			secMelee.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		if (secArmor != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.ARMOR);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.ARMOR);
			secArmor.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.combat.section.armor"));
			secArmor.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		if (secAmmo != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.AMMUNITION);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.AMMUNITION);
			secAmmo.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.combat.section.ammo"));
			secAmmo.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secCombat != null) secCombat.updateController(ctrl);
		if (secRanged != null) secRanged.updateController(ctrl);
		if (secMelee != null) secMelee.updateController(ctrl);
		if (secArmor != null) secArmor.updateController(ctrl);
		if (secAmmo != null) secAmmo.updateController(ctrl);
		if (secMartial != null) secMartial.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secCombat != null) secCombat.refresh();
		if (secRanged != null) secRanged.refresh();
		if (secMelee != null) secMelee.refresh();
		if (secArmor != null) secArmor.refresh();
		if (secAmmo != null) secAmmo.refresh();
		if (secMartial != null) secMartial.refresh();
	}

}
