package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.PriceModifiers;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;

/**
 * @author prelle
 *
 */
public class CommonEquipmentGenerator extends CommonEquipmentController  {

	private int conversionRate = 2000;

	//-------------------------------------------------------------------
	public CommonEquipmentGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canBeSelected(ItemTemplate, String, Decision[])
	 */
	@Override
	public Possible canBeSelected(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		Possible poss = super.canBeSelected(value, variantID, mode, decisions);
		if (!poss.get())
			return poss;

		// Check variant
		PieceOfGearVariant<SR6VariantMode> variant = null;
		if (variantID!=null) {
			variant = value.getVariant(variantID);
			if (variant==null) {
				return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_INVALID_VARIANT, variantID, value.getName());
			}
		}

		// Try to build item
		OperationResult<CarriedItem<ItemTemplate>> carried = null;
		carried = GearTool.buildItem(value, mode, variant, getModel(), false, decisions);
		// Check availability
		if (carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY) != null) {
			Availability avail = carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue();
			if (avail!=null && avail.getValue() >= 7) {
				return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,IRejectReasons.RES, IRejectReasons.IMPOSS_AVAILABLE_TOO_HIGH, avail.getValue());
			}
		}

		return poss;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, Decision... decisions) {
		CarryMode mode = CarryMode.CARRIED;
		if (value.getUsages()!=null && value.getUsages().size()>0) {
			if (value.getUsages().size()==1)
				mode = value.getUsages().get(0).getMode();
			else if (value.getUsages().stream().map(u->u.getMode()).collect(Collectors.toList()).contains(CarryMode.IMPLANTED))
				throw new IllegalArgumentException("More than one possible CarryMode - use other select() method");
		}
		OperationResult<CarriedItem<ItemTemplate>> res = select(value, null, mode, decisions);
//		if (res.wasSuccessful())
//			parent.runProcessors();
		return res;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#select(ItemTemplate, String, Decision[])
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

			OperationResult<CarriedItem<ItemTemplate>> ret = SR6GearTool.buildItem(value, mode, variant, getModel(), true, decisions);
			CarriedItem<ItemTemplate> item = ret.get();
			if (value.isCountable()) item.setCount(1);
			logger.log(Level.WARNING, "Add {0} to model", item.getKey());
			getModel().addCarriedItem(item);

			// Eventually record item in essence change list
			if (mode==CarryMode.IMPLANTED) {
				ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
				double essence = aVal.getModifiedValueDouble();
				ValueModification mod = new ValueModification(ShadowrunReference.CARRIED, item.getResolved().getId(), (int)(essence*1000));
				mod.setId(item.getUuid());
				mod.setWhen(null);
				getModel().getEssenceChanges().add(mod);
			}

			parent.runProcessors();
			return new OperationResult<CarriedItem<ItemTemplate>>(item);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1}", value, mode);
		}
	}

	//-------------------------------------------------------------------
	private void clearPriceModifications() {
		for (CarriedItem<ItemTemplate> tmp : parent.getModel().getCarriedItems()) {
			if (ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE.equals(tmp.getUuid()))
				continue;
			if (ItemTemplate.UUID_UNARMED.equals(tmp.getUuid()))
				continue;
				// Remove old price modifications
			ItemAttributeNumericalValue<SR6ItemAttribute> val = tmp.getAsValue(SR6ItemAttribute.PRICE);
			if (val==null) {
				logger.log(Level.ERROR, "No PRICE attribute in {0}", tmp);
			} else {
				for (Modification tmpMod : val.getIncomingModifications()) {
					if ((tmpMod instanceof ValueModification) && ItemTemplate.UUID_VOLATILE_PRICEMOD.equals(((ValueModification)tmpMod).getId()))
						val.removeIncomingModification(tmpMod);
				}
			}
		}
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
			conversionRate = 2000;
			todos.clear();
			clearPriceModifications();

			List<Modification> unprocessed = new ArrayList<>();
			// Prepare a list of price modifiers
			priceMods.clear();
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getReferenceType()==ShadowrunReference.CREATION_POINTS && mod.getResolvedKey()==CreatePoints.NUYEN) {
						model.setNuyen( model.getNuyen() + mod.getValue());
						logger.log(Level.DEBUG, "consume {0}", tmp);
					} else if (mod.getReferenceType()==ShadowrunReference.GEAR) {
						ItemTemplate template = mod.getResolvedKey();
						model.setNuyen( model.getNuyen() + mod.getValue());
						logger.log(Level.DEBUG, "add {0}", template);
					} else if (mod.getReferenceType()==ShadowrunReference.PRICEMOD) {
						if (mod.getId()==null) {
							logger.log(Level.ERROR, "PRICEMOD from "+mod.getSource()+" is missing UUID");
							System.err.println("CommonEquipmentGenerator: PRICEMOD from "+mod.getSource()+" is missing UUID");
						}
						priceMods.add(mod);
					} else
						unprocessed.add(tmp);
				} else {
					// No ValueModification
					unprocessed.add(tmp);
				}
			}

			CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
			if (sett.getKarmaToNuyen()>0) {
				int rate = model.hasRuleFlag(SR6RuleFlag.IN_DEBT)?5000:2000;
				int add  = sett.getKarmaToNuyen()*rate;
				logger.log(Level.INFO, "Convert {0} Karma into {1} Nuyen (Rate 1:{2})", sett.getKarmaToNuyen(), add, rate);
				model.setNuyen( model.getNuyen() + add);
				model.setKarmaFree( model.getKarmaFree() - sett.getKarmaToNuyen());

				if (model.hasRuleFlag(SR6RuleFlag.IN_DEBT)) {
					model.setDebtRate(sett.getKarmaToNuyen()*500);
					model.setDebt(add);
				}

			}

			logger.log(Level.WARNING, "{0} Nuyen available", model.getNuyen());


			logger.log(Level.DEBUG, "Modifiers {0}",priceMods);

			/*
			 * Walk through all items and pay for them
			 */
			int nuyen = model.getNuyen();
			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				if (ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE.equals(tmp.getUuid()))
					continue;
				if (ItemTemplate.UUID_UNARMED.equals(tmp.getUuid()))
					continue;
				if (!tmp.isAutoAdded()) {
					applyPriceModifiers(tmp);

					int cost = tmp.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
					if (tmp.getCount()>1)
						cost *= tmp.getCount();
					//if (logger.isLoggable(Level.TRACE))
					logger.log(Level.INFO, "Pay {0} for {1}   (before {2})", cost, tmp.getKey(), nuyen);
					nuyen -= cost;
				}

				// If it is an weapon, check if ammunition is present, warn otherwise
				if (getItemType(tmp)==ItemType.WEAPON_FIREARMS) {
					if (Shadowrun6Tools.getAmmunitionsFor(model, tmp).isEmpty()) {
						todos.add(new ToDoElement(Severity.INFO, IRejectReasons.RES, IRejectReasons.TODO_NO_AMMUNITION, tmp.getNameWithoutRating()));
					}
				}
			}
			model.setNuyen(nuyen);
			logger.log(Level.INFO, "Leave with {0} Karma", model.getKarmaFree());
			logger.log(Level.WARNING, "Nuyen remaining: {0}", model.getNuyen());

			return unprocessed;
		} finally {
			logger.log(Level.DEBUG, "LEAVE");
		}
	}

	//-------------------------------------------------------------------
	private void applyPriceModifiers(CarriedItem<ItemTemplate> tmp) {
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
			toAdd.setId(ItemTemplate.UUID_VOLATILE_PRICEMOD);
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

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getConvertedKarma()
	 */
	@Override
	public int getConvertedKarma() {
		return getModel().getCharGenSettings(CommonSR6GeneratorSettings.class).getKarmaToNuyen();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#getConversionRateKarma()
	 */
	@Override
	public int getConversionRateKarma() {
		return conversionRate;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canIncreaseConversion()
	 */
	@Override
	public boolean canIncreaseConversion() {
		if (getModel().getKarmaFree()<1) return false;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#increaseConversion()
	 */
	@Override
	public boolean increaseConversion() {
		if (!canIncreaseConversion()) {
			logger.log(Level.ERROR, "Trying to increase Karma -> Nuyen conversion although not allowed");
			return false;
		}

		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		sett.setKarmaToNuyen(sett.getKarmaToNuyen()+1);
		logger.log(Level.INFO, "increased Karma converted to Nuyen to {0}", sett.getKarmaToNuyen());

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#canDecreaseConversion()
	 */
	@Override
	public boolean canDecreaseConversion() {
		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		return sett.getKarmaToNuyen()>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#decreaseConversion()
	 */
	@Override
	public boolean decreaseConversion() {
		if (!canDecreaseConversion()) {
			logger.log(Level.ERROR, "Trying to decrease Karma -> Nuyen conversion although not allowed");
			return false;
		}

		CommonSR6GeneratorSettings sett = getModel().getCharGenSettings(CommonSR6GeneratorSettings.class);
		sett.setKarmaToNuyen(sett.getKarmaToNuyen()-1);
		logger.log(Level.INFO, "decreased Karma converted to Nuyen to {0}", sett.getKarmaToNuyen());

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#canChangeCount(de.rpgframework.genericrpg.items.CarriedItem, int)
	 */
	@Override
	public boolean canChangeCount(CarriedItem<ItemTemplate> item, int newCount) {
//		if (!item.getResolved().isCountable()) return false;

		return true;
	}


	//-------------------------------------------------------------------
	public void expandPACK(CarriedItem<ItemTemplate> item) {
		logger.log(Level.ERROR, "TODO: expandPACK");
		for (Modification mod : item.getOutgoingModifications() ) {
			logger.log(Level.ERROR, "Expand "+mod.getClass()+" = "+mod);
			ApplyModificationsGeneric.applyModification(getModel(), mod);
		}
		for (Modification mod : item.getIncomingModifications() ) {
			logger.log(Level.ERROR, "Expand "+mod.getClass()+" = "+mod);
			ApplyModificationsGeneric.applyModification(getModel(), mod);
		}

		// Move all virtual items from this pack to normal items
		for (CarriedItem<ItemTemplate> virt : getModel().getVirtualCarriedItems()) {
//			logger.log(Level.INFO, "Check "+virt+" from "+virt.getInjectedBy()+" or "+item);
			if (virt.getInjectedBy()==item.getResolved()) {
				getModel().removeVirtualCarriedItem(virt);
				getModel().addCarriedItem(virt);
			} else {
				logger.log(Level.INFO, "Keep "+virt+" from "+virt.getInjectedBy());
			}
		}
		//System.exit(1);
	}

}
