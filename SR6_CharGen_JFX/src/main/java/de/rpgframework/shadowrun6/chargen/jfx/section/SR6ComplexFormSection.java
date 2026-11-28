package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.chargen.jfx.section.ComplexFormSection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.SR6JFXTools;

/**
 * ComplexFormSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6ComplexFormSection extends ComplexFormSection {

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	public SR6ComplexFormSection() {
		super(
				ResourceI18N.get(SR6CharacterViewLayout.UI, "page.resonance.section.complexforms"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				null
		);
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		super.updateController(ctrl);
	}

	//-------------------------------------------------------------------
	@Override
	protected Decision[] askUserForDecisions(ComplexForm data) {
		if (control != null && control.getComplexFormController() != null) {
			return SR6JFXTools.askDecisions(control.getComplexFormController(), data);
		}
		return null;
	}

}
