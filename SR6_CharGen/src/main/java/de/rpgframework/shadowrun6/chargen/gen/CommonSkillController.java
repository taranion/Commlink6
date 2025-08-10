package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;

/**
 * @author prelle
 *
 */
public abstract class CommonSkillController extends ControllerImpl<SR6Skill> implements SR6SkillController, ComplexDataItemController<SR6Skill, SR6SkillValue> {

	protected final static Logger logger = System.getLogger(CommonSkillController.class.getPackageName()+".skill");

	protected final static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(CommonSkillGenerator.class, Locale.ENGLISH, Locale.ENGLISH);

	public final static String I18N_RESTRICTED_SKILL = "skill.error.restricted";
	public final static String I18N_SKILL_IS_MAXED   = "skill.error.isAtMaximum";
	public final static String I18N_NOT_SELECTED     = "skill.error.notSelected";
	public final static String I18N_SKILL_AUTOSELECT = "skill.error.isAutoSelected";
	public final static String I18N_NOT_AVAILABLE_SPEC = "skill.error.specNotAvailable";
	public final static String I18N_EXOTIC_WITHOUT_SPEC = "skill.error.exoticWithoutSpec";

	protected List<SR6Skill> available;
	protected List<SR6Skill> allowed;
	protected Shadowrun6Character model;

	protected Map<SR6Skill, SR6SkillValue> mapAutoSkillValues;

