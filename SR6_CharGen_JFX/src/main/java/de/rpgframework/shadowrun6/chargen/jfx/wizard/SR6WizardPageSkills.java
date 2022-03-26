package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.public_skins.GridPaneTableViewSkin.HeaderLine;

import de.rpgframework.jfx.rules.AttributeTable.Mode;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunSkillTable;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SkillTablePrioSkin;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SRSkillSettingsPane;

/**
 * @author prelle
 *
 */
public class SR6WizardPageSkills extends WizardPageSkills<SR6Skill, SR6SkillValue, Shadowrun6Character> {

	private final static Logger logger = System.getLogger(SR6WizardPageSkills.class.getPackageName());

	//-------------------------------------------------------------------
	public SR6WizardPageSkills(Wizard wizard, SR6CharacterGenerator charGen) {
		super(wizard, charGen);
		logger.log(Level.INFO, "Created with charGen="+charGen);
		if (getContent()==null) {
			logger.log(Level.ERROR, "No content");
		}
		bxDescription.setResolver( req -> Shadowrun6Tools.getRequirementString(req, Locale.getDefault()));
		
		table.setActionCallback(sVal -> openActionDialog(sVal));
		table.setSelectedListModifier(data -> injectHeader(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes#getTableForController(de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator)
	 */
	@Override
	protected ShadowrunSkillTable<SR6Skill,SR6SkillValue, Shadowrun6Character> getTableForController(
			IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, Shadowrun6Character> controller) {
		logger.log(Level.INFO, "getTableForController("+controller+")");
		// TODO Auto-generated method stub
		IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, Shadowrun6Character> realCtrl = controller;
		if (controller instanceof GeneratorWrapper) {
			realCtrl = ((GeneratorWrapper)controller).getWrapped();
		}
		
		ShadowrunSkillTable<SR6Skill,SR6SkillValue, Shadowrun6Character> ret = new ShadowrunSkillTable<>(controller, Mode.GENERATE);
		if (realCtrl instanceof IPriorityGenerator) {
			ret.setSkin(new SR6SkillTablePrioSkin(ret));
//		} else if (realCtrl instanceof PointBuyCharacterGenerator) {
//			return new PointBuyAttributeTable<>(controller);
		} else {
			logger.log(Level.ERROR, "Don't know what to return for "+realCtrl);
			System.err.println("SR6WizardPageSkills: Don't know what to return for "+realCtrl);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private CloseType openActionDialog(SR6SkillValue sVal) {
		logger.log(Level.INFO, "openActionDialog({})", sVal);
		
		SRSkillSettingsPane pane = new SRSkillSettingsPane(sVal, charGen.getSkillController());
		ManagedDialog dialog = new ManagedDialog("Settings", pane, CloseType.OK);
		CloseType close = FlexibleApplication.getInstance().showAlertAndCall(dialog, null);
		return close;
	}

	//-------------------------------------------------------------------
	private void injectHeader(List<SR6SkillValue> data) {
		boolean hadKnowledgeHeader = false;
		boolean hadLanguageHeader = false;
		for (int i=0; i<data.size(); i++) {
			SR6SkillValue sVal = data.get(i);
			SR6Skill skill = sVal.getModifyable();
			
			if (sVal instanceof SR6SkillValueHeader) {
				if (skill.getType()==SkillType.KNOWLEDGE) hadKnowledgeHeader=true;
				if (skill.getType()==SkillType.LANGUAGE ) hadLanguageHeader=true;
				continue;
			}
			
			if (skill.getType()==SkillType.KNOWLEDGE && !hadKnowledgeHeader) {
				SR6SkillValueHeader toAdd = new SR6SkillValueHeader(skill.getType().getName(Locale.getDefault()));
				toAdd.setResolved(skill);
				data.add(i++, toAdd);
				hadKnowledgeHeader=true;
				continue;
			}
			if (skill.getType()==SkillType.LANGUAGE && !hadLanguageHeader) {
				SR6SkillValueHeader toAdd = new SR6SkillValueHeader(skill.getType().getName(Locale.getDefault()));
				toAdd.setResolved(skill);
				data.add(i++, toAdd);
				hadLanguageHeader=true;
				continue;
			}
		}
		
	}

}

class SR6SkillValueHeader extends SR6SkillValue implements HeaderLine {
	
	private String name;
	
	public SR6SkillValueHeader(String header) {
		this.name = header;
	}
	
	public String getName() {
		return name;
	}
}

