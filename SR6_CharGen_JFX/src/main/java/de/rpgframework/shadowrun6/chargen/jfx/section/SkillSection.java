package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Section;
import org.prelle.javafx.SymbolIcon;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.NumericalValueWith1PoolController;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.rules.SkillTable;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunSkillTable;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SRSkillSettingsPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
	private SkillType[] type;

	private ToggleSwitch tsExpertMode;
	private Label lbPoints;
	private SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue> table;
	protected Button btnAdd;
	protected Button btnDel;

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
		btnAdd = new Button(null, new SymbolIcon("add"));
		btnDel = new Button(null, new SymbolIcon("delete"));
		getButtons().addAll(btnAdd, btnDel);
	}
	
	private void initLine() {
		tsExpertMode = new ToggleSwitch("Expert");
		lbPoints = new Label("?");
		lbPoints.setStyle("-fx-text-fill: -fx-text-base-color");

		Label hdPoints1 = new Label(ResourceI18N.get(RES, "head.points")+":");
		
		HBox line = new HBox(5, tsExpertMode, hdPoints1, lbPoints);
		VBox layout = new VBox(5, line, table);
		setContent(layout);		
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		//setContent(table);
		initLine();
		
		CheckBox cb1 = new CheckBox("Configuration Setting 1");
		CheckBox cb2 = new CheckBox("Configuration Setting 2");
		VBox back = new VBox(5, cb1, cb2);
		setSecondaryContent(back);
	}

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
		logger.log(Level.WARNING, "refresh");
		if (control!=null && control.getSkillController()!=null) {
			SR6SkillController skCtrl = control.getSkillController();
			table.setData(
					control.getSkillController().getSelected().stream().filter(sv -> Arrays.asList(type).contains(sv.getModifyable().getType())).collect(Collectors.toList())
					);
			if (skCtrl instanceof NumericalValueWith1PoolController) {
				lbPoints.setText(String.valueOf(((NumericalValueWith1PoolController<?,?>)skCtrl).getPointsLeft()));
			}
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
		logger.log(Level.INFO, "#############updateController with model "+control.getModel());
		if (control.getModel()==null) throw new NullPointerException("Controller has NULL as model");
		table.setModel(control.getModel());
		table.setController(control.getSkillController());
		table.setData(
				control.getSkillController().getSelected().stream().filter(sv -> Arrays.asList(type).contains(sv.getModifyable().getType())).collect(Collectors.toList())
				);
	}

	//-------------------------------------------------------------------
	private void onAdd() {
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
		ManagedDialog dialog = new ManagedDialog("Settings", pane, CloseType.OK);
		CloseType close = FlexibleApplication.getInstance().showAlertAndCall(dialog, null);
		return close;
	}

}
