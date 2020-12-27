package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.items.Availability;
import de.rpgframework.shadowrun6.items.Legality;

public class AvailabilityConverter implements StringValueConverter<Availability> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public Availability read(String v) throws Exception {
		v = v.trim();
		
		boolean addTo = false;
		if (v.startsWith("+"))
			addTo = true;
		
		try {
			return new Availability(Integer.parseInt(v), addTo);
		} catch (Exception e) {
		}
		
		String head = v.substring(0, v.length()-1);
		String tail = v.substring(v.length()-1);
		
		return new Availability(Integer.parseInt(head), Legality.valueOfShortCode(tail), addTo);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(Availability v) throws Exception {
		return v.toString();
	}
	
}