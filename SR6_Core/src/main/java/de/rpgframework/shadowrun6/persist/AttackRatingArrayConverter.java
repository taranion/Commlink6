package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.persist.IntegerArrayConverter;

/**
 * @author prelle
 *
 */
public class AttackRatingArrayConverter extends IntegerArrayConverter implements StringValueConverter<int[]> {

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
