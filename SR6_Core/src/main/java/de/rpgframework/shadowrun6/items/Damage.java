package de.rpgframework.shadowrun6.items;

import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.IDamage;

/**
 * @author Stefan
 *
 */
public class Damage implements Cloneable, IDamage {

	private DamageType type;
	private DamageElement element = DamageElement.REGULAR;
	protected int value;

	//--------------------------------------------------------------------
	public Damage() {
	}

	//--------------------------------------------------------------------
	public Damage(int val, DamageType type, DamageElement element) {
		this.value = val;
		this.type = type;
		this.element = element;
	}

	//--------------------------------------------------------------------
	public Damage(Damage copy) {
		value       = copy.value;
		type        = copy.getType();
		element     = copy.getElement();
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
		buf.append(String.valueOf(getValue()));

		if (type!=null)
			buf.append(type.getShortName());

		if (element!=null && element!=DamageElement.REGULAR) {
			buf.append("("+element.getShortName()+")");
		}

		return buf.toString();
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
