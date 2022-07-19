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

import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.AttributeSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SkillSection;
import de.rpgframework.shadowrun6.items.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MatrixPage extends Page {

	private final static Logger logger = System.getLogger(MatrixPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private GearSection secDevices, secSoftware;
	private MetamagicOrEchoSection secMeta;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private SR6CharacterController ctrl;
	private Predicate<CarriedItem<ItemTemplate>> filter;

	//-------------------------------------------------------------------
	public MatrixPage() {
		super(ResourceI18N.get(RES, "page.matrix.title"));
		filter = carried -> carried.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()==ItemType.ELECTRONICS && ItemSubType.matrixDevices().contains(carried.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue());
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initDevices();
		initMetamagic();
		initSoftware();
	}
	
	//-------------------------------------------------------------------
	private void initDevices() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.ELECTRONICS); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.ELECTRONICS); 
		secDevices = new GearSection(ResourceI18N.get(RES, "page.matrix.section.devices"), selectFilter, showFilter);
		secDevices.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secDevices, 4);
		FlexGridPane.setMinHeight(secDevices, 6);
		FlexGridPane.setMediumWidth(secDevices, 6);
		FlexGridPane.setMediumHeight(secDevices, 6);
	}
	
	//-------------------------------------------------------------------
	private void initMetamagic() {
		secMeta = new MetamagicOrEchoSection(
				ResourceI18N.get(RES, "page.matrix.section.echoes"),
				r -> Shadowrun6Tools.getRequirementString((Requirement)r, Locale.getDefault()), 
				MetamagicOrEcho.Type.ECHO
				);
		secMeta.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMeta, 4);
		FlexGridPane.setMinHeight(secMeta, 6);
		FlexGridPane.setMediumWidth(secMeta, 5);
		FlexGridPane.setMediumHeight(secMeta, 8);
	}
	
	//-------------------------------------------------------------------
	private void initSoftware() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.EMBEDDED, ItemType.SOFTWARE); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SOFTWARE); 
		secSoftware = new GearSection(ResourceI18N.get(RES, "page.matrix.section.software"), selectFilter, showFilter);
		secSoftware.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secSoftware, 4);
		FlexGridPane.setMinHeight(secSoftware, 6);
		FlexGridPane.setMediumWidth(secSoftware, 6);
		FlexGridPane.setMediumHeight(secSoftware, 6);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secDevices,secMeta, secSoftware);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secDevices.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secSoftware.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secMeta.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
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
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		this.ctrl = ctrl;
		secMeta.updateController(ctrl);
		secDevices.updateController(ctrl);
		secSoftware.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secDevices.refresh();
		secMeta.refresh();
		secSoftware.refresh();
	}

}
