package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.charctrl.ISpellController;
import de.rpgframework.shadowrun6.SR6Spell;

/**
 * @author prelle
 *
 */
public interface SR6SpellController extends ISpellController<SR6Spell> {

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISpellController#getNuyenCost(de.rpgframework.shadowrun.ASpell)
	 */
	@Override
	public default int getNuyenCost(SR6Spell value) {
		switch (value.getCategory()) {
		case COMBAT  : return 2000;
		case ILLUSION: return 1000;
		case MANIPULATION: return 1500;
		case DETECTION: return 500;
		case HEALTH: return 500;
		}
		return 0;
	}

}
