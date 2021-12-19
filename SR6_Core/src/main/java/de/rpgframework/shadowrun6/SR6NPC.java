package de.rpgframework.shadowrun6;

import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6NPC extends ANPC<ShadowrunAttribute, SR6Skill, SR6SkillValue, SR6Spell> {

	//-------------------------------------------------------------------
	/**
	 * Used in deriving classes to perform validation checks on loading,
	 * if necessary
	 * @return Error message or NULL
	 */
	public void validate() throws DataErrorException {
		// Validate spell references
		for (SpellValue<SR6Spell> tmp : spells) {
			if (tmp.getResolved()==null) {
				SR6Spell res = ShadowrunReference.SPELL.resolveAsDataItem(tmp.getKey());
				if (res==null)
					throw new DataErrorException(res, "Error in NPC '"+id+"': No spell with id '"+tmp.getKey()+"' found");
				tmp.setResolved(res);
			}
		}
		// Validate gear references
		for (CarriedItem tmp : gear) {
			if (tmp.getResolved()==null) {
				PieceOfGear res = ShadowrunReference.GEAR.resolveAsDataItem(tmp.getKey());
				if (res==null)
					throw new DataErrorException(res, "Error in NPC '"+id+"': No gear with id '"+tmp.getKey()+"' found");
				tmp.setResolved(res);
			}
		}
	}

}
