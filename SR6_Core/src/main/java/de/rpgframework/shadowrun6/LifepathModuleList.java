package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="lifemods")
@ElementList(entry="lifemods",type=LifepathModule.class,inline=true)
public class LifepathModuleList extends ArrayList<LifepathModule> {

	private static final long serialVersionUID = -8959073625177058674L;

	//-------------------------------------------------------------------
	/**
	 */
	public LifepathModuleList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public LifepathModuleList(Collection<? extends LifepathModule> c) {
		super(c);
	}

}
