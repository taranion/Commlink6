package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="rituals")
@ElementList(entry="ritual",type=SR6Ritual.class,inline=true)
public class SR6RitualList extends ArrayList<SR6Ritual> {

	private static final long serialVersionUID = -2864844515871126068L;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6RitualList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SR6RitualList(Collection<? extends SR6Ritual> c) {
		super(c);
	}

}
