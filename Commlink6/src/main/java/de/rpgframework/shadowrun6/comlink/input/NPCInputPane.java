package de.rpgframework.shadowrun6.comlink.input;

import java.io.IOException;

import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class NPCInputPane extends VBox {

	private AttributeInputPane<SR6Skill,SR6SkillValue> attributes;
	private SkillsInputPane skills;
	private GearInputPane gear;

	private Button btnGenerate;
	private SR6NPC model;

	//-------------------------------------------------------------------
	/**
	 */
	public NPCInputPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		attributes = new AttributeInputPane<SR6Skill,SR6SkillValue>();
		skills = new SkillsInputPane();
		gear = new GearInputPane();
		btnGenerate = new Button("Generate");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setSpacing(20);
		getChildren().addAll(attributes, new HBox(20,skills,gear), btnGenerate);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnGenerate.setOnAction(ev -> {
			try {
				Shadowrun6Core.getPersister().write(model, System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	//-------------------------------------------------------------------
	public void setModel(SR6NPC npc) {
		this.model = npc;
		attributes.setModel(npc);
		skills.setModel(npc);
		gear.setModel(npc);
	}

}
