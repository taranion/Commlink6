package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertManager;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun.chargen.jfx.pane.QualitySelector;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageQualities;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.ChildhoodGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SelectedSkillListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageLPChildhood extends WizardPage implements ControllerListener {

	private final static Logger logger = System.getLogger(AWizardPageQualities.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageLPChildhood.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterGenerator charGen;
	protected ChildhoodGenerator childhood;

	private ListView<SR6Skill> lvSkills;
	private ListView<QualityValue> lvQualities;
	protected Button btnAdd;
	protected Button btnDel;
	private TextField tfArea;
	private TextArea taDescription;

	protected GenericDescriptionVBox bxDescription;
	protected OptionalNodePane layout;
	private NumberUnitBackHeader backHeader;

	//-------------------------------------------------------------------
	public SR6WizardPageLPChildhood(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.childhood.title"));
		initComponents();
		initLayout();
		initInteractivity();

		childhood = ((GeneratorWrapper)charGen).getWrapped().getChildhoodGenerator();

		charGen.addListener(this);
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		Label phQualities = new Label(ResourceI18N.get(RES, "page.childhood.qualities.placeholder"));
		phQualities.setWrapText(true);
		lvSkills = new ListView<>();
		lvSkills.setCellFactory(lv -> new SelectedSkillListCell( () -> childhood.getSkillController()));
		btnAdd = new Button(null, new SymbolIcon("add"));
		btnDel = new Button(null, new SymbolIcon("delete"));
		btnDel.setDisable(true);
		lvQualities = new ListView<>();
		lvQualities.setPlaceholder(phQualities);
		lvQualities.setCellFactory(lv -> new QualityValueListCell( () -> charGen, true));
		tfArea = new TextField();

		taDescription = new TextArea();
		taDescription.setWrapText(true);
		taDescription.setMaxWidth(450);
		taDescription.setPromptText(ResourceI18N.get(RES, "page.childhood.prompt"));
		bxDescription = new GenericDescriptionVBox(Shadowrun6Tools.requirementResolver(Locale.getDefault()), Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	protected void initLayout() {

		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
		super.setBackHeader(backHeader);

		Label lbSkills = new Label(ResourceI18N.get(RES, "page.childhood.skills"));
		lbSkills.setWrapText(true);
		VBox bxSkills = new VBox(10, lbSkills, lvSkills);
		lvSkills.setStyle("-fx-pref-width: 12em; -fx-max-width: 16emx; -fx-max-height: 18em");

		Label lbQuality = new Label(ResourceI18N.get(RES, "page.childhood.quality"));
		lbQuality.setWrapText(true);
		HBox bxButtons = new HBox(10, btnAdd, btnDel);
		bxButtons.setAlignment(Pos.CENTER_RIGHT);
		VBox bxQualities = new VBox(10, lbQuality, bxButtons, lvQualities);
		bxQualities.setMaxWidth(420);
		bxQualities.setStyle("-fx-pref-width: 20em; -fx-max-width: 420px; -fx-max-height: 18em");

		Label lbSummarize = new Label(ResourceI18N.get(RES, "page.childhood.summarize"));
		Label lbArea      = new Label(ResourceI18N.get(RES, "page.childhood.area"));
		VBox bxFluff = new VBox(5, lbArea, tfArea, lbSummarize, taDescription);
		FlowPane content = new FlowPane(20,20,bxSkills, bxQualities, bxFluff);

		layout = new OptionalNodePane(content, bxDescription);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		btnQuality1.setOnAction(e -> handleSelect(bornThisWay.getQualityController1()));
//		btnQuality2.setOnAction(e -> handleSelect(bornThisWay.getQualityController2()));
		lvSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "show help for "+n);
			bxDescription.setData(n);
			if (n!=null) {
				layout.setTitle(n.getName());
			} else {
				layout.setTitle(null);
			}
		});
		lvQualities.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "show help for "+n);
			bxDescription.setData(n);
			btnDel.setDisable(n==null);
			if (n!=null) {
				layout.setTitle(n.getName());
			} else {
				layout.setTitle(null);
			}
		});
		taDescription.textProperty().addListener( (ov,o,n) -> {
			logger.log(Level.WARNING, "ToDo: Store description");
		});
		tfArea.textProperty().addListener( (ov,o,n) -> {
			childhood.selectChildhoodArea(n);
		});

		btnAdd.setOnAction(ev -> onAddQuality());
		btnDel.setOnAction(ev -> onDeleteQuality());
	}

	//-------------------------------------------------------------------
	private void handleSelect(IQualityController ctrl) {
		QualitySelector selector = new QualitySelector(
				ctrl,
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault())
				);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES,"section.quality.selector.title"), selector, CloseType.OK, CloseType.CANCEL);

		CloseType close = (CloseType) FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.ERROR,"Closed with "+close);
		if (close==CloseType.OK) {
			Quality toSelect = selector.getSelected();
			Possible possible = ctrl.canBeSelected(toSelect);
			logger.log(Level.ERROR, "possible = "+possible);
			if (possible.get()) {
				// Is there a need for a selection
				logger.log(Level.ERROR, "ctrl = " + ctrl);
				if (!ctrl.getChoicesToDecide(toSelect).isEmpty()) {
					// Yes, user must choose
					List<Choice> options = ctrl.getChoicesToDecide(toSelect);
					logger.log(Level.ERROR, "called getChoicesToDecide returns {0} choices", options.size());
					ChoiceSelectorDialog<Quality, QualityValue> choiceDialog = new ChoiceSelectorDialog<Quality, QualityValue>(ctrl);
					Decision[] decisions = choiceDialog.apply(toSelect, options);
					if (decisions != null) {
						logger.log(Level.ERROR, "call select(option, decision[{0}])", decisions.length);
						OperationResult<QualityValue> res = ctrl.select(toSelect, decisions);
						if (res.wasSuccessful()) {
							logger.log(Level.ERROR, "Selecting {0} with options was successful", toSelect);
						} else {
							logger.log(Level.ERROR, "Selecting {0} with options failed: {1}", toSelect, res.getError());
							AlertManager.showAlertAndCall(javafx.scene.control.Alert.AlertType.ERROR, "Failed adding", res.getError());
						}
					}
				} else {
					// No
					logger.log(Level.DEBUG, "call select(option)");
					OperationResult<QualityValue> res = ctrl.select(toSelect);
					if (res.wasSuccessful()) {
						logger.log(Level.INFO, "Selecting {0} was successful", toSelect);
					} else {
						logger.log(Level.WARNING, "Selecting {0} failed: {1}", toSelect, res.getError());
						AlertManager.showAlertAndCall(javafx.scene.control.Alert.AlertType.ERROR, "Failed adding", res.getError());
					}
				}
			} else {
				logger.log(Level.DEBUG, "can not be Selected(" + toSelect + "): " + possible.getI18NKey());

	    		FlexibleApplication.getInstance().showAlertAndCall(AlertType.NOTIFICATION, "Selection failed", possible.toString());
	    	}
		}
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
	protected void refresh() {
		boolean isLifepath =  charGen.getId().equals("lifepath");
		activeProperty().set( isLifepath );
		if (!isLifepath)
			return;

		backHeader.setValue(charGen.getModel().getKarmaFree());


		lvSkills.getItems().setAll(childhood.getAvailableSkills());
		lvQualities.getItems().setAll(childhood.getQualityController().getSelected());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.WARNING, "RCV {0}",type);
		logger.log(Level.INFO, "RCV " + type + " with " + Arrays.toString(param));

		if (type == BasicControllerEvents.GENERATOR_CHANGED) {
			logger.log(Level.INFO, "RCV " + type + " with " + Arrays.toString(param));
			charGen = (SR6CharacterGenerator) param[0];
			childhood =  ((SR6CharacterGenerator)charGen).getChildhoodGenerator();
			if (childhood!=null) {
//				selection.setController(childhood.getQualityController());
//				selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getQualityController()));
//				filter = new QualityFilterNode(RES, selection, QualityType.NORMAL);
//				selection.setFilterNode(filter);
				activeProperty().set( true );
			} else {
				activeProperty().set( false );
			}
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			refresh();
		}
