package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.PageReference;

/**
 * @author prelle
 *
 */
public class SR6JFXUtil {

	//-------------------------------------------------------------------
	/**
	 */
	public SR6JFXUtil() {
		// TODO Auto-generated constructor stub
	}
	
	//-------------------------------------------------------------------
	public static String createSourceText(DataItem item) {
		List<String> elements = new ArrayList<>();
		boolean shorted = item.getPageReferences().size()>2;
		String language = Locale.getDefault().getLanguage();
		for (PageReference ref : item.getPageReferences()) {
			if (!ref.getLanguage().equals(language))
				continue;
			if (shorted) {
				elements.add( ref.getProduct().getShortName(Locale.getDefault())+" "+ref.getPage() );				
			} else {
				elements.add( ref.getProduct().getName(Locale.getDefault())+" "+ref.getPage() );
			}
		}
		
		return String.join(", ", elements);
	}

}
