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
public class GetGearDefinitions implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetGearDefinitions.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetGearDefinitions(Shadowrun6Character model) {
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
			// From metatypes
			if (model.getMetatype()!=null && model.getMetatype().getGearDefinitions()!=null) {
				model.getMetatype().getGearDefinitions().forEach( gearDef -> model.addGearDefinition(gearDef));
			}
		} finally {
			logger.log(Level.TRACE,  "STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
