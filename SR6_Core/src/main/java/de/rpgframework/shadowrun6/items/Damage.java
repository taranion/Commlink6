package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.items.IDamage;

/**
 * @author Stefan
 *
 */
public class Damage extends ItemAttributeNumericalValue implements Cloneable, IDamage {

	private boolean addStrength;
	private DamageType type;
	private DamageElement element = DamageElement.REGULAR;

	//--------------------------------------------------------------------
	public Damage() {
		super(SR6ItemAttribute.DAMAGE,0, new ArrayList<Modification>());
	}

	//--------------------------------------------------------------------
	public Damage(int val, boolean addStrength, DamageType type, DamageElement element) {
		super(SR6ItemAttribute.DAMAGE,val, new ArrayList<Modification>());
		this.addStrength = addStrength;
		this.type = type;
		this.element = element;
	}

	//--------------------------------------------------------------------
	public Damage(Damage copy, List<Modification> mods) {
		super(SR6ItemAttribute.DAMAGE, copy.getValue(), mods);
		addStrength = copy.addStrength();
		type        = copy.getType();
		element     = copy.getElement();
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
		if (addStrength) {
			buf.append("("+ShadowrunAttribute.STRENGTH.getShortName()+"/2");
			buf.append("+"+getModifiedValue());
			buf.append(")");
		} else
			buf.append(String.valueOf(getModifiedValue()));

		buf.append(type.getShortName());

		if (element!=null && element!=DamageElement.REGULAR) {
			buf.append("("+element.getShortName()+")");
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
	public DamageElement getElement() {
		return element;
	}

	//--------------------------------------------------------------------
	/**
	 * @param weaponDamageType the weaponDamageType to set
	 */
	public void setElement(DamageElement element) {
		this.element = element;
	}


}
