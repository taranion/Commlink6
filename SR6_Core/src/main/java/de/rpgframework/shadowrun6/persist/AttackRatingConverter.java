package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

public class AttackRatingConverter implements StringValueConverter<int[]> {
	
	public final static int STRx2 = -2;
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public int[] read(String v) throws Exception {
		v = v.trim();
		
		int[] ret = new int[5];
		
		String[] buf = v.trim().split(",");
		for (int i=0; i<buf.length; i++) {
			if (buf[i].isEmpty()) {
				ret[i]=0;
			} else if ("STRx2".equals(buf[i])) {
				ret[i] = STRx2;
			} else
				ret[i] = Integer.parseInt(buf[i]);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(int[] v) throws Exception {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<5; i++) {
			if (i>0)
				buf.append(",");
			if (v[i]!=0) {
				if (v[i]==STRx2)
					buf.append("STRx2");
				else
					buf.append(v[i]);
			}
		}
		return buf.toString();
	}
	
}