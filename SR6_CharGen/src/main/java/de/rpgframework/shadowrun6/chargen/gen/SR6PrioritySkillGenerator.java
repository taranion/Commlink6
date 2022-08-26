package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import de.rpgframework.genericrpg.NumericalValueWith3PoolsController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Skill Points
 * Points 2 = Free Language/Knowledge skills
 * Points 3 = Karma
 * @author prelle
 *
 */
public class SR6PrioritySkillGenerator extends CommonSkillGenerator implements NumericalValueWith3PoolsController<SR6Skill, SR6SkillValue> {

	public final static String I18N_SKILLTYPE   = "skill.error.wrongSkillType";
	public final static String I18N_SPEC_LEVEL  = "skill.error.wrongSpecLevel";

	private List<ToDoElement> normalToDos;
	private List<ToDoElement> knowledgeToDos;

	//-------------------------------------------------------------------
	public SR6PrioritySkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
		normalToDos    = new ArrayList<>();
		knowledgeToDos = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<SR6SkillValue> getSelected() {
		SR6PrioritySettings settings = getModel().getCharGenSettings(SR6PrioritySettings.class);
		for (Entry<String, PerSkillPoints> entry : settings.perSkill.entrySet()) {
			
		}
		logger.log(Level.DEBUG, "getSelected() returns {0} entries", model.getSkillValues().size());
		
		List<SR6SkillValue> selected = model.getSkillValues();
		// Sort by type and name
		Collections.sort(selected, new Comparator<SR6SkillValue>() {
			public int compare(SR6SkillValue o1, SR6SkillValue o2) {
				SkillType t1 = o1.getModifyable().getType();
				SkillType t2 = o2.getModifyable().getType();
				boolean b1 = t1==SkillType.KNOWLEDGE || t1==SkillType.LANGUAGE;
				boolean b2 = t2==SkillType.KNOWLEDGE || t2==SkillType.LANGUAGE;
				if (b1 && !b2) return 1;
				if (!b1 && b2) return -1;
				if (b1 && b2 && (t1!=t2)) return t1.compareTo(t2);
				return o1.getName(Locale.getDefault()).compareTo(o2.getName(Locale.getDefault()));
			}});
		return selected;
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
				logger.log(Level.DEBUG, "Invest {0} free knowledge skill points for {1}", invest, key);
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
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#canBeSelected(SR6Skill, Decision[])
	 */
	@Override
	public Possible canBeSelected(SR6Skill data, Decision...decisions) {
		// First check if it is allowed to select this skill
		Possible pos = super.canBeSelected(data);
		if (!pos.get())
			return pos;
		
		// Now find out if it could be payed
		if (points1>0)
			return Possible.TRUE;
		if (points2>0 && (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)) {
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
	public OperationResult<SR6SkillValue> select(SR6Skill data, Decision...decisions) {
		logger.log(Level.WARNING, "ENTER select("+data+")");
		try {
			OperationResult<SR6SkillValue> result = super.select(data);
			if (result.hasError()) {
				logger.log(Level.WARNING, "Selecting {0} failed, because {1}",data.getId(), result.getMessages());
				return result;
			}
			
			logger.log(Level.INFO, "Selected skill {0}", data.getId());
			if (decisions.length>0) {
				logger.log(Level.INFO, "Decisions: {0}", List.of(decisions));
				for (Decision dec : decisions) {
					result.get().addDecision(dec);
				}
			}
			if (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE) {
				result.get().setUuid(UUID.randomUUID());
			}
			
			/*
			 * Now try to detect how to pay it
			 */
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			PerSkillPoints per = new PerSkillPoints();
			if ((data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE) && super.points2>0) {
				logger.log(Level.DEBUG, "pay with free knowledge skill points");
				per.points2++;				
			} else if (points1>0) {
				logger.log(Level.DEBUG, "pay with skill points");
				per.points1++;		
			} else {
				logger.log(Level.DEBUG, "pay with karma");
				per.points3++;		
			}
			settings.put(result.get(), per);
			
			getCharacterController().runProcessors();
			return result;
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+data+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(SR6SkillValue data) {
		logger.log(Level.WARNING, "ENTER deselect("+data+")");
		try {
			boolean success = super.deselect(data);
			if (!success) {
				logger.log(Level.WARNING, "Deselecting {0} failed",data.getKey());
				return false;
			}
			
			logger.log(Level.INFO, "Deselected skill {0}", data);		
			
			/*
			 * Already has been removed from model,
			 * Now remove it from generator
			 */
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			settings.remove(data);
			
			getCharacterController().runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE deselect("+data+")");
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
			
			allowed = canBeIncreasedPoints3(ref);
			if (allowed.get()) {
				return increasePoints3(ref);
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
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeIncreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints2(SR6SkillValue value) {
		Possible poss = super.canBeIncreasedPoints2(value);
		if (!poss.get())
			return poss;
		
		// Is it a language or knowledge skills?
		if (value.getModifyable().getType()!=SkillType.KNOWLEDGE && value.getModifyable().getType()!=SkillType.LANGUAGE)
			return new Possible(I18N_SKILLTYPE);
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints2(SR6SkillValue value) {
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		PerSkillPoints per = settings.get(value);
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
		PerSkillPoints per = settings.get(key);
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
		logger.log(Level.INFO, "ENTER: increasePoints3({0})", value);
		try {

			if (value == null) {
				logger.log(Level.ERROR, "Trying to increase a skill not previously selected");
				return new OperationResult<>(new Possible(I18N_NOT_SELECTED));
			}

			Possible allowed = canBeIncreasedPoints3(value);
			if (!allowed.get()) {
				logger.log(Level.ERROR, "Trying to increase a skill that may not be increased");
				return new OperationResult<>(allowed);
			}
			
			PerSkillPoints per = getPerSkill(value);
			if (per==null) {
				logger.log(Level.ERROR, "No PerSkillPoints found for {0}",value);
				return new OperationResult<>();
			}

			// Do increase
			int karma = getIncreaseCost(value);
			value.setDistributed(value.getDistributed() + 1);
			// Pay karma
			model.setKarmaFree(model.getKarmaFree() - karma);

			// Do increase
			per.points3++;
			logger.log(Level.INFO, "increase karma points of {0} to {1} - sum is now {2}", value.getModifyable().getId(),
					per.points3, per.getSum());

			parent.runProcessors();

			return new OperationResult<SR6SkillValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE: increasePoints3({0})", value.getKey());
		}
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
	private void updateAvailable() {
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
	private void ensureExistanceOfNativeLanguage() {
		ValueModification nat = new ValueModification(ShadowrunReference.SKILL, "language",0);
		boolean missingNative=true;
		for (SR6SkillValue tmp : model.getSkillValues()) {
			if (tmp.getSkill()==Shadowrun6Core.getSkill("language") && tmp.getModifiedValue()==4)
				missingNative=false;
		}
		if (missingNative) {
			SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("language"),4);
			val.setUuid(UUID.randomUUID());
			val.addDecision(new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), RES.getString("label.native_language")));
			val.addModification(nat);
			model.addSkillValue(val);
		}
	}
	
	//-------------------------------------------------------------------
	private SR6Skill getSkillFromPrioritySettings(String prioritySettingsId) {
		if (prioritySettingsId.contains("/")) {
			String key = prioritySettingsId.substring(0, prioritySettingsId.indexOf("/"));
			return Shadowrun6Core.getSkill(key);
		} else {
			return Shadowrun6Core.getSkill(prioritySettingsId);
		}
	}
	
	//-------------------------------------------------------------------
	private SR6SkillValue getFromPrioritySettings(String prioritySettingsId) {
		if (prioritySettingsId.contains("/")) {
			String key = prioritySettingsId.substring(0, prioritySettingsId.indexOf("/"));
			String uuid_s = prioritySettingsId.substring(prioritySettingsId.indexOf("/")+1);
			if (uuid_s==null)
				logger.log(Level.ERROR, "NPE for id "+prioritySettingsId);
			if ("null".equals(uuid_s)) {
				logger.log(Level.ERROR, "Pseudo NULL for id "+prioritySettingsId);
				return null;
			}
			UUID uuid = UUID.fromString(uuid_s);
			SR6Skill skill = Shadowrun6Core.getSkill(key);
			for (SR6SkillValue val : model.getSkillValues()) {
				if (val.getSkill()==skill && val.getUuid()!=null && val.getUuid().equals(uuid)) {
					return val;
				}
			}
			logger.log(Level.ERROR, "Did not detected "+key+" | "+uuid+" in "+model.getSkillValues());

		} else {
			return model.getSkillValue(Shadowrun6Core.getSkill(prioritySettingsId));
		}
		return null;
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
			points1   = 0;
			points2 = model.getAttribute(ShadowrunAttribute.LOGIC).getDistributed();
			todos.clear();
			normalToDos.clear();
			knowledgeToDos.clear();
			allowed.clear();
			maxLimit = 1;
			
			// Ensure auto-added native skill
			ensureExistanceOfNativeLanguage();
			
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.CREATION_POINTS) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getResolvedKey()==CreatePoints.SKILLS) {
						points1 += mod.getValue();
						logger.log(Level.DEBUG, "Consume "+mod+" and set pointsSkills to "+points1);
					} else {
						if (ApplyTo.POINTS==mod.getApplyTo()) {
							ShadowrunReference ref = (ShadowrunReference)mod.getReferenceType();
							switch (ref) {
							case SKILL:
								logger.log(Level.DEBUG, "Add "+mod.getValue()+" skill points from "+tmp.getSource());
								points1 += mod.getValue();
								break;
							case SKILL_KNOWLEDGE:
								logger.log(Level.DEBUG, "Add "+mod.getValue()+" knowledge skill points from "+tmp.getSource());
								points2 += mod.getValue();
								break;
							default:
								unprocessed.add(mod);
							}						
						} else {
							unprocessed.add(tmp);
						}					
					}
				} else if (tmp.getReferenceType()==ShadowrunReference.SKILL) {
					if (tmp instanceof ValueModification) {
						ValueModification mod = (ValueModification)tmp;
						SR6Skill key = mod.getResolvedKey();
						SR6SkillValue val = model.getSkillValue(key);
						// If skill is non-existent yet, add it
						if (val==null) {
							val = new SR6SkillValue(key,0);
							val.setResolved(key);
							model.addSkillValue(val);
						}
						logger.log(Level.INFO, "Consume {0} in skillval {1}", mod, val);
						val.addModification(mod);
						if (mod.getSet()==ValueType.MAX) {
							logger.log(Level.INFO, "Maximum of skill {0} is now {1}", key, getMaximum(val));
						}
					} else if (tmp instanceof AllowModification) {
						AllowModification mod = (AllowModification)tmp;
						SR6Skill key = mod.getResolvedKey();
						logger.log(Level.INFO, "Allow skill {0}", mod.getKey());
						if (key!=null)
							allowed.add(key);
					} else {
						logger.log(Level.WARNING, "ToDo: handle "+tmp);
						System.err.println("PrioritySR6SkillGenerator: ToDo: handle "+tmp);
						unprocessed.add(tmp);
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			updateAvailable();
			
			Shadowrun6Character model = parent.getModel();
			SR6PrioritySettings settings = getModel().getCharGenSettings(SR6PrioritySettings.class);
			for (Entry<String, PerSkillPoints> entry : settings.perSkill.entrySet()) {
				logger.log(Level.DEBUG, "---> {0}", entry.getKey());
				SR6SkillValue sVal = getFromPrioritySettings(entry.getKey());
				if (sVal==null) {
					logger.log(Level.ERROR, "Cannot find SkillValue for ''{0}'' from PrioritySettings", entry.getKey());
					continue;
				}
				SR6Skill key = sVal.getResolved();
				
				PerSkillPoints per = entry.getValue();
				logger.log(Level.DEBUG, entry.getKey()+" = "+per.toString());
				/* 
				 * Pay skill points 
				 */
				int required = per.points1;
				if (required>0) {
					if (points1>0) {
						int pay = Math.min(points1, required);
						logger.log(Level.DEBUG, "  Pay {0} skillpoints for {1}", pay, key);
						points1 -= pay;
						required -= pay;
					}
					// If not enough, convert
					if (required>0) {
						per.points1 -= required;
						logger.log(Level.WARNING, "Not enough skillpoints to pay {0} - reduce it to {1}", key, per.points1);
					}
				}
				
				/* Pay language/knowledge */
				required = per.points2;
				if (required>0) {
					if (points2>0) {
						int pay = Math.min(points2, required);
						logger.log(Level.DEBUG, "  Pay {0} knowledge skillpoints for {1}", pay, key);
						points2 -= pay;
						required -= pay;
					}
					// If not enough, convert
					if (required>0) {
						per.points2 -= required;
						logger.log(Level.WARNING, "Not enough skillpoints to pay {0} - reduce it to {1}", key, per.points1);
					}
				}
				
				/* Pay karma */
				if (per.points3>0) {
					int pay = per.getKarmaInvestSR6();
					model.setKarmaFree( model.getKarmaFree() - pay );
					logger.log(Level.DEBUG, "  Pay {0} karma for {1}", pay, key);
				}
				
				if (per.karmaSpec>0) {
					int pay = per.karmaSpec*5;
					model.setKarmaFree( model.getKarmaFree() - pay );
					logger.log(Level.DEBUG, "  Pay {0} karma for specializations {1}", pay, key);
				}
				
				// Update model
			}
			logger.log(Level.DEBUG, "Finish with {0} and {1} skill points and {2} Karma", points1, points2, getModel().getKarmaFree());
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, settings.toSkillString());

			/*
			 * Copy from settings to character
			 */
			List<SR6SkillValue> usedSkills = new ArrayList<>();
			for (Entry<String,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				logger.log(Level.DEBUG, "  final "+entry.getKey()+"= "+ entry.getValue());
				if (entry.getValue().getSum()==0) continue;
				SR6SkillValue val = getFromPrioritySettings(entry.getKey());
				if (val==null) {
					SR6Skill skill = getSkillFromPrioritySettings(entry.getKey());
					if (skill==null) {
						logger.log(Level.ERROR, "No skill '"+entry.getKey()+"'");
						System.err.println( "No skill '"+entry.getKey()+"'");
						System.exit(1);
					}
					val = new SR6SkillValue(skill, 0);
					model.addSkillValue(val);
					logger.log(Level.DEBUG, "  Add skill value to char: "+entry.getKey());
				}
				if (val.getSkill()==null) {
					logger.log(Level.WARNING, "Found SR6SkillValue without skill: "+val);
					continue;
				}
				if (val.getSpecializations().isEmpty()) {
					val.setDistributed(entry.getValue().getSum());					
				} else {
					val.setDistributed(entry.getValue().getSum()-1);
				}
				usedSkills.add(val);
			}
			// Reverse check: all skills in model should be in usedSkills
			for (SR6SkillValue val : new ArrayList<>(model.getSkillValues())) {
				if (!usedSkills.contains(val)) {
					// If not auto-added skill, remove it
					if (!val.isAutoAdded()) {
						logger.log(Level.WARNING, "Skill {0}/{1} was found in character, but not in skill generator settings", val, val.getUuid());
						model.getSkillValues().remove(val);
					} else {
						logger.log(Level.DEBUG, "Found auto-added skill {0}", val);
					}
				}
			}
			
			// Validate that there are no skill points left
			logger.log(Level.DEBUG, "Finish with {0} skill points and {1} knowledge skill points and {2} Karma", points1, points2, getModel().getKarmaFree());
			
			if (points1>0) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_SKILL_REMAIN_POINTS, points1));
				logger.log(Level.WARNING, "Have {0} skill points left", points1);
			}
			if (points2>0) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_SKILL_REMAIN_POINTS2, points2));
				logger.log(Level.WARNING, "Have {0} languages/knowledge skills left", points2);
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, "Error",e);
		} finally {
			logger.log(Level.TRACE, "STOP : Skills");
		}
		
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#getColumn1()
	 */
	@Override
	public String getColumn1() {
		return RES.getString("column.points1");
	}

