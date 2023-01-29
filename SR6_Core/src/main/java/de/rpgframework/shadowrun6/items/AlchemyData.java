package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;

/**
 * @author Stefan
 *
 */
@Root(name="alchemy")
public class AlchemyData implements IGearTypeData {

	/**
	 * For which items is the accessory usable
	 */
	@Attribute
	private String spell;
	@Attribute(name="drain")
	private int drain;
	@Attribute(name="trigger")
	private String trigger;

	//--------------------------------------------------------------------
	public AlchemyData() {
//		usewith  = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "AlchemyData(spell="+spell+")";
	}

	@Override
	public void copyToAttributes(AGearData copyTo) {
//		copyTo.setAttribute(SR6ItemAttribute.DRAIN, drain);
//		copyTo.setAttribute(SR6ItemAttribute.DEFENSE_SOCIAL, social);
//		copyTo.setAttribute(SR6ItemAttribute.DAMAGE_REDUCTION, damageReduction);
	}

}
