package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.chargen.jfx.listcell.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemTemplateFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.ItemTemplateListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageGear extends WizardPage implements ControllerListener{
	
	private final static Logger logger = System.getLogger(SR6WizardPageGear.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageGear.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterController charGen;
	
	private Label lbConverted, lbConvNuyen;
	private Button btnDec;
	private Button btnInc;
	
	protected ComplexDataItemControllerNode<ItemTemplate, CarriedItem<ItemTemplate>> selection;
	protected GenericDescriptionVBox<ItemTemplate> bxDescription;
	protected OptionalNodePane layout;
	private NumberUnitBackHeader backHeaderKarma;
	private NumberUnitBackHeader backHeaderNuyen;

	//-------------------------------------------------------------------
	public SR6WizardPageGear(Wizard wizard, SR6CharacterController charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.gear.title"));
		initComponents();
		initLayout();
		initInteractivity();
		
		charGen.addListener(this);
	}
	
	//-------------------------------------------------------------------
	protected void initComponents() {
		lbConverted = new Label("?");
		lbConvNuyen = new Label("?");
		btnDec = new Button("-");
		btnInc = new Button("+");
		
		selection = new ComplexDataItemControllerNode<>(charGen.getEquipmentController());
		
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.selected"));
		
		selection.setAvailableCellFactory(lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController()));
		selection.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell( () -> charGen.getEquipmentController()));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		
		bxDescription = new GenericDescriptionVBox<ItemTemplate>(null);
		
		selection.setFilterNode(new ItemTemplateFilterNode(RES, selection, ItemType.PACK));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(FlexibleApplication.getInstance(), charGen.getEquipmentController()));
		selection.setSelectedFilter(qv -> qv.getModifyable().getItemType()==ItemType.PACK);
		
		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		// Current Karma
		backHeaderKarma = new NumberUnitBackHeader("Karma");
		backHeaderKarma.setValue(charGen.getModel().getKarmaFree());
		backHeaderNuyen = new NumberUnitBackHeader("Nuyen");
		backHeaderNuyen.setValue(charGen.getModel().getNuyen());
		HBox.setMargin(backHeaderKarma, new Insets(0,10,0,10));
		HBox.setMargin(backHeaderNuyen, new Insets(0,10,0,10));
		if (ResponsiveControlManager.getCurrentMode()==WindowMode.EXPANDED) {
			super.setBackHeader(null);
		} else {
			super.setBackHeader(new HBox(backHeaderKarma, backHeaderNuyen));
		}
		
		// Information about spent PP
		Label hdConverted = new Label(ResourceI18N.get(RES, "page.gear.converted"));
		Label hdNuyen     = new Label(ResourceI18N.get(RES, "page.gear.nuyen"));
		hdConverted.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdNuyen.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox conversion = new HBox(10, btnDec, lbConverted, btnInc, hdConverted, lbConvNuyen, hdNuyen);
		conversion.setAlignment(Pos.CENTER_LEFT);
		
		VBox col1 = new VBox(10, conversion, selection);
		
		
		layout = new OptionalNodePane(col1, bxDescription);
		layout.setId("optional-spells");
		setContent(layout);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnDec.setOnAction(ev -> charGen.getEquipmentController().decreaseConversion());
		btnInc.setOnAction(ev -> charGen.getEquipmentController().increaseConversion());
		selection.showHelpForProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "show help for "+n);
			bxDescription.setData(n);
			if (n!=null) {
				layout.setTitle(n.getName());
			} else {
				layout.setTitle(null);
			}
		});
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		backHeaderKarma.setValue(charGen.getModel().getKarmaFree());
		backHeaderNuyen.setValue(charGen.getModel().getNuyen());
		btnDec.setDisable(!charGen.getEquipmentController().canDecreaseConversion());
		btnInc.setDisable(!charGen.getEquipmentController().canIncreaseConversion());
//		MagicOrResonanceType morType = charGen.getModel().getMagicOrResonanceType();
//		activeProperty().set( morType!=null && morType.usesSpells()); 
		selection.refresh();
		
		lbConverted.setText( String.valueOf(charGen.getEquipmentController().getConvertedKarma()) );
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageVisited()
	 */
	@Override
	public void pageVisited() {
		logger.log(Level.INFO, "pageVisited");
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		if (type==BasicControllerEvents.CHARACTER_CHANGED) 
			refresh();
		
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
//			bxLine.setManaged(charGen.getAdeptPowerController().canBuyPowerPoints());
//			bxLine.setVisible(charGen.getAdeptPowerController().canBuyPowerPoints());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		selection.setShowHeadings(value!=WindowMode.MINIMAL);
	}

}
