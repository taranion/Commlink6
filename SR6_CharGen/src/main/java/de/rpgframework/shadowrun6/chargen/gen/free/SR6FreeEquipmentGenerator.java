package de.rpgframework.shadowrun6.chargen.gen.free;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonEquipmentGenerator;
import de.rpgframework.shadowrun6.items.ItemTemplate;

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
