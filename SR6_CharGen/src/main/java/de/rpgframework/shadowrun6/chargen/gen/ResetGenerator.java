package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;

public class ResetGenerator implements ProcessingStep {

	private final static Logger logger = LogManager.getLogger(ResetGenerator.class.getPackageName());

	private Shadowrun6Character model;
	
	public ResetGenerator(CommonSR6CharacterGenerator charGen) {
		model = charGen.getModel();
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// Reset all attributes
		for (AttributeValue<ShadowrunAttribute> val : model.getAttributes()) {
			logger.debug("Reset "+val);
			val.clearModifications();
		}
		
		return unprocessed;
	}

}
