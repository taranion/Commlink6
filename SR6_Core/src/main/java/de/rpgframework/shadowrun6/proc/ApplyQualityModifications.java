package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyQualityModifications implements ProcessingStep {
	
	private final static Logger logger = System.getLogger(ApplyQualityModifications.class.getPackageName());
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public ApplyQualityModifications(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	public static void applyModification(Shadowrun6Character model, Modification tmp) {
		if (tmp.getReferenceType()==ShadowrunReference.QUALITY) {
			if (tmp instanceof DataItemModification) {
				DataItemModification mod = (DataItemModification)tmp;
				Quality item = Shadowrun6Core.getItem(Quality.class, mod.getKey());
				QualityValue value = model.getQuality(mod.getKey());
				if (item==null) {
					logger.log(Level.ERROR, "Cannot apply modification "+tmp+" - no such quality {0}", mod.getKey());
				}
				if (value==null) {
					value = new QualityValue(item, 0);
					// Handle decisions
					for (Decision dec : mod.getDecisions()) {
						value.addDecision(dec);
						logger.log(Level.DEBUG, "Add decision {0} to quality {1}", dec, item);
					}
					
					model.addQuality(value);
					logger.log(Level.DEBUG, "Add quality {0} to character", item);
				}
				value.addModification(mod);
			}
		} else {
			throw new IllegalArgumentException("Not a QUALITY modiciation");
		}
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
			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.QUALITY) {
					applyModification(model, tmp);
				} else {
					unprocessed.add(tmp);
				}
			}
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
