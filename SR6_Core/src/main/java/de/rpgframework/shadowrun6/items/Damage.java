package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.DamageType;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author Stefan
 *
 */
public class Damage extends ItemAttributeNumericalValue implements Cloneable {

	public enum WeaponDamageType {
		NORMAL,
		ELECTRICAL,
		FLECHETTE,
	}

	private boolean addStrength;
	private DamageType type;
	private WeaponDamageType weaponDamageType = WeaponDamageType.NORMAL;

	//--------------------------------------------------------------------
	public Damage() {
		super(SR6ItemAttribute.DAMAGE,0, new ArrayList<Modification>());
	}

	//--------------------------------------------------------------------
	public Damage(int val, boolean addStrength, DamageType type, WeaponDamageType dType) {
		super(SR6ItemAttribute.DAMAGE,val, new ArrayList<Modification>());
		this.addStrength = addStrength;
		this.type = type;
		this.weaponDamageType = dType;
	}

	//--------------------------------------------------------------------
	public Damage(Damage copy, List<Modification> mods) {
		super(SR6ItemAttribute.DAMAGE, copy.getValue(), mods);
		addStrength = copy.addStrength();
		type        = copy.getType();
		weaponDamageType = copy.getWeaponDamageType();
		modifications.addAll(mods);
	}

	//-------------------------------------------------------------------
 	public Damage clone() {
    		try {
				return (Damage) super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return null;
    }

	//--------------------------------------------------------------------
	public boolean addStrength() {
		return addStrength;
	}

	//--------------------------------------------------------------------
	public int getValue() {
		return Math.round(value);
	}

	//--------------------------------------------------------------------
	public DamageType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	public String toString() {
		StringBuffer buf = new StringBuffer();
		MultiLanguageResourceBundle res = Shadowrun6Core.getI18nResources();
		if (addStrength) {
			buf.append("("+ShadowrunAttribute.STRENGTH.getShortName()+"/2");
			buf.append("+"+getModifiedValue());
			buf.append(")");
		} else
			buf.append(String.valueOf(getModifiedValue()));

		if (type==DamageType.PHYSICAL)
			buf.append(res.getString("damage.physical.short"));
		else
			buf.append(res.getString("damage.stun.short"));

		if (weaponDamageType!=null && weaponDamageType!=WeaponDamageType.NORMAL) {
			buf.append("("+res.getString("damage.damagetype."+weaponDamageType.name().toLowerCase())+")");
		}

		return buf.toString();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the addStrength
	 */
	public boolean isAddStrength() {
		return addStrength;
	}

	//--------------------------------------------------------------------
	/**
	 * @param addStrength the addStrength to set
	 */
	public void setAddStrength(boolean addStrength) {
		this.addStrength = addStrength;
	}

	//--------------------------------------------------------------------
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	//--------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(DamageType type) {
		this.type = type;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the weaponDamageType
	 */
	public WeaponDamageType getWeaponDamageType() {
		return weaponDamageType;
	}

	//--------------------------------------------------------------------
	/**
	 * @param weaponDamageType the weaponDamageType to set
	 */
	public void setWeaponDamageType(WeaponDamageType weaponDamageType) {
		this.weaponDamageType = weaponDamageType;
	}


}
