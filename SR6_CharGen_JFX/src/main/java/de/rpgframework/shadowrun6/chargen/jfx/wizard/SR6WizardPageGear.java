package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.SymbolIcon;
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
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemTemplateFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.ItemTemplateListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageGear extends WizardPage implements ControllerListener{

	private final static Logger logger = System.getLogger(SR6WizardPageGear.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageGear.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterController charGen;

	private Label lbIntro,lbConverted, lbConvNuyen;
	private Button btnDec;
	private Button btnInc;

	protected ComplexDataItemControllerNode<ItemTemplate, CarriedItem<ItemTemplate>> selection;
	protected CarriedItemDescriptionPane bxDescription;
	protected OptionalNodePane layout;
	private NumberUnitBackHeader backHeaderKarma;
	private NumberUnitBackHeader backHeaderNuyen;
	private NumberUnitBackHeader backHeaderEssence;

	// Shall character requirements be ignored
	private CheckBox cbIgnoreRequirements;

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
		lbIntro     = new Label(ResourceI18N.get(RES, "page.gear.intro"));
		lbIntro.setWrapText(true);
		lbConverted = new Label("?");
		lbConvNuyen = new Label("?");
		btnDec = new Button("-");
		btnInc = new Button("+");

		selection = new ComplexDataItemControllerNode<>(charGen.getEquipmentController());

		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.selected"));

		selection.setAvailableCellFactory(lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController(), null));
		selection.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell( () -> charGen.getEquipmentController()));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);

		bxDescription = new CarriedItemDescriptionPane(Shadowrun6Tools.requirementResolver(Locale.getDefault()), charGen);

		selection.setFilterNode(new ItemTemplateFilterNode(RES, selection, ItemType.PACK));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getEquipmentController()));

		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());

		cbIgnoreRequirements = new CheckBox(ResourceI18N.get(RES, "page.gear.rule.ignoreGearRequirements"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Current Karma
		backHeaderKarma = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.karma"));
		backHeaderKarma.setValue(charGen.getModel().getKarmaFree());
		backHeaderNuyen = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.nuyen"));
		backHeaderNuyen.setValue(charGen.getModel().getNuyen());
		backHeaderEssence = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.essence"));
		float essence = charGen.getModel().getAttribute(ShadowrunAttribute.ESSENCE).getModifiedValue();
		backHeaderEssence.setValue( essence/1000.0f );
		HBox.setMargin(backHeaderKarma, new Insets(0,10,0,10));
		HBox.setMargin(backHeaderNuyen, new Insets(0,10,0,10));

		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox box = new HBox(backHeaderKarma, backHeaderNuyen, backHeaderEssence);
		HBox backHeader = new HBox(10, box, buf, new SymbolIcon("setting"));
		HBox.setHgrow(buf, Priority.ALWAYS);
		//backHeader.setMaxWidth(Double.MAX_VALUE);
		HBox.setMargin(box, new Insets(0,0,0,10));
		HBox.setMargin(backHeader.getChildren().get(2), new Insets(0,10,0,0));

//		if (ResponsiveControlManager.getCurrentMode()==WindowMode.EXPANDED) {
//			super.setBackHeader(null);
//		} else {
			super.setBackHeader(backHeader);
//		}

		setBackContent(cbIgnoreRequirements);

		// Information about spent PP
		Label hdConverted = new Label(ResourceI18N.get(RES, "page.gear.converted"));
		Label hdNuyen     = new Label(ResourceI18N.get(RES, "page.gear.nuyen"));
		hdConverted.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdNuyen.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox conversion = new HBox(10, btnDec, lbConverted, btnInc, hdConverted, lbConvNuyen, hdNuyen);
		conversion.setAlignment(Pos.CENTER_LEFT);

		VBox col1 = new VBox(10, lbIntro, conversion, selection);


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

		cbIgnoreRequirements.selectedProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "User chose cbIgnoreGearRequirements = "+n);
			charGen.getModel().setRuleValue(Shadowrun6Rules.IGNORE_GEAR_REQUIREMENTS, String.valueOf(n));
			charGen.runProcessors();
		});
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		logger.log(Level.TRACE, "refresh");
		backHeaderKarma.setValue(charGen.getModel().getKarmaFree());
		backHeaderNuyen.setValue(charGen.getModel().getNuyen());
		btnDec.setDisable(!charGen.getEquipmentController().canDecreaseConversion());
		btnInc.setDisable(!charGen.getEquipmentController().canIncreaseConversion());
//		MagicOrResonanceType morType = charGen.getModel().getMagicOrResonanceType();
//		activeProperty().set( morType!=null && morType.usesSpells());
		selection.refresh();
		cbIgnoreRequirements.setSelected(charGen.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.IGNORE_GEAR_REQUIREMENTS));

		lbConverted.setText( String.valueOf(charGen.getEquipmentController().getConvertedKarma()) );
		lbConvNuyen.setText( String.valueOf(charGen.getEquipmentController().getConvertedKarma()*charGen.getEquipmentController().getConversionRateKarma()) );
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
		logger.log(Level.DEBUG, "RCV {0}",type);
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			selection.setController(charGen.getEquipmentController());
			selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED || type==BasicControllerEvents.GENERATOR_CHANGED) {
			refresh();
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
