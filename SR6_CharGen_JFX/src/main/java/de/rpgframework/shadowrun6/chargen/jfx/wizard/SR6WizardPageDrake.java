package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

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
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.chargen.jfx.CommonShadowrunJFXResourceHook;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageQualities;
import de.rpgframework.shadowrun6.DrakeType;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.pane.DracoformAttributePane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6WizardPageDrake extends WizardPage implements ControllerListener {

	private final static Logger logger = System.getLogger(AWizardPageQualities.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageDrake.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterController charGen;

	private ImageView iView;
	private ChoiceBox<DrakeType> cbDrakeType;
	private DracoformAttributePane paneAttr;
	protected ComplexDataItemControllerNode<MetamagicOrEcho, MetamagicOrEchoValue> selection;
	protected GenericDescriptionVBox bxDescription;
	protected OptionalNodePane layout;
	private NumberUnitBackHeader backHeader;
	private Label lbLevel;
	protected HBox line;

	//-------------------------------------------------------------------
	public SR6WizardPageDrake(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.drake.title"));
		initComponents();
		initLayout();
		initInteractivity();

		charGen.addListener(this);
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		iView = new ImageView();
		iView.setFitHeight(620);
		iView.setFitWidth(620);
		iView.setImage(new Image(CommonShadowrunJFXResourceHook.class.getResourceAsStream("images/metatypes/drakes.png")));
		cbDrakeType = new ChoiceBox<>();
		cbDrakeType.getItems().addAll(Shadowrun6Core.getItemList(DrakeType.class));
		cbDrakeType.setConverter(new StringConverter<DrakeType>() {
			public String toString(DrakeType data) { return (data==null)?"-":data.getName();}
			public DrakeType fromString(String string) {return null;}
		});
		paneAttr = new DracoformAttributePane();

		lbLevel = new Label("?");
		lbLevel.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		selection = new ComplexDataItemControllerNode<>(charGen.getDrakeController());

		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.drake.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.drake.placeholder.selected"));

		selection.setAvailableCellFactory(lv -> new ComplexDataItemListCell<MetamagicOrEcho>( () -> selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));
		selection.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell<MetamagicOrEcho,MetamagicOrEchoValue>( ()->selection.getController() ));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getDrakeController()));

		bxDescription = new GenericDescriptionVBox(Shadowrun6Tools.requirementResolver(Locale.getDefault()), Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		Label hdGain = new Label(ResourceI18N.get(RES, "page.drake.level"));
		line = new HBox(5, hdGain, lbLevel);
		selection.setSelectedListHead(line);

		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
//		if (ResponsiveControlManager.getCurrentMode()==WindowMode.EXPANDED) {
//			super.setBackHeader(null);
//		} else {
			super.setBackHeader(backHeader);
//		}

		VBox content = new VBox(10, cbDrakeType, paneAttr, new HBox(20, iView,selection));

		layout = new OptionalNodePane(content, bxDescription);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbDrakeType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			charGen.getDrakeController().selectDrakeType(n);
		});

		selection.showHelpForProperty().addListener( (ov,o,n) -> {
			logger.log(Level.WARNING, "show help for "+n);
			bxDescription.setData(n);
			if (n!=null) {
				logger.log(Level.WARNING, "  modifications = "+n.getOutgoingModifications());
				layout.setTitle(n.getName());
			} else {
				layout.setTitle(null);
			}
		});
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
		backHeader.setValue(charGen.getModel().getKarmaFree());
		lbLevel.setText( String.valueOf(charGen.getDrakeController().getGrade()));
		selection.refresh();
		paneAttr.refresh();
		activeProperty().set( charGen.getModel().getBodytype()==BodyType.DRAKE);
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
			charGen = (SR6CharacterController) param[0];
			selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getDrakeController()));
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			selection.setController(charGen.getDrakeController());
			paneAttr.setController(charGen.getDrakeController());
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED || type==BasicControllerEvents.GENERATOR_CHANGED) {
			paneAttr.setController(charGen.getDrakeController());
			selection.refresh();
			refresh();
		}
	}

}
