package de.rpgframework.shadowrun6.chargen.jfx.section;

import org.prelle.javafx.Mode;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.Section;

import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.jfx.KarmaAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTablePrioritySkin;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTableSkin;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.AttributeTable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class AttributeSection extends Section {

	private ShadowrunAttributeTable<SR6Skill,SR6SkillValue,Shadowrun6Character> table;

	//-------------------------------------------------------------------
	public AttributeSection(String title, ScreenManagerProvider provider) {
		super(title, null);

		initComponents();
		initLayoutNormal();
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		table = new KarmaAttributeTable<SR6Skill,SR6SkillValue,Shadowrun6Character>();
	}

	//-------------------------------------------------------------------
	private void initLayoutNormal() {
//		HBox layout = new HBox(table);
//		layout.setStyle("-fx-spacing: 1em;");
		
		setContent(table);
		setMode(Mode.BACKDROP);
		
		
		CheckBox cb1 = new CheckBox("Configuration Setting 1");
		CheckBox cb2 = new CheckBox("Configuration Setting 2");
		VBox back = new VBox(5, cb1, cb2);
		setSecondaryContent(back);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		if (getSettingsButton()!=null)
//			getSettingsButton().setOnAction(ev -> onSettings());
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		table.setController(ctrl);
		
		Shadowrun6Character model = ctrl.getModel();
		MagicOrResonanceType mor = model.getMagicOrResonanceType();
		if (mor != null) {
			table.setShowMagic(mor.usesMagic());
			table.setShowResonance(mor.usesResonance());
		}

//		table.setModel(ctrl.getModel());
//		table.setMode(AttributeTable.Mode.GENERATE);
		if (ctrl instanceof IPriorityGenerator) {
 			System.err.println("AttributeSection: change controller to Priority");
			table = new PriorityAttributeTable<>();
		} else {
			System.err.println("AttributeSection: change controller to Karma");
			table = new KarmaAttributeTable<>();
		}
		setContent(table);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		table.refresh();
//		derived.refresh();
	}

//	//-------------------------------------------------------------------
//	public ReadOnlyObjectProperty<BasePluginData> showHelpForProperty() {
//		return showHelpFor;
//	}
//
//	//-------------------------------------------------------------------
//	private void onSettings() {
//		CharacterLeveller ctrl = (CharacterLeveller) control;
//		
//		VBox content = new VBox(20);
//		for (ConfigOption<?> opt : ctrl.getAttributeController().getConfigOptions()) {
//			CheckBox cb = new CheckBox(opt.getName());
//			cb.setSelected((Boolean)opt.getValue());
//			cb.selectedProperty().addListener( (ov,o,n) -> ((ConfigOption<Boolean>)opt).set((Boolean)n));
//			content.getChildren().add(cb);
//		}
//		
//		getManagerProvider().getScreenManager().showAlertAndCall(AlertType.NOTIFICATION, Resource.get(RES,  "dialog.settings.title"), content);
//	}

}
