package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSkillController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6SkillLeveller extends CommonSkillController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6SkillLeveller(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canSelectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
	 */
	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canDeselectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecializationValue)
	 */
	@Override
	public Possible canDeselectSpecialization(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#select(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
	 */
	@Override
	public OperationResult<SkillSpecializationValue<SR6Skill>> select(SR6SkillValue skillVal,
			SkillSpecialization<SR6Skill> spec, boolean expertise) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#deselect(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecializationValue)
	 */
	@Override
	public boolean deselect(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(SR6Skill data) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(SR6Skill data) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> decrease(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#getMaximum(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	public int getMaximum(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return 9;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		todos.clear();
		for (Modification _mod : previous) {
			if (_mod.getReferenceType() == ShadowrunReference.SKILL) {
				ValueModification mod = (ValueModification) _mod;
				SR6Skill key = mod.getResolvedKey();
				SR6SkillValue val = getModel().getSkillValue(key);
				if (mod.isConditional()) {
					logger.log(Level.DEBUG, "Add conditional modification {0} to {1}", mod, key);
				} else {
					logger.log(Level.DEBUG, "Add modification {0} to {1}", mod, key);
				}
				val.addModification(mod);
				if (mod.getSet() == ValueType.MAX) {
					logger.log(Level.INFO, "Changed maximum of {0} to {1}", key, val.getModifiedValue(ValueType.MAX));
				}
			} else
				unprocessed.add(_mod);
		}

		return unprocessed;
	}

}
