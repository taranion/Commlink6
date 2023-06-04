package de.rpgframework.shadowrun6.comlink.input;

import java.util.List;

import de.rpgframework.genericrpg.data.ASkillValue;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.ISkill;
import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.IShadowrunLifeform;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Spell;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class AttributeInputPane<S extends ISkill, V extends ASkillValue<S>> extends GridPane {

	private final static ShadowrunAttribute[] ATTRIBS = new ShadowrunAttribute[] {
			ShadowrunAttribute.BODY, ShadowrunAttribute.AGILITY, ShadowrunAttribute.REACTION, ShadowrunAttribute.STRENGTH,
			ShadowrunAttribute.WILLPOWER, ShadowrunAttribute.LOGIC, ShadowrunAttribute.INTUITION, ShadowrunAttribute.CHARISMA,
			ShadowrunAttribute.MAGIC, ShadowrunAttribute.RESONANCE, ShadowrunAttribute.ESSENCE
	};

	private Label[] label ;
	private TextField[] input ;

	private ANPC<S, V, SR6Spell> model;

	//-------------------------------------------------------------------
	public AttributeInputPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		input = new TextField[ATTRIBS.length];
		for (int i=0; i<input.length; i++) {input[i]=new TextField(); input[i].setPrefColumnCount(2);}
		label = new Label[ATTRIBS.length];
		for (int i=0; i<label.length; i++) {label[i]=new Label(ATTRIBS[i].getShortName()); label[i].setStyle("-fx-font-weight: bold");}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		for (int x=0; x<label.length; x++) {
			this.add(label[x], x, 0);
			this.add(input[x], x, 1);
		}
	}

	//-------------------------------------------------------------------
	private void updateInteractivity() {
		for (int x=0; x<input.length; x++) {
			ShadowrunAttribute attr = ATTRIBS[x];
			input[x].textProperty().addListener( (ov,o,n) -> {
					AttributeValue<ShadowrunAttribute> sVal = model.getAttribute(attr);
					if (n==null) {
						sVal.setDistributed(0);
						if (sVal!=null) {
							sVal.setDistributed(0);
							//model.removeSkillValue(sVal);
						}
					} else {
						try {
							int val = Integer.parseInt(n);
							if (val==0 && sVal!=null) {
								sVal.setDistributed(0);
								//model.removeSkillValue(sVal);
							} else if (val!=0) {
								if (sVal==null) {
									sVal = new AttributeValue<ShadowrunAttribute>(attr, val);
									model.addAttribute(sVal);
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
	public void setModel(ANPC<S, V, SR6Spell> model) {
		this.model = model;
		updateInteractivity();
		for (ShadowrunAttribute key : ATTRIBS) {
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(key);
			if (aVal!=null) {
				input[List.of(ATTRIBS).indexOf(key)].setText( String.valueOf(aVal.getDistributed()));
			} else {
				input[List.of(ATTRIBS).indexOf(key)].clear();
			}

		}
	}


}
