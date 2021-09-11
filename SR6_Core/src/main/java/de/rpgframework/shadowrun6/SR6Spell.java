package de.rpgframework.shadowrun6;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.ASpell;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="spell")
public class SR6Spell extends ASpell {

	//-------------------------------------------------------------------
	public SR6Spell() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SR6Spell(String id) {
		super(id);
		this.id = id;
	}

}
