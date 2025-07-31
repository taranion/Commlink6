package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSkillController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.Shadowrun6Rules;

/**
 * @author prelle
 *
 */
public class SR6SkillLeveller extends CommonSkillController {

	//-------------------------------------------------------------------
	public SR6SkillLeveller(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	private ValueModification getHighestModification(SR6Skill key) {
		ValueModification ret = null;
		for (Modification mod : model.getHistory()) {
			if (!(mod instanceof ValueModification))
				continue;
			if (mod.getReferenceType()!=ShadowrunReference.SKILL)
				continue;
			ValueModification amod = (ValueModification)mod;
			if (!amod.getKey().equals(key.getId()))
				continue;

			if (mod.getSource()!=this && mod.getSource()!=null)
				continue;
			if (ret==null || amod.getExpCost()>ret.getExpCost())
				ret = amod;
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private ValueModification getHighestModification(SR6Skill key, SkillSpecialization<SR6Skill> spec) {
		ValueModification ret = null;
		for (Modification mod : model.getHistory()) {
			if (!(mod instanceof ValueModification))
				continue;
			if (mod.getReferenceType()!=ShadowrunReference.SKILLSPECIALIZATION)
				continue;
			ValueModification amod = (ValueModification)mod;
			if (!amod.getKey().equals(key.getId()))
				continue;

			if (mod.getSource()!=this && mod.getSource()!=null)
				continue;
			if (ret==null || amod.getExpCost()>ret.getExpCost())
				ret = amod;
		}
		return ret;
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

		// No points left - maybe with karma?
		int karma = (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)?3:5;
		if (model.getKarmaFree()>=karma)
			return Possible.TRUE;
		return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, karma);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#canBeSelected(SR6Skill, Decision[])
	 */
	@Override
	public Possible canBeDeselected(SR6SkillValue data) {
		Possible poss = super.canBeDeselected(data);
		if (poss.get()) {
			ValueModification toUndo = getHighestModification(data.getModifyable());
			if (toUndo!=null) {
				if (toUndo.getSource()!=null)
					// Has been increased this session
					return Possible.TRUE;
				// Has been increased in career mode
				return new Possible( parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER) );
			} else {
				// Has been increased in generation mode
				return new Possible( parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN) );
			}
		}
		return poss;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectionController#select(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public OperationResult<SR6SkillValue> select(SR6Skill data, Decision...decisions) {
		OperationResult<SR6SkillValue> res = super.select(data, decisions);
		if (res.wasSuccessful()) {
			// Now pay
			int cost = (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)?3:5;
			model.setKarmaFree( model.getKarmaFree() - cost);
			model.setKarmaInvested( model.getKarmaInvested() + cost);

			ValueModification mod = new ValueModification(ShadowrunReference.SKILL, data.getId(), String.valueOf(res.get().getDistributed()));
			mod.setSet(ValueType.NATURAL);
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(cost);
			model.addToHistory(mod);

			parent.runProcessors();
		}
		return res;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#deselect(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	public boolean deselect(SR6SkillValue data) {
		if (!super.deselect(data))
			return false;
		SR6Skill key = data.getModifyable();
		ValueModification toUndo = getHighestModification(data.getModifyable());
		int karma = (key.getType()==SkillType.KNOWLEDGE || key.getType()==SkillType.LANGUAGE)?3:5;
		if (toUndo!=null) {
			// Decrease from career
			model.removeFromHistory(toUndo);
			karma = toUndo.getExpCost();

			logger.log(Level.INFO, "Deselecting"+key+" from prev. career session grants "+karma+" karma  ");
			model.setKarmaInvested(model.getKarmaInvested()-karma);
			model.setKarmaFree(model.getKarmaFree()+karma);
		} else {
			// Deselect from creation value
			logger.log(Level.INFO, "Removing " + key + " from chargen to {} granting {1} karma  ",
					data.getDistributed(), karma);
			model.setKarmaInvested(model.getKarmaInvested() - karma);
			model.setKarmaFree(model.getKarmaFree() + karma);
			// Since it granted Karma, log it
			ValueModification mod = new ValueModification(ShadowrunReference.SKILL, key.getId(), String.valueOf(data.getDistributed()));
			mod.setSet(ValueType.NATURAL);
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(-karma);
			model.addToHistory(mod);
		}

		// Inform listener
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	private int getIncreaseCost(SR6SkillValue sVal) {
		SR6Skill key = sVal.getModifyable();
		int newVal = (sVal==null)?1:(sVal.getDistributed()+1);

		// Raising language skills is like a new specialization
		if (key.getType()==SkillType.KNOWLEDGE)
			return 3;
		if (key.getType()==SkillType.LANGUAGE) {
			return newVal*3;
		}

		int cost = newVal*5;

		return cost;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(SR6SkillValue value) {
		// Check maximum
		Possible poss = super.canBeIncreased(value);
		if (!poss.get())
			return poss;

		// Enough karma?
		if (model.getKarmaFree()<getIncreaseCost(value))
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);


		// Skills for which groups character is incompetent, cannot be increased
		for (QualityValue qual : model.getQualities()) {
			if (qual.getModifyable().getId().equals("incompetent")) {
				Decision dec = value.getDecision(UUID.fromString("d0b872d0-0783-4b7a-b38a-35db1cef3685"));
				if (dec!=null && dec.getValue().equals(value.getKey()))
					return Possible.FALSE;
			}
		}
		// Is automatically added
//		if (model.isAutoSkill(value))
//			return false;

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(SR6SkillValue value) {
		Possible poss = super.canBeDecreased(value);
		if (!poss.get())
			return poss;

		// It is ensured here, that there is distributed>=1

		ValueModification toUndo = getHighestModification(value.getModifyable());
		if (toUndo!=null && value.getStart()<value.getDistributed()) {
			if (toUndo.getSource()!=null)
				// Has been increased this session
				return Possible.TRUE;
			// Has been increased in career mode
			return new Possible( parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER) );
		} else {
			// Has been increased in generation mode
			return new Possible( parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN) );
		}
	}


	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.ISkillController#canSelectSpecialization(de.rpgframework.shadowrun.AShadowrunSkillValue, de.rpgframework.genericrpg.data.SkillSpecialization, boolean)
	 */
	@Override
	public Possible canSelectSpecialization(SR6SkillValue skillVal, SkillSpecialization<SR6Skill> spec,
			boolean expertise) {
		List<SkillSpecialization<SR6Skill>> available = getAvailableSpecializations(skillVal);
		if (!available.contains(spec)) {
			return new Possible(Severity.STOPPER, RES, I18N_NOT_AVAILABLE_SPEC, skillVal.getKey(), spec.getId(), expertise);
		}
		
		if (expertise) {
			// Must have a specialization
			if (skillVal.getDistributed()<5) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS, 5);
			}
			// Must have a normal specialization
			SkillSpecializationValue<SR6Skill> specVal = skillVal.getSpecialization(spec);
			if (specVal==null) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_AVAILABLE, 5);
			}
			if (specVal.getDistributed()==1) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
			}
			// No other expertise may be present
			if (skillVal.getSpecializations().stream().anyMatch(sv -> sv.getDistributed()==1)) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
			}
			logger.log(Level.WARNING, "All clear for expe: "+skillVal.getSpecializations());
		} else {
			// May not have any other specialization
			boolean allow = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.EXPANDED_SPECIALIZATIONS);
			if (!allow && skillVal.getSpecializations().stream().anyMatch(sv -> sv.getDistributed()==0)) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
			}
			// May not already have anything in that field
			if (skillVal.getSpecializations().stream().anyMatch(sv -> sv.getResolved()==spec)) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ALREADY_PRESENT);
			}
		}

		// Need  5 Karma
		if (model.getKarmaFree()<5) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, 5);
		}

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

			SkillSpecializationValue<SR6Skill> ret = null;
			if (expertise) {
				ret = skillVal.getSpecialization(spec);
				ret.setDistributed(1);
			} else {
				ret = new SkillSpecializationValue<>(spec);
				skillVal.getSpecializations().add(ret);
			}
			logger.log(Level.WARNING, "Select specialization ''{0}'' in skill ''{1}'' as {2}", spec.getId(), skillVal.getKey(), skillVal.getSpecializations());

			// Now pay
			int cost = 0;
			if (!isFreeFirstExotic) {
				cost = 5;
				model.setKarmaFree( model.getKarmaFree() - cost);
				model.setKarmaInvested( model.getKarmaInvested() + cost);
			}

			ValueModification mod = new ValueModification(ShadowrunReference.SKILLSPECIALIZATION, skillVal.getSkill().getId()+"/"+ret.getKey(), String.valueOf(expertise?3:2));
			mod.setSet(ValueType.NATURAL);
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(cost);
			mod.setSource(this);
			model.addToHistory(mod);

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

			String key = skillVal.getSkill().getId()+"/"+spec.getKey();
			ValueModification toUndo = getHighestModification(skillVal.getModifyable(), spec.getModifyable());
			int karma = 5;
			if (toUndo!=null) {
				// Decrease from career
				model.removeFromHistory(toUndo);
				karma = toUndo.getExpCost();

				logger.log(Level.INFO, "Deselecting"+key+" from prev. career session grants "+karma+" karma  ");
				model.setKarmaInvested(model.getKarmaInvested()-karma);
				model.setKarmaFree(model.getKarmaFree()+karma);
			} else {
				// Deselect from creation value
				logger.log(Level.INFO, "Removing specialization {0} from chargen granting {1} karma  ", key, karma);
				model.setKarmaInvested(model.getKarmaInvested() - karma);
				model.setKarmaFree(model.getKarmaFree() + karma);
				// Since it granted Karma, log it
				ValueModification mod = new ValueModification(ShadowrunReference.SKILLSPECIALIZATION, key, String.valueOf(spec.getDistributed()));
				mod.setSet(ValueType.NATURAL);
				mod.setDate(Date.from(Instant.now()));
				mod.setExpCost(-karma);
				model.addToHistory(mod);
			}
			
			if (spec.getDistributed()==1) {
				spec.setDistributed(0);
			} else {
				skillVal.getSpecializations().remove(spec);
			}

			getCharacterController().runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE: deselect({0}, {1}",skillVal, spec);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(SR6Skill data, Decision... decisions) {
		return 5;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> increase(SR6SkillValue value) {
		Possible poss = canBeIncreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase {0} which is not possible: {1}", value.getKey(), poss);
			return new OperationResult<>(poss);
		}

		value.setDistributed(value.getDistributed() +1);
		logger.log(Level.INFO, "Increase {0} to {1}", value.getKey(), value.getDistributed());

		int cost = value.getDistributed()*5;
		model.setKarmaFree( model.getKarmaFree() - cost);
		model.setKarmaInvested( model.getKarmaInvested() + cost);

		ValueModification mod = new ValueModification(ShadowrunReference.SKILL, value.getModifyable().getId(), String.valueOf(value.getDistributed()));
		mod.setSet(ValueType.NATURAL);
		mod.setDate(Date.from(Instant.now()));
		mod.setExpCost(cost);
		model.addToHistory(mod);

		parent.runProcessors();

		return new OperationResult<SR6SkillValue>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<SR6SkillValue> decrease(SR6SkillValue value) {
		Possible poss = canBeDecreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to decrease {0} which is not possible: {1}", value.getKey(), poss);
			return new OperationResult<>(poss);
		}

		int cost = value.getDistributed()*5;
		value.setDistributed(value.getDistributed() -1);
		logger.log(Level.INFO, "Decrease {0} to {1}", value.getKey(), value.getDistributed());

		model.setKarmaFree( model.getKarmaFree() + cost);
		model.setKarmaInvested( model.getKarmaInvested() - cost);

		ValueModification toUndo = getHighestModification(value.getModifyable());
		if (toUndo!=null) {
			// Decrease from career
			model.removeFromHistory(toUndo);
		} else {
			// Decreasing below the creation value
			logger.log(Level.DEBUG,"Decrease a value that has chosen at creation - invested karma was "+cost);
			// Since it granted Karma, log it
			ValueModification mod = new ValueModification(ShadowrunReference.SKILL, value.getKey(), String.valueOf(value.getDistributed()));
			mod.setSet(ValueType.NATURAL);
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(-cost);
			model.addToHistory(mod);
		}

		parent.runProcessors();
		return new OperationResult<SR6SkillValue>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#getMaximum(de.rpgframework.shadowrun6.SR6SkillValue)
	 */
	@Override
	public int getMaximum(SR6SkillValue value) {
		int max = 9+value.getModifiedValue(ValueType.MAX);
		if (value.getModifyable().getType()==SkillType.LANGUAGE)
			max=3;
		return max;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		todos.clear();
		updateAvailable();
		for (Modification _mod : previous) {
			if (_mod.getReferenceType() == ShadowrunReference.SKILL) {
				if (_mod instanceof AllowModification) {
					AllowModification mod = (AllowModification)_mod;
					SR6Skill key = mod.getResolvedKey();
					logger.log(Level.INFO, "Allow skill {0}", mod.getKey());
					if (key!=null)
						allowed.add(key);
					continue;
				}

				DataItemModification mod = (DataItemModification) _mod;
				SR6Skill key = mod.getResolvedKey();
				if (mod.isConditional()) {
					logger.log(Level.DEBUG, "Add conditional modification {0} to {1}", mod, key);
				} else {
					logger.log(Level.DEBUG, "Add modification {0} to {1}", mod, key);
				}
				SR6SkillValue val = getModel().getSkillValue(key);
				if (val==null)
					continue;
				val.addIncomingModification(mod);
				if ((mod instanceof ValueModification) && ((ValueModification)mod).getSet() == ValueType.MAX) {
					logger.log(Level.INFO, "Changed maximum of {0} to {1}", key, val.getModifiedValue(ValueType.MAX));
				}
			} else
				unprocessed.add(_mod);
		}

		checkForExoticWeaponsSpecilization();
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(SR6SkillValue value) {
		return value.getDistributed();
	}

}
