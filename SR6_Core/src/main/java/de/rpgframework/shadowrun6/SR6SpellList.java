/**
 * 
 */
package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="spells")
@ElementList(entry="spell",type=SR6Spell.class,inline=true)
public class SR6SpellList extends ArrayList<SR6Spell> {

	private static final long serialVersionUID = -1947492169452643981L;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6SpellList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SR6SpellList(Collection<? extends SR6Spell> c) {
		super(c);
	}

}
