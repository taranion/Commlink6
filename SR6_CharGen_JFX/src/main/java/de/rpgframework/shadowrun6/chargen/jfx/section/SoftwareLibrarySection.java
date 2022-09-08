package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
public class SoftwareLibrarySection extends GearSection {

	private final static Logger logger = System.getLogger(SoftwareLibrarySection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SoftwareLibrarySection.class.getPackageName()+".Section");

	private static Predicate<ItemTemplate> selectFilter = (t) -> 
		t.getItemType(CarryMode.EMBEDDED)==ItemType.SOFTWARE;
	
	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param selectFilter
	 * @param showFilter
	 */
	public SoftwareLibrarySection(Predicate<CarriedItem<ItemTemplate>> showFilter) {
		super(ResourceI18N.get(RES, "section.software.title"), CarryMode.EMBEDDED, selectFilter, showFilter);
		// TODO Auto-generated constructor stub
	}

}
