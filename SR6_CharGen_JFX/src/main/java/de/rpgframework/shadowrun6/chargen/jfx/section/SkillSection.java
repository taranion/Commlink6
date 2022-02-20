package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;

import org.prelle.javafx.Section;

import de.rpgframework.genericrpg.chargen.CharacterController;
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
	private SkillType[] type;

	private SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue> table;

	//-------------------------------------------------------------------
	public SkillSection(String title, SkillType... type) {
		super(title, null);
		this.type = type;
		if (type.length==1)
			setId("skills-"+type[0].name().toLowerCase());
		else
			setId("skills-multiple");
		initComponents();
		initLayout();
//		table.setData(Shadowrun6Core.getItemList(SR6Skill.class));
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		System.getLogger(getClass().getPackageName()).log(Level.WARNING, "Skills = "+Shadowrun6Core.getSkills(type));
		table = new SkillTable<ShadowrunAttribute,SR6Skill,SR6SkillValue>(Shadowrun6Core.getSkills(type));
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

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public void updateController(CharacterController ctrl) {
		control = (SR6CharacterController) ctrl;
		System.getLogger(getClass().getPackageName()).log(Level.INFO, "#############updateController with model "+control.getModel());
		if (control.getModel()==null) throw new NullPointerException("Controller has NULL as model");
		table.setModel(control.getModel());
		table.setController(control.getSkillController());
		refresh();
	}

}
