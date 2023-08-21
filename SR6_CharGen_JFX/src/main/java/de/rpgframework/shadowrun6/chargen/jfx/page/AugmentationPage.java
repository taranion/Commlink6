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
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.EssenceSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class AugmentationPage extends Page {

	private final static Logger logger = System.getLogger(AugmentationPage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private EssenceSection secTrans;
	private GearSection secCyber;
	private GearSection secBio;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public AugmentationPage() {
		super(ResourceI18N.get(RES, "page.augmentation.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initEssence();
		initCyberware();
		initBioware();
	}

	//-------------------------------------------------------------------
	private void initCyberware() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CYBERWARE, ItemType.ELECTRONICS);
		Predicate<CarriedItem<ItemTemplate>> showFilter = item -> {
			if (item.getAsObject(SR6ItemAttribute.ITEMTYPE)==null) {
				logger.log(Level.WARNING, "No ITEMTYPE in "+item.getKey());
				return false;
			}
			ItemType type = item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
			// All items that directly classify as CYBERWARE
			if (type==ItemType.CYBERWARE && item.getCarryMode()==CarryMode.IMPLANTED) return true;
			if (type==ItemType.ELECTRONICS && item.getCarryMode()==CarryMode.IMPLANTED) return true;
			// or that are usually an accessory and now come in a BODYWARE variant
			//if (type==ItemType.ACCESSORY)
			return item.getVariant()!=null && item.getVariant().getEquipMode()==SR6VariantMode.BODYWARE;
		};

		secCyber = new GearSection(ResourceI18N.get(RES, "page.augmentation.section.cyberware"), CarryMode.IMPLANTED, selectFilter, showFilter);
		secCyber.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCyber, 4);
		FlexGridPane.setMinHeight(secCyber, 6);
		FlexGridPane.setMediumWidth(secCyber, 5);
		FlexGridPane.setMediumHeight(secCyber, 6);
		FlexGridPane.setMaxWidth(secCyber, 5);
		FlexGridPane.setMaxHeight(secCyber, 9);
	}

	//-------------------------------------------------------------------
	private void initBioware() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.BIOWARE);
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.IMPLANTED, ItemType.BIOWARE);
		secBio = new GearSection(ResourceI18N.get(RES, "page.augmentation.section.bioware"), CarryMode.IMPLANTED, selectFilter, showFilter);
		secBio.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secBio, 4);
		FlexGridPane.setMinHeight(secBio, 6);
		FlexGridPane.setMediumWidth(secBio, 5);
		FlexGridPane.setMediumHeight(secBio, 6);
		FlexGridPane.setMaxWidth(secBio, 5);
		FlexGridPane.setMaxHeight(secBio, 9);
	}

	//-------------------------------------------------------------------
	private void initEssence() {
		secTrans = new EssenceSection(ResourceI18N.get(RES, "page.augmentation.section.essence"));
		secTrans.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secTrans, 4);
		FlexGridPane.setMinHeight(secTrans, 4);
		FlexGridPane.setMediumWidth(secTrans, 4);
		FlexGridPane.setMediumHeight(secTrans, 4);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secTrans, secCyber, secBio);

		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secCyber.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secBio  .showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secTrans.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

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

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItem n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()), n));
			layout.setTitle(n.getName());
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");

		this.ctrl = ctrl;
		secCyber.updateController(ctrl);
		secBio  .updateController(ctrl);
		secTrans.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secCyber.refresh();
		secBio  .refresh();
		secTrans.refresh();
	}

}
