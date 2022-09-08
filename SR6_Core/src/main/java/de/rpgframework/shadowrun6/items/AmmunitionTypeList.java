package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="ammotypes")
@ElementList(entry="ammotype",type=AmmunitionType.class,inline=true)
public class AmmunitionTypeList extends ArrayList<AmmunitionType> {

	private static final long serialVersionUID = -4392566952214536373L;

	//-------------------------------------------------------------------
	/**
	 */
	public AmmunitionTypeList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public AmmunitionTypeList(Collection<? extends AmmunitionType> c) {
		super(c);
	}

}
