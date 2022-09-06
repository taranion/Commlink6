package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;

/**
 * @author prelle
 *
 */
public class GetModificationsFromQualities implements ProcessingStep {
	
	protected static final Logger logger = System.getLogger(GetModificationsFromQualities.class.getPackageName()+".quality");
	
	private ShadowrunCharacter<?,?,?,?> model;
	
	//-------------------------------------------------------------------
	public GetModificationsFromQualities(ShadowrunCharacter<?,?,?,?> model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE, "ENTER: process");
		try {
			
			for (QualityValue val : model.getQualities()) {
				logger.log(Level.DEBUG, "Apply modifications from quality {0} = {1}", val.getNameWithRating(), val.getEffectiveModifications(model));
				unprocessed.addAll(val.getEffectiveModifications(model));
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
