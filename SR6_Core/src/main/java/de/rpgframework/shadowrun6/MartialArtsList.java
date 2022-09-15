package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="martialarts")
@ElementList(entry="martialart",type=MartialArts.class,inline=true)
public class MartialArtsList extends ArrayList<MartialArts> {

	private static final long serialVersionUID = -8959073625177058674L;

	//-------------------------------------------------------------------
	/**
	 */
	public MartialArtsList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public MartialArtsList(Collection<? extends MartialArts> c) {
		super(c);
	}

}
