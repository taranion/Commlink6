package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import de.rpgframework.genericrpg.NumericalValueWith2PoolsController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
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
public class SR6PointBuySkillGenerator extends CommonSkillGenerator implements NumericalValueWith2PoolsController<SR6Skill,SR6SkillValue> {

	private final static Logger logger = System.getLogger(SR6PointBuySkillGenerator.class.getPackageName()+".skill");

	private int skillsFromCP;

	//-------------------------------------------------------------------
	public SR6PointBuySkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
		try {
			throw new RuntimeException("Trace");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillGenerator#getPerSkill(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	protected PerSkillPoints getPerSkill(SR6SkillValue value) {
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		return settings.perSkill.get(value.getKey());
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
		if (points1>0) return Possible.TRUE;
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
			OperationResult<SR6SkillValue> result = super.select(data, decisions);
			if (result.hasError()) {
				logger.log(Level.DEBUG, "Selecting {0} failed, because {1}",data.getId(), result.getMessages());
				return result;
			}

			logger.log(Level.INFO, "Selected skill {0}", data.getId());
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			PerSkillPoints per = new PerSkillPoints();
			if (points1>0 || (settings.cpToSkills<20 && settings.characterPoints>=2))
				per.points1=1;
			else
				per.points3=1;

			settings.perSkill.put(result.get().getKey(), per);
			logger.log(Level.DEBUG, "Added to PointBuy settings: {0}={1}", result.get().getKey(), per);
			// karma payment not needed as above code stores which types of points where used and point / karma status is recalculated based on that

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
		return new Possible( canBeIncreasedPoints(value).get() || canBeIncreasedPoints2(value).get()); // canBeIncreasedPoints checks for Points1 & Points2, canBeIncreasedPoints 2 checks for Points3 = Karma
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 * basically using parent 'increase' but need unique 'increase' method here to ensure runProcessors after change 
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue ref) {
		OperationResult<SR6SkillValue> result = super.increase(ref);
		parent.runProcessors();
		return result;
	}

	// Using 'canBeDecreased' from CommonSkillGenerator which uses the one from CommonSkillController

	//-------------------------------------------------------------------
	private SR6Skill getSkillFromSettings(String settingsId) {
		if (settingsId.contains("/")) {
			String key = settingsId.substring(0, settingsId.indexOf("/"));
			return Shadowrun6Core.getSkill(key);
		} else {
			return Shadowrun6Core.getSkill(settingsId);
		}
	}

