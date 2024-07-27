package de.rpgframework.shadowrun6;

import java.util.Locale;

import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.data.CommonCharacter;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.IReferenceResolver;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;

/**
 *
 */
public class SR6Quality extends Quality implements IReferenceResolver {

	@Element(name="geardef")
	protected ItemTemplateList gearDef;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6Quality() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.IReferenceResolver#resolveItem(java.lang.String)
	 */
	@Override
	public ItemTemplate resolveItem(String key) {
		if (gearDef==null) return null;

		for (ItemTemplate tmp : gearDef) {
			if (tmp.getId().equals(key)) {
				tmp.validate();
				return tmp;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.ComplexDataItem#validate()
	 */
	public void validate() throws DataErrorException {
		super.validate();
		if (gearDef!=null) {
			for (ItemTemplate tmp : gearDef) {
				tmp.setParentItem(this);
			}
		}
	}

}
