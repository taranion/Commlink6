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
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection.Type;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemUtil;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class CombatPage extends Page {

	private final static Logger logger = System.getLogger(CombatPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private GearSection secRanged;
	private GearSection secMelee;
	private GearSection secArmor;
	private GearSection secAmmo;
	protected CombatSection secCombat;

	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public CombatPage() {
		super(ResourceI18N.get(RES, "page.combat.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initRangedWeapons();
		initMeleeWeapons();
		initArmor();
		initCombat();
		initAmmunition();
	}
	
	//-------------------------------------------------------------------
	private void initCombat() {
		secCombat = new CombatSection(Type.PHYSICAL);
		FlexGridPane.setMinWidth(secCombat, 6);
		FlexGridPane.setMinHeight(secCombat, 6);
		FlexGridPane.setMediumWidth(secCombat, 8);
	}
	
	//-------------------------------------------------------------------
	private void initRangedWeapons() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_FIREARMS, ItemType.WEAPON_RANGED, ItemType.WEAPON_SPECIAL); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_FIREARMS, ItemType.WEAPON_RANGED, ItemType.WEAPON_SPECIAL); 
		secRanged = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.ranged"), selectFilter, showFilter
				);
		secRanged.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secRanged, 4);
		FlexGridPane.setMinHeight(secRanged, 6);
		FlexGridPane.setMediumWidth(secRanged, 5);
		FlexGridPane.setMediumHeight(secRanged, 6);
		FlexGridPane.setMaxWidth(secRanged, 5);
		FlexGridPane.setMaxHeight(secRanged, 7);
	}
	
	//-------------------------------------------------------------------
	private void initMeleeWeapons() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_CLOSE_COMBAT); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.WEAPON_CLOSE_COMBAT); 
		secMelee = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.melee"), selectFilter, showFilter
				);
		secMelee.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMelee, 4);
		FlexGridPane.setMinHeight(secMelee, 6);
		FlexGridPane.setMediumWidth(secMelee, 4);
		FlexGridPane.setMediumHeight(secMelee, 5);
		FlexGridPane.setMaxWidth(secMelee, 4);
		FlexGridPane.setMaxHeight(secMelee, 6);
	}
	
	//-------------------------------------------------------------------
	private void initArmor() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.ARMOR); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.ARMOR); 
		secArmor = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.armor"), selectFilter, showFilter
				);
		secArmor.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secArmor, 4);
		FlexGridPane.setMinHeight(secArmor, 6);
		FlexGridPane.setMediumWidth(secArmor, 4);
		FlexGridPane.setMediumHeight(secArmor, 5);
		FlexGridPane.setMaxWidth(secArmor, 4);
		FlexGridPane.setMaxHeight(secArmor, 6);
	}
	
	//-------------------------------------------------------------------
	private void initAmmunition() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.AMMUNITION); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = ItemUtil.AMMUNITION_FILTER;
		secAmmo = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.ammo"), selectFilter, showFilter
				);
		secAmmo.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secAmmo, 4);
		FlexGridPane.setMinHeight(secAmmo, 6);
		FlexGridPane.setMediumWidth(secAmmo, 5);
		FlexGridPane.setMediumHeight(secAmmo, 6);
		FlexGridPane.setMaxWidth(secAmmo, 5);
		FlexGridPane.setMaxHeight(secAmmo, 7);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secRanged, secMelee, secArmor, secCombat,secAmmo);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secRanged.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secMelee.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secArmor.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secAmmo.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
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
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		this.ctrl = ctrl;
		secRanged.updateController(ctrl);
		secMelee.updateController(ctrl);
		secArmor.updateController(ctrl);
		secAmmo.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secRanged.refresh();
		secMelee.refresh();
		secArmor.refresh();
		secAmmo.refresh();
		secCombat.setData(ctrl.getModel());
	}

}
