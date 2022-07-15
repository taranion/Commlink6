package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.items.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class GearPage extends Page {

	private final static Logger logger = System.getLogger(GearPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private GearSection secElectro;
	private GearSection secOther;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private GenericDescriptionVBox descBox ;

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
		
		descBox = new GenericDescriptionVBox<>((r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
	}
	
	//-------------------------------------------------------------------
	private void initElectro() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.ELECTRONICS); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.ELECTRONICS); 
		secElectro = new GearSection(
				ResourceI18N.get(RES, "page.gear.section.electro"),selectFilter, showFilter
				);
		secElectro.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secElectro, 4);
		FlexGridPane.setMinHeight(secElectro, 6);
		FlexGridPane.setMediumWidth(secElectro, 6);
		FlexGridPane.setMediumHeight(secElectro, 9);
		FlexGridPane.setMaxWidth(secElectro, 8);
		FlexGridPane.setMaxHeight(secElectro, 12);
	}
	
	//-------------------------------------------------------------------
	private void initOther() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY); 
		secOther = new GearSection(
				ResourceI18N.get(RES, "page.gear.section.other"), selectFilter, showFilter
				);
		secOther.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secOther, 4);
		FlexGridPane.setMinHeight(secOther, 6);
		FlexGridPane.setMediumWidth(secOther, 5);
		FlexGridPane.setMediumHeight(secOther, 9);
		FlexGridPane.setMaxWidth(secOther, 5);
		FlexGridPane.setMaxHeight(secOther, 12);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secOther, secElectro);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secOther.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secElectro.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItem n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n));
			layout.setTitle(n.getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		secElectro.updateController(ctrl);
		secOther.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secElectro.refresh();
		secOther.refresh();
	}

}
