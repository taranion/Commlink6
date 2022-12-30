package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;

/**
 * @author prelle
 *
 */
public class SR6EquipmentGenerator extends CommonEquipmentGenerator {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6EquipmentGenerator(SR6CharacterController parent) {
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
			carried = GearTool.buildItem(value, mode, variant, getModel(), false, decisions);
		} else {
			carried = GearTool.buildItem(value, mode, getModel(), false, decisions);
		}

		// Check availability
		if (carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY) != null) {
			Availability avail = carried.get().getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue();
			if (avail!=null && avail.getValue() >= 7) {
				boolean allowLegal = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ALLOW_LEGAL_AVAIL7PLUS);
				if (!allowLegal || avail.getLegality()!=Legality.LEGAL) {
					return new Possible(Possible.State.IMPOSSIBLE, Severity.STOPPER,SR6CharacterGenerator.RES, IRejectReasons.IMPOSS_AVAILABLE_TOO_HIGH, avail.getValue());
				}
			}
		}

		return poss;
	}

}
