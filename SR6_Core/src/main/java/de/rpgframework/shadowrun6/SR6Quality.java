package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 *
 */
public class SR6Quality extends Quality {

	@Element(name="geardef")
	protected ItemTemplateList gearDef;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6Quality() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	private ItemTemplate resolveItem(String key) {
		if (gearDef==null) return null;

		for (ItemTemplate tmp : gearDef) {
			if (tmp.getId().equals(key)) {
				tmp.validate();
				return tmp;
			}
		}
		return null;
	}

}
