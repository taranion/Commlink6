package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.controlsfx.control.ToggleSwitch;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.section.ComplexDataItemListSection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6EmulatedSoftwareController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MetamagicOrEchoController;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.EmulatedProgramCell;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * For technomancers that can emulate programs, either via Echo or via complex form
 * @author prelle
 *
 */
public class EmulatedProgramsSection extends ComplexDataItemListSection<ItemTemplate, CarriedItem<ItemTemplate>> {

	private final static Logger logger = System.getLogger(EmulatedProgramsSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EmulatedProgramsSection.class.getPackageName()+".Section");

	protected SR6CharacterController control;
	private SR6EmulatedSoftwareController emulatedCtrl;
	protected Shadowrun6Character model;

	//-------------------------------------------------------------------
	public EmulatedProgramsSection(String title) {
		super(title);
		list.setCellFactory(lv -> new EmulatedProgramCell( control));

		Label lbIntro = new Label(ResourceI18N.get(RES, "section.emulated_programs.label"));
		lbIntro.setWrapText(true);
		setHeaderNode(lbIntro);

		refresh();
	}

	//-------------------------------------------------------------------
	private Label makeLabel(ToggleSwitch ts, Rule rule) {
		Label lb = new Label(rule.getName(Locale.getDefault()), ts);
		lb.setWrapText(true);
		lb.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(lb, new Insets(0, 0, 0, -20));
		return lb;
	}

	//-------------------------------------------------------------------
	@Override
	protected void selectionChanged(CarriedItem<ItemTemplate> old, CarriedItem<ItemTemplate> neu) {
		logger.log(Level.ERROR, "Selection changed from "+old+" to "+neu);
		if (neu==null) {
			btnDel.setDisable(true);
		} else {
//			Possible possible = (addToContainer!=null)
//					?
//					control.getEquipmentController().canBeRemoved(addToContainer, addToHook, neu)
//							:
//					control.getEquipmentController().canBeDeselected(neu);
//			if (!possible.get()) logger.log(Level.WARNING, "Controller {0} says I cannot deselect/remove {1}", control.getEquipmentController(), neu);
//			btnDel.setDisable( !possible.get() );
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.INFO, "ENTER: onAdd");

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(CarriedItem<ItemTemplate> item) {
		logger.log(Level.INFO, "ENTER: onDelete");
			if (control.getEquipmentController().deselect(item)) {
				refresh();
			}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
//		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		if (model==null) return;

		emulatedCtrl = ((SR6MetamagicOrEchoController)control.getMetamagicOrEchoController()).getEmulatedSoftwareController();
		list.getItems().setAll(emulatedCtrl.getSelected());
	}

}
