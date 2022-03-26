package de.rpgframework.shadowrun6.persist;

import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;

/**
 * @author prelle
 *
 */
public class OnRoadOffRoadConverter implements StringValueConverter<OnRoadOffRoadValue> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(OnRoadOffRoadValue value) {
		if (value.getOnRoad()==value.getOffRoad())
			return String.valueOf(value.getOnRoad());
		else
			return value.getOnRoad()+"/"+value.getOffRoad();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public OnRoadOffRoadValue read(String v) {
		OnRoadOffRoadValue ret = new OnRoadOffRoadValue();
		StringTokenizer tok = new StringTokenizer(v," /");
		if (tok.countTokens()==1) {
			ret.set(Integer.parseInt(tok.nextToken()));
		} else {
			float on  = Float.parseFloat(tok.nextToken());
			float off = Float.parseFloat(tok.nextToken());
			ret.set(on, off);
		}
		return ret;
	}

}
