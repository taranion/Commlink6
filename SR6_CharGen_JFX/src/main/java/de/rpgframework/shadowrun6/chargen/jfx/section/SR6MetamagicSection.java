package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun6.SR6Discipline;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;

/**
 * Metamagic section for Shadowrun 6, usable directly from FXML.
 */
public class SR6MetamagicSection extends MetamagicOrEchoSection {

	//-------------------------------------------------------------------
	public SR6MetamagicSection() {
		super(
				ResourceI18N.get(SR6CharacterViewLayout.UI, "page.magic.section.metamagic"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				SR6Discipline.METAMAGIC
		);
	}

}
