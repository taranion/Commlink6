package de.rpgframework.shadlexowrun6.chargen.jfx.page;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.ComplexDataItemListSection;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.section.IconSection;
import de.rpgframework.shadowrun.ShadowrunAction.Category;
import de.rpgframework.shadowrun.chargen.jfx.CommonShadowrunJFXResourceHook;
import de.rpgframework.shadowrun.chargen.jfx.section.PersonaSection;
import de.rpgframework.shadowrun.chargen.jfx.section.ShadowrunActionSection;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.AccessoriesSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.ActiveProgramsSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6MatrixDevicePage extends Page {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private final static Logger logger = System.getLogger(SR6MatrixDevicePage.class.getPackageName());
	
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDevice;
	protected ActiveProgramsSection secPrograms;
	protected AccessoriesSection secAccessories;
	protected ImageView ivDeepDive;
	protected PersonaSection secPersona;
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
		initPrograms();
		initImage();
		initPersona();
		initAccessories();
		initActions();
	}
	
	//-------------------------------------------------------------------
	private void initAccessories() {
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY); 
		Predicate<CarriedItem<ItemTemplate>> showFilter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.SURVIVAL, ItemType.BIOLOGY); 
		secAccessories = new AccessoriesSection(
				ResourceI18N.get(RES, "page.gear.section.other"), selectFilter, showFilter
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
	private void initPrograms() {
		secPrograms = new ActiveProgramsSection( ResourceI18N.get(RES, "page.matrix.section.activePrograms"), null, null);			
//		secPrograms.setOnAddAction(ev -> {});
		FlexGridPane.setMinWidth(secPrograms, 4);
		FlexGridPane.setMinHeight(secPrograms, 4);
		FlexGridPane.setMediumWidth(secPrograms, 6);
		FlexGridPane.setMediumHeight(secPrograms, 4);
	}

	//-------------------------------------------------------------------
	private void initPersona() {
		Label hdDefRating = new Label(ResourceI18N.get(RES, "page.matrix.section.persona.defenseRating"));
		Label hdDefPool   = new Label(ResourceI18N.get(RES, "page.matrix.section.persona.defensePool"));
		GridPane ruleSpec = new GridPane();
		ruleSpec.setHgap(10);
		ruleSpec.setVgap(5);
		ruleSpec.add(hdDefRating, 0, 0);
		ruleSpec.add(hdDefPool  , 0, 1);
		
		secPersona = new PersonaSection(ResourceI18N.get(RES, "page.matrix.section.persona"));
		secPersona.setRuleSpecificNode(ruleSpec);

		FlexGridPane.setMinWidth(secPersona, 4);
		FlexGridPane.setMinHeight(secPersona, 6);
	}
	
	//-------------------------------------------------------------------
	private void initActions() {
		secActions = new ShadowrunActionSection(ResourceI18N.get(RES, "page.matrix.section.actions"));
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
		FlexGridPane.setMaxWidth(secActions, 10);
		FlexGridPane.setMaxHeight(secActions, 5);
	}

	//-------------------------------------------------------------------
	private void initLayout() {		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secPrograms,ivDeepDive,secPersona,secAccessories, secActions);
		ScrollPane scroll = new ScrollPane(flex);
		scroll.setFitToWidth(true);
		
		layout = new OptionalNodePane(scroll, new Label("Select something to get a description"));
		layout.setUseScrollPane(true);
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secPrograms.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secAccessories.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secPrograms.selectedDeviceProperty().addListener( (ov,o,n) -> secAccessories.setDevice(n));
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
	private void showDescription(ComplexDataItem n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n));
			layout.setTitle(n.getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		secPrograms.updateController(ctrl);

		//super.setController(ctrl);

//		List<CarriedItem<ItemTemplate>> matrixDevices = ctrl.getModel().getCarriedItems()
//			.stream()
//			.filter(ci -> ci.hasFlag(ItemTemplate.FLAG_MATRIX_DEVICE))
//			.collect(Collectors.toList());
//		cbDevice.getItems().setAll(matrixDevices);
		((GearSection)secAccessories).updateController(ctrl);
//		if (cbDevice.getValue()==null && !matrixDevices.isEmpty())
//			cbDevice.setValue(matrixDevices.get(0));
////		secDevices.updateController(ctrl);
////		secSoftware.updateController(ctrl);
//		refresh();
	}
	
	//-------------------------------------------------------------------
	private void deviceChanged(CarriedItem<ItemTemplate> device) {
		logger.log(Level.DEBUG, "Device changed to "+device);
		
//		AvailableSlot slot = device.getSlot(ItemHook.SOFTWARE);
//		logger.log(Level.DEBUG, "Slot = "+slot);
//		
//		if (slot!=null) {
//			secPrograms.setSlots((int)slot.getCapacity());
//			logger.log(Level.WARNING, "Embedded in slot = "+slot.getAllEmbeddedItems());
//			secPrograms.getItems().setAll(slot.getAllEmbeddedItems());
//		} else
//			secPrograms.setSlots(0);
//		
//		AvailableSlot accessories = device.getSlot(ItemHook.ELECTRONIC_ACCESSORY);
//		if (accessories!=null) {
//			secAccessories.setData( accessories.getAllEmbeddedItems());
//			secAccessories.setVisible(true);
//		} else {
//			// No external accessories
//			secAccessories.setData(new ArrayList<>());
//			secAccessories.setVisible(false);
//		}
		
	}

	//--------------------------------------------------------------------
	public void refresh()  {
		logger.log(Level.INFO, "refresh");
//		secPrograms.refresh();
//		secAccessories.refresh();
//		secPersona.refresh();
//		
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
