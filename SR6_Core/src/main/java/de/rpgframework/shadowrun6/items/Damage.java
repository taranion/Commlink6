package de.rpgframework.shadowrun6.items;

import java.util.List;

import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.IDamage;

/**
 * @author Stefan
 *
 */
public class Damage extends ItemAttributeNumericalValue<SR6ItemAttribute> implements Cloneable, IDamage {

	private DamageType type;
	private DamageElement element = DamageElement.REGULAR;

	//--------------------------------------------------------------------
	public Damage() {
		super(SR6ItemAttribute.DAMAGE);
	}

	//--------------------------------------------------------------------
	public Damage(int val, DamageType type, DamageElement element) {
		super(SR6ItemAttribute.DAMAGE);
		this.type = type;
		this.element = element;
	}

	//--------------------------------------------------------------------
	public Damage(Damage copy, List<Modification> mods) {
		super(SR6ItemAttribute.DAMAGE);
		super.modifications.addAll(mods);
		value       = copy.value;
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
		buf.append(String.valueOf(getModifiedValue()));

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
