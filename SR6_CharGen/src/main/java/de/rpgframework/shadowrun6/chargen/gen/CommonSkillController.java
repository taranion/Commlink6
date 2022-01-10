package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;

/**
 * @author prelle
 *
 */
public abstract class CommonSkillController extends ControllerImpl<SR6Skill> implements SR6SkillController {
	
	protected final static Logger logger = System.getLogger(CommonSkillController.class.getPackageName()+".skill");
	
	public final static String I18N_RESTRICTED_SKILL = "skill.error.restricted";
	public final static String I18N_SKILL_IS_MAXED   = "skill.error.isAtMaximum";
	public final static String I18N_NOT_SELECTED     = "skill.error.notSelected";
	public final static String I18N_MAX_SKILLS_MAXED = "skill.error.maxSkillsMaxed";
	public final static String I18N_SKILL_AUTOSELECT = "skill.error.isAutoSelected";

	protected List<SR6Skill> available;
	protected Shadowrun6Character model;

	//-------------------------------------------------------------------
	public CommonSkillController(SR6CharacterController parent) {
		super(parent);
		model = parent.getModel();
		
		available = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6Skill item) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	public abstract int getMaximum(SR6SkillValue value);

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(SR6SkillValue value) {
		if (!model.getSkillValues().contains(value)) {
			// Value not present in character
			return new Possible(I18N_NOT_SELECTED);
		}
		
		return new Possible(value.getDistributed()<getMaximum(value), I18N_SKILL_IS_MAXED);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(SR6SkillValue value) {
		if (!model.getSkillValues().contains(value)) {
			// Value not present in character
			return new Possible(I18N_NOT_SELECTED);
		}
		return new Possible(value.getDistributed()>0);
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
//	 */
//	@Override
//	public boolean increase(SR6SkillValue ref) {
//		logger.log(Level.DEBUG, "increase "+ref);
//		Possible allowed = canBeIncreased(ref);
//		if (!allowed.get()) {
//			logger.log(Level.WARNING, "Trying to increase {} which cannot be increased: {}", ref.getSkill().getId(), allowed);
//			return false;
//		}
//	
//		// Change model
//		ref.setDistributed(ref.getDistributed()+1);
//		logger.log(Level.INFO, "Increase skill "+ref.getModifyable().getId()+" to "+ref.getDistributed());
//		
//		return true;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
//	 */
//	@Override
//	public boolean decrease(SR6SkillValue ref) {
//		if (logger.isLoggable(Level.TRACE))
//			logger.log(Level.TRACE, "ENTER decrease " + ref);
//		try {
//			Possible allowed = canBeDecreased(ref);
//			if (!allowed.get())
//				return false;
//
//			// Change model
//			ref.setDistributed(ref.getDistributed() - 1);
//			if (ref.getModifiedValue() == 0)
//				model.removeSkillValue(ref);
//			logger.log(Level.INFO, "Decrease skill " + ref.getModifyable().getId() + " to " + ref.getDistributed());
//
//			return true;
//		} finally {
//			if (logger.isLoggable(Level.TRACE))
//			logger.log(Level.TRACE, "LEAVE decrease " + ref);
//		}
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#getAvailable()
	 */
	@Override
	public List<SR6Skill> getAvailable() {
		return available;
	}

	@Override
	public List<SR6SkillValue> getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSelectionCost(SR6Skill data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDeselectionCost(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#canBeSelected(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public Possible canBeSelected(SR6Skill skill) {
		if (skill.isRestricted() && !available.contains(skill)) {
			return new Possible(I18N_RESTRICTED_SKILL);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#canBeDeselected(de.rpgframework.genericrpg.SelectedValue)
	 */
	@Override
	public Possible canBeDeselected(SR6SkillValue value) {
		if (!model.getSkillValues().contains(value)) return Possible.FALSE;
		// If the skill has modifications, it should not be deletable
		if (value.getModifier()>0)
			return new Possible(I18N_SKILL_AUTOSELECT);
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#select(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data) {
		logger.log(Level.DEBUG, "ENTER select("+data+")");
		try {
			// Ensure selecting this skill is allowed 
			Possible possible = canBeSelected(data);
			if (!possible.get()) {
				logger.log(Level.WARNING, "Tried to select a skill that is not valid to select: "+possible);
				return new OperationResult<>(possible);
			}
			
			// Now add skill to character
			SR6SkillValue ret = new SR6SkillValue(data, 1);
			model.addSkillValue(ret);
			
			return new OperationResult<SR6SkillValue>(ret);			
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+data+")");			
		}
	}

	@Override
	public boolean deselect(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsOptionSelection(SR6Skill toSelect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<?> getOptions(SR6Skill toSelect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data, Object option) {
		// TODO Auto-generated method stub
		return null;
	}

}
