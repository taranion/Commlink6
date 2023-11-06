package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.QualityPathStepValue;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class GetModificationsFromQualityPaths implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsFromQualityPaths.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsFromQualityPaths(Shadowrun6Character model) {
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
			for (QualityPathValue qpVal : model.getQualityPaths()) {
				for (QualityPathStepValue stepVal : qpVal.getStepsTaken()) {
					if (stepVal.getResolved()==null) {
						logger.log(Level.ERROR, "Quality path ''{0}'' has unknown step ''{1}''", qpVal.getKey(), stepVal.getKey());
						BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Quality path '"+qpVal.getKey()+"' has unknown step '"+stepVal.getKey()+"'");
						continue;
					}
					for (Modification mod : stepVal.getResolved().getOutgoingModifications()) {
						logger.log(Level.DEBUG, "Quality path ''{0}'' step ''{1}'' adds: {2}", qpVal.getKey(), stepVal.getKey(),mod);
						unprocessed.add(mod);
					}
				}
			}

		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with {0} modifications still to process",unprocessed.size());
		}
		return unprocessed;
	}

}
