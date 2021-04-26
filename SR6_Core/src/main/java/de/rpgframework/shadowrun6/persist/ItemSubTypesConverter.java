/**
 * 
 */
package de.rpgframework.shadowrun6.persist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.shadowrun6.items.ItemSubType;

/**
 * @author prelle
 *
 */
public class ItemSubTypesConverter implements StringValueConverter<List<ItemSubType>> {

	//-------------------------------------------------------------------
	/**
	 */
	public ItemSubTypesConverter() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(List<ItemSubType> value) throws Exception {
		if (value==null || value.isEmpty())
			return null;
		StringBuffer buf = new StringBuffer();
		for (Iterator<ItemSubType> it=value.iterator(); it.hasNext(); ) {
			buf.append(it.next().name());
			if (it.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public List<ItemSubType> read(String v) throws Exception {
		List<ItemSubType> ret = new ArrayList<ItemSubType>();
		StringTokenizer tok = new StringTokenizer(v,",");
		while (tok.hasMoreTokens()) {
			try {
				ret.add(ItemSubType.valueOf(tok.nextToken()));
			} catch (IllegalArgumentException e) {
				LogManager.getLogger("shadowrun6").error(e.toString(),e);
				System.err.println(e.toString());
//				System.exit(1);
			}
		}
		return ret;
	}

}
