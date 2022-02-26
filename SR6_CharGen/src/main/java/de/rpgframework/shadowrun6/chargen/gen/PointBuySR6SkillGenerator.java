package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.rpgframework.genericrpg.NumericalValueWith2PoolsController;
import de.rpgframework.genericrpg.NumericalValueWith3PoolsController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Character Points
 * Points 2 = Karma
 * @author prelle
 *
 */
public class PointBuySR6SkillGenerator extends CommonSkillGenerator implements NumericalValueWith2PoolsController<SR6Skill,SR6SkillValue> {

	private int skillsFromCP;
//	private int pointsLangAndKnow;

	//-------------------------------------------------------------------
	public PointBuySR6SkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
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
		
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		// Are there enough unused skillpoints
		if (pointsSkills>0) return Possible.TRUE;
		// Are there enough unused skillpoints
		if (settings.characterPoints>=2) return Possible.TRUE;
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
				logger.log(Level.DEBUG, "Selecting {} failed, because {}",data.getId(), result.getMessages());
				return result;
			}
			
			logger.log(Level.INFO, "Selected skill {}", data.getId());
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			PerSkillPoints per = new PerSkillPoints();
			per.points1=1;
			settings.perSkill.put(result.get(), per);
			
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
		OperationResult<SR6SkillValue> result = increasePoints(ref);
		if (result.wasSuccessful()) return result;
		
		result = increasePoints2(ref);
		if (result.wasSuccessful()) return result;
		
		logger.log(Level.WARNING, "Could not raise with any method");
		return new OperationResult<>();
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
			// Reset values
			pointsSkills   = 12;
			skillsFromCP   = 0;
//			pointsLangAndKnow = 0;
			available.clear();
			todos.clear();
			
			for (Modification tmp : previous) {
				if (tmp instanceof AllowModification) {
					AllowModification mod = (AllowModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.SKILL) {
						SR6Skill skill = mod.getResolvedKey();
						if (skill==null) {
							logger.log(Level.ERROR, "AllowMod for unknown skill {}", mod.getKey());
						} else {
							logger.log(Level.DEBUG, "Allow skill {} from {}", mod.getKey(), mod.getSource());
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

			Shadowrun6Character model = parent.getModel();
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			for (Entry<SR6SkillValue,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				SR6Skill key = entry.getKey().getModifyable();
				PerSkillPoints per = entry.getValue();
				if (per == null) {
					logger.log(Level.WARNING, "No data for " + key);
					continue;
				}
				/* 
				 * Pay skilll points 
				 */
				int required = per.points1;
				if (required>0) {
					if (pointsSkills>0) {
						int pay = Math.min(pointsSkills, required);
						logger.log(Level.DEBUG, "Pay {} free CP for {}", pay, key);
						pointsSkills -= pay;
						required -= pay;
					}
					// If not enough, convert
					if (required>0) {
						int pay = required*2;
						logger.log(Level.DEBUG, "Convert {} CP to {} skill points for {}", pay, required, key);
						settings.characterPoints -= pay;
						settings.cpToSkills += required;
						required -= pay;
					}
				}
			}
			logger.log(Level.DEBUG, "Finish with {} skill points", pointsSkills);
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, settings.toSkillString());

			
			logger.log(Level.INFO, "{} CP converted to {} skills", settings.cpToSkills*2, settings.cpToSkills);
			
			/*
			 * Copy from settings to character
			 */
			List<SR6SkillValue> usedSkills = new ArrayList<>();
			for (Entry<SR6SkillValue,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				if (entry.getValue().getSum()==0) continue;
				SR6SkillValue val = entry.getKey();
				if (!model.getSkillValues().contains(val)) {
					model.addSkillValue(val);
				}
				val.setDistributed(entry.getValue().getSum());
				usedSkills.add(val);
			}
			// Reverse check: all skills in model should be in usedSkills
			for (SR6SkillValue val : new ArrayList<>(model.getSkillValues())) {
				if (!usedSkills.contains(val)) {
					logger.log(Level.WARNING, "Skill {} was found in character, but not in skill generator settings", val);
					model.getSkillValues().remove(val);
				}
			}
			
			
			/*
			 * Ensure limits of 20 are in kept
			 */
			if (settings.cpToSkills>20) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many CP converted to skill points"));
			}
			if (pointsSkills>0) {
				todos.add(new ToDoElement(Severity.STOPPER, pointsSkills+" skill points left to spend"));
			}

		} catch (Exception e) {
			
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		
		return unprocessed;
	}

	@Override
	public int getPointsLeft() {
		return pointsSkills;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController#canBeIncreasedPoints(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	public Possible canBeIncreasedPoints(SR6SkillValue key) {
		Possible allowed = super.canBeIncreasedPoints(key);
		if (!allowed.get())
			return allowed;
		
		// Are there enough free points?
		if (pointsSkills>0)
			return Possible.TRUE;
		// Only 20 attribute points may be generated from CP
		if (skillsFromCP >= 20)
			return new Possible("skill.points.already20CP");
		// Every conversion costs 2 CP
		if (parent.getModel().getCharGenSettings(SR6PointBuySettings.class).characterPoints < 2)
			return new Possible("skill.points.notEnoughCP");

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints2(SR6SkillValue key) {
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints per = settings.perSkill.get(key);
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);
		return new Possible(per.points3>0, I18N_NOT_RAISED_KARMA);
	}

	@Override
	public String getColumn1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumn2() {
		// TODO Auto-generated method stub
		return null;
	}

	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec) {
		return Possible.FALSE;
	}

	public Possible canDeselectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec){
		return Possible.FALSE;
	}
	public OperationResult<SR6SkillValue> selectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec) {
		return new OperationResult<>();
	}

	public OperationResult<SR6SkillValue> deselectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec){
		return new OperationResult<>();
	}

	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canDeselectSpecialization(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<SkillSpecializationValue<SR6Skill>> select(SR6SkillValue skillVal,
			SkillSpecialization<SR6Skill> spec, boolean expertise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deselect(SR6SkillValue skillVal, SkillSpecializationValue<SR6Skill> spec) {
		// TODO Auto-generated method stub
		return false;
	}

}
