package de.rpgframework.shadowrun6.chargen.jfx.section;

import org.prelle.javafx.Section;

import de.rpgframework.jfx.rules.SkillTable;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SkillSection extends Section {

	private SR6CharacterController control;
	private SkillType type;

	private SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue> table;

	//-------------------------------------------------------------------
	public SkillSection(String title, SR6CharacterController ctrl, SkillType type) {
		super(title, null);
		this.control = ctrl;
		this.type = type;
		setId("skills-"+type.name().toLowerCase());
		initComponents();
		initLayout();
		table.setData(Shadowrun6Core.getItemList(SR6Skill.class));
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		table = new SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue>(Shadowrun6Core.getSkills(type));
		table.setController(control.getSkillController());
		table.setModel(control.getModel());
		table.setMaxHeight(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setContent(table);
		
		CheckBox cb1 = new CheckBox("Configuration Setting 1");
		CheckBox cb2 = new CheckBox("Configuration Setting 2");
		VBox back = new VBox(5, cb1, cb2);
		setSecondaryContent(back);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		table.refresh();
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<SR6SkillValue> selectedSkillProperty() {
		return table.getSelectionModel().selectedItemProperty();
	}

}
