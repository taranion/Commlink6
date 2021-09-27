package de.rpgframework.shadowrun6.chargen.jfx.section;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.Section;

import de.rpgframework.jfx.rules.AttributeTable;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class AttributeSection extends Section {

	private AttributeTable<ShadowrunAttribute> table;

	//-------------------------------------------------------------------
	public AttributeSection(String title, ScreenManagerProvider provider) {
		super(title.toUpperCase(), null);

		initComponents();
		initLayoutNormal();
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		table = new AttributeTable<ShadowrunAttribute>(ShadowrunAttribute.primaryValues());
	}

	//-------------------------------------------------------------------
	private void initLayoutNormal() {
//		HBox layout = new HBox(table);
//		layout.setStyle("-fx-spacing: 1em;");
		
		setContent(table);
		
		
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
		table.setController(ctrl.getAttributeController());
		table.setModel(ctrl.getModel());
		table.setMode(AttributeTable.Mode.GENERATE);
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
