package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.items.ItemType;
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

	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private GenericDescriptionVBox descBox ;

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
		initAmmunition();
		
		descBox = new GenericDescriptionVBox<>((r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
	}
	
	//-------------------------------------------------------------------
	private void initRangedWeapons() {
		secRanged = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.ranged"),
				ItemType.WEAPON_FIREARMS, ItemType.WEAPON_RANGED, ItemType.WEAPON_SPECIAL
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
		secMelee = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.melee"),
				ItemType.WEAPON_CLOSE_COMBAT
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
		secArmor = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.armor"),
				ItemType.ARMOR
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
		secAmmo = new GearSection(
				ResourceI18N.get(RES, "page.combat.section.ammo"),
				ItemType.AMMUNITION
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
		flex.getChildren().addAll(secRanged, secMelee, secArmor, secAmmo);
		
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
	private void showDescription(ComplexDataItemValue n) {
		if (n==null) {
			layout.setOptional(new Label("Langer Text"));
		} else {
			descBox.setData(n.getModifyable());
			layout.setOptional(descBox);
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
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
	}

}
