package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
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
				logger.log(Level.WARNING, "TODO  add from technique "+techn.getId()+" / "+ref);
				// Calculate modifications
				ref.clearIncomingModifications();
//				for (Modification mod : ref.getTechnique().getModifications()) {
//					Modification realMod = ShadowrunTools.instantiateModification(mod, ref.getChoice(), 0);
//					logger.log(Level.DEBUG, "  instantiated mod "+realMod);
//					ref.addModification(realMod);
//				}
//				
//				
//				if (ref.getModifications()!=null && !ref.getModifications().isEmpty()) {
//					logger.log(Level.DEBUG, " - "+ref.getTechnique().getId()+" has modifications: "+ref.getModifications());
//					for (Modification mod : ref.getModifications()) {
//						mod.setSource(ref.getTechnique());
//					}
////					logger.log(Level.DEBUG, " - add modifications: "+ref.getModifications());
//					unprocessed.addAll(ref.getModifications());
//				}
			}
		} finally {
			logger.log(Level.TRACE,  "STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
