package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun6.items.Damage;

public class WeaponDamageConverter implements StringValueConverter<Damage> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public Damage read(String v) throws Exception {
		v = v.trim();

		Damage ret = new Damage();
		if (v.endsWith("(e)")) {
			ret.setElement(DamageElement.ELECTRICITY);
			v = v.substring(0, v.length()-3).trim();
		}
//		if (v.endsWith("(f)")) {
//			ret.setWeaponDamageType(WeaponDamageType.FLECHETTE);
//			v = v.substring(0, v.length()-3).trim();
//		}

		if (v.endsWith("P")) {
			ret.setType(DamageType.PHYSICAL);
			v = v.substring(0, v.length()-1);
		} else if (v.endsWith("S")) {
			ret.setType(DamageType.STUN);
			v = v.substring(0, v.length()-1);
		} else {
			ret.setType(DamageType.NO_CHANGE);
		}

		ret.setValue(Integer.parseInt(v.trim()));
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(Damage v) {
		if (v==null)
			return null;
		StringBuffer buf = new StringBuffer();
		buf.append(String.valueOf(v.getValue()));
		if (v.getType()==DamageType.PHYSICAL)
			buf.append("P");
		else
			buf.append("S");

		if (v.getElement()!=null && v.getElement()==DamageElement.ELECTRICITY) {
			buf.append("(e)");
		}
		return buf.toString();
	}

}