package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;

import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.EmulatedProgramsSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6ComplexFormSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6DataStructuresSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6EchoSection;
import javafx.fxml.FXML;

/**
 * Controller for ResonancePage.fxml.
 */
public class ResonancePageController {

	private final static Logger logger = System.getLogger(ResonancePageController.class.getPackageName());

	@FXML private SR6EchoSection secMeta;
	@FXML private SR6DataStructuresSection secDataStruct;
	@FXML private SR6ComplexFormSection secCplx;
	@FXML private EmulatedProgramsSection secPrograms;

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secMeta != null) secMeta.updateController(ctrl);
		if (secDataStruct != null) secDataStruct.updateController(ctrl);
		if (secCplx != null) secCplx.updateController(ctrl);
		if (secPrograms != null) secPrograms.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secMeta != null) secMeta.refresh();
		if (secDataStruct != null) secDataStruct.refresh();
		if (secCplx != null) secCplx.refresh();
		if (secPrograms != null) secPrograms.refresh();
	}

}
