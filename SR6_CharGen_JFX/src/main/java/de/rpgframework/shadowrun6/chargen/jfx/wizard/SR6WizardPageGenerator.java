package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.IGeneratorWrapper;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.jfx.wizard.WizardPageGenerator;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 *
 */
public class SR6WizardPageGenerator<G extends SR6CharacterGenerator> extends WizardPageGenerator<ShadowrunAttribute, Shadowrun6Character, G> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6WizardPageGenerator.class.getPackageName()+".SR6WizardPages");
	private static final List<PowerLevel> POWER_LEVELS = List.of(PowerLevel.STREET_LEVEL, PowerLevel.STANDARD, PowerLevel.ELITE);

	private IGeneratorWrapper<ShadowrunAttribute, Shadowrun6Character, G> model;
	private ChoiceBox<PowerLevel> cbLevel;

	//-------------------------------------------------------------------
	public SR6WizardPageGenerator(Wizard wizard, IGeneratorWrapper<ShadowrunAttribute, Shadowrun6Character, G> model,
			List<Class<G>> values, List<RuleInterpretation> interpretations, Rule[] allRules,
			Function<Class<G>, String[]> nameGetter) {
		super(wizard, model, values, interpretations, allRules, nameGetter);
		this.model = model;

		cbLevel = new ChoiceBox<>(FXCollections.observableArrayList(POWER_LEVELS));
		cbLevel.setConverter(new StringConverter<PowerLevel>() {
			public String toString(PowerLevel value) { return (value==null)?"-":value.getName(); }
			public PowerLevel fromString(String string) { return null;}
		});
		cbLevel.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				model.getModel().getCharGenSettings(CommonSR6GeneratorSettings.class).variant = n;
				//model.fireEvent(BasicControllerEvents.CHARACTER_CHANGED, model);
				model.runProcessors();
			}
		});
		Label lbLevel = new Label(ResourceI18N.get(RES, "page.generator.powerlevel"));
		lbLevel.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox bxLevel = new HBox(10, lbLevel, cbLevel);
		bxLevel.setAlignment(Pos.CENTER_LEFT);
		cbLevel.setValue( model.getModel().getCharGenSettings(CommonSR6GeneratorSettings.class).variant );
		setExtraNode(bxLevel);
	}

	//-------------------------------------------------------------------
	/**
	 * Called when a new character has been created - allows to apply power level
	 */
	@Override
	protected void newCharGenCreated(G newGen, Shadowrun6Character model) {
		CommonSR6GeneratorSettings settings = model.getCharGenSettings(CommonSR6GeneratorSettings.class);
		settings.variant = cbLevel.getSelectionModel().getSelectedItem();
	}

}
