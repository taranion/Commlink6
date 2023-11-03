package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.SetItemValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class GetModificationsFromCollectives implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsFromCollectives.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsFromCollectives(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			SetItemValue collective = model.getSurgeCollective();
			if (collective==null) {
				return previous;
			}

			// Iterate modifications
			collective.updateOutgoingModificiations(model);
			for (Modification mod : collective.getOutgoingModifications()) {
				logger.log(Level.DEBUG, "Add for ''{0}'' collective: {1}", collective.getKey(), mod);
				unprocessed.add(mod);
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
