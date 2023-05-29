package de.rpgframework.shadowrun6.chargen.gen.free;

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
import de.rpgframework.shadowrun6.chargen.gen.CommonEquipmentGenerator;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;

/**
 * @author prelle
 *
 */
public class SR6FreeEquipmentGenerator extends CommonEquipmentGenerator {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6FreeEquipmentGenerator(SR6CharacterController parent) {
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

		return poss;
	}

}
