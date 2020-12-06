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
@ElementList(entry="spell",type=Spell.class,inline=true)
public class SpellList extends ArrayList<Spell> {

	private static final long serialVersionUID = -1947492169452643981L;

	//-------------------------------------------------------------------
	/**
	 */
	public SpellList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SpellList(Collection<? extends Spell> c) {
		super(c);
	}

}
