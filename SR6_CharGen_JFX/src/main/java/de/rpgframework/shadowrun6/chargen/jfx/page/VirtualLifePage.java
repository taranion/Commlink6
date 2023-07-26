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
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SparkSection;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class VirtualLifePage extends Page {

	private final static Logger logger = System.getLogger(VirtualLifePage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private SparkSection secTrans;
	private GearSection secCodeMods;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public VirtualLifePage() {
		super(ResourceI18N.get(RES, "page.virtuallife.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initEssence();
		initCodemods();
	}

	//-------------------------------------------------------------------
	private void initCodemods() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CODEMODS);
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.IMPLANTED, ItemType.CODEMODS);

		secCodeMods = new GearSection(ResourceI18N.get(RES, "page.virtuallife.section.codemods"), CarryMode.IMPLANTED, selectFilter, showFilter);
		secCodeMods.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCodeMods, 4);
		FlexGridPane.setMinHeight(secCodeMods, 6);
		FlexGridPane.setMediumWidth(secCodeMods, 5);
		FlexGridPane.setMediumHeight(secCodeMods, 6);
		FlexGridPane.setMaxWidth(secCodeMods, 5);
		FlexGridPane.setMaxHeight(secCodeMods, 9);
	}


	//-------------------------------------------------------------------
	private void initEssence() {
		secTrans = new SparkSection();
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
		flex.getChildren().addAll(secTrans, secCodeMods);

		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secCodeMods.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secTrans   .showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
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
		if (ctrl==null)
			throw new NullPointerException("controller is null");

		this.ctrl = ctrl;
		secCodeMods.updateController(ctrl);
		secTrans.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secCodeMods.refresh();
		secTrans.refresh();
	}

}
