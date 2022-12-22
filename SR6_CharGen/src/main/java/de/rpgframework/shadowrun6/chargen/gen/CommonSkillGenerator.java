package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Skill Points
 * Points 2 = Free Language/Knowledge skills
 * Points 3 = Karma
 * @author prelle
 *
 */
public abstract class CommonSkillGenerator extends CommonSkillController implements SR6SkillGenerator {

	protected static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(CommonSkillGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	public final static String I18N_NOT_RAISED_POINT1 = "skill.error.notRaisedWithPoints1";
	public final static String I18N_NOT_RAISED_POINT2 = "skill.error.notRaisedWithPoints2";
	public final static String I18N_NOT_RAISED_KARMA = "skill.error.notRaisedWithKarma";
	public final static String I18N_NOT_AVAILABLE_SPEC = "skill.error.specNotAvailable";
	public final static String I18N_MAX_SKILLS_MAXED = "skill.error.maxSkillsMaxed";
	
	public final static UUID NATIVE_LANGUAGE = UUID.fromString("1ab9b4c2-b6d2-4d84-8c82-91920cbefe8b");

	protected int points1;
	protected int points2;
	/** How many skills are allowed to be maximized */
	protected int maxLimit;

	//-------------------------------------------------------------------
	/**
	 */
	public CommonSkillGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	protected void ensureExistanceOfNativeLanguage() {
		ValueModification nat = new ValueModification(ShadowrunReference.SKILL, "language",0);
		boolean missingNative=true;
		for (SR6SkillValue tmp : model.getSkillValues()) {
			if (tmp.getSkill()==Shadowrun6Core.getSkill("language") && tmp.getModifiedValue()==4)
				missingNative=false;
		}
		if (missingNative) {
			SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("language"),4);
			val.setUuid(NATIVE_LANGUAGE);
			val.addDecision(new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), RES.getString("label.native_language")));
			val.addModification(nat);
			model.addSkillValue(val);

			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			PerSkillPoints points = new PerSkillPoints();
			points.base = 4;
			settings.put(val, points);
		}
		
	}

	//-------------------------------------------------------------------
	protected List<SR6SkillValue> getMaximizedSkills() {
		List<SR6SkillValue> maxed = new ArrayList<SR6SkillValue>();
		for (SR6SkillValue val : model.getSkillValues()) {
			if (val.getDistributed() >= (6+val.getModifiedValue(ValueType.MAX)))
				maxed.add(val);
		}
		return maxed;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#canBeSelected(SR6Skill)
//	 */
//	@Override
//	public Possible canBeSelected(SR6Skill data) {
//		Possible pos = super.canBeSelected(data);
//		if (!pos.get())
//			return pos;
//		
//		if (pointsSkills>0)
//			return Possible.TRUE;
//		if (pointsLangAndKnow>0 && (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)) {
//			return Possible.TRUE;			
//		}
//		// No points left - maybe with karma?
//		int karma = (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)?3:5;
//		if (model.getKarmaFree()>=karma)
//			return Possible.TRUE;
//		return new Possible(new ValueRequirement(ShadowrunReference.ATTRIBUTE, "KARMA", karma));
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#getMaximum(SR6SkillValue)
	 */
	public int getMaximum(SR6SkillValue ref) {
		int maximum = 6  + ref.getModifiedValue(ValueType.MAX);
		if (ref.getSkill().getType()==SkillType.LANGUAGE) {
			// Language skills have different maximums
			maximum = SR6SkillValue.LANGLEVEL_EXPERT;
		}
		return maximum;
	}
	
	//-------------------------------------------------------------------
	protected abstract PerSkillPoints getPerSkill(SR6SkillValue value);
	
	//-------------------------------------------------------------------
	protected void setPerSkill(SR6SkillValue value, PerSkillPoints per) {
		try {
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			settings.put(value, per);
		} catch (ClassCastException cce) {
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			settings.put(value, per);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(SR6SkillValue value) {
		Possible allowed = super.canBeIncreased(value);
		if (!allowed.get())
			return allowed;
		
		// Is the new value acceptable
		allowed = wouldNewValueBeOkay(value);
		if (!allowed.get())
			return allowed;

		// Way of payment must be decided by child class
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	protected Possible wouldNewValueBeOkay(SR6SkillValue ref) {
		// Check if skill is already at maximum
		int maximum = getMaximum(ref);
		if (ref.getDistributed()>=maximum) {
			return new Possible(I18N_SKILL_IS_MAXED);
		}
		
		// Maximum not reached yet
		Collection<SR6SkillValue> alreadyMaxed = getMaximizedSkills();

		// Only allow to max an skill, if there isn't one already
		if ((ref.getDistributed()+1)==maximum && alreadyMaxed.size()>=maxLimit) {
			return new Possible(Severity.STOPPER, RES,  I18N_MAX_SKILLS_MAXED, maxLimit);
		}
		
		return Possible.TRUE;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue ref) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER increase({0})", ref);
		try {
			Possible allowed = canBeIncreasedPoints(ref);
			if (allowed.get()) {
				return increasePoints(ref);
			}
			
			allowed = canBeIncreasedPoints2(ref);
			if (allowed.get()) {
				return increasePoints2(ref);
			}

			logger.log(Level.ERROR, "Neither with skill points, nor with Karma was increasing possible");
			return new OperationResult<>();
		} finally {
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "ENTER increase({0})", ref);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> decrease(SR6SkillValue ref) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER decrease " + ref);
		try {
			SR6Skill key = ref.getModifyable();
			
			Possible allowed = canBeDecreasedPoints2(ref);
			if (allowed.get()) {
				return decreasePoints2(ref);
			}
			
			allowed = canBeDecreasedPoints(ref);
			if (allowed.get()) {
				return decreasePoints(ref);
			}

//			logger.log(Level.ERROR, "Neither with skill points, nor with Karma was decreasing possible");
			return new OperationResult<>();
		} finally {
			if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "LEAVE decrease " + ref);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#getPointsLeft2()
	 */
	@Override
	public int getPointsLeft2() {
		return points2;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeIncreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints2(SR6SkillValue value) {
		// Increasing something not present, auto-selects it
//		if (!model.getSkillValues().contains(value)) {
//			// Value not present in character
//			return new Possible(I18N_NOT_SELECTED);
//		}
		
		// Is the new value acceptable
		Possible allowed = wouldNewValueBeOkay(value);
		if (!allowed.get())
			return allowed;
		
		// Are there enough points		
		if (points2<1){
			return new Possible(I18N_NOT_RAISED_POINT2);
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#increasePoints2(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> increasePoints2(SR6SkillValue value) {
		logger.log(Level.INFO, "ENTER increasePoints2");
		try {
			Possible allowed = canBeIncreasedPoints2(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase with points2, but "+allowed);
				return new OperationResult<>(allowed);
			}

			if (value.getDistributed()==0) {
				logger.log(Level.DEBUG, "Don't increase, but select");
				return select(value.getModifyable());
			}
			PerSkillPoints per = getPerSkill(value);
			// Do increase
			per.points2++;
			logger.log(Level.INFO, "Increased using points2 to "+per.getSum()+ " with "+per);
			
			getCharacterController().runProcessors();
			return new OperationResult<SR6SkillValue>(value);
		} finally {
			logger.log(Level.INFO, "LEAVE increasePoints2");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#decreasePoints2(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> decreasePoints2(SR6SkillValue value) {
		Possible allowed = canBeDecreasedPoints2(value);
		if (!allowed.get()) 
			return new OperationResult<>(allowed);
		
		// Do increase
		value.setDistributed(value.getDistributed()-1);
		points2++;
		
		return new OperationResult<SR6SkillValue>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return points1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#canBeIncreasedPoints(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints(SR6SkillValue value) {
//		if (!model.getSkillValues().contains(value)) {
//			return canBeSelected(value.getModifyable());
//			// Value not present in character
////			return Possible.FALSE;
//		}
		
		// Is the new value acceptable
		Possible allowed = wouldNewValueBeOkay(value);
		if (!allowed.get())
			return allowed;
		
		if (points1<1){
			return new Possible(I18N_NOT_RAISED_POINT1);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints(SR6SkillValue value) {
		PerSkillPoints per = getPerSkill(value);
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);
		
		return new Possible(per.points1>0, I18N_NOT_RAISED_POINT1);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#increasePoints(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> increasePoints(SR6SkillValue value) {
//		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.INFO, "ENTER increasePoints({0})", value.getModifyable().getId());

		try {
			Possible allowed = canBeIncreasedPoints(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase with points1, but "+allowed);
				return new OperationResult<>(allowed);
			}

			if (value.getDistributed()==0) {
				logger.log(Level.DEBUG, "Don't increase, but select");
				return select(value.getModifyable());
			}
			PerSkillPoints per = getPerSkill(value);
			// Do increase
			per.points1++;
			logger.log(Level.INFO, "increase points of {0} to {1} - sum is now {2}", value.getModifyable().getId(),
					per.points1, per.getSum());

			getCharacterController().runProcessors();
			return new OperationResult<SR6SkillValue>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "LEAVE increasePoints({0})", value.getModifyable().getId());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#decreasePoints(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> decreasePoints(SR6SkillValue value) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER decreasePoints({0})", value.getModifyable().getId());

		try {
			Possible allowed = canBeDecreasedPoints(value);
			if (!allowed.get())
				return new OperationResult<>(allowed);

			PerSkillPoints per = getPerSkill(value);
			// Do increase
			per.points1--;
			logger.log(Level.INFO, "decrease points of {0} to {1} - sum is now {2}", value.getModifyable().getId(),
					per.points1, per.getSum());
			if (per.getSum()==0) {
				deselect(value);
			} else {
				getCharacterController().runProcessors();
			}
			return new OperationResult<SR6SkillValue>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "LEAVE decreasePoints({0})", value.getModifyable().getId());
		}
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
		return String.valueOf(getSelectionCostString(data));
	}

}
