package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class SR6WizardPageAttributes extends WizardPageAttributes<SR6Skill, SR6SkillValue, Shadowrun6Character> {

	private final static Logger logger = System.getLogger(SR6WizardPageAttributes.class.getPackageName());

	protected NumberUnitBackHeader backHeaderCP;

	//-------------------------------------------------------------------
	public SR6WizardPageAttributes(Wizard wizard, SR6CharacterGenerator charGen) {
		super(wizard, charGen);
		logger.log(Level.DEBUG, "Created with charGen="+charGen);
		if (getContent()==null) {
			logger.log(Level.ERROR, "No content");
			System.err.println("SR6WizardPageAttributes<init>: No content");
		}

	}

	// -------------------------------------------------------------------
	protected void initBackHeader() {
		super.initBackHeader();
		backHeaderCP = new NumberUnitBackHeader(ResourceI18N.get(UI, "label.cp"));
		HBox.setMargin(backHeaderCP, new Insets(0,10,0,10));

		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox box = new HBox(backHeaderKarma, backHeaderCP);
		HBox backHeader = new HBox(10, box, buf);
		HBox.setHgrow(buf, Priority.ALWAYS);
		//backHeader.setMaxWidth(Double.MAX_VALUE);
		HBox.setMargin(box, new Insets(0,0,0,10));
		HBox.setMargin(backHeader.getChildren().get(2), new Insets(0,10,0,0));
		super.setBackHeader(backHeader);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes#getTableForController(de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator)
	 */
	@Override
	protected ShadowrunAttributeTable<SR6Skill, SR6SkillValue, Shadowrun6Character> getTableForController(
			IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, ?, Shadowrun6Character> controller) {
		logger.log(Level.INFO, "getTableForController("+controller+")");
		// TODO Auto-generated method stub
		IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, ?, Shadowrun6Character> realCtrl = controller;
		if (controller instanceof GeneratorWrapper) {
			realCtrl = ((GeneratorWrapper)controller).getWrapped();
		}

		if (realCtrl instanceof IPriorityGenerator) {
			return new PriorityAttributeTable<>(controller);
		} else if (realCtrl instanceof PointBuyCharacterGenerator) {
			return new PointBuyAttributeTable<>(controller);
		}
		logger.log(Level.ERROR, "Don't know what to return for "+realCtrl);
		return null;
	}

	// -------------------------------------------------------------------
	protected void refresh() {
		super.refresh();

		IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, ?, Shadowrun6Character> realCtrl = charGen;
		if (charGen instanceof GeneratorWrapper) {
			realCtrl = ((GeneratorWrapper)charGen).getWrapped();
		}

		if (realCtrl instanceof IPriorityGenerator) {
			backHeaderCP.setVisible(false);
			backHeaderCP.setManaged(false);
		} else if (realCtrl instanceof PointBuyCharacterGenerator) {
			backHeaderCP.setVisible(true);
			backHeaderCP.setManaged(true);
			backHeaderCP.setValue( ((PointBuyCharacterGenerator)realCtrl).getSettings().characterPoints );
		}
	}

}
