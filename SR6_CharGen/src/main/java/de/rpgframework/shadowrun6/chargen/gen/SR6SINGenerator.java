package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonSINController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6SINGenerator extends CommonSINController {

	private final static Logger logger = System.getLogger(SR6SINGenerator.class.getPackageName());

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6SINGenerator(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
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

			for (Modification tmp : previous) {
				unprocessed.add(tmp);
			}

			// If a real SIN exists, but isn't auto-added anymore, remove it
			if (real!=null && real.getInjectedBy()==null) {
				getModel().removeSIN(real);
				logger.log(Level.INFO, "removed REAL sin");
			}

			// Now pay SINs
			for (SIN sin : getModel().getSINs()) {
				if (sin.getQuality()==FakeRating.REAL_SIN)
					continue;
				int cost = sin.getQualityValue() * 2500;
				logger.log(Level.INFO, "Pay {0} nuyen for rating {1} SIN: {2}", cost, sin.getQualityValue(), sin.getName());
				getModel().setNuyen( getModel().getNuyen() - cost);
			}

		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		return unprocessed;
	}

}
