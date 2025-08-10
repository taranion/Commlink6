package de.rpgframework.shadowrun6.chargen.gen.free;

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
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSkillGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Points 1 = Free skill points for languages/knowledges
 * Points 2 = Karma
 * @author prelle
 *
 */
public class SR6FreeSkillGenerator extends CommonSkillGenerator {

	//-------------------------------------------------------------------
	public SR6FreeSkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillGenerator#getPerSkill(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	protected PerSkillPoints getPerSkill(SR6SkillValue value) {
		System.err.println("SR6FreeSkillGenerator.getPerSkill:"+value);
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#canBeSelected(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public Possible canBeSelected(SR6Skill skill, Decision... dec) {
		return Possible.TRUE;
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
		return Possible.TRUE;
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
		if (!model.getSkillValues().contains(ref)) {
			model.addSkillValue(ref);
		}

		ref.setDistributed( ref.getDistributed() +1);
		logger.log(Level.INFO, "Increased skill {0} to {1}", ref.getKey(), ref.getModifiedValue(ValueType.NATURAL));

		parent.runProcessors();

		return new OperationResult<SR6SkillValue>(ref);
	}
	
	// Using canBeDecreased from CommonSkillGenerator which uses the one from CommonSkillController
	// Using canBeDeselected and deselect from CommonSkillGenerator which uses the one from CommonSkillController

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> decrease(SR6SkillValue ref) {
		Possible poss = canBeDecreased(ref);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to decrease skill {0} which is not allowed: "+poss);
			return new OperationResult<>(poss);
		}

		ref.setDistributed( ref.getDistributed() -1);
		logger.log(Level.INFO, "Decreased skill {0} to {1}", ref.getKey(), ref.getModifiedValue(ValueType.NATURAL));
		if (ref.getModifiedValue()<=0) {//if skill 0, deselect skill
			deselect(ref);
		}

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

			// Reset values
			maxLimit = 1;
			points1   = model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue(ValueType.NATURAL);;
			points2   = model.getKarmaFree();
			updateAvailable();
			allowed.clear();
			todos.clear();

			// Ensure native language is present
			ensureNativeLanguage();

			for (Modification tmp : previous) {
				if (tmp instanceof AllowModification) {
					AllowModification mod = (AllowModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.SKILL) {
						SR6Skill skill = mod.getResolvedKey();
						if (skill==null) {
							logger.log(Level.ERROR, "AllowMod for unknown skill {0} from {1}", mod.getKey(), mod.getSource());
						} else {
							logger.log(Level.INFO, "Allow skill {0} from {1}", mod.getKey(), mod.getSource());
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

		} catch (Exception e) {

		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}

		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void ensureNativeLanguage() {
		logger.log(Level.DEBUG, "Check for existing native language: "+model.getSkillValues());
		for (SR6SkillValue sVal : new ArrayList<>(model.getSkillValues())) {
			if (NATIVE_LANGUAGE.equals(sVal.getUuid()))
				return;
		}
		// If you got here, there is no native language yet
		SR6SkillValue lang = new SR6SkillValue(Shadowrun6Core.getSkill("language"), 4);
		lang.setUuid(NATIVE_LANGUAGE);
		model.addSkillValue(lang);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController#canBeIncreasedPoints(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	public Possible canBeIncreasedPoints(SR6SkillValue val) {
		return Possible.FALSE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueWith2PoolsController#canBeIncreasedPoints2(java.lang.Object)
	 */
	@Override
	public Possible canBeIncreasedPoints2(SR6SkillValue value) {
		return Possible.FALSE;
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
		return RES.getString("free.points1");
	}

	@Override
	public String getColumn2() {
		return RES.getString("free.points2");
	}

	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {

		boolean isExotic = "exotic_weapons".equals(skillVal.getKey());

		// Check if there already is one specialization in this skill
		if (!skillVal.getSpecializations().isEmpty() && !isExotic) 
			return Possible.FALSE;

		List<SkillSpecialization<SR6Skill>> available = getAvailableSpecializations(skillVal);
		if (!available.contains(spec)) {
			return new Possible(Severity.STOPPER, RES, I18N_NOT_AVAILABLE_SPEC, skillVal.getKey(), spec.getId(), expertise);
		}

		// If this is Exotic Weapons, no Karma/Points are needed
		if (skillVal.getKey().equals("exotic_weapons"))
			return Possible.TRUE;

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
