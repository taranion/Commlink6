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
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

/**
 * @author prelle
 *
 */
public class GearPage extends Page {

	private final static Logger logger = System.getLogger(GearPage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private GearSection secElectro;
	private GearSection secOther;
	private GearSection secAlchemical;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public GearPage() {
		super(ResourceI18N.get(RES, "page.gear.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initOther();
		initElectro();
		initAlchemical();
	}

	//-------------------------------------------------------------------
	private void initElectro() {
		Predicate<ItemTemplate> selectFilter = (item) -> {
			if (item.getItemType(CarryMode.CARRIED)!=ItemType.ELECTRONICS) return false;
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
		secElectro = new GearSection(
				ResourceI18N.get(RES, "page.gear.section.electro"),selectFilter, showFilter
				);
		secElectro.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secElectro, 4);
		FlexGridPane.setMinHeight(secElectro, 6);
		FlexGridPane.setMediumWidth(secElectro, 5);
		FlexGridPane.setMediumHeight(secElectro, 9);
//		FlexGridPane.setMaxWidth(secElectro, 8);
//		FlexGridPane.setMaxHeight(secElectro, 9);
	}

	//-------------------------------------------------------------------
	private void initOther() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.TOOLS, ItemType.BIOLOGY, ItemType.PACK, ItemType.CHEMICALS, ItemType.NANOWARE);
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.TOOLS, ItemType.BIOLOGY, ItemType.PACK, ItemType.CHEMICALS, ItemType.NANOWARE);
		secOther = new GearSection(
				ResourceI18N.get(RES, "page.gear.section.other"), selectFilter, showFilter
				);
		secOther.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secOther, 4);
		FlexGridPane.setMinHeight(secOther, 6);
		FlexGridPane.setMediumWidth(secOther, 5);
		FlexGridPane.setMediumHeight(secOther, 9);
		FlexGridPane.setMaxWidth(secOther, 5);
		FlexGridPane.setMaxHeight(secOther, 9);
	}

	//-------------------------------------------------------------------
	private void initAlchemical() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.MAGICAL);
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.MAGICAL);
		secAlchemical = new GearSection(
				ResourceI18N.get(RES, "page.gear.section.magical"), selectFilter, showFilter
				);
		secAlchemical.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secAlchemical, 4);
		FlexGridPane.setMinHeight(secAlchemical, 4);
		FlexGridPane.setMediumWidth(secAlchemical, 5);
		FlexGridPane.setMediumHeight(secAlchemical, 6);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secOther, secElectro, secAlchemical);
		ScrollPane scroll = new ScrollPane(flex);
		scroll.setFitToWidth(true);

		layout = new OptionalNodePane(scroll, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secOther.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secElectro.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secAlchemical.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
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
//
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
		secElectro.updateController(ctrl);
		secOther.updateController(ctrl);
		secAlchemical.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secElectro.refresh();
		secOther.refresh();
		secAlchemical.refresh();
	}

}
