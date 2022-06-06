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
@Root(name="datastructures")
@ElementList(entry="datastructure",type=DataStructure.class,inline=true)
public class DataStructureList extends ArrayList<DataStructure> {

	private static final long serialVersionUID = 4412921542586155157L;

	//-------------------------------------------------------------------
	/**
	 */
	public DataStructureList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public DataStructureList(Collection<? extends DataStructure> c) {
		super(c);
	}

}
