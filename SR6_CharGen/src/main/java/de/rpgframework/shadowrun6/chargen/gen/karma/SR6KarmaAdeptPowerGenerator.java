package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6KarmaAdeptPowerGenerator extends SR6AdeptPowerController {

	private int maxPP;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6KarmaAdeptPowerGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#canBuyPowerPoints()
	 */
	@Override
	public boolean canBuyPowerPoints() {
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#canIncreasePowerPoints()
	 */
	@Override
	public boolean canIncreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#canDecreasePowerPoints()
	 */
	@Override
	public boolean canDecreasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#increasePowerPoints()
	 */
	@Override
	public boolean increasePowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#decreasePowerPoints()
	 */
	@Override
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
			SR6KarmaSettings settings = getModel().getCharGenSettings(SR6KarmaSettings.class);

			int finalMagic = getModel().getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue(ValueType.NATURAL);
			if (getModel().getMagicOrResonanceType()!=null && getModel().getMagicOrResonanceType().usesPowers()) {
				getModel().getAttribute(ShadowrunAttribute.POWER_POINTS).setDistributed(finalMagic);
				freePoints = finalMagic;
			} else {
				freePoints = 0;
				getModel().getAttribute(ShadowrunAttribute.POWER_POINTS).setDistributed(0);
			}

			// Distribute
			previous = super.process(previous);

			return previous;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
