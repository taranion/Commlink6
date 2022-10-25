package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.WindowMode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterIOException;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.page.AugmentationPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage2;
import de.rpgframework.shadowrun6.chargen.jfx.page.CareerPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.CombatPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.GearPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.LifePage;
import de.rpgframework.shadowrun6.chargen.jfx.page.MagicPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.ResonancePage;
import de.rpgframework.shadowrun6.chargen.jfx.page.SR6MatrixDevicePage;
import de.rpgframework.shadowrun6.chargen.jfx.page.SkillPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.VehiclePage;
import de.rpgframework.shadowrun6.chargen.jfx.wizard.GenerationWizard;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CharacterLeveller;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6CharacterViewLayout extends CharacterViewLayout<ShadowrunAttribute, Shadowrun6Character, SR6CharacterController> implements ControllerListener {
	
	private final static ResourceBundle UI = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private final static Logger logger = System.getLogger(SR6CharacterViewLayout.class.getPackageName());
	
	private BasicDataPage2 pgBasic;
	private SkillPage pgSkills;
	private CombatPage pgCombat;
	private AugmentationPage pgAugment;
	private MagicPage pgMagic;
	private ResonancePage pgResonance;
	private SR6MatrixDevicePage pgMatrix;
	private VehiclePage pgVehicles;
	private GearPage pgGear;
	private LifePage pgLife;
	private CareerPage pgCareer;
	
	private Label lbMode;
	private Label lbKarma, lbNuyen;
	/* For conversions */
	private Button btnInc, btnDec;
	private Label lbConvert;
	private VBox bxConvert;
	
	private VBox bxHoverToDo;
	
	//-------------------------------------------------------------------
	/**
	 * @param ctrl Either a GeneratorWrapper or a CharacterLeveller
	 */
	public SR6CharacterViewLayout() {
		super(RoleplayingSystem.SHADOWRUN6);
		initComponents();
		initPages();
		
		setOnBackAction(ev -> closeRequested( ));
		
//		pages.setSecondaryContent(new Label("Zweiter Content"));
//		pages.setSecondaryHeader(new Label("Zweiter Title"));
//		pages.setMode(Mode.BACKDROP);
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		lbMode = new Label("Unknown Mode");
		lbMode.getStyleClass().add(JavaFXConstants.STYLE_HEADING3);
		lbMode.setStyle("-fx-text-fill: accent; -fx-font-style: italic");
		lbMode.setMaxWidth(Double.MAX_VALUE);
		lbMode.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(lbMode, new Insets(0, 100, 0, 0));
		
		pages.setAdditionalHeader(lbMode);
		lbMode.setManaged(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		lbMode.setVisible(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);

		lbKarma = new Label("?");
		lbKarma.setStyle("-fx-text-fill: accent; -fx-font-weight: bold");		
		lbNuyen = new Label("?");
		lbNuyen.setStyle("-fx-text-fill: accent; -fx-font-weight: bold");		
		Label hdKarma = new Label("Karma");
		hdKarma.setLabelFor(lbKarma);
		Label hdNuyen = new Label("Nuyen");
		lbConvert = new Label("?");
		lbConvert.setStyle("-fx-text-fill: accent; -fx-font-weight: bold");	
		btnInc = new Button(null, new SymbolIcon("chevronup"));
		btnInc.setStyle("-fx-font-size: 90%");
		btnInc.setTooltip(new Tooltip(ResourceI18N.get(UI, "action.conversionIncrease")));
		btnInc.setOnAction( ev -> control.getEquipmentController().increaseConversion());
		btnDec = new Button(null, new SymbolIcon("chevrondown"));
		btnDec.setStyle("-fx-font-size: 90%");
		btnDec.setTooltip(new Tooltip(ResourceI18N.get(UI, "action.conversionDecrease")));
		btnDec.setOnAction( ev -> control.getEquipmentController().decreaseConversion());
		bxConvert = new VBox(0, btnInc, lbConvert, btnDec);
		bxConvert.setAlignment(Pos.CENTER);
		VBox bxCenter = new VBox(5, lbNuyen, hdNuyen, bxConvert, lbKarma, hdKarma);
		VBox.setMargin(lbNuyen, new Insets(10, 0, 0, 0));
		bxCenter.setAlignment(Pos.CENTER);
		extraNodesCenterProperty().add(bxCenter);
		
		bxHoverToDo = new VBox(0);
		bxHoverToDo.setId("todos");
		bxHoverToDo.setStyle("-fx-max-width: 20em");
		bxHoverToDo.getStyleClass().addAll("hover","todos");
	}
	
	//-------------------------------------------------------------------
	public void initPages() {
		pgBasic  = new BasicDataPage2();
		pgSkills = new SkillPage();
		pgCombat = new CombatPage();
		pgAugment= new AugmentationPage();
		pgMagic  = new MagicPage();
		pgResonance= new ResonancePage();
		pgMatrix = new SR6MatrixDevicePage(); 
		pgVehicles = new VehiclePage();
		pgGear   = new GearPage();
		pgLife   = new LifePage();
		pgCareer = new CareerPage();
		getPages().addAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMagic, pgMatrix, pgResonance, pgVehicles, pgGear, pgLife);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#startCreation()
	 */
	@Override
	public void startCreation(CharacterGenerator<?,?> charGen) {
		logger.log(Level.WARNING, "ENTER: Start creation");
		Label tmp = new Label(charGen.getModel().getName());
		tmp.getStyleClass().add(JavaFXConstants.STYLE_HEADING2);
		super.pages.setHeader(tmp);
		GeneratorWrapper wrapper = (GeneratorWrapper) charGen; //new GeneratorWrapper(new Shadowrun6Character(), null);
		
		logger.log(Level.ERROR, "Inform "+wrapper.getWrapped());
		handleControllerEvent(BasicControllerEvents.GENERATOR_CHANGED, wrapper);
		
//		wrapper.setWrapped(new PriorityCharacterGenerator());
		logger.log(Level.ERROR, "Create wizard for "+wrapper.getWrapped());
		GenerationWizard wizard = new GenerationWizard(wrapper);
		while (true) {
			CloseType close = FlexibleApplication.getInstance().showAndWait(wizard);
			logger.log(Level.INFO, "Wizard closed via "+close);
			//		controller.refresh();
			if (close==CloseType.FINISH) {
//				wrapper.finish();
//				try {
//					logger.log(Level.DEBUG, "Call save() on "+wrapper.getClass());
//					wrapper.save(Shadowrun6Core.encode(wrapper.getModel()));
					return;
//				} catch (CharacterIOException e) {
//					super.showCharacterIOException(e, wrapper.getModel());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			if (close==CloseType.CANCEL) {
				//			getApplication().closeAppLayout();
				logger.log(Level.INFO, "call historyBack()");
				dontShowConfirmationDialog = true;
				getApplication().closeScreen(this);
				return;
			}
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model, CharacterHandle handle) {
		logger.log(Level.INFO, "ENTER: Continue creation");
		this.handle = handle;
		
		if (this.control==null)
			throw new IllegalStateException("No controller set");

		try {
			Label tmp = new Label(model.getName());
			tmp.getStyleClass().add(JavaFXConstants.STYLE_HEADING2);
			super.pages.setHeader(tmp);

			control.addListener(this);
			refreshController();
			refreshPages();
		
//			GeneratorWrapper wrapper = new GeneratorWrapper((Shadowrun6Character) model, handle);
//			logger.log(Level.INFO, "ToDo: Detect previously used generator: {0}", model.getCharGenUsed());
//			try {
//				logger.log(Level.DEBUG, "JSON = "+model.getChargenSettingsJSON());
//				if (model.getCharGenUsed()==null) {
//					throw new NullPointerException(ResourceI18N.get(UI, "error.no_chargen_in_character"));
//				}
//				SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator(model.getCharGenUsed(), model,
//						handle);
//				wrapper.setWrapped(charGen);
//				wrapper.addListener(this);
//				super.control = wrapper;
//				logger.log(Level.INFO, "Generator to continue with: {0}", charGen.getClass().getSimpleName());
//				refreshController();
//				refreshPages();
//			} catch (Exception e) {
//				logger.log(Level.ERROR, "Error creating generator '" + model.getCharGenUsed(), e);
//				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2,
//						"Internal error creating character generator instance");
//				showAnyException(e, model, 
//						ResourceI18N.get(UI, "error.title"),
//						ResourceI18N.get(UI, "error.continuingCreation"));
//				return;
//			}
			
			logger.log(Level.WARNING, "ToDo: open wizard");
		} finally {
			logger.log(Level.INFO, "LEAVE: Continue creation");
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#edit(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void edit(Shadowrun6Character model, CharacterHandle handle) {
		logger.log(Level.WARNING, "ENTER: Edit "+model);
		this.handle = handle;
		control.addListener(this);
		extraButtonsTopProperty().remove(btnFinish);
		Label tmp = new Label(control.getModel().getName());
		tmp.getStyleClass().add(JavaFXConstants.STYLE_HEADING2);
		super.pages.setHeader(tmp);
		
		try {
			refreshController();
			refreshSidebar();
			refreshPages();
			refreshToDos();
			control.runProcessors();
		} finally {
			logger.log(Level.WARNING, "LEAVE: Edit " + model);
		}
	}

	//-------------------------------------------------------------------
	private void refreshController() {
		control.setAllowRunProcessor(false);
		pgBasic.setController(control);
		pgSkills.setController(control);
		pgCombat.setController(control);
		pgAugment.setController(control);
		pgMagic.setController(control);
		pgMatrix.setController(control);
		pgResonance.setController(control);
		pgVehicles.setController(control);
		pgGear.setController(control);
		pgLife.setController(control);
		pgCareer.setController(control);
		control.setAllowRunProcessor(true);
		control.runProcessors();
		
		refreshPages();
		refreshSidebar();
		
		MagicOrResonanceType mtype = control.getModel().getMagicOrResonanceType();
		if (mtype==null) {
			getPages().setAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMatrix, pgVehicles, pgGear, pgLife);
		} else {
			if (mtype.usesMagic()) {
				getPages().setAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMagic, pgMatrix, pgVehicles, pgGear, pgLife);
			} else if (mtype.usesResonance()) {
				getPages().setAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMatrix, pgResonance, pgVehicles, pgGear, pgLife);
			} else {
				getPages().setAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMatrix, pgVehicles, pgGear, pgLife);
			}
		}
		if (control instanceof SR6CharacterLeveller) {
			getPages().addAll(pgCareer);
		}
	}

	//-------------------------------------------------------------------
	private void refreshToDos() {
		logger.log(Level.INFO, "refreshToDos");
		bxHoverToDo.getChildren().clear();
		if (control.getToDos().isEmpty()) {
			setHoverNode(null);
		} else {
			ToDoElement.Severity worst = Severity.INFO;
			for (ToDoElement todo : control.getToDos()) {
				if (todo.getSeverity().ordinal()<worst.ordinal())
					worst = todo.getSeverity();
				Label hover = new Label(todo+"");
				hover.setWrapText(true);
				hover.setStyle("-fx-text-fill: textcolor-"+todo.getSeverity().name().toLowerCase());
				bxHoverToDo.getChildren().add(hover);
			}
			bxHoverToDo.getStyleClass().setAll("todos", "todos-"+worst.name().toLowerCase());
			setHoverNode(new Group(bxHoverToDo));
		}
	}

	//-------------------------------------------------------------------
	private void refreshPages() {
		if (control.getModel().isInCareerMode()) {
			lbMode.setText(ResourceI18N.get(UI, "label.mode.career"));
		} else {
			lbMode.setText(ResourceI18N.get(UI, "label.mode.chargen"));
		}
		super.pages.getHeader().setText(control.getModel().getName());
		
		pgBasic.refresh();
		pgSkills.refresh();
		pgCombat.refresh();
		pgAugment.refresh();
		pgMagic.refresh();
		pgMatrix.refresh();
		pgResonance.refresh();
		pgVehicles.refresh();
		pgGear.refresh();
		pgLife.refresh();
		pgCareer.refresh();

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.WARNING, "RCV "+type);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			control = (SR6CharacterController) param[0];
			control.addListener(this);
			refreshController();
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			refreshPages();
		}
