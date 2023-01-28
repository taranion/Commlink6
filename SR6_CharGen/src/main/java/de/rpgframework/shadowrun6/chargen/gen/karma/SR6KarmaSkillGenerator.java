package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSkillGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Free skill points
 * Points 2 = (convertible) Skill points from Character Points
 * Points 3 = Karma
 * @author prelle
 *
 */
public class SR6KarmaSkillGenerator extends CommonSkillGenerator {

	private int freeLangKnow;

	//-------------------------------------------------------------------
	public SR6KarmaSkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillGenerator#getPerSkill(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	protected PerSkillPoints getPerSkill(SR6SkillValue value) {
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#canBeSelected(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public Possible canBeSelected(SR6Skill skill, Decision... dec) {
		// Check if that skill is allowed
		Possible allowed = super.canBeSelected(skill);
		if (!allowed.get()) return allowed;

		// Has the user enough karma
		int pay = (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE)?3:5;
		if (model.getKarmaFree()>=pay) return Possible.TRUE;

		return Possible.FALSE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#select(SR6Skill, Decision[])
	 */
	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data, Decision...decisions) {
		logger.log(Level.DEBUG, "ENTER select("+data+")");
		try {
			OperationResult<SR6SkillValue> result = super.select(data);
			if (result.hasError()) {
				logger.log(Level.DEBUG, "Selecting {0} failed, because {1}",data.getId(), result.getMessages());
				return result;
			}

			logger.log(Level.INFO, "Selected skill {0}", data.getId());

			getCharacterController().runProcessors();
			return result;
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+data+")");
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

		// Can it be payed in any way
		return new Possible( canBeIncreasedPoints(value).get() || canBeIncreasedPoints2(value).get());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue ref) {
		Possible poss = canBeIncreased(ref);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to increase skill {0} which is not allowed: "+poss);
			return new OperationResult<>(poss);
		}

		ref.setDistributed( ref.getDistributed() +1);
		logger.log(Level.INFO, "Increased skill {0} to {1}", ref.getKey(), ref.getModifiedValue(ValueType.NATURAL));
		// Pay karma
		int karma = ref.getModifiedValue(ValueType.NATURAL);
		model.setKarmaFree( model.getKarmaFree() -karma );
		model.setKarmaInvested( model.getKarmaInvested() +karma);

		parent.runProcessors();

		return new OperationResult<SR6SkillValue>(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		try {
			Shadowrun6Character model = parent.getModel();
			SR6KarmaSettings settings = getModel().getCharGenSettings(SR6KarmaSettings.class);

			// Reset values
			maxLimit = 1;
			settings.skills = 0;
			points1   = 0;
			points2   = 0;
			freeLangKnow   = model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue(ValueType.NATURAL);
//			pointsLangAndKnow = 0;
			available.clear();
			allowed.clear();
			todos.clear();

			for (Modification tmp : previous) {
				if (tmp instanceof AllowModification) {
					AllowModification mod = (AllowModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.SKILL) {
						SR6Skill skill = mod.getResolvedKey();
						if (skill==null) {
							logger.log(Level.ERROR, "AllowMod for unknown skill {0}", mod.getKey());
						} else {
							logger.log(Level.DEBUG, "Allow skill {0} from {1}", mod.getKey(), mod.getSource());
							this.allowed.add(skill);
							this.available.add(skill);
						}
					} else {
						unprocessed.add(mod);
					}
//				if (tmp instanceof ValueModification) {
//					ValueModification mod = (ValueModification)tmp;
//					if (ApplyTo.POINTS==mod.getApplyTo()) {
//						ShadowrunReference ref = (ShadowrunReference)mod.getReferenceType();
//						switch (ref) {
//						case SKILL:
//							logger.log(Level.DEBUG, "Add "+mod.getValue()+" skill points from "+tmp.getSource());
//							pointsSkills += mod.getValue();
//							break;
//						case SKILL_KNOWLEDGE:
//							logger.log(Level.DEBUG, "Add "+mod.getValue()+" knowledge skill points from "+tmp.getSource());
//							pointsLangAndKnow += mod.getValue();
//							break;
//						default:
//							unprocessed.add(mod);
//						}
//					}
				} else {
					unprocessed.add(tmp);
				}
			}

			// Be sure to remove all skills that are not allowed
			removeRestrictedSkills();


			for (SR6SkillValue val : model.getSkillValues()) {
				int karma = 0;
				switch (val.getSkill().getType()) {
				case LANGUAGE:
					// Pay language levels other than native
					if (val.getDistributed()<4) {
						karma += val.getDistributed()*3;
						logger.log(Level.INFO, "Pay {0} for language {1}", karma, val.getKey(), val.getDistributed());
					}
					break;
				case KNOWLEDGE:
					// No specializations for Knowledge
					karma+=3;
					break;
				default:
					// Pay specialization (should be max. 1)
					karma += val.getSpecializations().size()*5;
					int from = val.getModifier(ValueType.NATURAL);
					int upTo = val.getModifiedValue(ValueType.NATURAL);
					for (int i=from+1; i<=upTo; i++) {
						karma += i*5;
					}
				}
				logger.log(Level.INFO, "Pay {0} for {1} with {2} specializations", karma, val.getKey(), val.getSpecializations().size());
				model.setKarmaFree( model.getKarmaFree() -karma);
				model.setKarmaInvested( model.getKarmaInvested() +karma);
				settings.skills += karma;
			}

		} catch (Exception e) {

		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}

		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void removeRestrictedSkills() {
		logger.log(Level.DEBUG, "Check for existing restricted skills: "+model.getSkillValues());
		for (SR6SkillValue sVal : new ArrayList<>(model.getSkillValues())) {
			SR6Skill skill = sVal.getResolved();
			if (skill.isRestricted() && !allowed.contains(skill)) {
				logger.log(Level.INFO, "Skill {0} is not allowed anymore - remove it from character", skill);
				model.removeSkillValue(sVal);
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController#canBeIncreasedPoints(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	public Possible canBeIncreasedPoints(SR6SkillValue key) {
		Possible allowed = wouldNewValueBeOkay(key);
		if (!allowed.get())
			return allowed;

		// Are there enough free points?
		if (points1>0)
			return Possible.TRUE;
		if (points2>0)
			return Possible.TRUE;

//		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
//		// Every conversion costs 2 CP
//		if (settings.characterPoints < 2)
//			return new Possible("skill.points.notEnoughCP");
//		// Only 20 attribute points may be generated from CP
//		if (skillsFromCP >= 20)
//			return new Possible("skill.points.already20CP");

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeIncreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints2(SR6SkillValue value) {
		// Is the new value acceptable
		Possible allowed = wouldNewValueBeOkay(value);
		if (!allowed.get())
			return allowed;

		// Is there enough Karma
		int karmaNeeded = (value.getDistributed()+1) *5;
		if (karmaNeeded>model.getKarmaFree()){
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#increasePoints2(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> increasePoints2(SR6SkillValue value) {
		return new OperationResult<>(Possible.FALSE);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints2(SR6SkillValue key) {
		return Possible.FALSE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#increasePoints2(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> decreasePoints2(SR6SkillValue value) {
		return new OperationResult<>(Possible.FALSE);
	}

	@Override
	public String getColumn1() {
		return RES.getString("pointbuy.points1");
	}

	@Override
	public String getColumn2() {
		return RES.getString("pointbuy.points2");
	}

//	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec) {
//		return Possible.FALSE;
//	}

//	public Possible canDeselectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec){
//		return Possible.FALSE;
//	}
//	public OperationResult<SR6SkillValue> selectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec) {
//		return new OperationResult<>();
//	}
//
//	public OperationResult<SR6SkillValue> deselectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec){
//		return new OperationResult<>();
//	}

	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {
		/*
		 * You cannot acquire more than one specialization in a skill at character creation,
		 * and you cannot acquire an expertise.
		 */
		if (expertise) return Possible.FALSE;

		// Check if there already is one specialization in this skill
		if (!skillVal.getSpecializations().isEmpty())
			return Possible.FALSE;

		List<SkillSpecialization<SR6Skill>> available = getAvailableSpecializations(skillVal);
		if (!available.contains(spec)) {
			return new Possible(Severity.STOPPER, RES, I18N_NOT_AVAILABLE_SPEC, skillVal.getKey(), spec.getId(), expertise);
		}

		// If this is Exotic Weapons, no Karma/Points are needed
		if (skillVal.getKey().equals("exotic_weapons"))
			return Possible.TRUE;

		// Need a skill point or 5 Karma
		if (points1<1 && model.getKarmaFree()<5)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, 5);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canDeselectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecializationValue)
	 */
	@Override
	public Possible canDeselectSpecialization(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		return new Possible(skillVal.getSpecializations().contains(spec));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#select(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
	 */
	@Override
	public OperationResult<SkillSpecializationValue<SR6Skill>> select(SR6SkillValue skillVal,
			SkillSpecialization<SR6Skill> spec, boolean expertise) {
		logger.log(Level.TRACE, "ENTER: select({0}, {1}, {2})", skillVal.getKey(), spec, expertise);
		try {
			Possible poss = canSelectSpecialization(skillVal, spec, expertise);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Tried to select a specialization, which is not allowed because: "+poss.getMostSevere());
				return new OperationResult<>(poss);
			}

			SkillSpecializationValue<SR6Skill> ret = new SkillSpecializationValue<>(spec);
			skillVal.getSpecializations().add(ret);
			logger.log(Level.INFO, "Select specialization ''{0}'' in skill ''{1}''", spec.getId(), skillVal.getKey());

			parent.runProcessors();

			return new OperationResult<>(ret);
		} finally {
			logger.log(Level.TRACE, "LEAVE: select({0}, {1}, {2})", skillVal.getKey(), spec, expertise);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#deselect(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecializationValue)
	 */
	@Override
	public boolean deselect(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		logger.log(Level.DEBUG, "ENTER: deselect({0}, {1}",skillVal, spec);
		try {
			Possible poss = canDeselectSpecialization(skillVal, spec);
			if (!poss.get())
				return false;

			skillVal.getSpecializations().remove(spec);

			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE: deselect({0}, {1}",skillVal, spec);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#getPoints(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getPoints(SR6SkillValue key) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#getPoints2(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getPoints2(SR6SkillValue key) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(SR6SkillValue key) {
		return key.getModifiedValue(ValueType.NATURAL);
	}

}
