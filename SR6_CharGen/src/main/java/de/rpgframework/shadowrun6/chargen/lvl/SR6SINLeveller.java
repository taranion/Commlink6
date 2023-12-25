package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonSINController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

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
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();
		try {
			todos.clear();
			// Search for an eventually existing real SIN
			// it will be deleted, if there is no modification for it
			SIN real = null;
			for (SIN s : getModel().getSINs()) {
				if (s.getQuality()==FakeRating.REAL_SIN) {
					real = s; break;
				}
			}

			boolean autoAddedRealSIN = false;
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.SIN) {
					DataItemModification mod = (DataItemModification)tmp;
					logger.log(Level.DEBUG, "process "+mod);
					FakeRating qual = mod.getResolvedKey();

					// Re-use an eventually existing REAL_SIN
					if (qual==FakeRating.REAL_SIN) {
						autoAddedRealSIN = true;
						if (real==null) {
							real = new SIN(qual);
							real.setName(getModel().getName());
							logger.log(Level.INFO, "add REAL sin");
							getModel().addSIN(real);
						} else {
							logger.log(Level.INFO, "keep existing REAL sin");
						}
					}
				} else
					unprocessed.add(tmp);
			}

			// If a real SIN exists, but isn't auto-added anymore, remove it
			if (real!=null && !autoAddedRealSIN) {
				getModel().removeSIN(real);
				logger.log(Level.INFO, "removed REAL sin");
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
