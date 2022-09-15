package de.rpgframework.shadowrun6.persist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.Technique.Category;

public class TechniqueCategoryConverter implements StringValueConverter<List<Category>> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public List<Category> read(String v) throws Exception {
		v = v.trim();
		List<Category> ret = new ArrayList<Category>();
		
		StringTokenizer tok = new StringTokenizer(v, ", /");
		while (tok.hasMoreTokens()) {
			String tmp = tok.nextToken();
			ret.add(Category.valueOf(tmp));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(List<Category> v) throws Exception {
		if (v.isEmpty())
			return null;
		
		StringBuffer buf = new StringBuffer();
		for (Iterator<Category> it = v.iterator(); it.hasNext(); ) {
			buf.append(it.next().name());
			if (it.hasNext())
				buf.append(",");
		}
		
		return buf.toString();
	}
	
}