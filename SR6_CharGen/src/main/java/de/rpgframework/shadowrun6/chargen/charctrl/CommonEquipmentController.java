package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.PriceModifiers;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public abstract class CommonEquipmentController extends ControllerImpl<ItemTemplate> implements ISR6EquipmentController {

	protected static Logger logger = System.getLogger(CommonEquipmentController.class.getPackageName());

	protected List<ValueModification> priceMods;

	//-------------------------------------------------------------------
	public CommonEquipmentController(SR6CharacterController parent) {
		super(parent);
		priceMods = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public static ItemType getItemType(CarriedItem<ItemTemplate> model) {
		if (model.hasAttribute(SR6ItemAttribute.ITEMTYPE))
			return model.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
		return null;
	}

	//-------------------------------------------------------------------
	public static ItemSubType getItemSubType(CarriedItem<ItemTemplate> model) {
		if (model.hasAttribute(SR6ItemAttribute.ITEMSUBTYPE))
			return model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
		return null;
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
		return Shadowrun6Core.getItemList(ItemTemplate.class).stream()
				.filter(p -> parent.showDataItem(p))
				.filter(p -> !p.isModOnly())
				.collect(Collectors.toList());
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
	private List<CarryMode> getAllowedModes(ItemTemplate value, PieceOfGearVariant<SR6VariantMode> variant) {
		List<CarryMode> ret = new ArrayList<>();
		for (Usage usage : value.getUsages()) {
			if (!ret.contains(usage.getMode()))
				ret.add(usage.getMode());
		}

		if (variant!=null) {
			for (Usage usage : variant.getUsages()) {
				if (!ret.contains(usage.getMode()))
					ret.add(usage.getMode());
			}
		}

		if (ret.isEmpty())
			ret.add(CarryMode.CARRIED);
		return ret;
	}
	//-------------------------------------------------------------------
	private int getPerItemPrice(CarriedItem<ItemTemplate> tmp) {
		ItemAttributeNumericalValue<SR6ItemAttribute> priceVal = tmp.getAsValue(SR6ItemAttribute.PRICE);
		double baseCost = priceVal.getDistributed();
		// Add price modifications that apply
		for (ValueModification priceMod : priceMods) {
			PriceModifiers pmType = priceMod.getResolvedKey();
			ItemType type = tmp.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
			ItemSubType subtype = tmp.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
			double factor = priceMod.getValueAsDouble();
			int extraCost = (int)( factor*baseCost);
			ValueModification toAdd = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), extraCost, priceMod.getSource());
			switch (pmType) {
			case CLOTHING:
				if (subtype==ItemSubType.ARMOR_CLOTHES)
					priceVal.addIncomingModification(toAdd);
				break;
			case ARMOR:
				if (type==ItemType.ARMOR || type==ItemType.ARMOR_ADDITION) {
					System.err.println("Add extra "+extraCost+" to "+tmp+"   factor="+factor);
					priceVal.addIncomingModification(toAdd);
				}
				break;
			case EVERYTHING:
				priceVal.addIncomingModification(toAdd);
				break;
			}
		}

		return priceVal.getModifiedValue();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canBeSelected(ItemTemplate, String, Decision[])
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

		// Check variant
		PieceOfGearVariant<SR6VariantMode> variant = null;
		if (variantID!=null) {
			variant = value.getVariant(variantID);
			if (variant==null) {
				return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_INVALID_VARIANT, variantID, value.getName());
			}
		}

		// Compare carry mode
		List<CarryMode> allowed = getAllowedModes(value, variant);
		if (mode!=null && !allowed.contains(mode)) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_INVALID_CARRYMODE, mode.name(), String.valueOf(allowed));
		}


		// Try to build item
		OperationResult<CarriedItem<ItemTemplate>> carried = null;
		carried = GearTool.buildItem(value, mode, variant, getModel(), false, decisions);
		// Check money
		if (carried.get().getAsValue(SR6ItemAttribute.PRICE) != null) {
			//int nuyen = carried.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			int nuyen = getPerItemPrice(carried.get());
			if (nuyen>getModel().getNuyen()) {
				// Not enough money. Career and CharGen mode both have options to ignore this
				if (getModel().isInCareerMode()) {
					boolean payGear = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CAREER_PAY_GEAR);
					if (payGear) {
						return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());
					}
				} else {
					boolean allowNegative = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN);
					if (!allowNegative) {
						return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());
					}
				}
			}
		}

		// Prevent "used" cultured bioware
		if (value.getItemType(mode)==ItemType.BIOWARE && value.getItemSubtype(mode)==ItemSubType.BIOWARE_CULTURED) {
			Decision dec = carried.get().getDecision(ItemTemplate.UUID_AUGMENTATION_QUALITY);
			if (dec!=null) {
				AugmentationQuality quality = AugmentationQuality.valueOf( dec.getValue() );
				if (AugmentationQuality.USED.equals(quality) && !parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.ALLOW_USED_CULTURED_BIOWARE)) {
					return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_USED_CULTURED_BIOWARE);
				}
			}
		}

		// Check requirements of carried item
		if (!parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.IGNORE_GEAR_REQUIREMENTS)) {
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
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#select(ItemTemplate, String, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		logger.log(Level.WARNING, "ENTER select({0}, {1}", value, mode);
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
		return deselect(value, RemoveMode.UNDO);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value, RemoveMode mode) {
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
//		if (!value.getModifyable().isCountable())
//			return Possible.FALSE;

		int nuyen = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		if (getModel().getNuyen()<nuyen) {
			boolean allowNegative = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN);
			if (!allowNegative) {
				return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());
			}
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(CarriedItem<ItemTemplate> value) {
//		if (!value.getModifyable().isCountable())
//			return Possible.FALSE;
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
		value.setDirty(true);
		logger.log(Level.INFO, "Increase count of {0} to {1}", value, value.getCount());

		parent.runProcessors();
		int price = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		logger.log(Level.INFO, "Price now {0}",price);
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> decrease(CarriedItem<ItemTemplate> value) {
		logger.log(Level.TRACE, "decrease {0}", value);
		Possible poss = canBeDecreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to decrease count on item where not allowed");
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
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getEmbeddableIn(CarriedItem, ItemHook)
	 */
	@Override
	public List<ItemTemplate> getEmbeddableIn(CarriedItem<ItemTemplate> ref, ItemHook slot) {
		return ItemUtil.getEmbeddableIn(ref, slot);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canBeEmbedded(CarriedItem, ItemHook, ItemTemplate, Decision[])
	 */
	@Override
	public Possible canBeEmbedded(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, String variantID, Decision... decisions) {
		if (!getEmbeddableIn(container, slot).contains(value)) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_EMBEDDABLE, value.getName(), slot, container.getNameWithRating());
		}

		// Check for variant
		SR6PieceOfGearVariant variant = null;
		if (variantID!=null) {
			variant = (SR6PieceOfGearVariant) value.getVariant(variantID);
			if (variant==null) {
				return new Possible(IRejectReasons.IMPOSS_MUST_CHOOSE_VARIANT);
			}
		}

		// Check requirements
		Requirement unmet = ItemUtil.areRequirementsMet(container, value, variant);
		if (unmet!=null) {
			return new Possible(unmet);
		}

		OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, CarryMode.EMBEDDED, variant, getModel(), true, decisions);
		if (res.hasError()) {
			State state = State.POSSIBLE;
			for (ToDoElement error : res.getMessages()) {
				if (error.getSeverity()==Severity.STOPPER) state=State.IMPOSSIBLE;
				if (error.toString().contains("Missing decision") && state.ordinal()<State.DECISIONS_MISSING.ordinal()) state=State.DECISIONS_MISSING;
			}
			return new Possible(state, res.getMessages().toString());
		}
		CarriedItem<ItemTemplate> toEmbed = res.get();


		ItemAttributeNumericalValue<SR6ItemAttribute> val = res.get().getAsValue(SR6ItemAttribute.PRICE);
		if (val==null) {
			logger.log(Level.ERROR, "No PRICE attribute after building "+res.get());
		} else {
			int nuyen = val.getModifiedValue();
			if (nuyen > getModel().getNuyen()) {
				return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, nuyen, getModel().getNuyen());
			}
		}

		// Check if capacity is sufficient
		AvailableSlot realSlot = (AvailableSlot) container.getSlot(slot);
		logger.log(Level.INFO, "Slot to add element in: {0}  with capacity = {1}", realSlot, slot.hasCapacity());
		if (realSlot==null) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NO_SUCH_SLOT, slot.name(), container.getNameWithoutRating());
		}
		logger.log(Level.INFO, "Items in slot={0},  free capacity={1}  Slot {2} hasCapacity={3}", realSlot.getAllEmbeddedItems().size(), realSlot.getFreeCapacity(), slot.name(), slot.hasCapacity());
		if (container.getUuid().equals(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE))
			return Possible.TRUE;
		// If slot has a limit on size of accessories, verify it
		if (realSlot.getMaxSizePerItem()!=null) {
			float required = 1;
			if (toEmbed.hasAttribute(SR6ItemAttribute.SIZE)) {
				if (toEmbed.isFloat(SR6ItemAttribute.SIZE))
					required = toEmbed.getAsFloat(SR6ItemAttribute.SIZE).getModifiedValue();
				else
					required = toEmbed.getAsValue(SR6ItemAttribute.SIZE).getModifiedValue();
			}
			if (required>realSlot.getMaxSizePerItem()) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_SIZE, slot.name(), container.getNameWithoutRating());
			}
		}


		// Special handling for software: check types


		if (slot.hasCapacity() && realSlot.getCapacity()<99) {
			float free = realSlot.getFreeCapacity();
			float required = 1;
			if (toEmbed.hasAttribute(SR6ItemAttribute.SIZE)) {
				if (toEmbed.isFloat(SR6ItemAttribute.SIZE))
					required = toEmbed.getAsFloat(SR6ItemAttribute.SIZE).getModifiedValue();
				else
					required = toEmbed.getAsValue(SR6ItemAttribute.SIZE).getModifiedValue();
			}
			if (free<required) {
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_CAPACITY,
						required,
						realSlot.getMaxSizePerItem(),
						container.getKey(), free);
			}
		} else if (!slot.hasCapacity()) {
			if (!realSlot.getAllEmbeddedItems().isEmpty()) {
				logger.log(Level.INFO, "Cannot embed");
				return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_SLOT_OCCUPIED, slot.getName());
			}
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#embed(CarriedItem, ItemHook, ItemTemplate, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, String variantID, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER embed {0} into {1}", value, container);
		try {
			Possible poss = canBeEmbedded(container, slot, value, variantID, decisions);
			if (!poss.get()) {
				logger.log(Level.WARNING, "Trying to embed, which isn't possible: "+poss.getMostSevere());
				return new OperationResult<>();
			}

			SR6PieceOfGearVariant variant = null;
			if (variantID!=null)
				variant = (SR6PieceOfGearVariant) value.getVariant(variantID);
			OperationResult<CarriedItem<ItemTemplate>> res = GearTool.buildItem(value, CarryMode.EMBEDDED, variant, getModel(), true, decisions);
			if (!res.wasSuccessful()) {
				logger.log(Level.ERROR, "Error building item: "+res.getMessages());
				return res;
			}
			container.addAccessory(res.get(), slot);
			logger.log(Level.DEBUG, "recalculate item after embedding");
			GearTool.recalculate("", ShadowrunReference.ITEM_ATTRIBUTE, getModel(), container);
			logger.log(Level.WARNING, "Embedded {0} into {1}/{2}", value.getId()+"/"+variant, container.getKey(), container.getUuid());

			parent.runProcessors();
			return res;
		} finally {
			logger.log(Level.TRACE, "LEAVE embed{0}", value);
		}
	}

	//-------------------------------------------------------------------
	public Possible canBeRemoved(CarriedItem<ItemTemplate> container, ItemHook slot, CarriedItem<ItemTemplate> toRemove) {
		if (container.getUuid()==ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE && slot==ItemHook.SOFTWARE) return Possible.TRUE;
		return new Possible( container.getAccessories().contains(toRemove) );
	}

	//-------------------------------------------------------------------
	public Possible removeEmbedded(CarriedItem<ItemTemplate> container, ItemHook slot, CarriedItem<ItemTemplate> toRemove) {
		Possible poss = canBeRemoved(container, slot, toRemove);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Tried to remove accessory {0} from {1}, but: {2}", toRemove, container, poss.toString());
			return poss;
		}

		logger.log(Level.INFO, "Remove {0} from {1}", toRemove.getKey(), container.getKey());
		boolean success = container.removeAccessory(toRemove, slot);
		if (!success)
			return Possible.FALSE;
		logger.log(Level.DEBUG, "ToDo: recalculate item after removing embedded");
		GearTool.recalculate("", ShadowrunReference.ITEM_ATTRIBUTE, getModel(), container);

		parent.runProcessors();
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(CarriedItem<ItemTemplate> value) {
		return value.getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getItemEnhancementController(de.rpgframework.genericrpg.items.CarriedItem)
	 */
	@Override
	public ComplexDataItemController<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>> getItemEnhancementController(CarriedItem<ItemTemplate> toModify) {
		return 	new CommonItemEnhancementController(parent, toModify);
	}

}