//		Page page = getVisiblePage();
//		if (page!=null && page instanceof ControllerListener) {
//			((ControllerListener)page).handleControllerEvent(type, param);
//		}
		
		refreshSidebar();
		refreshToDos();
	}

	//-------------------------------------------------------------------
	private void refreshSidebar() {
		if (control==null || control.getModel()==null) return;
		lbKarma.setText( String.valueOf( control.getModel().getKarmaFree() ));
		int nuyen = control.getModel().getNuyen();
		if (nuyen>=10000) {
			lbNuyen.setText( (nuyen/1000)+"k");
		} else {
			lbNuyen.setText( String.valueOf(nuyen) );
		}
		
		bxConvert.setVisible(control.getEquipmentController().getConversionRateKarma()>0);
		bxConvert.setManaged(control.getEquipmentController().getConversionRateKarma()>0);
		lbConvert.setText(String.valueOf(control.getEquipmentController().getConvertedKarma()));
		btnInc.setDisable(!control.getEquipmentController().canIncreaseConversion());
		btnDec.setDisable(!control.getEquipmentController().canDecreaseConversion());
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		lbMode.setManaged(value!=WindowMode.MINIMAL);
		lbMode.setVisible(value!=WindowMode.MINIMAL);
	}

	//-------------------------------------------------------------------
	/**
	 * This method is called, after closing the page has been confirmed -
	 * AND the character has already been saved, if requested.
	 * To enable reverting the character to the state from the disk, reload it here
	 */
	private void closeRequested() {
		logger.log(Level.DEBUG, "ENTER closeRequested");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#encodeCharacter(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	protected byte[] encodeCharacter(Shadowrun6Character model) throws CharacterIOException {
		logger.log(Level.DEBUG, "START: encodeCharacter");
		return Shadowrun6Core.encode(model); 
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#decodeCharacter(byte[])
	 */
	@Override
	protected Shadowrun6Character decodeCharacter(byte[] encoded) throws CharacterIOException {
		logger.log(Level.DEBUG, "START: decodeCharacter");
		Shadowrun6Character model = Shadowrun6Core.decode(encoded);
		Shadowrun6Tools.resolveChar(model);
		return model;
	}

}
