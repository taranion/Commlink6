package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

@Root(name="datastructure")
@DataItemTypeKey(id="datastructure")
public class DataStructure extends ComplexDataItem {
	
	@Attribute
	private boolean multi;

	//-------------------------------------------------------------------
	public DataStructure() {
		this.hasLevel = true;
	}

	//-------------------------------------------------------------------
	public boolean isMulti() {
		return multi;
	}

}
