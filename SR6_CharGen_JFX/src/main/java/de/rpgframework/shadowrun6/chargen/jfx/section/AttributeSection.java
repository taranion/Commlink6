package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.Section;

import de.rpgframework.jfx.rules.AttributeTable.Mode;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.jfx.LevellingAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PrioritySR6AttributeGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;
import de.rpgframework.shadowrun6.chargen.lvl.SR6AttributeLeveller;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class AttributeSection extends Section {

	private final static Logger logger = System.getLogger(AttributeSection.class.getPackageName());

	private ShadowrunAttributeTable<SR6Skill,SR6SkillValue,Shadowrun6Character> table;
	
	private Mode mode = Mode.GENERATE;

	//-------------------------------------------------------------------
	public AttributeSection(String title, ScreenManagerProvider provider) {
		super(title, null);
		logger.log(Level.DEBUG, "<init>");

		initLayoutNormal();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initLayoutNormal() {
//		HBox layout = new HBox(table);
//		layout.setStyle("-fx-spacing: 1em;");
		
		setContent(table);
		setMode(org.prelle.javafx.Mode.BACKDROP);
		
		
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
		logger.log(Level.DEBUG, "updateController");
		
		Shadowrun6Character model = ctrl.getModel();

		IAttributeController attrib = ctrl.getAttributeController();
//		table.setModel(ctrl.getModel());
//		table.setMode(AttributeTable.Mode.GENERATE);
		if (attrib instanceof PrioritySR6AttributeGenerator) {
 			System.err.println("AttributeSection: change controller to Priority");
			table = new PriorityAttributeTable<>(ctrl);
		} else if (attrib instanceof SR6PointBuyAttributeGenerator) {
 			System.err.println("AttributeSection: change controller to Point Buy");
			table = new PointBuyAttributeTable<>(ctrl);
		} else if (attrib instanceof SR6AttributeLeveller) {
			table = new LevellingAttributeTable<>(ctrl);
		} else {
			System.err.println("AttributeSection: change controller to Karma");
			logger.log(Level.ERROR, "Don't support controller "+model.getCharGenUsed());
		}
		
		MagicOrResonanceType mor = model.getMagicOrResonanceType();
		if (mor != null) {
			table.setShowMagic(mor.usesMagic());
			table.setShowResonance(mor.usesResonance());
		}

		setContent(table);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		logger.log(Level.DEBUG, "refresh");
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
