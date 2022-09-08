package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.VehicleData.VehicleType;
import de.rpgframework.shadowrun6.persist.OnRoadOffRoadConverter;

/**
 * @author prelle
 *
 */
public class Powertrain extends DataItem implements Comparable<Powertrain> {

	@Attribute(required=false)
	private ItemSubType subtype;
	@Attribute(name="acc")
	@AttribConvert(OnRoadOffRoadConverter.class)
	private OnRoadOffRoadValue acceleration;
	@Attribute(name="spdi")
	@AttribConvert(OnRoadOffRoadConverter.class)
	private OnRoadOffRoadValue speedInterval;
	@Attribute(name="tspd")
	private int topSpeed;
	@Attribute(name="bp",required=true)
	private int buildPoints;
	@Attribute(required=true)
	private VehicleType type;

	//-------------------------------------------------------------------
	public Powertrain() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Powertrain other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;
		
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the subtype
	 */
	public ItemSubType getSubtype() {
		return subtype;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the acceleration
	 */
	public OnRoadOffRoadValue getAcceleration() {
		return acceleration;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the speedInterval
	 */
	public OnRoadOffRoadValue getSpeedInterval() {
		return speedInterval;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the topSpeed
	 */
	public int getTopSpeed() {
		return topSpeed;
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
	 * @return the type
	 */
	public VehicleType getType() {
		return type;
	}

}
