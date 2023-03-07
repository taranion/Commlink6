package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="draketypes")
@ElementList(entry="draketype",type=DrakeType.class,inline=true)
public class DrakeTypeList extends ArrayList<DrakeType> {

	private static final long serialVersionUID = -2864844515871126068L;

	//-------------------------------------------------------------------
	/**
	 */
	public DrakeTypeList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public DrakeTypeList(Collection<? extends DrakeType> c) {
		super(c);
	}

}
