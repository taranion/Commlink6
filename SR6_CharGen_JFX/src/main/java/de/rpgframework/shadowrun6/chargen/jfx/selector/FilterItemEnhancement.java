package de.rpgframework.shadowrun6.chargen.jfx.selector;

import de.rpgframework.jfx.NameFilter;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;

/**
 * @author prelle
 *
 */
public class FilterItemEnhancement extends NameFilter<SR6ItemEnhancement> {

	//-------------------------------------------------------------------
	public FilterItemEnhancement() {
		super(null);
	}

	//-------------------------------------------------------------------
	public FilterItemEnhancement(String promptText) {
		super(promptText);
	}

}
