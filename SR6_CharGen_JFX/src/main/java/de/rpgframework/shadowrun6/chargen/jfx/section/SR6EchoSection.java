package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun6.SR6Discipline;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;

/**
 * Echoes section for Shadowrun 6, usable directly from FXML.
 */
public class SR6EchoSection extends MetamagicOrEchoSection {

	//-------------------------------------------------------------------
	public SR6EchoSection() {
		super(
				ResourceI18N.get(SR6CharacterViewLayout.UI, "page.resonance.section.echoes"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				SR6Discipline.ECHO
		);
	}

}
