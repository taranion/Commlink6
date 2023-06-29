package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="senses")
@ElementList(entry="sense",type=Sense.class,inline=true)
public class SenseList extends ArrayList<Sense> {

	//-------------------------------------------------------------------
	/**
	 */
	public SenseList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SenseList(Collection<? extends Sense> c) {
		super(c);
	}

}
