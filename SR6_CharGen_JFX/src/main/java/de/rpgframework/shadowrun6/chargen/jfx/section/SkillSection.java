package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.Section;
import org.prelle.javafx.SymbolIcon;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.NumericalValueWith1PoolController;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.rules.SkillTable;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunSkillTable;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySkillGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SRSkillSettingsPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.SkillSelector;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CharacterLeveller;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SkillSection extends Section {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(ShadowrunSkillTable.class.getName());

	protected Logger logger = System.getLogger(getClass().getPackageName());

	private SR6CharacterController control;
	protected Shadowrun6Character model;
	private SkillType[] type;

	private ToggleSwitch tsExpertMode;
	private Label lbPoints;
	private HBox line;
	private SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue> table;
	protected Button btnAdd;
	protected Button btnDel;
	private ToggleSwitch cbRuleUndoCareer;
	private ToggleSwitch cbRuleUndoChargen;

	private IntegerProperty flexWidthProperty = new SimpleIntegerProperty(4);

	//-------------------------------------------------------------------
	public SkillSection(String title, SkillType... type) {
		super(title, null);
		this.type = type;
		if (type.length==1)
			setId("skills-"+type[0].name().toLowerCase());
		else
			setId("skills-multiple");
		initComponents();
		initLayout();
		initInteractivity();
//		table.setData(Shadowrun6Core.getItemList(SR6Skill.class));
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		table = new SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue>();
		table.setMaxHeight(Double.MAX_VALUE);
		table.setAttributeMode(SkillTable.Mode.NO_ATTRIB);
		table.setHidePoolColumn(true);
		btnAdd = new Button(null, new SymbolIcon("add"));
		btnDel = new Button(null, new SymbolIcon("delete"));
		getButtons().addAll(btnAdd, btnDel);
	}

	private void initLine() {
		tsExpertMode = new ToggleSwitch("Expert");
		lbPoints = new Label("?");
		lbPoints.setStyle("-fx-text-fill: -fx-text-base-color");

		Label hdPoints1 = new Label(ResourceI18N.get(RES, "head.points")+":");

		line = new HBox(5, tsExpertMode, hdPoints1, lbPoints);
		VBox layout = new VBox(5, line, table);
		setContent(new ScrollPane(layout));
	}

	//-------------------------------------------------------------------
	private void initSecondaryContent() {
		cbRuleUndoCareer = new ToggleSwitch();
		cbRuleUndoCareer.setGraphicTextGap(0);
		cbRuleUndoChargen       = new ToggleSwitch();
		cbRuleUndoChargen.setGraphicTextGap(0);

		cbRuleUndoCareer.selectedProperty().addListener( (ov,o,n) -> {
			if (control!=null) { control.getRuleController().setRuleValue(ShadowrunRules.CAREER_UNDO_FROM_CAREER, n); control.runProcessors(); }
		});
		cbRuleUndoChargen.selectedProperty().addListener( (ov,o,n) -> {
			if (control!=null) { control.getRuleController().setRuleValue(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN, n); control.runProcessors(); }
		});

		setMode(Mode.REGULAR);
		VBox bxRules = new VBox(10);
		bxRules.getChildren().add(makeLabel(cbRuleUndoChargen, ShadowrunRules.CAREER_UNDO_FROM_CHARGEN));
		bxRules.getChildren().add(makeLabel(cbRuleUndoCareer , ShadowrunRules.CAREER_UNDO_FROM_CAREER));
		setSecondaryContent(bxRules);

	}

	//-------------------------------------------------------------------
	private Label makeLabel(ToggleSwitch ts, Rule rule) {
		Label lb = new Label(rule.getName(Locale.getDefault()), ts);
		lb.setWrapText(true);
		lb.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(lb, new Insets(0, 0, 0, -20));
		return lb;
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		//setContent(table);
		initLine();
		initSecondaryContent();
	}

	//-------------------------------------------------------------------
	public ReadOnlyIntegerProperty flexWidthProperty() { return flexWidthProperty; }

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnAdd.setOnAction(ev -> onAdd());
		btnDel.setOnAction(ev -> onDelete(table.getSelectionModel().getSelectedItem()));
		btnDel.setDisable(true);
		table.useExpertModeProperty().bind(tsExpertMode.selectedProperty());
		table.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> btnDel.setDisable(n==null || !control.getSkillController().canBeDeselected(n).get()));
		table.setActionCallback( v -> openActionDialog(v));
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (control!=null && control.getSkillController()!=null) {
			SR6SkillController skCtrl = control.getSkillController();
			table.setData(
					control.getSkillController().getSelected().stream().filter(sv -> Arrays.asList(type).contains(sv.getModifyable().getType())).collect(Collectors.toList())
					);
			if (skCtrl instanceof NumericalValueWith1PoolController) {
				lbPoints.setText(String.valueOf(((NumericalValueWith1PoolController<?,?>)skCtrl).getPointsLeft()));
			}

			// Secondary content
			cbRuleUndoCareer.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER));
			cbRuleUndoChargen.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN));
		}
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<SR6SkillValue> selectedSkillProperty() {
		return table.getSelectionModel().selectedItemProperty();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public void updateController(CharacterController ctrl) {
		control = (SR6CharacterController) ctrl;
		System.err.println("#############SkillSection.updateController with model "+control.getSkillController().getClass());
		logger.log(Level.INFO, "#############updateController with model "+control.getModel());
		if (control.getModel()==null) throw new NullPointerException("Controller has NULL as model");
		table.setModel(control.getModel());
		table.setController(control.getSkillController());
		table.setData(
				control.getSkillController().getSelected().stream().filter(sv -> Arrays.asList(type).contains(sv.getModifyable().getType())).collect(Collectors.toList())
				);

		table.useExpertModeProperty().addListener( (ov,o,n) -> flexWidthProperty.set(n?7:6));
		if (control.getSkillController() instanceof SR6PrioritySkillGenerator
				|| control.getSkillController() instanceof SR6PointBuySkillGenerator) {
			line.setVisible(true);
			line.setManaged(true);
		} else {
			line.setVisible(false);
			line.setManaged(false);
		}
		if (ctrl instanceof SR6CharacterLeveller) {
			setMode(Mode.BACKDROP);
		} else {
		}
	}

	//-------------------------------------------------------------------
	private void onAdd() {
		if (type.length==1 && type[0]==SkillType.LANGUAGE) {
			SR6Skill lang = Shadowrun6Core.getSkill("language") ;
			ChoiceSelectorDialog<SR6Skill, SR6SkillValue> dialog = new ChoiceSelectorDialog<SR6Skill, SR6SkillValue>(control.getSkillController());
			Decision[] dec = dialog.apply(lang, lang.getChoices());
			if (dec!=null) {
				OperationResult<SR6SkillValue> result = control.getSkillController().select(lang, dec);
				if (result.wasSuccessful()) {
					table.getItems().add(result.get());
					table.refresh();
				} else {
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, result.getError());
				}
			}
		} else {
			SkillSelector selector = new SkillSelector(control, (skill) -> {
				switch (skill.getType()) {
				case LANGUAGE: case KNOWLEDGE: return false;
				default:
					return true;
				}
			});
			NavigButtonControl btnCtrl = new NavigButtonControl();
			selector.setButtonControl(btnCtrl);
			ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(SkillSelector.RES, "selector.skill.title"), selector, CloseType.OK, CloseType.CANCEL);
			CloseType closed = FlexibleApplication.getInstance().showAlertAndCall(dialog, btnCtrl);
			logger.log(Level.INFO, "Closed via {0}",closed);
			if (closed==CloseType.OK) {
				OperationResult<SR6SkillValue> res = control.getSkillController().select(selector.getSelected());
				logger.log(Level.INFO, "Result was {0}",res);
				if (!res.wasSuccessful()) {
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, res.toString());
					return;
				}
				// If this was the exotic weapons, immediately select a specialization
				if (res.wasSuccessful() && selector.getSelected().getId().equals("exotic_weapons")) {
					openActionDialog(res.get());
				}
			}
		}
	}

	//-------------------------------------------------------------------
	private void onDelete(SR6SkillValue item) {
		logger.log(Level.DEBUG, "onDelete");
		if (control.getSkillController().deselect(item)) {
			table.getItems().remove(item);
			table.refresh();
		} else
			logger.log(Level.WARNING, "deselecting {0} failed", item);
	}

	//-------------------------------------------------------------------
	private CloseType openActionDialog(SR6SkillValue sVal) {
		logger.log(Level.INFO, "openActionDialog({0})", sVal);

		SRSkillSettingsPane pane = new SRSkillSettingsPane(sVal, control.getSkillController());
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "dialog.specializations.title"), pane, CloseType.OK);
		CloseType close = FlexibleApplication.getInstance().showAlertAndCall(dialog, null);
		return close;
	}

}
