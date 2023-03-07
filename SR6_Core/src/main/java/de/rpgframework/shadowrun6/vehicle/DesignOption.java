package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="designopt")
public class DesignOption extends ComplexDataItem implements Comparable<DesignOption> {

	@Attribute(required=true)
	private String id;
	@Attribute
	private String max;
	@Attribute(name="bp",required=true)
	private int buildPoints;

	//-------------------------------------------------------------------
	public DesignOption() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DESIGNOPT:"+id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DesignOption other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;

		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	public String getMax() {
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
