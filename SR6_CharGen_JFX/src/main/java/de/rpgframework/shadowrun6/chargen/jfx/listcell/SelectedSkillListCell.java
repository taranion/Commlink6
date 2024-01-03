package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Locale;
import java.util.function.Supplier;

import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.ChildhoodGenerator.SimpleSkillController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

/**
 *
 */
public class SelectedSkillListCell extends ListCell<SR6Skill> {

	private CheckBox checked;
	private Supplier<SimpleSkillController> controlSup;

	//-------------------------------------------------------------------
	/**
	 */
	public SelectedSkillListCell(Supplier<SimpleSkillController> ctrl) {
		checked = new CheckBox();
		controlSup = ctrl;
		checked.setOnAction(ev -> {
			if (checked.isSelected())
				controlSup.get().select(getItem());
			else
				controlSup.get().deselect(getItem());
		});
	}

	//-------------------------------------------------------------------
	public void updateItem(SR6Skill item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setGraphic(checked);
			setText(item.getName(Locale.getDefault()));

			SimpleSkillController control = controlSup.get();
			checked.setSelected(control.isSelected(item));
			if (control.isSelected(item)) {
				checked.setDisable(false);
			} else {
				checked.setDisable(!control.canBeSelected(item));
			}
		}
	}
}
