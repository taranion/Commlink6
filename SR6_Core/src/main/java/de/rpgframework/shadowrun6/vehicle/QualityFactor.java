package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;

/**
 * @author prelle
 *
 */
public class QualityFactor extends DataItem implements Comparable<QualityFactor> {

	@Attribute(required=true)
	private String id;
	@Attribute
	private float multi;

	//-------------------------------------------------------------------
	public QualityFactor() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QUALITYF:"+id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(QualityFactor other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;
		
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	public float getMultiplier() {
		return multi;
	}

}
