package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.TechniqueValue;

/**
 * @author prelle
 *
 */
public class GetModificationsFromTechniques implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsFromTechniques.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsFromTechniques(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE,  "START: process");
		try {
			// Calculate effective modifications from available techniques
			for (TechniqueValue ref :model.getTechniquesAll()) {
				Technique techn = ref.getResolved();
				logger.log(Level.DEBUG, "TODO  add from technique "+techn.getId()+" / "+ref);
				// Calculate modifications
				ref.clearIncomingModifications();
				for (Modification mod : techn.getOutgoingModifications()) {
					logger.log(Level.ERROR, "TODO  from technique "+techn.getId()+" / "+mod);
					Modification realMod = mod.getReferenceType().instantiateModification(mod, ref, 1, model);
					realMod.setSource(ref);
					logger.log(Level.INFO, "--item {0}: realMod={1} ", ref.getKey(), realMod);
					unprocessed.add(realMod);
				}
			}
		} finally {
			logger.log(Level.TRACE,  "STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
