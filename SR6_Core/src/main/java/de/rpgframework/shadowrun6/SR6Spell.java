package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.ASpell;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="spell")
public class SR6Spell extends ASpell {

	@Attribute
	private boolean wild;

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

	//-------------------------------------------------------------------
	/**
	 * @return the wild
	 */
	public boolean isWild() {
		return wild;
	}

}
