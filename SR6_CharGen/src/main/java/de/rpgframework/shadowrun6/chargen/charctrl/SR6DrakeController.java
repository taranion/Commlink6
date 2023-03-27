package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.AllowModification.AllowType;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.DrakeType;
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6DrakeController extends ControllerImpl<MetamagicOrEcho>
		implements IMetamagicOrEchoController {

	protected static Logger logger = System.getLogger(SR6DrakeController.class.getPackageName()+".drake");

	private List<ShadowrunAttribute> allowedAdjust;

	private boolean isCharGen;
	private int maxGrade = Integer.MAX_VALUE;
	private DrakeAttributeController attrCtrl;

	private int adjustPoints, adjustPointsMax;

	//-------------------------------------------------------------------
	public SR6DrakeController(SR6CharacterController parent, boolean isCharGen) {
		super(parent);
		this.isCharGen = isCharGen;
		allowedAdjust = new ArrayList<>();
		attrCtrl = new DrakeAttributeController(parent, this);
	}

	//-------------------------------------------------------------------
	public int getAdjustmentPointsMax() {
		return adjustPointsMax;
	}
	//-------------------------------------------------------------------
	public int getAdjustmentPointsLeft() {
		return adjustPoints;
	}

	//-------------------------------------------------------------------
	public void selectDrakeType(DrakeType type ) {
		logger.log(Level.INFO, "set Drake Type to {0}",type);
		if (getModel().getDrakeType()==null || getModel().getDrakeType().getResolved()!=type) {
			DrakeTypeValue toSet = new DrakeTypeValue(type);
			for (Modification tmp : type.getModifications()) {
				if (tmp instanceof AllowModification) {
					AllowModification allow = (AllowModification)tmp;
					ShadowrunAttribute key = allow.getResolvedKey();
					AttributeValue<ShadowrunAttribute> aVal = new AttributeValue<ShadowrunAttribute>(key,1);
					toSet.setAttribute(aVal);
				}
			}
			getModel().setDrakeType(toSet);

			parent.runProcessors();
		}
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
		if (getModel().getDrakeType()==null) return List.of();
		return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
				.filter(p -> parent.showDataItem(p))
				.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
				.filter(m -> m.getType()==Type.DRACOGENESIS_POWER)
				.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<MetamagicOrEchoValue> getSelected() {
//		System.err.println("SR6DrakeController.getSelected: "+getModel().getMetamagicOrEchoes().size()+" items");
		return getModel().getMetamagicOrEchoes().stream().filter(v -> v.getResolved().getType()==Type.DRACOGENESIS_POWER).collect(Collectors.toList());
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
		List<MetamagicOrEchoValue> list = getSelected();
		// Determine the grade
		int grade = 0;
		for (MetamagicOrEchoValue tmp : list) {
			if (tmp.isAutoAdded()) continue;
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
		int karma = (int)getSelectionCost(value);

		// Check if character is aware drake
		if (getModel().getBodytype()!=BodyType.DRAKE) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_DRAKE_ONLY, karma);
		}

		if (getModel().getKarmaFree()<karma) {
			return Possible.FALSE;
//			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, karma);
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
				logger.log(Level.ERROR, "Trying to select a dracogenesis power that cannot be selected: {0}",possible.getI18NKey());
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

			int karma =  (int)getSelectionCost(value);;
			getModel().addMetamagicOrEcho(selected);
			logger.log(Level.INFO, "Add dracogenesis power '" + value.getId() + "' for " + karma + " karma");
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
		if (value.isAutoAdded()) return Possible.FALSE;
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
				logger.log(Level.ERROR, "Trying to deselect a dracogenesis power that cannot be deselected: {0}",possible.getI18NKey());
				return false;
			}

			int karma = 5 + getGrade();
			Shadowrun6Character model = getModel();
			model.removeMetamagicOrEcho(value);
			model.setKarmaFree( model.getKarmaFree() + karma);
			model.setKarmaInvested( model.getKarmaInvested() - karma);

			logger.log(Level.INFO, "Remove dracogenesis power '" + value.getModifyable().getId() + "' for " + karma + " karma");

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
		return 5 + getGrade() +1;
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
		int karma = (int)getSelectionCost(item);

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
				logger.log(Level.ERROR, "Trying to increase a dracogenesis power that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			int karma = (int)getSelectionCost(value.getResolved());
			value.setDistributed(value.getDistributed()+1);

			logger.log(Level.INFO, "Increased dracogenesis power '" + value.getModifyable().getId() + "' for " + karma + " karma");
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
				logger.log(Level.ERROR, "Trying to decrease a dracogenesis power that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			value.setDistributed(value.getDistributed()+1);
			int karma = 5 + getGrade() +1;

			logger.log(Level.INFO, "Decreased dracogenesis power '" + value.getModifyable().getId() + "' for " + karma + " karma");
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
			Shadowrun6Character model = getModel();
			allowedAdjust.clear();
			adjustPointsMax = 6;

			// If this is in chargen mode, pay drake quality
			if (isCharGen) {
				if (model.getBodytype()==BodyType.DRAKE) {
					logger.log(Level.INFO, "Pay 50 Karma for Drake");
					model.setKarmaFree( model.getKarmaFree() - 50);
				} else if (model.getBodytype()==BodyType.DRAKE_LATENT) {
					logger.log(Level.INFO, "Pay 25 Karma for being a latent Drake");
					model.setKarmaFree( model.getKarmaFree() - 25);
				}
			}

			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE && tmp instanceof AllowModification && ((AllowModification)tmp).getWhat()==AllowType.DRACOFORM) {
					AllowModification allow = (AllowModification)tmp;
					ShadowrunAttribute attr = tmp.getReferenceType().resolve(allow.getKey());
					allowedAdjust.add(attr);
					continue;
				}
				if (tmp.getReferenceType()==ShadowrunReference.CREATION_POINTS) {
					ValueModification mod = (ValueModification)tmp;
					CreatePoints type = tmp.getReferenceType().resolve(mod.getKey());
					if (type==CreatePoints.ADJUST_DRAKE) {
						logger.log(Level.DEBUG, "Add {0} to available adjustment points from ''{1}''", mod.getValue(),mod.getKey());
						adjustPointsMax += mod.getValue();
						continue;
					}
				}

				if (tmp.getReferenceType()==ShadowrunReference.METAECHO) {
					DataItemModification mod = (DataItemModification)tmp;
					MetamagicOrEcho item = mod.getResolvedKey();
					MetamagicOrEchoValue val = model.getMetamagicOrEcho(mod.getKey());
					if (val==null || !item.hasLevel()) {
						val = new MetamagicOrEchoValue(item);
						val.addModification(mod);
						getModel().addMetamagicOrEcho(val);
						logger.log(Level.DEBUG, "Auto-Added Dracogenesis Power ''{0}''", mod.getKey());
					} else {
						val.addModification(mod);
						logger.log(Level.DEBUG, "Auto-Increased Dracogenesis Power ''{0}''", mod.getKey());
					}
					continue;
				}
				unprocessed.add(tmp);
			}
			logger.log(Level.DEBUG, "Allowed for dracform adjustment points: {0}", allowedAdjust);
			adjustPoints = adjustPointsMax;

			DrakeTypeValue drake = model.getDrakeType();
			if (drake!=null) {
				logger.log(Level.DEBUG, "Drake attributes: {0} ", drake.getAttributes());
				// Clear all attributes from the body that are not allowed
				for (AttributeValue<ShadowrunAttribute> aVal : new ArrayList<>(drake.getAttributes())) {
					ShadowrunAttribute key = aVal.getModifyable();
					if (allowedAdjust.contains(key)) {
						adjustPoints -= aVal.getDistributed();
					}
				}
			}


			// Pay karma and apply modifications
			int payNext = 6;
			int grade = 0;
			for (MetamagicOrEchoValue val : model.getMetamagicOrEchoes()) {
				if (val.getModifyable().getType()!=Type.DRACOGENESIS_POWER) continue;
				logger.log(Level.DEBUG, "Metaecho {0} is auto={1}", val.getKey(), val.isAutoAdded());
				if (val.isAutoAdded()) {
					logger.log(Level.DEBUG, "Ignore auto-added drake power {0}", val.getKey());
					continue;
				}
				if (val.getModifyable().hasLevel()) {
					for (int i=0; i<val.getDistributed(); i++) {
						logger.log(Level.INFO, "Pay {0} Karma for dracogenesis power ''{1}'' {2}", payNext, val.getModifyable().getId(), (i+1));
						model.setKarmaFree( model.getKarmaFree() - payNext);
						grade++;
						payNext++;
					}
				} else {
					logger.log(Level.INFO, "Pay {0} Karma for dracogenesis power ''{1}''", payNext, val.getModifyable().getId());
					model.setKarmaFree( model.getKarmaFree() - payNext);
					grade++;
					payNext++;
				}
				// Add modifications
				for (Modification mod : val.getModifications()) {
					Modification copy = Shadowrun6Tools.instantiateModification(mod, val, val.getDistributed(), model);
					logger.log(Level.DEBUG, "Add modification "+copy);
					unprocessed.add(copy);
				}
			}

			logger.log(Level.INFO, "Dracogenesis grade = "+grade);
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
	public List<ShadowrunAttribute> getDracoformAttributes() {
		return new ArrayList<>(allowedAdjust);
	}

	//-------------------------------------------------------------------
	public NumericalValueController<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>> getAttributeController() {
		return attrCtrl;
	}

}


class DrakeAttributeController extends ControllerImpl<ShadowrunAttribute> implements NumericalValueController<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>> {

	private SR6DrakeController drakeParent;

	public DrakeAttributeController(SR6CharacterController parent, SR6DrakeController drakeParent) {
		super(parent);
		this.drakeParent = drakeParent;
	}

	@Override
	public RecommendationState getRecommendationState(ShadowrunAttribute item) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

	@Override
	public int getValue(AttributeValue<ShadowrunAttribute> value) {
		DrakeTypeValue drake = parent.getModel().getDrakeType();
		if (drake==null) return 0;
		return drake.getAttribute(value.getModifyable()).getDistributed();
	}

	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		return new Possible(drakeParent.getAdjustmentPointsLeft()>0);
	}

	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		return new Possible(value.getModifiedValue()>1);
	}

	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
		DrakeTypeValue drake = parent.getModel().getDrakeType();
		if (drake==null) return new OperationResult<>(Possible.FALSE);

		logger.log(Level.WARNING, "Increase drake attribute {0}", value.getModifyable());
		value.setDistributed( value.getDistributed() +1);
		parent.runProcessors();
		return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
	}

	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
		DrakeTypeValue drake = parent.getModel().getDrakeType();
		if (drake==null) return new OperationResult<>(Possible.FALSE);

		logger.log(Level.INFO, "Decrease drake attribute {0}", value.getModifyable());
		value.setDistributed( value.getDistributed() -1);
		parent.runProcessors();
		return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
	}

}