package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;

/**
 * @author prelle
 *
 */
public class MatrixData implements IGearTypeData {

	private transient ItemTemplate parent;

	@Attribute(name="a")
	private Integer attack;
	@Attribute(name="s")
	private Integer sleaze;
	@Attribute(name="d")
	private Integer dataProcessing;
	@Attribute(name="f")
	private Integer firewall;
	@Attribute(name="programs")
	private Integer programSlots;
	@Attribute(name="devrat")
	private Integer deviceRating;

	//-------------------------------------------------------------------
	/**
	 */
	public MatrixData() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IGearTypeData#copyToAttributes(de.rpgframework.genericrpg.items.AGearData)
	 */
	@Override
	public void copyToAttributes(AGearData copyTo) {
		if (attack!=null)
			copyTo.setAttribute(SR6ItemAttribute.ATTACK, attack);
		if (sleaze!=null)
			copyTo.setAttribute(SR6ItemAttribute.SLEAZE, sleaze);
		if (dataProcessing!=null)
			copyTo.setAttribute(SR6ItemAttribute.DATA_PROCESSING, dataProcessing);
		if (firewall!=null)
			copyTo.setAttribute(SR6ItemAttribute.FIREWALL, firewall);
		if (programSlots!=null)
			copyTo.setAttribute(SR6ItemAttribute.CONCURRENT_PROGRAMS, programSlots);
		if (deviceRating!=null) {
			copyTo.setAttribute(SR6ItemAttribute.DEVICE_RATING, deviceRating);
			if (copyTo.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue()==ItemSubType.RIGGER_CONSOLE) {
				copyTo.setAttribute(SR6ItemAttribute.NOISE_REDUCTION, deviceRating);
			}
		}
	}

}
