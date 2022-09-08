package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;

/**
 * @author prelle
 *
 */
public class ConsoleType extends DataItem implements Comparable<ConsoleType> {

	@Attribute(name="pil")
	private int pilot;
	@Attribute(name="sen")
	private int sensor;
	@Attribute(name="bp",required=true)
	private int buildPoints;
	@Attribute
	private int special;

	//-------------------------------------------------------------------
	public ConsoleType() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ConsoleType other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;
		
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the pilot
	 */
	public int getPilot() {
		return pilot;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the sensor
	 */
	public int getSensor() {
		return sensor;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the buildPoints
	 */
	public int getBuildPoints() {
		return buildPoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the special
	 */
	public int getSpecial() {
		return special;
	}

}
