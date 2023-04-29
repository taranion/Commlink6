package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.ShadowrunAction;
import de.rpgframework.shadowrun.ShadowrunAction.Category;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.jfx.CommonShadowrunJFXResourceHook;
import de.rpgframework.shadowrun.chargen.jfx.section.ShadowrunActionSection;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.WorldType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.AccessoriesSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.ActiveProgramsSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6PersonaSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SoftwareLibrarySection;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemSubTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author prelle
 *
 */
public class SR6MatrixDevicePage extends Page {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private final static Logger logger = System.getLogger(SR6MatrixDevicePage.class.getPackageName());

	private GearSection secDevices;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDevice;
	private SoftwareLibrarySection secSoftware;
	protected ActiveProgramsSection secPrograms;
	protected AccessoriesSection secAccessories;
	protected ImageView ivDeepDive;
	protected SR6PersonaSection secPersona;
	protected CombatSection secCombat;
	protected ShadowrunActionSection secActions;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private SR6CharacterController ctrl;

	//-------------------------------------------------------------------
	public SR6MatrixDevicePage() {
		super("Matrix");
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initDevices();
		initSoftware();
		initPrograms();
		initImage();
		initPersona();
		initCombat();
		initAccessories();
		initActions();
	}

	//-------------------------------------------------------------------
	private void initDevices() {
		Predicate<ItemTemplate> selectFilter = new ItemSubTypeFilter(CarryMode.CARRIED, ItemSubType.matrixDevices());

		secDevices = new GearSection(ResourceI18N.get(RES, "page.matrix.section.devices"), CarryMode.CARRIED, selectFilter, ItemUtil.MATRIXDEVICES_FILTER);
		secDevices.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secDevices, 4);
		FlexGridPane.setMinHeight(secDevices, 6);
		FlexGridPane.setMediumWidth(secDevices, 5);
		FlexGridPane.setMediumHeight(secDevices, 8);
	}

	//-------------------------------------------------------------------
	private void initSoftware() {
		secSoftware = new SoftwareLibrarySection();
		secSoftware.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secSoftware, 4);
		FlexGridPane.setMinHeight(secSoftware, 6);
		FlexGridPane.setMediumWidth(secSoftware, 6);
		FlexGridPane.setMediumHeight(secSoftware, 8);
		FlexGridPane.setMaxWidth(secSoftware, 7);
	}

	//-------------------------------------------------------------------
	private void initPrograms() {
		Predicate<ItemTemplate> programFilter = (c) -> c.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.SOFTWARE;
		secPrograms = new ActiveProgramsSection( ResourceI18N.get(RES, "page.matrix.section.activePrograms"), programFilter, null);
//		secPrograms.setOnAddAction(ev -> {});
		FlexGridPane.setMinWidth(secPrograms, 4);
		FlexGridPane.setMinHeight(secPrograms, 4);
		FlexGridPane.setMediumWidth(secPrograms, 6);
		FlexGridPane.setMediumHeight(secPrograms, 4);
	}

	//-------------------------------------------------------------------
	private void initAccessories() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY);
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY);
		secAccessories = new AccessoriesSection(
				ResourceI18N.get(RES, "page.matrix.section.accessories"), selectFilter, showFilter
				);
		secAccessories.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secAccessories, 4);
		FlexGridPane.setMinHeight(secAccessories, 6);
	}


	//--------------------------------------------------------------------
	private void initImage() {
		ivDeepDive = new ImageView();
		ivDeepDive.setPreserveRatio(true);

		InputStream is = CommonShadowrunJFXResourceHook.class.getResourceAsStream("images/DeckerDeepDive.png");
		if (is!=null) {
			Image img = new Image(is);
			ivDeepDive.setImage(img);
			ivDeepDive.setFitWidth(350);
		} else {
			logger.log(Level.ERROR, "Missing image images/DeckerDeepDive.png");
		}
		FlexGridPane.setMaxWidth(ivDeepDive, 5);
		FlexGridPane.setMaxHeight(ivDeepDive, 6);
	}

	//-------------------------------------------------------------------
	private void initPersona() {
		secPersona = new SR6PersonaSection(ResourceI18N.get(RES, "page.matrix.section.persona"));

		FlexGridPane.setMinWidth(secPersona, 4);
		FlexGridPane.setMinHeight(secPersona, 5);
	}

	//-------------------------------------------------------------------
	private void initCombat() {
		secCombat = new CombatSection(WorldType.MATRIX);
		FlexGridPane.setMinWidth(secCombat, 6);
		FlexGridPane.setMinHeight(secCombat, 6);
		FlexGridPane.setMediumWidth(secCombat, 8);
	}

	//-------------------------------------------------------------------
	private void initActions() {
		Function<ShadowrunAction, Node> resolver = (act) -> {
			Label ret = new Label();
			ret.setMaxWidth(Double.MAX_VALUE);
			ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
			if (act.getSkills()!=null) {
				SR6Skill skill = Shadowrun6Core.getSkill(act.getSkills()[0]);
				ShadowrunAttribute attr = (act.getAttribute()!=null)?act.getAttribute():skill.getAttribute();
				Pool<Integer> pool = Shadowrun6Tools.getSkillPool(ctrl.getModel(), skill, attr);
				ret.setText( pool.toString());
			} else
				ret.setText("-");
			return ret;
		};
		secActions = new ShadowrunActionSection(resolver);
		secActions.setAll(
				Shadowrun6Core.getItemList(Shadowrun6Action.class)
				.stream()
				.filter( act -> act.getCategory()==Category.MATRIX)
				.sorted(new Comparator<Shadowrun6Action>() {
					public int compare(Shadowrun6Action o1, Shadowrun6Action o2) {
						return o1.getName().compareTo(o2.getName());
					}
				})
				.collect(Collectors.toList())
				);
		FlexGridPane.setMinWidth(secActions, 4);
		FlexGridPane.setMinHeight(secActions, 8);
		FlexGridPane.setMediumWidth(secActions, 6);
		FlexGridPane.setMediumHeight(secActions, 9);
		FlexGridPane.setMaxWidth(secActions, 9);
		FlexGridPane.setMaxHeight(secActions, 5);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		// No ivDeepDive, secPrograms,
		flex.getChildren().addAll(secCombat, secDevices,secSoftware,secAccessories,secPersona,secActions);
		ScrollPane scroll = new ScrollPane(flex);
		scroll.setFitToWidth(true);

		layout = new OptionalNodePane(scroll, new Label("Select something to get a description"));
		layout.setUseScrollPane(true);
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secPrograms.showHelpForProperty().addListener( (ov,o,n) -> {showDescription(n); clearExceptPrograms(); });
		secSoftware.showHelpForProperty().addListener( (ov,o,n) -> {showDescription(n); clearExceptPrograms(); });
		secAccessories.showHelpForProperty().addListener( (ov,o,n) -> {showDescription(n); clearExceptAccessories();});
		secPrograms.selectedDeviceProperty().addListener( (ov,o,n) -> secAccessories.setDevice(n));
	}

	//-------------------------------------------------------------------
	private void clearExceptPrograms() {
		secAccessories.getSelectionModel().clearSelection();;
		secActions.getSelectionModel().clearSelection();
	}

	//-------------------------------------------------------------------
	private void clearExceptAccessories() {
		secPrograms.getSelectionModel().clearSelection();;
		secActions.getSelectionModel().clearSelection();
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
			layout.setOptional( new GenericDescriptionVBox(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()), n.getModifyable()));
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

		secDevices.updateController(ctrl);
		secSoftware.updateController(ctrl);
		secPrograms.updateController(ctrl);
		((GearSection)secAccessories).updateController(ctrl);
		secPersona.updateController(ctrl);
		secActions.setAll(
		Shadowrun6Core.getItemList(Shadowrun6Action.class)
		.stream()
		.filter( act -> act.getCategory()==Category.MATRIX)
		.sorted(new Comparator<Shadowrun6Action>() {
			public int compare(Shadowrun6Action o1, Shadowrun6Action o2) {
				return o1.getName().compareTo(o2.getName());
			}
		})
		.collect(Collectors.toList())
		);
		secCombat.setData(ctrl.getModel());
	}

	//--------------------------------------------------------------------
	public void refresh()  {
		logger.log(Level.INFO, "refresh");
		secDevices.refresh();
		secSoftware.refresh();
		secPrograms.refresh();
		secAccessories.refresh();
		secPersona.refresh();
		secActions.refresh();
		secCombat.setData(ctrl.getModel());

//		secActions.setAll(
//				Shadowrun6Core.getItemList(Shadowrun6Action.class)
//				.stream()
//				.filter( act -> act.getCategory()==Category.MATRIX)
//				.sorted(new Comparator<Shadowrun6Action>() {
//					public int compare(Shadowrun6Action o1, Shadowrun6Action o2) {
//						return o1.getName().compareTo(o2.getName());
//					}
//				})
//				.collect(Collectors.toList())
//				);
	}

	//-------------------------------------------------------------------
	private Image resolveIcon(ItemTemplate item) {
		String file = "icons/"+item.getId()+".png";
		logger.log(Level.INFO, "Resolve "+file);
		InputStream is = Shadowrun6DataPlugin.class.getResourceAsStream(file);
		logger.log(Level.INFO, "Stream = "+is);
		if (is==null) {
			logger.log(Level.ERROR, "Missing icon for program: "+file);
			return null;
		} else {
			return new Image(is);
		}
	}

}
