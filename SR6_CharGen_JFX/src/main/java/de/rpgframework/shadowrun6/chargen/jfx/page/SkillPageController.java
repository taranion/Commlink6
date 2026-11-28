package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.KnowledgeSkillSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SkillSection;
import javafx.fxml.FXML;

/**
 * Controller for SkillPage.fxml.
 */
public class SkillPageController {

	private final static Logger logger = System.getLogger(SkillPageController.class.getPackageName());

	@FXML private SkillSection secNormal;
	@FXML private KnowledgeSkillSection secKnowl;
	@FXML private SkillSection secLang;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		if (secLang != null) {
			secLang.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.skills.section.language"));
			secLang.setSkillTypes(SkillType.LANGUAGE);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secNormal != null) secNormal.updateController(ctrl);
		if (secKnowl != null) secKnowl.updateController(ctrl);
		if (secLang != null) secLang.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secNormal != null) secNormal.refresh();
		if (secKnowl != null) secKnowl.refresh();
		if (secLang != null) secLang.refresh();
	}

}
