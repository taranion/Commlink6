package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger.Level;

import de.rpgframework.genericrpg.data.AttributeValue;
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
		return true;
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
		logger.log(Level.INFO, "Increase power points to {0}", val.getDistributed());

		parent.runProcessors();
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
		logger.log(Level.INFO, "Decrease power points to {0}", val.getDistributed());
		parent.runProcessors();
		return true;
	}

}
