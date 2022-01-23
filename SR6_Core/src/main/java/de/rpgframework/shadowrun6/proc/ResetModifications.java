package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class ResetModifications implements ProcessingStep {
	
	private final static Logger logger = System.getLogger(ResetModifications.class.getPackageName());
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public ResetModifications(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");

		try {
			// Remove all auto-qualities or quality levels
			for (QualityValue val : new ArrayList<>(model.getQualities())) {
				boolean remove = val.isRemoveOnReset();
				val.clearModifications();
				if (remove)
					model.removeQuality(val);
			}
			
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
