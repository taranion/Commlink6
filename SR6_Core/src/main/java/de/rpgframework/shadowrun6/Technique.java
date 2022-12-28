package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.ATechnique;
import de.rpgframework.shadowrun6.persist.TechniqueCategoryConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="technique")
public class Technique extends ATechnique {

	public enum Category {
		GENERAL,
		GRAPPLING,
		MOBILITY,
		RANGED,
		STRIKING,
		WEAPON,
		;

		public String getName(Locale loc) {
			return Shadowrun6Core.getI18nResources().getString("technique.category."+name().toLowerCase(), loc);
		}
	}
	
	@Attribute(name="cat")
	@AttribConvert(TechniqueCategoryConverter.class)
	private List<Category> categories;

	//-------------------------------------------------------------------
	public Technique() {
		super();
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @return the category
	 */
	public Collection<Category> getCategories() {
		return new ArrayList<Technique.Category>(categories);
	}

}
