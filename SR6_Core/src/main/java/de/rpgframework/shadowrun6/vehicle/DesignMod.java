package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="designmod")
public class DesignMod extends ComplexDataItem implements Comparable<DesignMod> {

	@Attribute(required=true)
	private String id;
	@Attribute
	private int max;
	@Attribute(name="bp",required=true)
	private int buildPoints;

	//-------------------------------------------------------------------
	public DesignMod() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DesignMod other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;

		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the buildPoints
	 */
	public int getBuildPoints() {
		return buildPoints;
	}

}
