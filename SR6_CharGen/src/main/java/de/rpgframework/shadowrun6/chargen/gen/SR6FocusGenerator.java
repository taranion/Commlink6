package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CommonFocusController;

/**
 *
 */
public class SR6FocusGenerator extends SR6CommonFocusController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6FocusGenerator(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		unprocessed = super.process(unprocessed);

		Shadowrun6Character model = getModel();
		for (FocusValue focus : model.getFoci()) {
			int karma = focus.getCostKarma();
			logger.log(Level.INFO,"Pay "+karma+" Karma for force "+focus.getLevel()+" focus "+focus);
			model.setKarmaFree(model.getKarmaFree()-karma);
		}

		return unprocessed;
	}

}
