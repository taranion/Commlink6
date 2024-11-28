package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6MetamagicOrEchoController extends ControllerImpl<MetamagicOrEcho>
		implements IMetamagicOrEchoController {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".metaecho");

	private boolean isCharGen;
	private int maxGrade = Integer.MAX_VALUE;

	private SR6EmulatedSoftwareController emulated;

	//-------------------------------------------------------------------
	public SR6MetamagicOrEchoController(SR6CharacterController parent, boolean isCharGen) {
		super(parent);
		this.isCharGen = isCharGen;
		emulated = new SR6EmulatedSoftwareController(parent);;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(MetamagicOrEcho item) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	private List<MetamagicOrEcho> getNormalAvailable() {
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();
		if (type!=null && type.usesMagic()) {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.METAMAGIC||m.getType()==Type.METAMAGIC_ADEPT)
					.collect(Collectors.toList());
		} else if (type!=null && type.usesResonance()) {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.ECHO)
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<MetamagicOrEcho> getAvailable() {
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();
		MetaType meta = getModel().getMetatype();

		List<MetamagicOrEcho> ret = getNormalAvailable();
		RuleController rules = parent.getRuleController();
		if (!type.usesMagic() && !type.usesResonance()) {
			// Only for mundane
			if (meta.isAI() && rules.getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_NEUROMORPHISM)) {
				ret.addAll( Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.NEUROMORPHISM)
					.collect(Collectors.toList()) );
			} else if ( rules.getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM)) {
				ret.addAll( Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.TRANSHUMANISM)
					.collect(Collectors.toList()) );
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<MetamagicOrEchoValue> getSelected() {
		return getModel().getMetamagicOrEchoes();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(MetamagicOrEchoValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(MetamagicOrEcho value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController#getGrade()
	 */
	@Override
	public int getGrade() {
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();
		MetaType meta = getModel().getMetatype();
		List<MetamagicOrEchoValue> list = null;
		if (type != null && type.usesMagic()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable()!=null)
					.filter(m -> m.getModifyable().getType() == Type.METAMAGIC || m.getModifyable().getType() == Type.METAMAGIC_ADEPT)
					.filter(m -> !m.isAutoAdded())
					.collect(Collectors.toList());
		} else if (type != null && type.usesResonance()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.ECHO)
					.filter(m -> !m.isAutoAdded())
					.collect(Collectors.toList());
		} else {
			if (meta.isAI()) {
				list = getSelected().stream()
						.filter(m -> m.getModifyable().getType() == Type.NEUROMORPHISM)
						.collect(Collectors.toList());
			} else {
				list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.TRANSHUMANISM)
					.collect(Collectors.toList());
			}
		}
		// Determine the grade
		int grade = 0;
		for (MetamagicOrEchoValue tmp : list) {
			if (tmp.getModifyable().hasLevel())
				grade += tmp.getDistributed();
			else
				grade ++;
		}
		return grade;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(MetamagicOrEcho value, Decision... decisions) {
		// Check if all requirements are met
		List<Requirement> notMet = new ArrayList<>();
		for (Requirement req : value.getRequirements()) {
			if (!Shadowrun6Tools.isRequirementMet(getModel(), value, req, decisions)) {
				notMet.add(req);
			}
		}
		if (notMet.size()>0) {
			return new Possible(notMet, (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
		}
		// Is it available in general?
		if (!getAvailable().contains(value)) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_AVAILABLE);
		}

		// Is maximum grade reached
		if (getGrade()>=maxGrade) {
			return new Possible(false, IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
		}

		// Calculate Karma cost
		int karma = 10 + getGrade() +1;

		if (getModel().getKarmaFree()<karma) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, karma);
		}

		// Check if initiation/immersion/transhumanism is allowed (for chargen)
		if (isCharGen) {
			if (value.getType()==Type.TRANSHUMANISM && !parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM)) {
				return new Possible(false, IRejectReasons.IMPOSS_NOT_AVAILABLE);
			}
			if (value.getType()==Type.NEUROMORPHISM && !parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_NEUROMORPHISM)) {
				return new Possible(false, IRejectReasons.IMPOSS_NOT_AVAILABLE);
			}
			if (value.getType()!=Type.TRANSHUMANISM && !parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CHARGEN_ALLOW_INITIATION)) {
				return new Possible(false, IRejectReasons.IMPOSS_NOT_AVAILABLE);
			}
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<MetamagicOrEchoValue> select(MetamagicOrEcho value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0})", value);
		try {
			Possible possible = canBeSelected(value, decisions);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to select a metamagic/echo that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			MetamagicOrEchoValue selected = new MetamagicOrEchoValue(value);
			logger.log(Level.INFO, "{0} has Level = {1}", value, value.hasLevel());
			if (value.hasLevel()) {
				selected.setDistributed(1);
			}
			for (Decision dec : decisions) {
				selected.addDecision(dec);
			}

			int karma = 10 + getGrade() +1;
			getModel().addMetamagicOrEcho(selected);
			logger.log(Level.INFO, "Add metamagic/echo '" + value.getId() + "' for " + karma + " karma");
			Shadowrun6Character model = getModel();
			model.setKarmaFree( model.getKarmaFree() - karma);
			model.setKarmaInvested( model.getKarmaInvested() + karma);

			// Log in history
			DataItemModification mod = new DataItemModification(ShadowrunReference.METAECHO, value.getId());
			if (value.hasLevel()) {
				mod = new ValueModification(ShadowrunReference.METAECHO, value.getId(), 1);
			}
			mod.setExpCost(karma);
			model.addToHistory(mod);

			// Record eventually meaning for essence
			Shadowrun6Tools.recordEssenceChange(model, selected);

			parent.runProcessors();
			return new OperationResult<MetamagicOrEchoValue>(selected);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(MetamagicOrEchoValue value) {
		// Is it selected?
		if (!getSelected().contains(value)) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		// may not be deleted, because auto-added
		if (value.isAutoAdded())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_AUTO_ADDED);

		return Possible.TRUE;
	}
	//-------------------------------------------------------------------
	private ValueModification getHighestModification(MetamagicOrEcho key) {
		ValueModification ret = null;
		for (Modification mod : getModel().getHistory()) {
			if (!(mod instanceof ValueModification))
				continue;
			ValueModification amod = (ValueModification)mod;
			if (mod.getSource()!=this && mod.getSource()!=null)
				continue;
			if (amod.getResolvedKey()!=key)
				continue;
			if (amod.getExpCost()<0)
				continue;
			if (ret==null || amod.getExpCost()>ret.getExpCost())
				ret = amod;
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(MetamagicOrEchoValue value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible possible = canBeDeselected(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to deselect a metamagic/echo that cannot be deselected: {0}",possible.getI18NKey());
				return false;
			}

			int karma = 10 + getGrade();
			Shadowrun6Character model = getModel();
			model.removeMetamagicOrEcho(value);
			model.setKarmaFree( model.getKarmaFree() + karma);
			model.setKarmaInvested( model.getKarmaInvested() - karma);
			logger.log(Level.INFO, "Remove metamagic/echo '" + value.getModifyable().getId() + "' for " + karma + " karma");

			// Undo in history
			ValueModification mod = getHighestModification(value.getResolved());
			if (mod!=null)
				model.removeFromHistory(mod);

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(MetamagicOrEcho data, Decision... decisions) {
		return 10 + getGrade() +1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(MetamagicOrEcho data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(MetamagicOrEchoValue value) {
		// It must be already possessed
		if (!getModel().getMetamagicOrEchoes().contains(value)) {
			return canBeSelected(value.getModifyable());
//			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		MetamagicOrEcho item = value.getModifyable();
		if (!item.hasLevel()) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}

		// Is maximum grade reached
		if (getGrade()>=maxGrade) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);
		}

		// Calculate Karma cost
		int karma = 10 + getGrade() +1;

		if (getModel().getKarmaFree()<karma) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, karma);
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(MetamagicOrEchoValue value) {
		// It must be already possessed
		if (!getModel().getMetamagicOrEchoes().contains(value)) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		MetamagicOrEcho item = value.getModifyable();
		if (!item.hasLevel()) {
			return new Possible(IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
		}

		if (value.getDistributed()<1) {
			return new Possible(false, IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<MetamagicOrEchoValue> increase(MetamagicOrEchoValue value) {
		logger.log(Level.TRACE, "ENTER increase({0})", value);
		try {
			Possible possible = canBeIncreased(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to increase a metamagic/echo that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			int karma = 10 + getGrade() +1;
			value.setDistributed(value.getDistributed()+1);

			logger.log(Level.INFO, "Increased metamagic/echo '" + value.getModifyable().getId() + "' for " + karma + " karma");
			Shadowrun6Character model = getModel();
			model.setKarmaFree( model.getKarmaFree() - karma);
			model.setKarmaInvested( model.getKarmaInvested() + karma);
			// Increase should work even when increased from 0 - in this case the item needs to be added
			if (!model.getMetamagicOrEchoes().contains(value)) {
				model.getMetamagicOrEchoes().add(value);
			}

			// Log in history
			DataItemModification mod = new DataItemModification(ShadowrunReference.METAECHO, value.getKey());
			if (value.getResolved().hasLevel()) {
				mod = new ValueModification(ShadowrunReference.METAECHO, value.getKey(), 1);
			}
			mod.setExpCost(karma);
			model.addToHistory(mod);

			parent.runProcessors();
			return new OperationResult<MetamagicOrEchoValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<MetamagicOrEchoValue> decrease(MetamagicOrEchoValue value) {
		logger.log(Level.TRACE, "ENTER decrease({0})", value);
		try {
			Possible possible = canBeDecreased(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to decrease a metamagic/echo that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			value.setDistributed(value.getDistributed()-1);
			int karma = 10 + getGrade() -1;

			logger.log(Level.INFO, "Decreased metamagic/echo '" + value.getModifyable().getId() + "' for " + karma + " karma");
			Shadowrun6Character model = getModel();
			model.setKarmaFree( model.getKarmaFree() + karma);
			model.setKarmaInvested( model.getKarmaInvested() - karma);

			// Undo in history
			ValueModification mod = getHighestModification(value.getResolved());
			if (mod!=null)
				model.removeFromHistory(mod);

			parent.runProcessors();
			return new OperationResult<MetamagicOrEchoValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();
		try {
			maxGrade = Integer.MAX_VALUE;
			Shadowrun6Character model = getModel();
			MagicOrResonanceType mrType = model.getMagicOrResonanceType();
			if (mrType!=null && isCharGen) {
				if (mrType.usesMagic()) {
					maxGrade = parent.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_MAX_INITIATION);
				} else if (mrType.usesResonance()) {
					maxGrade = parent.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_MAX_SUBMERSION);
				} else {
					maxGrade = parent.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_MAX_TRANSHUMAN);
				}
			}
			logger.log(Level.INFO, "Maximum grade is {0}", maxGrade);

			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.METAECHO) {
					DataItemModification mod = (DataItemModification)tmp;
					MetamagicOrEcho item = mod.getResolvedKey();
					MetamagicOrEchoValue val = model.getMetamagicOrEcho(mod.getKey());
					if (val==null || !item.hasLevel()) {
						val = new MetamagicOrEchoValue(item);
						val.addIncomingModification(mod);
						getModel().addMetamagicOrEcho(val);
						logger.log(Level.DEBUG, "Auto-Added Metamagic/Echo ''{0}''", mod.getKey());
					} else {
						val.addIncomingModification(mod);
						logger.log(Level.DEBUG, "Auto-Increased Metamagic/Echo ''{0}''", mod.getKey());
					}
					continue;
				}
				unprocessed.add(tmp);
			}

			// Pay karma and apply modifications
			CommonSR6GeneratorSettings settings = parent.getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
			if (isCharGen) {
				settings.setKarmaForInitiation(0);
			}
			int payNext = 11;
			int grade = 0;
			for (MetamagicOrEchoValue val : model.getMetamagicOrEchoes()) {
				if (val.getModifyable().getType()==Type.DRACOGENESIS_POWER) continue;
				if (val.getModifyable().hasLevel()) {
					for (int i=0; i<val.getDistributed(); i++) {
						if (isCharGen) {
							logger.log(Level.INFO, "Pay {0} Karma for metaecho ''{1}'' {2}", payNext, val.getModifyable().getId(), (i+1));
							model.setKarmaFree( model.getKarmaFree() - payNext);
						}
						grade++;
						payNext++;
					}
				} else {
					if (isCharGen) {
						logger.log(Level.INFO, "Pay {0} Karma for metaecho ''{1}''", payNext, val.getModifyable().getId());
						model.setKarmaFree( model.getKarmaFree() - payNext);
						settings.setKarmaForInitiation( settings.getKarmaForInitiation() + payNext);
					}
					grade++;
					payNext++;
				}
				// Add modifications
				for (Modification mod : val.getIncomingModifications()) {
					Modification copy = Shadowrun6Tools.instantiateModification(mod, val, val.getDistributed(), model);
					logger.log(Level.DEBUG, "Add modification "+copy);
					unprocessed.add(copy);
				}
			}

			logger.log(Level.INFO, "Initiation/Submersion grade = "+grade);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(MetamagicOrEchoValue value) {
		return value.getDistributed();
	}

	//-------------------------------------------------------------------
	public SR6EmulatedSoftwareController getEmulatedSoftwareController() {
		return emulated;
	}

}