	//-------------------------------------------------------------------
	public CommonSkillController(SR6CharacterController parent) {
		super(parent);
		model = parent.getModel();

		available = new ArrayList<>();
		allowed   = new ArrayList<>();
		mapAutoSkillValues = new HashMap<>();
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
		// Knowledge skills have no level
		if (value.getModifyable().getType()==SkillType.KNOWLEDGE)
			return Possible.FALSE;

		return new Possible(value.getDistributed()<getMaximum(value), I18N_SKILL_IS_MAXED);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(SR6SkillValue value) {
		// Knowledge skills have no level
		if (value.getModifyable().getType()==SkillType.KNOWLEDGE)
			return Possible.FALSE;
		// Language skills cannot be decreased below 1
		if (value.getModifyable().getType()==SkillType.KNOWLEDGE)
			return Possible.FALSE;
		// Native language skill cannot be decreased below 4
		if (value.getModifyable().getType()==SkillType.LANGUAGE && value.getDistributed()==4)
			return Possible.FALSE;

		if (!model.getSkillValues().contains(value)) {
			// Value not present in character
			return new Possible(I18N_NOT_SELECTED);
		}
		return new Possible(value.getDistributed()>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#getAvailable()
	 */
	@Override
	public List<SR6Skill> getAvailable() {
		return available;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<SR6SkillValue> getSelected() {
		return model.getSkillValues();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#getAll()
	 */
	@Override
	public List<SR6SkillValue> getAll() {
		List<SR6SkillValue> ret = new ArrayList<>();
		for (SR6Skill key : Shadowrun6Core.getItemList(SR6Skill.class)) {
			if (key.isRestricted() && !allowed.contains(key)) {
				mapAutoSkillValues.remove(key);
				continue;
			}
			// Check if skill is already selected
			SR6SkillValue sVal = model.getSkillValue(key);
			if (sVal==null) {
				// Check if it is already present in the map
				sVal = mapAutoSkillValues.get(key);
				if (sVal==null) {
					// If not, create it - if allowed
					sVal = new SR6SkillValue(key, 0);
					mapAutoSkillValues.put(key, sVal);
				}
			}
			if (sVal!=null)
				ret.add(sVal);
		}
//		logger.log(Level.DEBUG, "getAll() returns {0}",ret);
//		logger.log(Level.DEBUG, "allowed are {0}",allowed);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#canBeSelected(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public Possible canBeSelected(SR6Skill skill, Decision... decisions) {
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
		if (value.getModifier()>0 || value.isAutoAdded())
			return new Possible(I18N_SKILL_AUTOSELECT);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#select(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data, Decision...decisions) {
		logger.log(Level.DEBUG, "ENTER select("+data+")");
		if (data==null) throw new NullPointerException();
		try {
			// Ensure selecting this skill is allowed
			Possible possible = canBeSelected(data, decisions);
			if (!possible.get()) {
				logger.log(Level.WARNING, "Tried to select a skill that is not valid to select: "+possible);
				return new OperationResult<>(possible);
			}

			// Now add skill to character
			// It may be already present due to modifications
			SR6SkillValue ret = model.getSkillValue(data);
			if (ret==null || data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE) {
				ret = new SR6SkillValue(data, 1);
				ret.setUuid(UUID.randomUUID());
				model.addSkillValue(ret);
			} else { //if skill is present, it is invisible due to value being zero, thus set to 1			
				ret.setDistributed(1);
			}
			logger.log(Level.DEBUG, "Added skill {0} to model", data);

			for (Decision dec : decisions) {
				ret.addDecision(dec);
			}

			return new OperationResult<SR6SkillValue>(ret);
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+data+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(SR6SkillValue value) {
		logger.log(Level.DEBUG, "ENTER deselect("+value+")");
		try {
			// Ensure deselecting this skill is allowed
			Possible possible = canBeDeselected(value);
			if (!possible.get()) {
				logger.log(Level.WARNING, "Tried to deselect a skill that is not valid to deselect: "+possible);
				return false;
			}

			// Now remove skill from character
			model.removeSkillValue(value);

			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE deselect("+value+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#getAvailableSpecializations()
	 */
	public List<SkillSpecialization<SR6Skill>> getAvailableSpecializations(SR6SkillValue skillVal) {
		List<SkillSpecialization<SR6Skill>> ret = new ArrayList<>();
		for (SkillSpecialization raw : skillVal.getSkill().getSpecializations()) {
			ret.add(raw);
		}
		// Only those not already selected
//		ret = ret.stream().filter(sp -> skillVal.getSpecializations().stream().allMatch(s2 -> !s2.getKey().equals(sp.getId()))).collect(Collectors.toList());

		return ret;

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6SkillValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(SR6Skill value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	protected void updateAvailable() {
		available.clear();

		for (SR6Skill skill : Shadowrun6Core.getItemList(SR6Skill.class)) {
			// If it is restricted, don't add it
			if (skill.isRestricted() && !allowed.contains(skill)) {
				continue;
			}

			// If it is a knowledge skill or not present yet, it is available
			if (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE)
				available.add(skill);
			else if (model.getSkillValue(skill)==null || model.getSkillValue(skill).getModifiedValue()==0) {
				available.add(skill);
			}
		}
	}

	//-------------------------------------------------------------------
	protected void checkForExoticWeaponsSpecilization() {
		SR6SkillValue val = model.getSkillValue("exotic_weapons");
		if (val==null)
			return;
		int value = getValue(val);
		if (value<1)
			return;
		
		// Exotic weapons has been taken
		if (val.getSpecializations().isEmpty()) {
			todos.add(new ToDoElement(Severity.WARNING, RES, I18N_EXOTIC_WITHOUT_SPEC));
		}
	}

	//--------------Calculate karma costs for increasing a skill by 1
	protected int getIncreaseCost(SR6SkillValue sVal) {
		// Learning knowledge skill as well as learning or raising language skills is always just 3 karma
		SR6Skill key = null;
		if (sVal==null) {
			key = Shadowrun6Core.getSkill(sVal.getKey());
		} else
			key = sVal.getModifyable();
		if (key==null || sVal==null ) {
			logger.log(Level.ERROR, "Cannot find Skill for ''{0}'' from PrioritySettings", sVal);
			return(0);
			}

		if (key.getType()==SkillType.KNOWLEDGE || key.getType()==SkillType.LANGUAGE)
			return 3;

		// Learning or raising active skill is 5 times new skill level
		int newVal = (sVal==null)?1:(sVal.getDistributed()+1);
		int cost = newVal*5; 
		return cost;
	}

}
