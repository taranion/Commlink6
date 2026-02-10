package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6EquipmentLeveller extends CommonEquipmentController implements ISR6EquipmentController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6EquipmentLeveller(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
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
//		if (carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY) != null) {
//			Availability avail = carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue();
//			if (avail!=null && avail.getValue() >= 7) {
//				boolean allowLegal = getModel().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ALLOW_LEGAL_AVAIL7PLUS);
//				if (!allowLegal || avail.getLegality()!=Legality.LEGAL) {
//					return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,SR6CharacterGenerator.RES, IRejectReasons.IMPOSS_AVAILABLE_TOO_HIGH, avail.getValue());
//				}
//			}
//		}

		return poss;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#select(ItemTemplate, String, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, String variantID, CarryMode mode, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1}", value, mode);
		try {
			OperationResult<CarriedItem<ItemTemplate>> result = super.select(value, variantID, mode, decisions);
			if (result.wasSuccessful()) {
				CarriedItem<ItemTemplate> item = result.get();

				ValueModification logMod = new ValueModification(ShadowrunReference.CARRIED, value.getId(), 0);
				logMod.setId(item.getUuid());
				logMod.setDate(new Date());

				Shadowrun6Character model = getModel();
				boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
				if (payGear) {
					int nuyen = item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
					logger.log(Level.INFO, "Buy {0} for {1} nuyen", value.getId(), nuyen);
					model.setNuyen( model.getNuyen() - nuyen );
					logMod.setValue(nuyen);
				}

				Shadowrun6Tools.recordEssenceChange(model, item);
				model.addToHistory(logMod);

				// Pay essence
				if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST)) {
					double essenceCost = item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue();
					logger.log(Level.ERROR, "Need to pay {0} essence ", essenceCost);
					int toPay = (int)(essenceCost *1000);
					int essHole = model.getEssenceHoleUnused();
					if (essHole>0) {
						if (essHole>toPay) {
							// Pay fully by reducing essence hole
							model.setEssenceHoleUnsed( essHole - toPay);
							logger.log(Level.ERROR, "Fully pay {0} essence by reducing essence hole to {1}", essenceCost, model.getEssenceHoleUnused());
						} else {
							// Partial pay
							toPay -= model.getEssenceHoleUnused();
							model.setEssenceHoleUnsed(0);
							logger.log(Level.ERROR, "Partially pay {0} essence by reducing essence hole to 0 and pay remaining {1}", toPay, essenceCost);
						}
					}
				}

				parent.runProcessors();
			}

			return result;
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {1}", value, mode);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController#embed(CarriedItem, ItemHook, ItemTemplate, Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, String variantID, Decision... decisions) {
		int oldValue = container.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		OperationResult<CarriedItem<ItemTemplate>> after = super.embed(container, slot, value, variantID, decisions);
		if (after.hasError())
			return after;

		ValueModification logMod = new ValueModification(ShadowrunReference.CARRIED, value.getId(), 0);
		logMod.setId(after.get().getUuid());
		logMod.setDate(new Date());
		
		Shadowrun6Character model = getModel();
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int newValue = container.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			int nuyen = newValue - oldValue;
			logger.log(Level.INFO, "Buy {0} for {1} nuyen", value.getId(), nuyen);
			model.setNuyen( model.getNuyen() - nuyen );
			logMod.setValue(nuyen);
		}

		model.addToHistory(logMod);
		parent.runProcessors();
		
		return after;
	}

	//-------------------------------------------------------------------
	public Possible removeEmbedded(CarriedItem<ItemTemplate> container, ItemHook slot, CarriedItem<ItemTemplate> toRemove) {
		int oldValue = container.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		Possible after = super.removeEmbedded(container, slot, toRemove);
		if (!after.get())
			return after;

		ValueModification logMod = new ValueModification(ShadowrunReference.CARRIED, toRemove.getKey(), 0);
		logMod.setId(toRemove.getUuid());
		logMod.setDate(new Date());
		
		Shadowrun6Character model = getModel();
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int newValue = container.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			int nuyen = oldValue - newValue;
			logger.log(Level.INFO, "Sell {0} for {1} nuyen", toRemove.getKey(), nuyen);
			model.setNuyen( model.getNuyen() - nuyen );
			logMod.setValue(nuyen);
		}

		model.addToHistory(logMod);
		parent.runProcessors();
		
		return after;
	}

	//-------------------------------------------------------------------
	private ValueModification getModificationFromEssenceChange(CarriedItem<ItemTemplate> value) {
		for (ValueModification mod : getModel().getEssenceChanges()) {
			if (mod.getId()!=null && mod.getId().equals(value.getUuid())) {
				return mod;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value, RemoveMode mode) {
		logger.log(Level.INFO, "deselect {0} via {1}", value, mode);
		boolean success = super.deselect(value, mode);
		if (success) {
			if (mode==RemoveMode.UNDO) {
				Shadowrun6Character model = getModel();
				int nuyen = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
				logger.log(Level.INFO, "Sell {0} for {1} nuyen", value.getKey(), nuyen);

				model.setNuyen( model.getNuyen() + nuyen );

				// Remove the history modification
				for (Modification mod : model.getHistory()) {
					if (mod instanceof DataItemModification) {
						if (((DataItemModification)mod).getId()==null) {
							logger.log(Level.ERROR, "The character history has a modification without UUID: {0}", mod);
							continue;
						}

						if (((DataItemModification)mod).getId().equals(value.getUuid())) {
							model.getHistory().remove(mod);
							break;
						}
					}
				}
			}

			parent.runProcessors();

		}
		return success;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value) {
		return deselect(value, RemoveMode.UNDO);
//		logger.log(Level.TRACE, "ENTER deselect({0})", value);
//		try {
//			boolean success = super.deselect(value);
//			if (!success) {
//				return false;
//			}
//
//			Shadowrun6Character model = getModel();
//			int nuyen = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
//			logger.log(Level.INFO, "Sell {0} for {1} nuyen", value.getKey(), nuyen);
//
//			model.setNuyen( model.getNuyen() + nuyen );
//
//			// Eventually handle essence
//			if (value.hasAttribute(SR6ItemAttribute.ESSENCECOST)) {
//				ItemAttributeFloatValue<SR6ItemAttribute> val = value.getAsFloat(SR6ItemAttribute.ESSENCECOST);
//				logger.log(Level.WARNING, "\n\n\n\nAdd an essence hole of {0}", val.getModifiedValue());
//				// Add to essence hole
//				model.setEssenceHoleUnsed((int)val.getModifiedValue()*1000);
//			}
//
//			parent.runProcessors();
//			return true;
//		} finally {
//			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#getConvertedKarma()
	 */
	@Override
	public int getConvertedKarma() {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#getConversionRateKarma()
	 */
	@Override
	public int getConversionRateKarma() {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#canIncreaseConversion()
	 */
	@Override
	public boolean canIncreaseConversion() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#increaseConversion()
	 */
	@Override
	public boolean increaseConversion() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#canDecreaseConversion()
	 */
	@Override
	public boolean canDecreaseConversion() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#decreaseConversion()
	 */
	@Override
	public boolean decreaseConversion() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#canChangeCount(de.rpgframework.genericrpg.items.CarriedItem, int)
	 */
	@Override
	public boolean canChangeCount(CarriedItem<ItemTemplate> item, int newCount) {
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> increase(CarriedItem<ItemTemplate> value) {
		SR6GearTool.recalculate(null, getModel(), value);
		int oldValue = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
		OperationResult<CarriedItem<ItemTemplate>> after = super.increase(value);
		if (after.hasError())
			return after;

		ValueModification logMod = new ValueModification(ShadowrunReference.CARRIED, value.getKey(), 0);
		logMod.setId(after.get().getUuid());
		logMod.setDate(new Date());
		
		Shadowrun6Character model = getModel();
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int newValue = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			int nuyen = newValue - oldValue;
			logger.log(Level.WARNING, "Old {0}   New {1} Diff {2}", oldValue, newValue, nuyen);
			logger.log(Level.INFO, "Increasing {0} for {1} nuyen", value.getKey(), nuyen);
			model.setNuyen( model.getNuyen() - nuyen );
			logMod.setValue(nuyen);
		}

		model.addToHistory(logMod);
		parent.runProcessors();
		
		return after;		
	}

}
