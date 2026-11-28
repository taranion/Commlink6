package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;

import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6ContactSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6LifestyleSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6SINSection;
import javafx.fxml.FXML;

/**
 * Controller for SocialPage.fxml (formerly LifePage): contacts, SINs, and lifestyles.
 */
public class SocialPageController {

	private final static Logger logger = System.getLogger(SocialPageController.class.getPackageName());

	@FXML private SR6ContactSection secContacts;
	@FXML private SR6SINSection secSINs;
	@FXML private SR6LifestyleSection secLifestyles;

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secContacts != null) secContacts.updateController(ctrl);
		if (secSINs != null) secSINs.updateController(ctrl);
		if (secLifestyles != null) secLifestyles.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secContacts != null) secContacts.refresh();
		if (secSINs != null) secSINs.refresh();
		if (secLifestyles != null) secLifestyles.refresh();
	}

}
