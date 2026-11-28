package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.fxml.FXML;

/**
 * Controller for GearPage.fxml.
 */
public class GearPageController {

	private final static Logger logger = System.getLogger(GearPageController.class.getPackageName());

	@FXML private GearSection secOther;
	@FXML private GearSection secElectro;
	@FXML private GearSection secAlchemical;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		// Other
		if (secOther != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.TOOLS, ItemType.BIOLOGY, ItemType.PACK, ItemType.CHEMICALS, ItemType.NANOWARE);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.TOOLS, ItemType.BIOLOGY, ItemType.PACK, ItemType.CHEMICALS, ItemType.NANOWARE);
			secOther.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.gear.section.other"));
			secOther.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		// Electronics
		if (secElectro != null) {
			Predicate<ItemTemplate> selectFilter = (item) -> {
				if (item.getItemType(CarryMode.CARRIED) != ItemType.ELECTRONICS) return false;
				ItemSubType sub = item.getItemSubtype(CarryMode.CARRIED);
				switch (sub) {
				case COMMLINK:
				case CYBERDECK:
				case DATATERM:
				case CYBERTERM:
				case COMMNODE:
				case RIGGER_CONSOLE:
				case TAC_NET:
					return false;
				default:
					return true;
				}
			};

			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.ELECTRONICS) {
				public boolean test(CarriedItem<ItemTemplate> item) {
					boolean isElectronic = super.test(item);
					if (!isElectronic) return false;
					ItemSubType foundType = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
					switch (foundType) {
					case COMMLINK:
					case CYBERDECK:
					case RIGGER_CONSOLE:
					case TAC_NET:
						return false;
					default:
						return true;
					}
				}
			};
			secElectro.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.gear.section.electro"));
			secElectro.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}

		// Alchemical / Magical gear
		if (secAlchemical != null) {
			Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.MAGICAL);
			Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.MAGICAL);
			secAlchemical.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.gear.section.magical"));
			secAlchemical.setFilter(CarryMode.CARRIED, selectFilter, showFilter);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secOther != null) secOther.updateController(ctrl);
		if (secElectro != null) secElectro.updateController(ctrl);
		if (secAlchemical != null) secAlchemical.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secOther != null) secOther.refresh();
		if (secElectro != null) secElectro.refresh();
		if (secAlchemical != null) secAlchemical.refresh();
	}

}
