package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.Shadowrun6Rules;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6VariantMode;

/**
 * @author prelle
 *
 */
public abstract class CommonEquipmentController extends ControllerImpl<ItemTemplate> implements IEquipmentController {
	
	//-------------------------------------------------------------------
	public CommonEquipmentController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public static Possible checkDecisionsAndRequirements(Shadowrun6Character model, ItemTemplate data, String variantID, Decision...decisions) {
		Possible p1 = Shadowrun6Tools.areRequirementsMet(model, data, decisions);
		Possible p2 = GenericRPGTools.areAllDecisionsPresent(data, variantID, decisions);
		
		return new Possible(p1, p2);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<ItemTemplate> getAvailable() {
		return Shadowrun6Core.getItemList(ItemTemplate.class);
	}

	//-------------------------------------------------------------------
	public List<ItemTemplate> getAvailable(CarryMode mode, ItemType...types) {
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		list = list.stream().filter(new ItemTypeFilter(mode, types)).collect(Collectors.toList());
		return list;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	@SuppressWarnings("unchecked")
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
		return canBeSelected(value, null, CarryMode.CARRIED, decisions);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController#canBeSelected(ItemTemplate, String, Decision[])
	 */
	@Override
	public Possible canBeSelected(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		// Ensure all choices are made
		Possible poss =  GenericRPGTools.areAllDecisionsPresent(value, variantID, decisions);
		if (!poss.get())
			return poss;
		
		if (value.requiresVariant() && variantID==null) {
			return new Possible(Possible.State.DECISIONS_MISSING, IRejectReasons.IMPOSS_MUST_CHOOSE_VARIANT);
		}
		
		// Try to build item
		OperationResult<CarriedItem<ItemTemplate>> carried = null;
		if (variantID!=null) {
			PieceOfGearVariant<SR6VariantMode> variant = value.getVariant(variantID);
			if (variant==null) {
				return new Possible(Severity.WARNING, SR6CharacterGenerator.RES, IRejectReasons.IMPOSS_INVALID_VARIANT, variantID, value.getName());
			}
			carried = GearTool.buildItem(value, mode, variant, getModel(), true, decisions);
		} else {		
			carried = GearTool.buildItem(value, mode, getModel(), true, decisions);
		}
		// Check availability
		if (carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY) != null) {
			Availability avail = carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue();
			if (avail!=null && avail.getValue() >= 7) {
				return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,SR6CharacterGenerator.RES, IRejectReasons.IMPOSS_AVAILABLE_TOO_HIGH, avail.getValue());
			}
		}
		// Check money
		if (carried.get().getAsValue(SR6ItemAttribute.PRICE) != null) {
			int nuyen = carried.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			if (nuyen>getModel().getNuyen()) {
				return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,SR6CharacterGenerator.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());
			}
		}
		
		// Check requirements of carried item
		if (!getModel().getRuleValueAsBoolean(Shadowrun6Rules.IGNORE_GEAR_REQUIREMENTS)) {
			Possible poss2 = Shadowrun6Tools.areRequirementsMet(getModel(), value, decisions);
			if (!poss2.get())
				return poss;
			// Shadowrun6Tools.areRequirementsMet(getModel(), carried.get());
		} else {
			logger.log(Level.DEBUG, "IGNORE_GEAR_REQUIREMENTS = true");
		}
		
		return poss;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, Decision... decisions) {
		return select(value, null, CarryMode.CARRIED, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController#select(ItemTemplate, String, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1}", value, mode);
		try {
			Possible poss = canBeSelected(value, variantID, mode, decisions);
			if (!poss.getRequireDecisions()) {
				logger.log(Level.ERROR, "Trying to select {0} which may not be selected: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}

			PieceOfGearVariant<SR6VariantMode> variant = null;
			if (variantID!=null) {
				variant = value.getVariant(variantID);
			}

			poss =  GenericRPGTools.areAllDecisionsPresent(value, variantID, decisions);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Trying to select {0} but decisions are missing: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}
			
			OperationResult<CarriedItem<ItemTemplate>> ret = GearTool.buildItem(value, mode, variant, getModel(), true, decisions);
			CarriedItem<ItemTemplate> item = ret.get();
			if (value.isCountable()) item.setCount(1);
			logger.log(Level.INFO, "Add {0} to model", item.getKey());
			getModel().addCarriedItem(item);
			
			parent.runProcessors();
			return new OperationResult<CarriedItem<ItemTemplate>>(item);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1}", value, mode);
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible poss = canBeDeselected(value);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Trying to deselect {0} which may not be deselected: {1}", value, poss.toString());
				return false;
			}

			logger.log(Level.INFO, "Remove {0} from model", value.toString());
			getModel().removeCarriedItem(value);
			
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
	public float getSelectionCost(ItemTemplate data) {
		return data.getAttribute(SR6ItemAttribute.PRICE).getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(ItemTemplate data) {
		int cost = data.getAttribute(SR6ItemAttribute.PRICE).getDistributed();
		if (cost==0) {
			String raw = data.getAttribute(SR6ItemAttribute.PRICE).getRawValue();
			String[] table = data.getAttribute(SR6ItemAttribute.PRICE).getLookupTable();
			if (data.requiresVariant()) {
				int min = Integer.MAX_VALUE;
				for (SR6PieceOfGearVariant variant : data.getVariants()) {
					if (variant.getAttribute(SR6ItemAttribute.PRICE)!=null) {
						int t = variant.getAttribute(SR6ItemAttribute.PRICE).getDistributed();
						min = Math.min(min, t);
					}
				}
				return String.valueOf(min)+"+";
			}
			String rtg = Shadowrun6Core.getI18nResources().getString("label.rating.short");
			if (raw.equals("$RATING") && table!=null)
				return table[0]+"+";
			if (raw.indexOf("$RATING")>-1)
				raw = raw.replace("$RATING", rtg);
			return raw;
		}
		return String.valueOf(cost);
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
		logger.log(Level.TRACE, "dencrease {0}", value);
		Possible poss = canBeDecreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase count on item where not allowed");
			return new OperationResult<>(poss);
		}
		
		value.setCount( value.getCount()-1 );
		logger.log(Level.INFO, "Decrease count of {0} to {1}", value, value.getCount());
		if (value.getCount()==0) {
			getModel().removeCarriedItem(value);
		}
		
		parent.runProcessors();
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
	public Possible canBeEmbedded(CarriedItem container, ItemHook slot, ItemTemplate value, String variant, Decision... decisions) {
		if (!getEmbeddableIn(container, slot).contains(value)) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_EMBEDDABLE, value.getName(), slot, container.getNameWithRating());
		}
		
		OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, CarryMode.EMBEDDED, getModel(), true, decisions);
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
	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem container, ItemHook slot, ItemTemplate value, String variant, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER embed {0} into {1}", value, container);
		try {
			Possible poss = canBeEmbedded(container, slot, value, variant, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to embed, which isn't possible: "+poss.getMostSevere());
				return new OperationResult<>();
			}
			
			OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, CarryMode.EMBEDDED, getModel(), true, decisions);
			logger.log(Level.ERROR, "ToDo: really embed");
			if (res.wasSuccessful()) {
				container.addAccessory(res.get(), slot);
			}
			logger.log(Level.ERROR, "ToDo: recalculate item after embedding");
			GearTool.recalculate("", getModel(), container);
			logger.log(Level.INFO, "Embedded {0} into {1}", value.getId()+"/"+variant, container.getKey());
			
			parent.runProcessors();
			return res;
		} finally {
			logger.log(Level.TRACE, "LEAVE embed{0}", value);
		}
	}

}
