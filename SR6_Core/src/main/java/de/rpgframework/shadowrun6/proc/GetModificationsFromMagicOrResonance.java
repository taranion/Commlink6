package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class GetModificationsFromMagicOrResonance implements ProcessingStep {
	
	protected static final Logger logger = System.getLogger(GetModificationsFromMagicOrResonance.class.getPackageName());
	
	private Shadowrun6Character model;
	
	//-------------------------------------------------------------------
	public GetModificationsFromMagicOrResonance(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE,"ENTER: process");
		try {
			// Apply modifications by Magic Or Resonance
			if (model.getMagicOrResonanceType()!=null) {
				logger.log(Level.DEBUG,"Apply modifications from MagicOrResonance "+model.getMagicOrResonanceType().getId());
				unprocessed.addAll(model.getMagicOrResonanceType().getOutgoingModifications());
			}
		} finally {
			logger.log(Level.TRACE,"LEAVE: process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
