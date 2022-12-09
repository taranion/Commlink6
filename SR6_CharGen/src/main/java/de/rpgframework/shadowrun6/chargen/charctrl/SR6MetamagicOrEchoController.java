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
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
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
	
	//-------------------------------------------------------------------
	public SR6MetamagicOrEchoController(SR6CharacterController parent, boolean isCharGen) {
		super(parent);
		this.isCharGen = isCharGen;
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
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<MetamagicOrEcho> getAvailable() {
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
		} else {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.TRANSHUMANISM)
					.collect(Collectors.toList());
		}
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
		List<MetamagicOrEchoValue> list = null;
		if (type != null && type.usesMagic()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable()!=null)
					.filter(m -> m.getModifyable().getType() == Type.METAMAGIC || m.getModifyable().getType() == Type.METAMAGIC_ADEPT)
					.collect(Collectors.toList());
		} else if (type != null && type.usesResonance()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.ECHO)
					.collect(Collectors.toList());
		} else {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.TRANSHUMANISM)
					.collect(Collectors.toList());
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
		return Possible.TRUE;
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
	public float getSelectionCost(MetamagicOrEcho data) {
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
			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		}
		
		MetamagicOrEcho item = value.getModifyable();
		if (!item.hasLevel()) {
			return new Possible(IRejectReasons.IMPOSS_ITEM_HAS_NO_LEVELS);
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
			Possible possible = canBeIncreased(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to decrease a metamagic/echo that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			value.setDistributed(value.getDistributed()+1);
			int karma = 10 + getGrade() +1;

			logger.log(Level.INFO, "Decreased metamagic/echo '" + value.getModifyable().getId() + "' for " + karma + " karma");
			Shadowrun6Character model = getModel(); 
			model.setKarmaFree( model.getKarmaFree() + karma);
			model.setKarmaInvested( model.getKarmaInvested() - karma);

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
			logger.log(Level.ERROR, "Maximum grade is {0}", maxGrade);
			
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.METAECHO) {
					DataItemModification mod = (DataItemModification)tmp;
					MetamagicOrEcho item = mod.getResolvedKey();
					MetamagicOrEchoValue val = model.getMetamagicOrEcho(mod.getKey());
					if (val==null || !item.hasLevel()) {
						val = new MetamagicOrEchoValue(item);
						val.addModification(mod);
						getModel().addMetamagicOrEcho(val);
						logger.log(Level.DEBUG, "Auto-Added Metamagic/Echo ''{0}''", mod.getKey());
					} else {
						val.addModification(mod);
						logger.log(Level.DEBUG, "Auto-Increased Metamagic/Echo ''{0}''", mod.getKey());
					}
					continue;
				}
				unprocessed.add(tmp);
			}
			
			// Pay karma and apply modifications
			int payNext = 11;
			int grade = 0;
			for (MetamagicOrEchoValue val : model.getMetamagicOrEchoes()) {
				if (val.getModifyable().hasLevel()) {
					for (int i=0; i<val.getDistributed(); i++) {
						logger.log(Level.INFO, "Pay {0} Karma for metaecho ''{1}'' {2}", payNext, val.getModifyable().getId(), (i+1));
						model.setKarmaFree( model.getKarmaFree() - payNext);
						grade++;
						payNext++;
					}
				} else {
					logger.log(Level.INFO, "Pay {0} Karma for metaecho ''{1}''", payNext, val.getModifyable().getId());
					model.setKarmaFree( model.getKarmaFree() - payNext);
					grade++;
					payNext++;
				}
				// Add modifications
				for (Modification mod : val.getModifications()) {
					Modification copy = Shadowrun6Tools.instantiateModification(mod, val, model);
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

}
