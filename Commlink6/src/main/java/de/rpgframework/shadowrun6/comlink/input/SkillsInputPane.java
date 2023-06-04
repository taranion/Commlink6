package de.rpgframework.shadowrun6.comlink.input;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.IShadowrunLifeform;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class SkillsInputPane extends GridPane {

	private final static List<SR6Skill> SKILLS = Shadowrun6Core.getItemList(SR6Skill.class);

	private Label[] label ;
	private TextField[] input ;

	private ANPC<SR6Skill,SR6SkillValue, SR6Spell> model;

	static {
		Collections.sort(SKILLS, new Comparator<SR6Skill>() {
			public int compare(SR6Skill o1, SR6Skill o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	//-------------------------------------------------------------------
	public SkillsInputPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		input = new TextField[SKILLS.size()];
		for (int i=0; i<input.length; i++) {input[i]=new TextField(); input[i].setPrefColumnCount(2);}
		label = new Label[SKILLS.size()];
		for (int i=0; i<label.length; i++) {label[i]=new Label(SKILLS.get(i).getName()); label[i].setStyle("-fx-font-weight: bold");}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		for (int y=0; y<label.length; y++) {
			this.add(label[y], 0, y);
			this.add(input[y], 1, y);
		}
	}

	//-------------------------------------------------------------------
	private void updateInteractivity() {
		for (int x=0; x<input.length; x++) {
			SR6Skill attr = SKILLS.get(x);
			input[x].textProperty().addListener( (ov,o,n) -> {
				SR6SkillValue sVal = model.getSkillValue(attr);
				if (n==null) {
					model.getSkillValue(attr).setDistributed(0);
					if (sVal!=null)
					model.removeSkillValue(sVal);
				} else {
					try {
						int val = Integer.parseInt(n);
						if (val==0 && sVal!=null)
							model.removeSkillValue(sVal);
						else if (val!=0) {
							if (sVal==null) {
								sVal = new SR6SkillValue(attr, val);
								model.addSkillValue(sVal);
							} else {
								sVal.setDistributed(val);
							}
						}
					} catch (NumberFormatException e) {
					}
				}
			});
		}
	}

	//-------------------------------------------------------------------
	public void setModel(ANPC<SR6Skill,SR6SkillValue, SR6Spell> model) {
		this.model = model;
		updateInteractivity();
		for (SR6Skill key : SKILLS) {
			SR6SkillValue aVal = model.getSkillValue(key);
			if (aVal!=null) {
				input[SKILLS.indexOf(key)].setText( String.valueOf(aVal.getDistributed()));
			} else {
				input[SKILLS.indexOf(key)].clear();
			}

		}
	}


}
