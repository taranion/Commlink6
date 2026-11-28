package de.rpgframework.shadowrun6.chargen.jfx.section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.SR6JFXTools;

/**
 * DataStructuresSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6DataStructuresSection extends DataStructuresSection {

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	public SR6DataStructuresSection() {
		super(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.resonance.section.datastructures"));
	}

	//-------------------------------------------------------------------
	@Override
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		super.updateController(ctrl);
	}

	//-------------------------------------------------------------------
	@Override
	protected Decision[] requestUserDecisions(DataStructure value) {
		if (control != null && control.getDataStructureController() != null) {
			return SR6JFXTools.askDecisions(control.getDataStructureController(), value);
		}
		return new Decision[0];
	}

}
