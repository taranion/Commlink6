package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.WindowMode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.section.PersonaSection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

/**
 *
 */
public class PersonaConfigPane extends VBox implements ResponsiveControl {

	protected final static Logger logger = System.getLogger(PersonaConfigPane.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(PersonaConfigPane.class.getPackageName()+".Panes");

	protected SR6CharacterController control;
	protected Shadowrun6Character model;

	private RadioButton chkModeAR, chkModeVR, chkModeHot;
	private ToggleGroup grpInterfaceMode;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbProcessDevice;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbAttackDevice;

	private Label cbARGloves, cbImageLink, cbSoundLink;
	private Label cbCyberjack, cbDatajack, cbControlRig, cbTrodes;
	private Label cbHotSim;
	private List<Label> itemLabels;

	//-------------------------------------------------------------------
	/**
	 */
	public PersonaConfigPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	public String get(String key) {
		return Shadowrun6Core.getItem(ItemTemplate.class, key).getName();
	}

	//-------------------------------------------------------------------
	public Label createLabel(String key) {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, key);
		Label label = new Label(item.getName() );
		label.setUserData(item);
		return label;
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		chkModeAR  = new RadioButton();
		chkModeVR  = new RadioButton();
		chkModeHot = new RadioButton();
		grpInterfaceMode = new ToggleGroup();
		grpInterfaceMode.getToggles().addAll(chkModeAR, chkModeVR, chkModeHot);

		cbProcessDevice = new ChoiceBox<>();
		cbAttackDevice  = new ChoiceBox<>();
		cbProcessDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});
		cbAttackDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});


		cbARGloves   = createLabel("ar_gloves");
		cbImageLink  = createLabel("image_link");
		cbSoundLink  = createLabel("soundlink");
		cbCyberjack  = createLabel("cyberjack");
		cbDatajack   = createLabel("datajack");
		cbControlRig = createLabel("control_rig");
		cbTrodes     = createLabel("trodes");
		cbHotSim     = createLabel("sim_module_hot");
		itemLabels   = List.of(cbARGloves, cbImageLink, cbSoundLink, cbCyberjack, cbDatajack, cbControlRig, cbTrodes, cbHotSim);
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		Label headAR = new Label(ResourceI18N.get(RES, "pane.persona.ifmode.ar"));
		Label headVR = new Label(ResourceI18N.get(RES, "pane.persona.ifmode.vr"));
		Label headHot= new Label(ResourceI18N.get(RES, "pane.persona.ifmode.hot"));
		headAR.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		headVR.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		headHot.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		TextFlow flowAR = new TextFlow(new Label(ResourceI18N.get(RES, "pane.persona.ifmode.support")+": "));
		flowAR.setLineSpacing(2);
		flowAR.getChildren().add(cbARGloves); flowAR.getChildren().add(new Label(", "));
		flowAR.getChildren().add(cbImageLink); flowAR.getChildren().add(new Label(", "));
		flowAR.getChildren().add(cbSoundLink);
		TextFlow flowVR = new TextFlow(new Label(ResourceI18N.get(RES, "pane.persona.ifmode.requires")+": "));
		flowVR.getChildren().add(cbCyberjack); flowVR.getChildren().add(new Label(", "));
		flowVR.getChildren().add(cbDatajack); flowVR.getChildren().add(new Label(", "));
		flowVR.getChildren().add(cbControlRig); flowVR.getChildren().add(new Label(", "));
		flowVR.getChildren().add(cbTrodes);
		TextFlow flowHot = new TextFlow(new Label(ResourceI18N.get(RES, "pane.persona.ifmode.requires")+": "));
		flowHot.getChildren().add(cbHotSim); flowHot.getChildren().add(new Label(", "));
		flowHot.getChildren().add(new Label(ResourceI18N.get(RES, "pane.persona.ifmode.orCyberdeck")));

		// Device Grid
		Label lbInterfaceMode = new Label(ResourceI18N.get(RES, "pane.persona.ifmode.title"));
		lbInterfaceMode.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		Label lbProcessDevice = new Label(ResourceI18N.get(RES, "pane.persona.procdevice"));
		Label lbAttackDevice = new Label(ResourceI18N.get(RES, "pane.persona.attackdevice"));
		lbProcessDevice.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbAttackDevice.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		GridPane deviceGrid = new GridPane();
		deviceGrid.setVgap(5); deviceGrid.setHgap(5);
		deviceGrid.add(lbInterfaceMode, 0, 0);
		deviceGrid.add(     chkModeAR , 1, 0);
		deviceGrid.add(        headAR , 2, 0);
		deviceGrid.add(        flowAR , 2, 1);
		deviceGrid.add(     chkModeVR , 1, 2);
		deviceGrid.add(        headVR , 2, 2);
		deviceGrid.add(        flowVR , 2, 3);
		deviceGrid.add(    chkModeHot , 1, 4);
		deviceGrid.add(        headHot, 2, 4);
		deviceGrid.add(        flowHot, 2, 5);
		deviceGrid.add(lbProcessDevice, 0, 6);
		deviceGrid.add(cbProcessDevice, 1, 6, 2,1);
		deviceGrid.add(lbAttackDevice , 0, 7);
		deviceGrid.add(cbAttackDevice , 1, 7, 2,1);
		getChildren().add(deviceGrid);
		GridPane.setValignment(lbInterfaceMode, VPos.TOP);
		deviceGrid.getColumnConstraints().add(new ColumnConstraints(130));
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbAttackDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary AS device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});
		cbProcessDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary DF device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});

	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		List<CarriedItem<ItemTemplate>> asItems = new ArrayList<>();
		List<CarriedItem<ItemTemplate>> dfItems = new ArrayList<>();
		CarriedItem<ItemTemplate> primaryAS = null;
		CarriedItem<ItemTemplate> primaryDF = null;
		for (CarriedItem<ItemTemplate> item : model.getCarriedItemsRecursive()) {
			if (!item.hasFlag(SR6ItemFlag.MATRIX_DEVICE))
				continue;

			if (item.hasAttribute(SR6ItemAttribute.ATTACK)) {
				asItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY))
					primaryAS = item;
			} else if (item.hasAttribute(SR6ItemAttribute.DATA_PROCESSING)) {
				dfItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY))
					primaryDF = item;
			} else {
				logger.log(Level.WARNING, "Found matrix item with either ATTACK nor DATA_PROCESSING: {0}", item);
			}
		}
		logger.log(Level.DEBUG, "refresh with asItems={0} and dfItems={1}", asItems, dfItems);
		logger.log(Level.DEBUG, "refresh with primaryAS={0} and primaryDF={1}", primaryAS, primaryDF);

		// Update, if necessary
		if (!cbAttackDevice.getItems().containsAll(asItems)) {
			cbAttackDevice.getItems().setAll(asItems);
		}
		if (!cbProcessDevice.getItems().containsAll(dfItems)) {
			cbProcessDevice.getItems().setAll(dfItems);
		}
		cbAttackDevice.setValue(primaryAS);
		cbProcessDevice.setValue(primaryDF);

		// Update item labels
		for (Label itemLabel : itemLabels) {
			ItemTemplate temp = (ItemTemplate)itemLabel.getUserData();
			boolean found = model.getCarriedItemsRecursive().stream().anyMatch(ci -> ci.getResolved()==temp);
			if (found) {
				Label icon = new Label("\u25A0");
				icon.setStyle("-fx-text-fill: green");
				itemLabel.setGraphic(icon);
			} else {
				Label icon = new Label("\u25A1");
				icon.setStyle(null);
				itemLabel.setGraphic(icon);
			}
		}


//		// Update ASDF values
//		if (primaryAS!=null) {
//			lbAttack.setText( String.valueOf(primaryAS.getAsValue(SR6ItemAttribute.ATTACK).getModifiedValue()));
//			lbSleaze.setText( String.valueOf(primaryAS.getAsValue(SR6ItemAttribute.SLEAZE).getModifiedValue()));
//		} else {
//			lbAttack.setText("-"); lbSleaze.setText("-");
//		}
//		if (primaryDF!=null) {
//			lbDatap.setText( String.valueOf(primaryDF.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue()));
//			lbFirew.setText( String.valueOf(primaryDF.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue()));
//		} else {
//			lbDatap.setText("-"); lbFirew.setText("-");
//		}
//
//		// Defense rating
//		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_MATRIX);
//		if (aVal==null)
//			lbDefenseRating.setText("?");
//		else {
//			lbDefenseRating.setText( String.valueOf(aVal.getModifiedValue()) );
//			if (aVal.getPool()!=null) {
//				lbDefenseRating.setText( aVal.getPool().toString() );
//				lbDefenseRating.setTooltip(new Tooltip(aVal.getPool().toExplainString()));
//			} else {
//				lbDefenseRating.setTooltip(null);
//			}
//		}
	}

}
