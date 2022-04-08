package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CommonEquipmentController extends ControllerImpl<ItemTemplate> implements IEquipmentController {

	//-------------------------------------------------------------------
	public CommonEquipmentController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<ItemTemplate> getAvailable() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<CarriedItem<ItemTemplate>> getSelected() {
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		getModel().getCarriedItems().forEach(it -> ret.add(it));
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(ItemTemplate value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(CarriedItem<ItemTemplate> value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(ItemTemplate value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(ItemTemplate value, Decision... decisions) {
		// Ensure all choices are made
		for (Choice choice : value.getChoices()) {
			boolean choiceNotFound = true;
			for (Decision dec : decisions) {
				if (dec.getChoiceUUID()==choice.getUUID()) {
					choiceNotFound = false;
					// ToDo: validate choice value
					break;
				}
			}
			if (choiceNotFound)
				return new Possible(Possible.State.DECISIONS_MISSING, IRejectReasons.IMPOSS_MISSING_DECISIONS);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1}", value, List.of(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.getRequireDecisions()) {
				logger.log(Level.ERROR, "Trying to select {0} which may not be selected: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}

			OperationResult<CarriedItem<ItemTemplate>> ret = GearTool.buildItem(value, decisions);
			CarriedItem<ItemTemplate> item = ret.get();
			if (value.isCountable()) item.setCount(1);
			logger.log(Level.INFO, "Add {0} to model", item.toString());
			getModel().addCarriedItem(item);
			
			parent.runProcessors();
			return new OperationResult<CarriedItem<ItemTemplate>>(item);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {0}", value, List.of(decisions));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(CarriedItem<ItemTemplate> value) {
		if (!getModel().getCarriedItems().contains(value))
			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		if (value.isAutoAdded())
			return new Possible(false, IRejectReasons.IMPOSS_AUTO_ADDED);
		return Possible.TRUE;
	}

	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "ENTER");
		try {
			Shadowrun6Character model = getModel();
			// Reset character nuyen
			model.setNuyen(0);
			
			List<Modification> unprocessed = new ArrayList<>();
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.CREATION_POINTS && mod.getResolvedKey()==CreatePoints.NUYEN) {
						model.setNuyen( model.getNuyen() + mod.getValue());
						logger.log(Level.DEBUG, "consume {0}", tmp);
					}
				} else {
					// No ValueModification
					unprocessed.add(tmp);
				}
			}
			logger.log(Level.INFO, "{0} Nuyen available", model.getNuyen());
			
			/*
			 * Walk through all items and pay for them
			 */
			int nuyen = model.getNuyen();
			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				logger.log(Level.DEBUG, "Pay {0} for {1}", tmp.getAsValue(SR6ItemAttribute.PRICE), tmp.getNameWithRating());
				int cost = tmp.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
				nuyen -= cost;
			}
			model.setNuyen(nuyen);
			logger.log(Level.INFO, "Nuyen remaining: {0}", model.getNuyen());
			
			return unprocessed;
		} finally {
			logger.log(Level.DEBUG, "LEAVE");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(ItemTemplate data) {
		// TODO Auto-generated method stub
		return data.getAttribute(SR6ItemAttribute.PRICE).getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(CarriedItem<ItemTemplate> value) {
		if (!value.getModifyable().isCountable())
			return Possible.FALSE;
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(CarriedItem<ItemTemplate> value) {
		if (!value.getModifyable().isCountable())
			return Possible.FALSE;
		if (value.getCount()<2)
			return Possible.FALSE;
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> increase(CarriedItem<ItemTemplate> value) {
		logger.log(Level.TRACE, "increase {0}", value);
		Possible poss = canBeIncreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase count on item where not allowed");
			return new OperationResult<>(poss);
		}
		
		value.setCount( value.getCount()+1 );
		logger.log(Level.INFO, "Increase count of {0} to {1}", value, value.getCount());
		
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> decrease(CarriedItem<ItemTemplate> value) {
		// TODO Auto-generated method stub
		return new OperationResult<>(value);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController#getEmbeddableIn(CarriedItem, ItemHook)
	 */
	@Override
	public List<ItemTemplate> getEmbeddableIn(CarriedItem ref, ItemHook slot) {
		return ItemUtil.getEmbeddableIn(ref, slot);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController#canBeEmbedded(CarriedItem, ItemHook, ItemTemplate, Decision[])
	 */
	@Override
	public Possible canBeEmbedded(CarriedItem container, ItemHook slot, ItemTemplate value, Decision... decisions) {
		if (!getEmbeddableIn(container, slot).contains(value)) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_EMBEDDABLE, value.getName(), slot, container.getNameWithRating());
		}
		
		OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, decisions);
		if (res.hasError()) {
			return new Possible(State.IMPOSSIBLE, res.getMessages().toString());
		}
		
		ItemAttributeNumericalValue<SR6ItemAttribute> val = res.get().getAsValue(SR6ItemAttribute.PRICE);
		if (val==null) {
			logger.log(Level.ERROR, "No PRICE attribute after building "+res.get());
		} else {
			int nuyen = val.getModifiedValue();
			if (nuyen > getModel().getNuyen()) {
				return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());			
			}
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController#embed(CarriedItem, ItemHook, ItemTemplate, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem container, ItemHook slot, ItemTemplate value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER embed {0} into {1}", value, container);
		try {
			Possible poss = canBeEmbedded(container, slot, value, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to embed, which isn't possible: "+poss.getMostSevere());
				return new OperationResult<>();
			}
			
			OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, decisions);
			logger.log(Level.WARNING, "ToDo: really embed");
			logger.log(Level.WARNING, "ToDo: recalculate item after embedding");
			return res;
		} finally {
			logger.log(Level.TRACE, "LEAVE embed{0}", value);
		}
	}

}
