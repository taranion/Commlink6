package de.rpgframework.shadowrun6.chargen.jfx.section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.chargen.jfx.section.SINSection;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;

/**
 * SINSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6SINSection extends SINSection {

	//-------------------------------------------------------------------
	public SR6SINSection() {
		super(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.life.section.sins"));
	}

}
