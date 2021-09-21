package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author stefa
 *
 */
public class SR6PriorityTableController extends PriorityTableController<Shadowrun6Character, SR6PrioritySettings> {

	//-------------------------------------------------------------------
	public SR6PriorityTableController(PriorityCharacterGenerator parent) {
		super(parent, SR6PrioritySettings.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#roll()
	 */
	@Override
	public void roll() {
		// TODO Auto-generated method stub
		
	}

}
