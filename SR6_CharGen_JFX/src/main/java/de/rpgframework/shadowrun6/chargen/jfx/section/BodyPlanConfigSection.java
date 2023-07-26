package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.Section;
import org.prelle.javafx.TitledComponent;

import de.rpgframework.ResourceI18N;
import de.rpgframework.classification.Gender;
import de.rpgframework.genericrpg.NumericalValue;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class BodyPlanConfigSection extends Section {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(BodyPlanConfigSection.class.getPackageName()+".Section");

	private SR6CharacterController control;

	private TextField tfArmPairs;
	private ChoiceBox<String> cbLowerBody;
	private ChoiceBox<String> cbTail;

	//-------------------------------------------------------------------
	public BodyPlanConfigSection() {
		super(ResourceI18N.get(RES, "section.bodyplan.title"), null);

		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfArmPairs = new TextField();
		tfArmPairs.setPrefColumnCount(3);
		cbLowerBody     = new ChoiceBox<String>();
//		cbLowerBody.setConverter(new StringConverter<Gender>() {
//			public String toString(Gender v) { return (v!=null)?ResourceI18N.get(RES,"gender."+v.name().toLowerCase()):"?"; }
//			public Gender fromString(String string) {return null;}
//			});
		cbLowerBody.getItems().addAll(List.of("Two Legs","Centaur","Serpentine","Wheels","Spider"));

		cbTail     = new ChoiceBox<String>();
		cbTail.getItems().addAll(List.of("None","Balance","Prehensile","Thagomizer","Swimming"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		FlowPane layout = new FlowPane(
				new TitledComponent(ResourceI18N.get(RES, "section.bodyplan.armpairs"),  tfArmPairs).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "section.bodyplan.lowerbody"), cbLowerBody).setTitleMinWidth(120d),
				new TitledComponent(ResourceI18N.get(RES, "section.bodyplan.tail"), cbTail).setTitleMinWidth(120d)
				);
		layout.setPrefWrapLength(300);
		layout.setVgap(5);
		layout.setHgap(10);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		tfArmPairs.textProperty().addListener( (ov,o,n) -> { control.runProcessors();});
//		cbLowerBody.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> control.getModel().setGender(n));
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		System.getLogger(BodyPlanConfigSection.class.getPackageName()).log(Level.INFO, "updateController to "+ctrl);
		this.control = ctrl;
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (control==null)
			return;
		Shadowrun6Character model = control.getModel();
	}

}
