package de.rpgframework.shadowrun6.chargen.lvl;

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

}
