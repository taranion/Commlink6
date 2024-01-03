package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonSINController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6SINLeveller extends CommonSINController {

	private final static Logger logger = System.getLogger(SR6SINLeveller.class.getPackageName());

	//-------------------------------------------------------------------
	public SR6SINLeveller(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	@Override
	public void roll() {
		logger.log(Level.ERROR, "roll() not implemented");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");

		try {
			todos.clear();
			boolean autoAddedRealSIN = false;
			// Search for an eventually existing real SIN
			// it will be deleted, if there is no modification for it
			SIN real = null;
			for (SIN s : getModel().getSINs()) {
				if (s.getQuality()==FakeRating.REAL_SIN) {
					if (s.getInjectedBy()!=null) autoAddedRealSIN = true;
					real = s; break;
				}
			}

			// If a real SIN exists, but isn't auto-added anymore, remove it
			if (real!=null && !autoAddedRealSIN) {
				getModel().removeSIN(real);
				logger.log(Level.WARNING, "removed REAL sin");
			}

		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.SINController#createNewSIN(java.lang.String, de.rpgframework.shadowrun.SIN.FakeRating, int)
	 */
	@Override
	public SIN[] createNewSIN(FakeRating quality, int count) {
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int cost = quality.getValue() * 2500 * count;
			logger.log(Level.INFO, "Pay {0} nuyen for {2} rating {1} SIN", cost, quality.getValue(), count);
			getModel().setNuyen( getModel().getNuyen() - cost);
		} else {
			logger.log(Level.INFO, "CAREER_PAY_GEAR is not active");
		}

		return super.createNewSIN(quality, count);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.SINController#createNewSIN(java.lang.String, de.rpgframework.shadowrun.SIN.FakeRating)
	 */
	@Override
	public SIN createNewSIN(String name, FakeRating quality) {
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int cost = quality.getValue() * 2500;
			logger.log(Level.INFO, "Pay {0} nuyen for rating {1} SIN", cost, quality.getValue());
			getModel().setNuyen( getModel().getNuyen() - cost);
		} else {
			logger.log(Level.INFO, "CAREER_PAY_GEAR is not active");
		}

		return super.createNewSIN(name, quality);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.SINController#createNewLicense(de.rpgframework.shadowrun.LicenseType, de.rpgframework.shadowrun.SIN, de.rpgframework.shadowrun.SIN.FakeRating, java.lang.String)
	 */
	@Override
	public LicenseValue createNewLicense(SIN sin, FakeRating quality, String name) {
		boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
		if (payGear) {
			int cost = quality.getValue() * 200;
			logger.log(Level.INFO, "Pay {0} nuyen for rating {1} license", cost, quality.getValue());
			getModel().setNuyen( getModel().getNuyen() - cost);
		} else {
			logger.log(Level.INFO, "CAREER_PAY_GEAR is not active");
		}

		return super.createNewLicense(sin, quality, name);
	}

}
