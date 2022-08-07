package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6AdeptPowerLeveller extends SR6AdeptPowerController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6AdeptPowerLeveller(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			todos.clear();
			allocatePP();
			
			Shadowrun6Character model = getModel();
			
			for (AdeptPowerValue val : model.getAdeptPowers()) {
				// Apply modifications
				unprocessed.addAll(val.getModifications());
			}
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
