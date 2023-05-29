package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6FreeAdeptPowerGenerator extends SR6AdeptPowerController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6FreeAdeptPowerGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController#canBuyPowerPoints()
	 */
	@Override
	public boolean canBuyPowerPoints() {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#canIncreasePowerPoints()
	 */
	@Override
	public boolean canIncreasePowerPoints() {
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#canDecreasePowerPoints()
	 */
	@Override
	public boolean canDecreasePowerPoints() {
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#increasePowerPoints()
	 */
	@Override
	public boolean increasePowerPoints() {
		AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(ShadowrunAttribute.POWER_POINTS);
		if (val==null) {
			val = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.POWER_POINTS);
			getModel().setAttribute(val);
		}
		val.setDistributed( val.getDistributed() +1);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController#decreasePowerPoints()
	 */
	@Override
	public boolean decreasePowerPoints() {
		AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(ShadowrunAttribute.POWER_POINTS);
		if (val==null) {
			return false;
		}
		val.setDistributed( val.getDistributed() -1);
		if (val.getDistributed()<1)
			val.setDistributed(0);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		try {

			return previous;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
