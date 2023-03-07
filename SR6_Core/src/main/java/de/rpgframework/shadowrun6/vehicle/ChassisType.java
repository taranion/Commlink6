package de.rpgframework.shadowrun6.vehicle;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "chassistype")
public class ChassisType extends DataItem implements Comparable<ChassisType> {

	@Attribute(required=true)
	private ItemType type;
	@Attribute(required=true)
	private ItemSubType subtype;
	@Attribute(name="bod",required=true)
	private int body;
	@Attribute(name="arm",required=true)
	private int armor;
	@Attribute(name="hand",required=true)
	private int handling;
	@Attribute(name="seat",required=true)
	private int seats;
	@Attribute(name="bp",required=true)
	private int buildPoints;
	@Attribute(required=true)
	private int size;

	//-------------------------------------------------------------------
	public ChassisType() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ChassisType other) {
		if (getName()==null) return 0;
		if (other.getName()==null) return 0;

		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ItemType getType() {
		return type;
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
	 * @return the body
	 */
	public int getBody() {
		return body;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the armor
	 */
	public int getArmor() {
		return armor;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the handling
	 */
	public int getHandling() {
		return handling;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the seats
	 */
	public int getSeats() {
		return seats;
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
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

}
