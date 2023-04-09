package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;

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
				Shadowrun6Character model = getModel();
				int nuyen = item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
				logger.log(Level.INFO, "Buy {0} for {1} nuyen", value.getId(), nuyen);
				model.setNuyen( model.getNuyen() - nuyen );

				// Pay essence
				if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST)) {
					double essenceCost = item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue();
					int essHole = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE).getModifiedValue();
					if (essHole>0) {
						double essHole2 = (double)essHole / 2.0;
						if (essHole2>essenceCost) {
							// Pay fully by reducing essence hole
							essHole2 -= essenceCost;
							logger.log(Level.INFO, "Fully pay {0} essence by reducing essence hole to {1}", essenceCost, essHole2);
							model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE).setDistributed((int)(essHole2*1000));
							essenceCost = 0;
						} else if (essHole2>0){
							double orig = essenceCost;
							essenceCost -= essHole2;
							logger.log(Level.INFO, "Partially pay {0} essence by reducing essence hole to 0 and pay remaining {1}", orig, essenceCost);
						} else {
							logger.log(Level.INFO, "Pay {0} ", essenceCost);
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
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			boolean success = super.deselect(value);
			if (!success) {
				return false;
			}

			Shadowrun6Character model = getModel();
			int nuyen = value.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			logger.log(Level.INFO, "Sell {0} for {1} nuyen", value.getKey(), nuyen);

			model.setNuyen( model.getNuyen() + nuyen );
			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IEquipmentController#getConvertedKarma()
	 */
	@Override
	public int getConvertedKarma() {
		return 0;
	}

	@Override
	public int getConversionRateKarma() {
		return 0;
	}

	@Override
	public boolean canIncreaseConversion() {
		return false;
	}

	@Override
	public boolean increaseConversion() {
		return false;
	}

	@Override
	public boolean canDecreaseConversion() {
		return false;
	}

	@Override
	public boolean decreaseConversion() {
		return false;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		return unprocessed;
	}

	@Override
	public boolean canChangeCount(CarriedItem<ItemTemplate> item, int newCount) {
		// TODO Auto-generated method stub
		return false;
	}

}
