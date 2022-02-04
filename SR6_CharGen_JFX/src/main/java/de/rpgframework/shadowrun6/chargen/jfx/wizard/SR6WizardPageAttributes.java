package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.Wizard;

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
import de.rpgframework.shadowrun6.chargen.gen.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;

/**
 * @author prelle
 *
 */
public class SR6WizardPageAttributes extends WizardPageAttributes<SR6Skill, SR6SkillValue, Shadowrun6Character> {

	private final static Logger logger = System.getLogger(SR6WizardPageAttributes.class.getPackageName());

	//-------------------------------------------------------------------
	public SR6WizardPageAttributes(Wizard wizard, SR6CharacterGenerator charGen) {
		super(wizard, charGen);
		logger.log(Level.INFO, "Created with charGen="+charGen);
		if (getContent()==null) {
			logger.log(Level.ERROR, "No content");
			System.err.println("SR6WizardPageAttributes<init>: No content");
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes#getTableForController(de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator)
	 */
	@Override
	protected ShadowrunAttributeTable<SR6Skill, SR6SkillValue, Shadowrun6Character> getTableForController(
			IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, Shadowrun6Character> controller) {
		logger.log(Level.INFO, "getTableForController("+controller+")");
		// TODO Auto-generated method stub
		IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, Shadowrun6Character> realCtrl = controller;
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

}
