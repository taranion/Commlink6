package de.rpgframework.shadowrun6.persist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.items.FireMode;

public class FireModesConverter implements StringValueConverter<List<FireMode>> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public List<FireMode> read(String v) throws Exception {
		v = v.trim();
		List<FireMode> ret = new ArrayList<FireMode>();
		
		StringTokenizer tok = new StringTokenizer(v, " /");
		while (tok.hasMoreTokens()) {
			String tmp = tok.nextToken();
			ret.add(FireMode.getByValue(Locale.ENGLISH,tmp));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(List<FireMode> v) throws Exception {
		if (v.isEmpty())
			return null;
		
		StringBuffer buf = new StringBuffer();
		for (Iterator<FireMode> it = v.iterator(); it.hasNext(); ) {
			buf.append(it.next().getValue());
			if (it.hasNext())
				buf.append("/");
		}
		
		return buf.toString();
	}
	
}