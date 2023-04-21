package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

/**
 * @author prelle
 *
 */
public class AttackRatingArrayConverter implements StringValueConverter<int[]> {

	@Override
	public String write(int[] v) throws Exception {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<v.length; i++) {
			if (i>0)
				buf.append(",");
			if (v[i]!=0) {
				buf.append(v[i]);
			}
		}
		return buf.toString();
	}

	@Override
	public int[] read(String v) throws Exception {
		v = v.trim();
		String[] buf = v.trim().split(",");
		int[] ret = new int[5];
		for (int i=0; i<buf.length; i++) {
			buf[i] = buf[i].trim();
			if (buf[i].isEmpty()) {
				ret[i]=0;
			} else
				ret[i] = Integer.parseInt(buf[i]);
		}
		return ret;
	}

}
