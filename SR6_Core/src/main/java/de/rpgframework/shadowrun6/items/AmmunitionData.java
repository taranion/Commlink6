package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.chargen.ai.ChoiceType;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public class AmmunitionData implements IGearTypeData {

	@Attribute(name="gz")
	@AttribConvert(WeaponDamageConverter.class)
	private Damage damageGroundZero;
	@Attribute(name="close")
	@AttribConvert(WeaponDamageConverter.class)
	private Damage damageClose;
	@Attribute(name="near")
	@AttribConvert(WeaponDamageConverter.class)
	private Damage damageNear;
	@Attribute(name="blast")
	private int blast;
	@Attribute
	private ChoiceType choice;
	
	
	//-------------------------------------------------------------------
	public AmmunitionData() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IGearTypeData#copyToAttributes(de.rpgframework.genericrpg.items.AGearData)
	 */
	@Override
	public void copyToAttributes(AGearData copyTo) {
		if (damageGroundZero!=null)
			copyTo.setAttribute(SR6ItemAttribute.BLAST_GZ, damageGroundZero);
		if (damageClose!=null)
			copyTo.setAttribute(SR6ItemAttribute.BLAST_CLOSE, damageClose);
		if (damageNear!=null)
			copyTo.setAttribute(SR6ItemAttribute.BLAST_NEAR, damageNear);
		if (blast!=0)
			copyTo.setAttribute(SR6ItemAttribute.BLAST_RANGE, blast);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the blast
	 */
	public int getBlast() {
		return blast;
	}

	//-------------------------------------------------------------------
	/**
	 * @param blast the blast to set
	 */
	public void setBlast(int blast) {
		this.blast = blast;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageGroundZero
	 */
	public Damage getDamageGroundZero() {
		return damageGroundZero;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damageGroundZero the damageGroundZero to set
	 */
	public void setDamageGroundZero(Damage damageGroundZero) {
		this.damageGroundZero = damageGroundZero;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageClose
	 */
	public Damage getDamageClose() {
		return damageClose;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damageClose the damageClose to set
	 */
	public void setDamageClose(Damage damageClose) {
		this.damageClose = damageClose;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageNear
	 */
	public Damage getDamageNear() {
		return damageNear;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damageNear the damageNear to set
	 */
	public void setDamageNear(Damage damageNear) {
		this.damageNear = damageNear;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the choice
	 */
	public ChoiceType getChoice() {
		return choice;
	}

	//-------------------------------------------------------------------
	/**
	 * @param choice the choice to set
	 */
	public void setChoice(ChoiceType choice) {
		this.choice = choice;
	}

}
