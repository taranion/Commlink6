package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="sensorfunctions")
@ElementListUnion({
    @ElementList(entry="sensorfunction", type=SensorFunction.class),
})
public class SensorFunctionList extends ArrayList<SensorFunction> {

	private static final long serialVersionUID = -4307087686099123762L;

	//-------------------------------------------------------------------
	/**
	 */
	public SensorFunctionList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SensorFunctionList(Collection<? extends SensorFunction> c) {
		super(c);
	}

}
