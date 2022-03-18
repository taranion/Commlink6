package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.shadowrun.Ritual;

/**
 * @author prelle
 *
 */
public class SR6Ritual extends Ritual {

	@Attribute(name="thr")
	private int threshold;

	//-------------------------------------------------------------------
	public SR6Ritual() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @return the threshold
	 */
	public int getThreshold() {
		return threshold;
	}

}
