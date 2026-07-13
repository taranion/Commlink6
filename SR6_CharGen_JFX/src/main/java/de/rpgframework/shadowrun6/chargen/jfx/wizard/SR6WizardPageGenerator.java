package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.reflect.Field;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.IGeneratorWrapper;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.CommonCharacter;
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
	private static final List<PowerLevel> HOUSE_RULE_LEVELS = List.of(PowerLevel.STREET_LEVEL, PowerLevel.STANDARD, PowerLevel.ELITE);
	private static final List<PowerLevel> OFFICIAL_LIFEPATH_LEVELS = List.of(PowerLevel.STANDARD);

	private IGeneratorWrapper<ShadowrunAttribute, Shadowrun6Character, G> model;
	private ChoiceBox<PowerLevel> cbLevel;
	private ChoiceBox<RuleInterpretation> cbStrictness;
	private ListView<Class<G>> generatorOptions;

	//-------------------------------------------------------------------
	public SR6WizardPageGenerator(Wizard wizard, IGeneratorWrapper<ShadowrunAttribute, Shadowrun6Character, G> model,
			List<Class<G>> values, List<RuleInterpretation> interpretations, Rule[] allRules,
			Function<Class<G>, String[]> nameGetter) {
		super(wizard, model, values, interpretations, allRules, nameGetter);
		this.model = model;

		cbLevel = new ChoiceBox<>(FXCollections.observableArrayList(HOUSE_RULE_LEVELS));
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
		wirePowerLevelFiltering();
		updatePowerLevelOptions();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void wirePowerLevelFiltering() {
		try {
			Field strictnessField = WizardPageGenerator.class.getDeclaredField("cbStrictness");
			strictnessField.setAccessible(true);
			cbStrictness = (ChoiceBox<RuleInterpretation>) strictnessField.get(this);
			cbStrictness.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> updatePowerLevelOptions());

			Field optionsField = WizardPageGenerator.class.getDeclaredField("options");
			optionsField.setAccessible(true);
			generatorOptions = (ListView<Class<G>>) optionsField.get(this);
			generatorOptions.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> updatePowerLevelOptions());
		} catch (ReflectiveOperationException e) {
			System.getLogger(SR6WizardPageGenerator.class.getPackageName()).log(System.Logger.Level.WARNING,
					"Could not bind power level filtering to generator page internals", e);
		}
	}

	//-------------------------------------------------------------------
	private void updatePowerLevelOptions() {
		List<PowerLevel> allowed = isOfficialLifePathSelection()?OFFICIAL_LIFEPATH_LEVELS:HOUSE_RULE_LEVELS;
		if (!cbLevel.getItems().equals(allowed))
			cbLevel.getItems().setAll(allowed);
		PowerLevel selected = cbLevel.getValue();
		if (selected==null || !allowed.contains(selected)) {
			cbLevel.setValue(PowerLevel.STANDARD);
		}
		if (isOfficialLifePathSelection()) {
			model.getModel().getCharGenSettings(CommonSR6GeneratorSettings.class).variant = PowerLevel.STANDARD;
		}
	}

	//-------------------------------------------------------------------
	private boolean isOfficialLifePathSelection() {
		return isLifePathSelected() && !isHouseRulesSelected();
	}

	//-------------------------------------------------------------------
	private boolean isHouseRulesSelected() {
		RuleInterpretation interpretation = cbStrictness!=null?cbStrictness.getValue():null;
		if (interpretation!=null)
			return "houserules".equals(interpretation.getId());
		if (model.getModel() instanceof CommonCharacter) {
			String strictness = ((CommonCharacter<?, ?, ?, ?>)model.getModel()).getStrictness();
			return "houserules".equals(strictness);
		}
		return false;
	}

	//-------------------------------------------------------------------
	private boolean isLifePathSelected() {
		Class<G> selectedGenerator = generatorOptions!=null?generatorOptions.getSelectionModel().getSelectedItem():null;
		if (selectedGenerator!=null)
			return "lifepath".equals(getGeneratorId(selectedGenerator));
		return model.getWrapped()!=null && "lifepath".equals(model.getWrapped().getId());
	}

	//-------------------------------------------------------------------
	private String getGeneratorId(Class<?> generatorClass) {
		GeneratorId annotation = generatorClass.getAnnotation(GeneratorId.class);
		if (annotation!=null)
			return annotation.value();
		return generatorClass.getSimpleName().toLowerCase().contains("lifepath")?"lifepath":generatorClass.getSimpleName();
	}

	//-------------------------------------------------------------------
	/**
	 * Called when a new character has been created - allows to apply power level
	 */
	@Override
	protected void newCharGenCreated(G newGen, Shadowrun6Character model) {
		updatePowerLevelOptions();
		CommonSR6GeneratorSettings settings = model.getCharGenSettings(CommonSR6GeneratorSettings.class);
		settings.variant = isOfficialLifePathSelection()?PowerLevel.STANDARD:cbLevel.getSelectionModel().getSelectedItem();
	}

}