//		if (type==BasicControllerEvents.CHARACTER_CHANGED || type==BasicControllerEvents.GENERATOR_CHANGED) {
//			paneAttr.setController(charGen.getDrakeController());
//			selection.refresh();
//			refresh();
//		}
	}

	//-------------------------------------------------------------------
	private void onAddQuality() {
		IQualityController ctrl = childhood.getQualityController();
		QualitySelector selector = new QualitySelector(
				ctrl,
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault())
				);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES,"section.quality.selector.title"), selector, CloseType.OK, CloseType.CANCEL);

		CloseType close = (CloseType) FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.ERROR,"Closed with "+close);
		if (close==CloseType.OK) {
			Quality toSelect = selector.getSelected();
			Possible possible = ctrl.canBeSelected(toSelect);
			logger.log(Level.ERROR, "possible = "+possible);
			if (possible.get()) {
				// Is there a need for a selection
				logger.log(Level.ERROR, "ctrl = " + ctrl);
				if (!ctrl.getChoicesToDecide(toSelect).isEmpty()) {
					// Yes, user must choose
					List<Choice> options = ctrl.getChoicesToDecide(toSelect);
					logger.log(Level.ERROR, "called getChoicesToDecide returns {0} choices", options.size());
					ChoiceSelectorDialog<Quality, QualityValue> choiceDialog = new ChoiceSelectorDialog<Quality, QualityValue>(ctrl);
					Decision[] decisions = choiceDialog.apply(toSelect, options);
					if (decisions != null) {
						logger.log(Level.ERROR, "call select(option, decision[{0}])", decisions.length);
						OperationResult<QualityValue> res = ctrl.select(toSelect, decisions);
						if (res.wasSuccessful()) {
							logger.log(Level.ERROR, "Selecting {0} with options was successful", toSelect);
						} else {
							logger.log(Level.ERROR, "Selecting {0} with options failed: {1}", toSelect, res.getError());
							AlertManager.showAlertAndCall(javafx.scene.control.Alert.AlertType.ERROR, "Failed adding", res.getError());
						}
					}
				} else {
					// No
					logger.log(Level.DEBUG, "call select(option)");
					OperationResult<QualityValue> res = ctrl.select(toSelect);
					if (res.wasSuccessful()) {
						logger.log(Level.INFO, "Selecting {0} was successful", toSelect);
					} else {
						logger.log(Level.WARNING, "Selecting {0} failed: {1}", toSelect, res.getError());
						AlertManager.showAlertAndCall(javafx.scene.control.Alert.AlertType.ERROR, "Failed adding", res.getError());
					}
				}
			} else {
				logger.log(Level.DEBUG, "can not be Selected(" + toSelect + "): " + possible.getI18NKey());

	    		FlexibleApplication.getInstance().showAlertAndCall(AlertType.NOTIFICATION, "Selection failed", possible.toString());
	    	}
		}
	}

	//-------------------------------------------------------------------
	private void onDeleteQuality() {
		QualityValue val = lvQualities.getSelectionModel().getSelectedItem();
		childhood.getQualityController().deselect(val);
	}

}