	//-------------------------------------------------------------------
	private SR6SkillValue getFromSettings(String settingsId) {
		if (settingsId.contains("/")) {
			String key = settingsId.substring(0, settingsId.indexOf("/"));
			String uuid_s = settingsId.substring(settingsId.indexOf("/")+1);
			if (uuid_s==null)
				logger.log(Level.ERROR, "NPE for id "+settingsId);
			if ("null".equals(uuid_s)) {
				logger.log(Level.ERROR, "Pseudo NULL for id "+settingsId);
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
			return model.getSkillValue(Shadowrun6Core.getSkill(settingsId));
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.WARNING, "ENTER process for "+this+" and model "+model);
		List<Modification> unprocessed = new ArrayList<>();

		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		try {
			Shadowrun6Character model = parent.getModel();
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			logger.log(Level.INFO, "Start with {0} character points", settings.characterPoints);
			logger.log(Level.INFO, "Skills in settings {0}", settings.perSkill);
			logger.log(Level.WARNING, "Previous = "+previous);

			// Reset values
			maxLimit = 1;
			settings.cpToSkills = 0;
			points1   = 12;
			points2   = Math.min(20, settings.characterPoints/2 );
			skillsFromCP   = 0;
//			pointsLangAndKnow = 0;
			available.clear();
			allowed.clear();
			todos.clear();

			// Ensure native language is present
			ensureExistanceOfNativeLanguage(SR6PointBuySettings.class);

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
				} else {
					unprocessed.add(tmp);
				}
			}

			checkForExoticWeaponsSpecilization();
			
			// Be sure to remove all skills that are not allowed
			logger.log(Level.INFO, "Skills in settings2 {0}", settings.perSkill);
			try {
				removeRestrictedSkills();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.log(Level.INFO, "Skills in settings3 {0}", settings.perSkill);
			updateAvailable();


			logger.log(Level.DEBUG, "Have {0} free and up to {1} convertible skill points", points1, points2);
			for (Entry<String,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				logger.log(Level.INFO, "... {0}={1}", entry.getKey(), entry.getValue());
				SR6Skill key = Shadowrun6Core.getSkill( entry.getKey() );
				if (key == null) {
					logger.log(Level.WARNING, "Unknown skill ''{0}'' in character", entry.getKey());
					continue;
				}
				PerSkillPoints per = entry.getValue();
				if (per == null) {
					logger.log(Level.WARNING, "No data for " + key);
					continue;
				}
				// If skill is not allowed, remove points
				if (key.isRestricted() && !allowed.contains(key) && per.getSum()>0) {
					per.points1=0;
					per.points2=0;
					per.points3=3;
					per.pointSpec=0;
					per.karmaSpec=0;
				}

				/*
				 * Pay skill points
				 */
				logger.log(Level.INFO, "Check {0}={1}", entry.getKey(), per);
				if (per.points1>0 || per.pointSpec>0) {
					int toPay = per.points1 + per.pointSpec;
					if (toPay>0 && points1>0) {
						int payHere = Math.min(points1, toPay);
						logger.log(Level.INFO, "Pay {0} free SP for {1}", payHere, key);
						points1 -= payHere;
						toPay   -= payHere;
					}
					if (toPay>0 && points2>0) {
						int payHere = Math.min(points2, toPay);
						logger.log(Level.INFO, "Pay {0} SP with converted CP for {1}: {2}", payHere, key, per);
						points2 -= payHere;
						toPay   -= payHere;
						skillsFromCP += payHere;
						settings.cpToSkills += payHere;
						settings.characterPoints -= payHere*2;
					}
					if (toPay>0) {
						logger.log(Level.WARNING, "More skill points invested in {0} than possible - reduce it", key);
						per.points1 -= toPay;
					}
				}
				if (per.points3>0) {
					int pay = per.getKarmaInvestSR6(); // Active skills = sum of 5 per skill level paid with karma
							// Knowledge skills are only 3 per skill
							if (key.getType()==SkillType.KNOWLEDGE) {
								pay = 3;
							}
							// Language skills are only 3 per skill level (not sum of 3 per new skill level)
							if (key.getType()==SkillType.LANGUAGE) {
								pay = per.points3*3;
							}
					logger.log(Level.INFO, "Pay {0} Karma for {1}", pay, key);
					model.setKarmaFree( model.getKarmaFree() - pay);
				}
				if (per.karmaSpec>0) {
					logger.log(Level.INFO, "Pay {0} Karma for specializations in {1}", per.karmaSpec*5, key);
					model.setKarmaFree( model.getKarmaFree() - per.karmaSpec*5);
				}
			}
			logger.log(Level.INFO, "Finish with {0} free and up to {1} convertible skill points", points1, points2);
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, settings.toSkillString());


			logger.log(Level.INFO, "{0} CP converted to {1} skills", settings.cpToSkills*2, settings.cpToSkills);

			/*
			 * Copy from settings to character
			 */
			logger.log(Level.DEBUG, "Copy SkillValues to character");
			List<SR6SkillValue> usedSkills = new ArrayList<>();
			for (Entry<String,PerSkillPoints> entry : settings.perSkill.entrySet()) {
				logger.log(Level.DEBUG, "  final {0} = {1}",String.format("%11s",entry.getKey()), entry.getValue());
				if (entry.getValue().getSum()==0) continue;
				SR6SkillValue val = getFromSettings(entry.getKey());
				if (val==null) {
					SR6Skill skill = getSkillFromSettings(entry.getKey());
					if (skill==null) {
						logger.log(Level.ERROR, "No skill '"+entry.getKey()+"'");
						System.err.println( "No skill '"+entry.getKey()+"'");
						continue;
					}
					val = new SR6SkillValue(skill, 0);
					model.addSkillValue(val);
					logger.log(Level.DEBUG, "  Add skill value to char: "+entry.getKey());
				}
				if (val.getSkill()==null) {
					logger.log(Level.WARNING, "Found SR6SkillValue without skill: "+val);
					continue;
				}
				// Reduce final value by one if there was a specialization
				if ("exotic_weapons".equals(entry.getKey())&&val.getSpecializations().size()>0){
					val.setDistributed(entry.getValue().getSum()- (val.getSpecializations().size()-1));
				} else {
					val.setDistributed(entry.getValue().getSum()- val.getSpecializations().size());
				}
				usedSkills.add(val);
			}
			// Reverse check: all skills in model should be in usedSkills
			for (SR6SkillValue val : new ArrayList<>(model.getSkillValues())) {
				if (!usedSkills.contains(val)) {
					logger.log(Level.WARNING, "Skill {0} was found in character, but not in skill generator settings", val);
					model.getSkillValues().remove(val);
				}
			}


			/*
			 * Ensure limits of 20 are in kept
			 */
			if (settings.cpToSkills>20) {
				todos.add(new ToDoElement(Severity.STOPPER, PointBuyCharacterGenerator.RES, PointBuyCharacterGenerator.TODO_SKILLS_TOO_MANY_CP_CONVERTED, settings.cpToSkills));
			}
			if (points1>0) {
				todos.add(new ToDoElement(Severity.STOPPER, points1+" skill points left to spend"));
			}

		} catch (Exception e) {
			logger.log(Level.ERROR, "Problem calculating skills",e);
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

		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		for (Entry<String,PerSkillPoints> entry : new ArrayList<Entry<String,PerSkillPoints>>(settings.perSkill.entrySet())) {
			SR6SkillValue val = getFromSettings(entry.getKey());
			if (val==null) {
				settings.perSkill.remove(entry.getKey());
			} else {
				SR6Skill skill = val.getResolved();
				if (skill.isRestricted() && !allowed.contains(skill)) {
					logger.log(Level.INFO, "Skill {0} is not allowed anymore - remove it from PointBuy settings", skill);
					settings.perSkill.remove(entry.getKey());
				}
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * How many skill points can be used from converting CP
	 */
	public int getConvertiblePoints() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		// How many skill points may still be generated (20 - already converted)
		int max = 20 - settings.cpToSkills;
		return Math.min(max, settings.characterPoints/2);
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
		if (points1>0) // free skill points
			return Possible.TRUE;
		if (points2>0) // converted skill points
			return Possible.TRUE;

		return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);
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
		int karmaNeeded = getIncreaseCost(value, value.getResolved());
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
		logger.log(Level.INFO, "ENTER increasePoints2");
		try {
			Possible allowed = canBeIncreasedPoints2(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase with Karma, but "+allowed);
				return new OperationResult<>(allowed);
			}

			if (value.getDistributed()==0) {
				logger.log(Level.DEBUG, "Don't increase, but select");
				OperationResult<SR6SkillValue> result = super.select(value.getModifyable());
				if (result.hasError()) {
					logger.log(Level.DEBUG, "Selecting {0} failed, because {1}",value.getKey(), result.getMessages());
					return result;
				}

				logger.log(Level.INFO, "Selected skill {0}", value.getKey());
				SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
				PerSkillPoints per = new PerSkillPoints();
				per.points3=1;

				settings.perSkill.put(result.get().getKey(), per);
				logger.log(Level.DEBUG, "Added to PointBuy settings: {0}={1}", result.get().getKey(), per);
				getCharacterController().runProcessors();
				return result;
			}
			PerSkillPoints per = getPerSkill(value);
			// Do increase
			per.points3++;
			logger.log(Level.INFO, "Increased using points3/Karma to "+per.getSum()+ " with "+per);

			// karma payment not needed as above code stores which types of points where used and point / karma status is recalculated based on that

			getCharacterController().runProcessors();
			return new OperationResult<SR6SkillValue>(value);
		} finally {
			logger.log(Level.INFO, "LEAVE increasePoints2");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeDecreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeDecreasedPoints2(SR6SkillValue key) {
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints per = settings.perSkill.get(key.getKey());
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);
		return new Possible(per.points3>0, I18N_NOT_RAISED_KARMA);
	}

	//-------------------------------------------------------------------
	public boolean deselect(SR6SkillValue value) {
		boolean ret = super.deselect(value);
		if (ret) {
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			settings.perSkill.remove(value.getKey());
			getCharacterController().runProcessors();
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#increasePoints2(java.lang.Object)
	 */
	@Override
	public OperationResult<SR6SkillValue> decreasePoints2(SR6SkillValue value) {
		logger.log(Level.INFO, "ENTER decreasePoints2");
		try {
			Possible allowed = canBeDecreasedPoints2(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to decrease with Karma, but "+allowed);
				return new OperationResult<>(allowed);
			}

			PerSkillPoints per = getPerSkill(value);
			// Do decrease
			per.points3--;
			logger.log(Level.INFO, "Decreased using points3/Karma to "+per.getSum()+ " with "+per);

			if (per.getSum()==0) {
				deselect(value);
			}
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			logger.log(Level.INFO, "After decrease {0}", settings.perSkill);

			getCharacterController().runProcessors();
			return new OperationResult<SR6SkillValue>(value);
		} finally {
			logger.log(Level.INFO, "LEAVE increasePoints2");
		}
	}

	@Override
	public String getColumn1() {
		return RES.getString("pointbuy.points1");
	}

	@Override
	public String getColumn2() {
		return RES.getString("pointbuy.points2");
	}

	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {
		/*
		 * You cannot acquire more than one specialization in a skill at character creation,
		 * and you cannot acquire an expertise.
		 */
		if (expertise) return Possible.FALSE;
		
		boolean isExotic = "exotic_weapons".equals(skillVal.getKey());

		// Check if there already is one specialization in this skill
		if (!skillVal.getSpecializations().isEmpty() && !isExotic)
			return Possible.FALSE;

		List<SkillSpecialization<SR6Skill>> available = getAvailableSpecializations(skillVal);
		if (!available.contains(spec)) {
			return new Possible(Severity.STOPPER, RES, I18N_NOT_AVAILABLE_SPEC, skillVal.getKey(), spec.getId(), expertise);
		}

		// If this is Exotic Weapons, no Karma/Points are needed
		if (isExotic && skillVal.getSpecializations().size()<1)
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
			
			boolean isExotic = "exotic_weapons".equals(skillVal.getKey());
			boolean isFreeFirstExotic = isExotic && skillVal.getSpecializations().isEmpty();

			SkillSpecializationValue<SR6Skill> ret = new SkillSpecializationValue<>(spec);
			skillVal.getSpecializations().add(ret);
			logger.log(Level.INFO, "Select specialization ''{0}'' in skill ''{1}''", spec.getId(), skillVal.getKey());

			// Now pay
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			if (settings.get(skillVal)==null) {
				settings.put(skillVal, new PerSkillPoints());
			}

			if (!isFreeFirstExotic) {
				if (points1>0 || (settings.cpToSkills<20 && settings.characterPoints>=2)) {
					logger.log(Level.INFO, "Pay with free skill points");
					settings.get(skillVal).pointSpec++;
				} else {
					settings.get(skillVal).karmaSpec++;
					logger.log(Level.INFO, "Pay with karma");
				}
			} else {
				logger.log(Level.INFO, "Don't pay, because it was free");
			}
			logger.log(Level.INFO, "After paying: {0}",settings.get(skillVal));

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
			// Get back
			SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
			PerSkillPoints val = settings.perSkill.get(skillVal.getKey());
			val.karmaSpec=0;
			val.pointSpec=0;

			getCharacterController().runProcessors();
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
		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints val = settings.perSkill.get(key.getKey());
		if (val==null) return 0;
		return val.points1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#getPoints2(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getPoints2(SR6SkillValue key) {
		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints val = settings.perSkill.get(key.getKey());
		if (val==null) return 0;
		return val.points3;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(SR6SkillValue key) {
		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints val = settings.perSkill.get(key.getKey());
		if (val==null) {
			return 0;//-1;
		}
		logger.log(Level.DEBUG, "{0} = {1}", key.getKey(), val.getSum());
		return val.getSum();
	}

	//-------------------------------------------------------------------
	/**
	 * Variant of the decrease from chargen\gen\CommonSkillGenerator.java which in addition checks for decrease of karma (points3) first
	 */
	public OperationResult<SR6SkillValue> decrease(SR6SkillValue ref) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER decrease " + ref);
		try {
			SR6Skill key = ref.getModifyable();

			Possible allowed = canBeDecreasedPoints3(ref);
			if (allowed.get()) {
				return decreasePoints3(ref);
			}

			allowed = canBeDecreasedPoints2(ref);
			if (allowed.get()) {
				return decreasePoints2(ref);
			}

			allowed = canBeDecreasedPoints(ref); // using method from CommonSkillGenerator
			if (allowed.get()) {
				return decreasePoints(ref); // using method from CommonSkillGenerator
			}

//			logger.log(Level.ERROR, "Neither with skill points, nor with Karma was decreasing possible");
			return new OperationResult<>();
		} finally {
			if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "LEAVE decrease " + ref);
			getCharacterController().runProcessors();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Variant of canBeDecreasedPoints3 in SR6PrioritySkillGenerator
	 */
	public Possible canBeDecreasedPoints3(SR6SkillValue key) {
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints per = settings.get(key);
		if (per==null)
			return new Possible(I18N_NOT_SELECTED);

		if (per.points3==0)
			return new Possible(I18N_NOT_RAISED_KARMA);

		return Possible.TRUE;
	}
	//-------------------------------------------------------------------
	/**
	 * Variant of decreasePoints3 in SR6PrioritySkillGenerator
	 */
	public OperationResult<SR6SkillValue> decreasePoints3(SR6SkillValue value) {
		Possible allowed = canBeDecreasedPoints3(value);
		if (!allowed.get()) {
			logger.log(Level.ERROR, "Trying to decrease spent Karma, though not allowed: {0}", value);
			return new OperationResult<>(allowed);
		}

		if (value==null) {
			logger.log(Level.ERROR, "Trying to decrease a skill not previously selected");
			return new OperationResult<>(new Possible(I18N_NOT_SELECTED));
		}

		PerSkillPoints per = getPerSkill(value);
		if (per==null) {
			logger.log(Level.ERROR, "No PerSkillPoints found for {0}",value);
			return new OperationResult<>();
		}

		// Do decrease
		value.setDistributed(value.getDistributed()-1); //decrease skill
		per.points3--; //decrease karma levels spend for skill
		
		int numberSpecs = value.getSpecializations().size();
		if ("exotic_weapons".equals(value.getKey())&&value.getSpecializations().size()>0)numberSpecs -= 1;
		if (per.getSum()<=numberSpecs) { //if points spend for skill = 0 (ignoring points spend for specializations), deselect skill
			deselect(value); 
		}
		// Return karma
		int karma = getIncreaseCost(value, value.getResolved()); //Note: Important to get karma cost after level is changed as method assumes +1 increase versus input
		model.setKarmaFree(model.getKarmaFree() + karma);

		parent.runProcessors();

		return new OperationResult<SR6SkillValue>(value);
	}

}
