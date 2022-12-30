package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="lifemod")
public class LifepathModule extends ComplexDataItem {

	public enum Type {
		ADULT,
		CHOICES,
		EVENT
	}

	@Attribute
	private Type type;

	//-------------------------------------------------------------------
	public LifepathModule() {
	}

}
