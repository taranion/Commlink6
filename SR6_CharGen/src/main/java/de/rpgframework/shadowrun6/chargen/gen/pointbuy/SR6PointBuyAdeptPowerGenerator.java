package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6PointBuyAdeptPowerGenerator extends SR6AdeptPowerController {

	private int maxPP;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6PointBuyAdeptPowerGenerator(SR6CharacterController parent) {
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
	public boolean canIncreasePowerPoints() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();
		if (type==null) return false;
		if (!type.usesPowers()) return false;

		// Would the new value be below MAGIC
		int magic = getModel().getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
		if (settings.boughtPP>=magic) {
			return false;
		}

		// Can the increase be payed?
		int cpNeeded = (type.paysPowers())?8:4;
		return settings.characterPoints>cpNeeded;
	}

	//-------------------------------------------------------------------
	public boolean canDecreasePowerPoints() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		return settings.boughtPP>0;
	}

	//-------------------------------------------------------------------
	public boolean increasePowerPoints() {
		if (!canIncreasePowerPoints()) {
			logger.log(Level.WARNING, "Trying to increase PP which isn't possible");
			return false;
		}

		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		settings.boughtPP++;
		logger.log(Level.INFO, "Increased power points to {0}", settings.boughtPP);

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	public boolean decreasePowerPoints() {
		if (!canDecreasePowerPoints()) {
			logger.log(Level.WARNING, "Trying to decrease PP which isn't possible");
			return false;
		}

		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		settings.boughtPP--;
		logger.log(Level.INFO, "Decreased power points to {0}", settings.boughtPP);

		parent.runProcessors();
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
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			settings.ppCP=0;
			settings.ppKarma=0;
			logger.log(Level.INFO, "Start with {0} character points", settings.characterPoints);

			getModel().getAttribute(ShadowrunAttribute.POWER_POINTS).setDistributed(settings.boughtPP);
			freePoints = settings.boughtPP;
			MagicOrResonanceType type = getModel().getMagicOrResonanceType();
			if (type!=null) {
				int perPP = (type.paysPowers()?8:4);
				int max = settings.characterPoints / perPP;
				int cp = Math.min(max, settings.boughtPP);
				int karma = settings.boughtPP - cp;

				logger.log(Level.INFO, "Spend {0} CP for {1} Power points", cp*perPP, cp);
				settings.characterPoints -= cp;
				settings.ppCP++;

				logger.log(Level.INFO, "Spend {0} Karma for {1} Power points", karma*5, karma);
				settings.characterPoints -= cp;
				settings.ppKarma++;
			}

			// Distribute
			previous = super.process(previous);

			return previous;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
