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
@Root(name="qualities")
@ElementList(entry="quality",type=SR6Quality.class,inline=true)
public class QualityList extends ArrayList<SR6Quality> {

	private static final long serialVersionUID = -9164631466079745472L;

	//-------------------------------------------------------------------
	/**
	 */
	public QualityList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public QualityList(Collection<? extends SR6Quality> c) {
		super(c);
	}

}
