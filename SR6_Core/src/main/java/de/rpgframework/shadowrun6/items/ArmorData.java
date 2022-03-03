package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;

/**
 * @author prelle
 *
 */
public class ArmorData implements IGearTypeData {

	@Attribute(name="rating")
	private int rating;
	@Attribute
	private int social;
	@Attribute(name="dr")
	private int damageReduction;
//	@Attribute(name="add")
//	private boolean addsToMain;

	//-------------------------------------------------------------------
	/**
	 */
	public ArmorData() {
	}

	//-------------------------------------------------------------------
	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @return the addsToMain
//	 */
//	public boolean addsToMain() {
//		return addsToMain;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @param addsToMain the addsToMain to set
//	 */
//	public void setAddsToMain(boolean addsToMain) {
//		this.addsToMain = addsToMain;
//	}

	//-------------------------------------------------------------------
	public int getSocial() {
		return social;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageReduction
	 */
	public int getDamageReduction() {
		return damageReduction;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IGearTypeData#copyToAttributes(de.rpgframework.genericrpg.items.AGearData)
	 */
	@Override
	public void copyToAttributes(AGearData copyTo) {
		copyTo.setAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL, rating);
		copyTo.setAttribute(SR6ItemAttribute.DEFENSE_SOCIAL, social);
		copyTo.setAttribute(SR6ItemAttribute.DAMAGE_REDUCTION, damageReduction);
		
//		if (addsToMain) {
//			System.getLogger("shadowrun6.data").log(Level.WARNING, "ToDo: copy Attributes: addsToMain is ignored");
//		}
	}

}
