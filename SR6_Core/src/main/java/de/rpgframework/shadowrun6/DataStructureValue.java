package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.data.ComplexDataItemValue;

/**
 * @author prelle
 *
 */
@Root(name = "datastructureval")
public class DataStructureValue extends ComplexDataItemValue<DataStructure> {

	//-------------------------------------------------------------------
	public DataStructureValue() {
	}

	//-------------------------------------------------------------------
	public DataStructureValue(DataStructure data) {
		super(data);
	}

	//-------------------------------------------------------------------
	public DataStructureValue(DataStructure data, int val) {
		super(data, val);
	}

}