	@Override
	public String getColumn2() {
		return RES.getString("column.points2");
	}

	@Override
	public String getColumn3() {
		return RES.getString("column.points3");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canSelectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
	 */
	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec, boolean expertise) {
		List<SkillSpecialization<SR6Skill>> available = getAvailableSpecializations(skillVal);
		if (!available.contains(spec)) {
			return new Possible(Severity.STOPPER, RES, I18N_NOT_AVAILABLE_SPEC, skillVal.getKey(), spec.getId(), expertise);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canDeselectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
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
	public OperationResult<SkillSpecializationValue<SR6Skill>> select(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec, boolean expertise) {
		logger.log(Level.TRACE, "ENTER: select({0}, {1}, {2})", skillVal.getKey(), spec, expertise);
		try {
			Possible poss = canSelectSpecialization(skillVal, spec, expertise);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Tried to select a specialization, which is not allowed because: "+poss.getMostSevere());
				return new OperationResult<>(poss);
			}
			
			SkillSpecializationValue<SR6Skill> ret = new SkillSpecializationValue<>(spec);
			skillVal.getSpecializations().add(ret);
			logger.log(Level.INFO, "Select specialization '{0}' in skill '{1}'", spec.getId(), skillVal.getKey());
			
			// Now pay
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			if (settings.get(skillVal)==null) {
				settings.put(skillVal, new PerSkillPoints());
			}
			if (points1>0) {
				logger.log(Level.INFO, "Pay with skill points");
				settings.get(skillVal).points1++;
			} else {
				settings.get(skillVal).karmaSpec++;
				logger.log(Level.INFO, "Pay with karma "+settings.get(skillVal));
			}
			
			
			parent.runProcessors();
			
			return new OperationResult<>(ret);
		} finally {
			logger.log(Level.TRACE, "LEAVE: select({0}, {1}, {2})", skillVal.getKey(), spec, expertise);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#deselect(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
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
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#getRecommendationState(SR6SkillValue)
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
	@Override
	public void roll() {
		logger.log(Level.INFO, "ENTER roll()");
		try {
			Random random = new Random();
			SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
//			updateAvailable();
			
			// Now spend points
			while (points1>0) {	
				// Decide if to increase existing or add new
				int dice = random.nextInt(5);
				List<SR6SkillValue> actionSkills = model.getSkillValues(SkillType.regularValues());
				if (dice==0 || actionSkills.size()<2) {
					// Add new
					int ran = random.nextInt(available.size());
					SR6Skill toAdd = available.get(ran);
					if (canBeSelected(toAdd).get()) {
						SR6SkillValue val = new SR6SkillValue(toAdd,1);
						PerSkillPoints points = new PerSkillPoints();
						points.points1=1;
						settings.put(val, points);
						model.addSkillValue(val);
						logger.log(Level.INFO, "Added skill ''{0}'' with value 1", toAdd.getId());
						points1--;
						updateAvailable();
					} else {
						logger.log(Level.ERROR, "Picked a skill I cannot select: "+toAdd+" with "+points1);
					}
				} else {
					// Raise existing
					int ran = random.nextInt(actionSkills.size());
					SR6SkillValue toInc = actionSkills.get(ran);
					if (canBeIncreasedPoints(toInc).get()) {
						PerSkillPoints val = settings.get(toInc);
						toInc.setDistributed( toInc.getDistributed()+1);
						val.points1++;
						logger.log(Level.INFO, "Increased ''{0}'' to {1}", toInc.getKey(), val.getSum());
						points1--;
					} else {
						logger.log(Level.ERROR, "Picked a skill I cannot increase: "+toInc+" with "+points1);
					}
				}
			}
			
			parent.runProcessors();
		} finally {
			logger.log(Level.INFO, "LEAVE roll()");
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith1PoolController#getPoints(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getPoints(SR6SkillValue key) {
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		PerSkillPoints val = settings.get(key);
		if (val==null) {
			logger.log(Level.ERROR, "Cannot determine points1 for not present SkillValue {0}", key);
			return -1;
		}
		return val.points1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#getPoints2(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getPoints2(SR6SkillValue key) {
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		PerSkillPoints val = settings.get(key);
		if (val==null) {
			logger.log(Level.ERROR, "Cannot determine points2 for not present SkillValue {0}", key);
			return -1;
		}
		return val.points2;
	}

}
