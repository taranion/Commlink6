package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "ammotype")
public class AmmunitionType extends ComplexDataItem {

	@Attribute(name="cmult")
	private float costMultiplier;

	//-------------------------------------------------------------------
	public AmmunitionType() {
	}

	//-------------------------------------------------------------------
	/**
	 * @return the costMultiplier
	 */
	public float getCostMultiplier() {
		return costMultiplier;
	}

}
