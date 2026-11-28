package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.EssenceSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import javafx.fxml.FXML;

/**
 * Controller for CyberwarePage.fxml.
 */
public class CyberwarePageController {

	private final static Logger logger = System.getLogger(CyberwarePageController.class.getPackageName());

	@FXML private EssenceSection secTrans;
	@FXML private GearSection secCyber;
	@FXML private GearSection secBio;
	@FXML private GearSection secNano;
	@FXML private GearSection secGene;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		// Cyberware
		Predicate<ItemTemplate> cyberSelect = new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CYBERWARE, ItemType.ELECTRONICS);
		Predicate<CarriedItem<ItemTemplate>> cyberShow = item -> {
			if (item.getAsObject(SR6ItemAttribute.ITEMTYPE) == null) return false;
			ItemType type = item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
			if (type == ItemType.CYBERWARE && item.getCarryMode() == CarryMode.IMPLANTED) return true;
			if (type == ItemType.ELECTRONICS && item.getCarryMode() == CarryMode.IMPLANTED) return true;
			return item.getVariant() != null && item.getVariant().getEquipMode() == SR6VariantMode.BODYWARE;
		};
		if (secCyber != null) {
			secCyber.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.augmentation.section.cyberware"));
			secCyber.setFilter(CarryMode.IMPLANTED, cyberSelect, cyberShow);
		}

		// Bioware
		if (secBio != null) {
			secBio.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.augmentation.section.bioware"));
			secBio.setFilter(CarryMode.IMPLANTED,
					new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.BIOWARE),
					new CarriedItemItemTypeFilter(CarryMode.IMPLANTED, ItemType.BIOWARE));
		}

		// Nanoware
		if (secNano != null) {
			secNano.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.augmentation.section.nanoware"));
			secNano.setFilter(CarryMode.IMPLANTED,
					new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.NANOWARE),
					new CarriedItemItemTypeFilter(CarryMode.IMPLANTED, ItemType.NANOWARE));
		}

		// Geneware
		if (secGene != null) {
			secGene.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.augmentation.section.geneware"));
			secGene.setFilter(CarryMode.IMPLANTED,
					new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.GENEWARE),
					new CarriedItemItemTypeFilter(CarryMode.IMPLANTED, ItemType.GENEWARE));
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secTrans != null) secTrans.updateController(ctrl);
		if (secCyber != null) secCyber.updateController(ctrl);
		if (secBio != null) secBio.updateController(ctrl);
		if (secNano != null) secNano.updateController(ctrl);
		if (secGene != null) secGene.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secTrans != null) secTrans.refresh();
		if (secCyber != null) secCyber.refresh();
		if (secBio != null) secBio.refresh();
		if (secNano != null) secNano.refresh();
		if (secGene != null) secGene.refresh();
	}

}
