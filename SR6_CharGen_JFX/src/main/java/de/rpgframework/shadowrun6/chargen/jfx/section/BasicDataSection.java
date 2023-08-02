package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.Section;
import org.prelle.javafx.TitledComponent;

import de.rpgframework.ResourceI18N;
import de.rpgframework.classification.Gender;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonMetatypeGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class BasicDataSection extends Section {

	private final static ResourceBundle RES = PropertyResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private SR6CharacterController control;

	private TextField tfStreetName;
	private TextField tfRealName;
	private ChoiceBox<Gender> cbGender;
	private ChoiceBox<SR6MetaType> cbMetatype;
	private ChoiceBox<MagicOrResonanceType> cbMOR;
	private TextField tfHeat;
	private TextField tfRep;

	//-------------------------------------------------------------------
	public BasicDataSection(String title) {
		super(title, null);

		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfStreetName = new TextField();
		tfRealName   = new TextField();
		tfStreetName.setPrefColumnCount(10);
		cbGender     = new ChoiceBox<Gender>();
		cbGender.setConverter(new StringConverter<Gender>() {
			public String toString(Gender v) { return (v!=null)?ResourceI18N.get(RES,"gender."+v.name().toLowerCase()):"?"; }
			public Gender fromString(String string) {return null;}
			});
		cbGender.getItems().addAll(Gender.values());
		cbMetatype   = new ChoiceBox<SR6MetaType>();
		cbMetatype.setConverter(new StringConverter<SR6MetaType>() {
			public String toString(SR6MetaType v) { return (v!=null)?v.getName():"?"; }
			public SR6MetaType fromString(String string) {return null;}
			});
		cbMetatype.getItems().addAll(Shadowrun6Core.getItemList(SR6MetaType.class));
		cbMOR   = new ChoiceBox<MagicOrResonanceType>();
		cbMOR.setConverter(new StringConverter<MagicOrResonanceType>() {
			public String toString(MagicOrResonanceType v) { return (v!=null)?v.getName():"?"; }
			public MagicOrResonanceType fromString(String string) {return null;}
			});
		cbMOR.getItems().addAll(Shadowrun6Core.getItemList(MagicOrResonanceType.class));
		tfHeat        = new TextField();
		tfHeat.setPrefColumnCount(2);
		tfRep        = new TextField();
		tfRep.setPrefColumnCount(2);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		FlowPane layout = new FlowPane(
				new TitledComponent(ResourceI18N.get(RES, "label.streetname"),  tfStreetName).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "label.realname"), tfRealName).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "label.gender"), cbGender).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "label.metatype"), cbMetatype).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "label.magicOrResonance"), cbMOR).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "label.heat"), tfHeat).setTitleMinWidth(60d),
				new TitledComponent(ResourceI18N.get(RES, "label.reputation"), tfRep)
				);
		layout.setPrefWrapLength(300);
		layout.setVgap(5);
		layout.setHgap(10);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tfStreetName.textProperty().addListener( (ov,o,n) -> {control.getModel().setName(n); control.runProcessors();});
		tfRealName.textProperty().addListener( (ov,o,n) -> {control.getModel().setRealName(n);});
		cbGender.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> control.getModel().setGender(n));
		tfHeat.textProperty().addListener( (ov,o,n) -> {
			try {
				control.getModel().getAttribute(ShadowrunAttribute.HEAT).setDistributed(Integer.parseInt(n));
				tfHeat.getStyleClass().remove("invalid");
				control.runProcessors();
			} catch (NumberFormatException nfe) {
				if (!tfHeat.getStyleClass().contains("invalid"))
					tfHeat.getStyleClass().add("invalid");
			}
		});

		cbMOR.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> {
			if (control instanceof SR6CharacterGenerator) {
				((SR6CharacterGenerator)control).getMagicOrResonanceController().select(n);
			}
		});
		cbMetatype.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> {
			if (control instanceof SR6CharacterGenerator) {
				 ((CommonMetatypeGenerator) ((SR6CharacterGenerator)control).getMetatypeController()).select(n);
			}
		});
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		System.getLogger(BasicDataSection.class.getPackageName()).log(Level.INFO, "updateController to "+ctrl);
		this.control = ctrl;
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (control==null)
			return;
		Shadowrun6Character model = control.getModel();
		tfStreetName.setText(model.getName());
		tfRealName.setText(model.getRealName());
		cbGender.setValue(model.getGender());
		cbMetatype.setValue(model.getMetatype());
		cbMOR.setValue(model.getMagicOrResonanceType());
		if (model.getAttribute(ShadowrunAttribute.HEAT)!=null)
			tfHeat.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.HEAT).getDistributed()));
		if (model.getAttribute(ShadowrunAttribute.REPUTATION)!=null)
			tfRep.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.REPUTATION).getDistributed()));

		cbMetatype.setDisable(model.isInCareerMode());
		cbMOR.setDisable(model.isInCareerMode());
	}

}
