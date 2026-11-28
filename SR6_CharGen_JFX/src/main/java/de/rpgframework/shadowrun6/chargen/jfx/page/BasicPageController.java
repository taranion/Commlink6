package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.AttributeSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CritterPowerSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.QualityPathsSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6QualitySection;
import javafx.fxml.FXML;

/**
 * Controller for BasicPage.fxml.
 */
public class BasicPageController {

	private final static Logger logger = System.getLogger(BasicPageController.class.getPackageName());

	@FXML private BasicDataSection secBasicData;
	@FXML private AppearanceSection secAppearance;
	@FXML private AttributeSection secAttrib;
	@FXML private SR6QualitySection secQualities;
	@FXML private QualityPathsSection secQualPaths;
	@FXML private CritterPowerSection secCritterPower;

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		if (secBasicData != null) secBasicData.updateController(ctrl);
		if (secAppearance != null) secAppearance.updateController(ctrl);
		if (secAttrib != null) secAttrib.updateController(ctrl);
		if (secQualities != null) secQualities.updateController(ctrl);
		if (secQualPaths != null) secQualPaths.updateController(ctrl);
		if (secCritterPower != null) secCritterPower.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secBasicData != null) secBasicData.refresh();
		if (secAppearance != null) secAppearance.refresh();
		if (secAttrib != null) secAttrib.refresh();
		if (secQualities != null) secQualities.refresh();
		if (secQualPaths != null) secQualPaths.refresh();
		if (secCritterPower != null) secCritterPower.refresh();

		if (control != null && control.getModel() != null) {
			boolean hasCritterPowers = !control.getModel().getCritterPowers().isEmpty();
			if (secCritterPower != null) {
				secCritterPower.setVisible(hasCritterPowers);
				secCritterPower.setManaged(hasCritterPowers);
			}
			boolean hasQualityPaths = !control.getModel().getQualityPaths().isEmpty();
			if (secQualPaths != null) {
				secQualPaths.setVisible(hasQualityPaths);
				secQualPaths.setManaged(hasQualityPaths);
			}
		}
	}

}
