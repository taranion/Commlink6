package de.rpgframework.shadowrun6.items;

import java.util.Locale;

/**
 * @author prelle
 *
 */
public class Availability {

	private int value;
	private Legality type;
	
	private boolean addToAvailability;

	//-------------------------------------------------------------------
	public Availability() {
		type = Legality.LEGAL;
	}

	//-------------------------------------------------------------------
	public Availability(int value, boolean add) {
		type = Legality.LEGAL;
		this.value = value;
		this.addToAvailability = add;
	}

	//-------------------------------------------------------------------
	public Availability(int value, Legality type, boolean add) {
		this.type  = type;
		this.value = value;
		this.addToAvailability = add;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return getName(Locale.getDefault());
	}

	//-------------------------------------------------------------------
	public String getName(Locale locale) {
		StringBuffer buf = new StringBuffer(addToAvailability?"+":"");
		
		if (type==null || type==Legality.LEGAL)
			return buf+String.valueOf(value);
		if (type==Legality.LEGAL)
			return buf+String.valueOf(value);
		return buf+String.valueOf(value)+" "+type.getShortCode(locale); // removed +"(" and +")" from +type.getShortCode() to avoid brackets around legality
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof Availability) {
			Availability other = (Availability)o;
			if (value!=other.getValue()) return false;
			return type==other.getLegality();
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public int getValue() {
		return Math.round(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public Legality getLegality() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setLegality(Legality type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the addToAvailability
	 */
	public boolean isAddToAvailability() {
		return addToAvailability;
	}

}
