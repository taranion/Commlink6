package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rpgframework.genericrpg.NumericalValueWith3PoolsController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Skill Points
 * Points 2 = Free Language/Knowledge skills
 * Points 3 = Karma
 * @author prelle
 *
 */
public class PrioritySR6SkillGenerator extends CommonSkillGenerator implements NumericalValueWith3PoolsController<SR6Skill, SR6SkillValue> {

	private List<ToDoElement> normalToDos;
	private List<ToDoElement> knowledgeToDos;

	//-------------------------------------------------------------------
	public PrioritySR6SkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
		normalToDos    = new ArrayList<>();
		knowledgeToDos = new ArrayList<>();
	}
	
	//-------------------------------------------------------------------
	public static void optimize(Map<SR6SkillValue, PerSkillPoints> perSkill, int knowPoints, int skillPoints) {
		// Prepare a map to temporarily store data in
		Map<SR6SkillValue, PerSkillPoints> result = new LinkedHashMap<>();
		for (SR6SkillValue key : perSkill.keySet()) {
			result.put(key, new PerSkillPoints());
		}
		
		// Pay knowledge/language skills first
		List<SR6SkillValue> knowIDs = new ArrayList<>();
		int requiredPointsInKnowledgeSkills = 0;
		for (SR6SkillValue key : perSkill.keySet()) {
			SR6Skill skill = key.getModifyable();
			if (skill.getType()==SkillType.KNOWLEDGE || skill.getType()==SkillType.LANGUAGE) {
				knowIDs.add(key);
				requiredPointsInKnowledgeSkills += perSkill.get(key).getSum();
			}
		}
		// Now sort knowledge skill - highest rank first
		Collections.sort(knowIDs, new Comparator<SR6SkillValue>() {
			public int compare(SR6SkillValue o1, SR6SkillValue o2) {
				return - Integer.compare(o1.getDistributed(), o2.getDistributed());
			}
		});
		// Pay with knowledge points, as long as it is possible
		for (SR6SkillValue key : knowIDs) {
			PerSkillPoints old = perSkill.get(key);
			PerSkillPoints neu = result.get(key);
			int required = old.getSum();
			int invest   = Math.min(knowPoints, required);
			if (invest>0) {
				logger.log(Level.DEBUG, "Invest {} free knowledge skill points for {}", invest, key);
				neu.points2 = invest;
				required -= invest;
				requiredPointsInKnowledgeSkills -= invest;
			}
		}
		
		/*
		 * Regular skill points
		 */
		
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#canBeSelected(SR6Skill)
	 */
	@Override
	public Possible canBeSelected(SR6Skill data) {
		// First check if it is allowed to select this skill
		Possible pos = super.canBeSelected(data);
		if (!pos.get())
			return pos;
		
		// Now find out if it could be payed
		if (pointsSkills>0)
			return Possible.TRUE;
		if (pointsLangAndKnow>0 && (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)) {
			return Possible.TRUE;			
		}
		// No points left - maybe with karma?
		int karma = (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)?3:5;
		if (model.getKarmaFree()>=karma)
			return Possible.TRUE;
		return new Possible(new ValueRequirement(ShadowrunReference.ATTRIBUTE, "KARMA", karma));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#select(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data) {
		logger.log(Level.DEBUG, "ENTER select("+data+")");
		try {
			OperationResult<SR6SkillValue> result = super.select(data);
			if (result.hasError()) {
				logger.log(Level.WARNING, "Selecting {} failed, because {}",data.getId(), result.getMessages());
				return result;
			}
			
			logger.log(Level.INFO, "Selected skill {}", data.getId());
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
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
	private int getIncreaseCost(SR6SkillValue sVal) {
		SR6Skill key = sVal.getModifyable();

		if (key.getType()==SkillType.KNOWLEDGE || key.getType()==SkillType.LANGUAGE) 
			return 3;
		
		int newVal = (sVal==null)?1:(sVal.getModifiedValue()+1);
		return newVal*5;
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
		return new Possible( canBeIncreasedPoints(value).get() || canBeIncreasedPoints2(value).get() || canBeIncreasedPoints3(value).get() );
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue ref) {
		OperationResult<SR6SkillValue> result = super.increase(ref);
		
		parent.runProcessors();
		return result;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints2(SR6SkillValue value) {
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		PerSkillPoints per = settings.perSkill.get(value);
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);
		
		return new Possible(per.points2>0, I18N_NOT_RAISED_POINT2);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith3PoolsController#getPointsLeft3()
	 */
	@Override
	public int getPointsLeft3() {
		return model.getKarmaFree();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith3PoolsController#canBeIncreasedPoints3(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints3(SR6SkillValue key) {
		int karma = getIncreaseCost(key);
		if (model.getKarmaFree()>=karma)
			return Possible.TRUE;
		return new Possible(new ValueRequirement(ShadowrunReference.ATTRIBUTE, "KARMA", karma));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith3PoolsController#canBeDecreasedPoints3(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints3(SR6SkillValue key) {
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		PerSkillPoints per = settings.perSkill.get(key);
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);
		
		return new Possible(per.points3>0, I18N_NOT_RAISED_KARMA);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith3PoolsController#increasePoints3(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> increasePoints3(SR6SkillValue value) {
		Possible allowed = canBeIncreasedPoints3(value);
		if (!allowed.get()) 
			return new OperationResult<>(allowed);
		
		if (value==null) {
			logger.log(Level.ERROR, "Trying to increase a skill not previously selected");
			return new OperationResult<>(new Possible(I18N_NOT_SELECTED));
		}
		
		// Do increase
		int karma = getIncreaseCost(value);
		value.setDistributed(value.getDistributed()+1);
		// Pay karma
		model.setKarmaFree(model.getKarmaFree() - karma);
		
		return new OperationResult<SR6SkillValue>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith3PoolsController#decreasePoints3(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> decreasePoints3(SR6SkillValue value) {
		Possible allowed = canBeDecreasedPoints3(value);
		if (!allowed.get()) 
			return new OperationResult<>(allowed);
		
		if (value==null) {
			logger.log(Level.ERROR, "Trying to decrease a skill not previously selected");
			return new OperationResult<>(new Possible(I18N_NOT_SELECTED));
		}
		
		// Do increase
		value.setDistributed(value.getDistributed()-1);
		// Return karma
		int karma = getIncreaseCost(value);
		model.setKarmaFree(model.getKarmaFree() + karma);
		
		return new OperationResult<SR6SkillValue>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		
		logger.log(Level.TRACE, "START: Skills");
		try {
			// Reset values
			pointsSkills   = 0;
			pointsLangAndKnow = 0;
			normalToDos.clear();
			knowledgeToDos.clear();
			
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					if (ApplyTo.POINTS==mod.getApplyTo()) {
						ShadowrunReference ref = (ShadowrunReference)mod.getReferenceType();
						switch (ref) {
						case SKILL:
							logger.log(Level.DEBUG, "Add "+mod.getValue()+" skill points from "+tmp.getSource());
							pointsSkills += mod.getValue();
							break;
						case SKILL_KNOWLEDGE:
							logger.log(Level.DEBUG, "Add "+mod.getValue()+" knowledge skill points from "+tmp.getSource());
							pointsLangAndKnow += mod.getValue();
							break;
						default:
							unprocessed.add(mod);
						}						
					}					
				} else {
					unprocessed.add(tmp);
				}
			}
			
			Shadowrun6Character model = parent.getModel();
			SR6PrioritySettings settings = getModel().getCharGenSettings(SR6PrioritySettings.class);
			for (Entry<SR6SkillValue, PerSkillPoints> entry : settings.perSkill.entrySet()) {
				SR6Skill key = entry.getKey().getModifyable();
				PerSkillPoints per = entry.getValue();
				/* 
				 * Pay skill points 
				 */
				int required = per.points1;
				if (required>0) {
					if (pointsSkills>0) {
						int pay = Math.min(pointsSkills, required);
						logger.log(Level.DEBUG, "  Pay {} skillpoints for {}", pay, key);
						pointsSkills -= pay;
						required -= pay;
					}
					// If not enough, convert
					if (required>0) {
						per.points1 -= required;
						logger.log(Level.WARNING, "Not enough skillpoints to pay {} - reduce it to ", key, per.points1);
					}
				}
			}
			logger.log(Level.DEBUG, "Finish with {} skill points", pointsSkills);
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, settings.toSkillString());

			/*
			 * Copy from settings to character
			 */
			List<SR6SkillValue> usedSkills = new ArrayList<>();
			for (Entry<SR6SkillValue,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				if (entry.getValue().getSum()==0) continue;
				if (!model.getSkillValues().contains(entry.getKey())) {
					model.addSkillValue(entry.getKey());
				}
				SR6SkillValue val = entry.getKey();
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
		} catch (Exception e) {
			
		} finally {
			logger.log(Level.TRACE, "STOP : Skills");
		}
		
		return unprocessed;
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

	@Override
	public String getColumn3() {
		// TODO Auto-generated method stub
		return null;
	}

}
