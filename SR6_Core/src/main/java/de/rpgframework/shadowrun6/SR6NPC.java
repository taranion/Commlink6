package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6NPC extends ANPC<ShadowrunAttribute, SR6Skill, SR6SkillValue, SR6Spell> {

	@Element(name="geardef")
	protected ItemTemplateList gearDef;

	//-------------------------------------------------------------------
	private ItemTemplate resolveItem(String key) {
		if (gearDef==null) return null;
		
		for (ItemTemplate tmp : gearDef) {
			if (tmp.getId().equals(key)) {
				return tmp;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * Used in deriving classes to perform validation checks on loading, if
	 * necessary
	 * 
	 * @return Error message or NULL
	 */
	public void validate() throws DataErrorException {
		// Validate skill references
		for (SR6SkillValue tmp : skills) {
			if (tmp.getModifyable() == null) {
				SR6Skill res = ShadowrunReference.SKILL.resolveAsDataItem(tmp.getKey());
				if (res == null)
					throw new DataErrorException(res,
							"Error in NPC '" + id + "': No spell with id '" + tmp.getKey() + "' found");
				tmp.setResolved(res);
			}
		}
		// Validate spell references
		for (SpellValue<SR6Spell> tmp : spells) {
			if (tmp.getResolved() == null) {
				SR6Spell res = ShadowrunReference.SPELL.resolveAsDataItem(tmp.getKey());
				if (res == null)
					throw new DataErrorException(res,
							"Error in NPC '" + id + "': No spell with id '" + tmp.getKey() + "' found");
				tmp.setResolved(res);
			}
		}
		
		// Validate gear definitions
		if (gearDef != null) {
			for (ItemTemplate tmp : gearDef) {
				tmp.setParentItem(this);
			}
		}
		
		
		// Validate gear references
		for (CarriedItem tmp : gear) {
			if (tmp.getResolved() == null) {
				// At first resolve within NPC
				PieceOfGear res = resolveItem(tmp.getKey());
				// Resolve globally, if needed
				if (res==null)
					res = ShadowrunReference.GEAR.resolveAsDataItem(tmp.getKey());
				if (res == null)
					throw new DataErrorException(res,
							"Error in NPC '" + id + "': No gear with id '" + tmp.getKey() + "' found");
				tmp.setResolved(res);
			}
		}
		// Validate critter power references
		for (CritterPowerValue tmp : critterpowers) {
			if (tmp.getResolved() == null) {
				CritterPower res = ShadowrunReference.CRITTER_POWER.resolveAsDataItem(tmp.getKey());
				if (res == null)
					throw new DataErrorException(res,
							"Error in NPC '" + id + "': No critter power with id '" + tmp.getKey() + "' found");
				tmp.setResolved(res);
			}
		}
	}

}
