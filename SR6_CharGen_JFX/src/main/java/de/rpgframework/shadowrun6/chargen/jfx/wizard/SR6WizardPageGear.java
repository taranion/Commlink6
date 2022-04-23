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
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.listcell.ComplexDataItemListCell;
import de.rpgframework.shadowrun.chargen.jfx.listcell.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemTemplateFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageGear extends WizardPage implements ControllerListener{
	
	private final static Logger logger = System.getLogger(SR6WizardPageGear.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageGear.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterController charGen;
	
	private Label lbPPCurrent, lbPPMax;
	
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
		lbPPCurrent = new Label("?");
		lbPPMax     = new Label("?");
		
		selection = new ComplexDataItemControllerNode<>(charGen.getEquipmentController());
		
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.gear.placeholder.selected"));
		
		selection.setAvailableCellFactory(lv -> new ComplexDataItemListCell<ItemTemplate>( () -> charGen.getEquipmentController()));
		selection.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell( () -> charGen.getEquipmentController()));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		
		bxDescription = new GenericDescriptionVBox<ItemTemplate>(null);
		
		selection.setFilterNode(new ItemTemplateFilterNode(RES, selection, ItemType.PACK));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(FlexibleApplication.getInstance(), charGen.getEquipmentController()));
//		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);
		
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
		Label hdUnspent = new Label(ResourceI18N.get(RES, "page.gear.unspent"));
		hdUnspent.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox selectedHeading = new HBox(10, hdUnspent, lbPPCurrent, new Label("/"), lbPPMax);
		selection.setSelectedListHead(selectedHeading);
		
		
		layout = new OptionalNodePane(selection, bxDescription);
		layout.setId("optional-spells");
		setContent(layout);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
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
//		MagicOrResonanceType morType = charGen.getModel().getMagicOrResonanceType();
//		activeProperty().set( morType!=null && morType.usesSpells()); 
//		selection.refresh();
		
//		lbPPCurrent.setText( String.valueOf(charGen.getSpellController().get) );
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
