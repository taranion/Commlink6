package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;

/**
 * @author prelle
 *
 */
public class SR6PointBuyAdeptPowerGenerator extends SR6AdeptPowerGenerator {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6PointBuyAdeptPowerGenerator(SpliMoCharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public boolean canIncreasePowerPoints() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		
		return false;
	}

	//-------------------------------------------------------------------
	public boolean canDecreasePowerPoints() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		return settings.boughtPP>0;
	}

	//-------------------------------------------------------------------
	public boolean increasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	public boolean decreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		try {
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			logger.log(Level.INFO, "Start with {0} character points", settings.characterPoints);
			
			
			
			return previous;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
