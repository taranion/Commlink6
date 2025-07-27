package de.rpgframework.shadowrun6.chargen.gen.free;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AnimalismController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * 
 */
public class SR6FreeAnimalismController extends SR6AnimalismController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 * @param isCharGen
	 */
	public SR6FreeAnimalismController(SR6CharacterController parent, boolean isCharGen) {
		super(parent, isCharGen);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(MetamagicOrEcho value, Decision... decisions) {
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(MetamagicOrEchoValue value) {
		return Possible.TRUE;
	}
}
