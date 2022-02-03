package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;

public class ResetGenerator implements ProcessingStep {

	protected final static Logger logger = System.getLogger(ResetGenerator.class.getPackageName()+".reset");

	protected CommonSR6CharacterGenerator charGen;
	
	//-------------------------------------------------------------------
	public ResetGenerator(CommonSR6CharacterGenerator charGen) {
		this.charGen = charGen;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// Reset all attributes
		Shadowrun6Character model = charGen.getModel();
		for (AttributeValue<ShadowrunAttribute> val : model.getAttributes()) {
			val.clearModifications();
		}
		
		model.setKarmaFree(50);
		model.setKarmaInvested(0);
		PowerLevel level = null;
		if (charGen instanceof LifePathCharacterGenerator) {
			SR6LifePathSettings settings = model.getCharGenSettings(SR6LifePathSettings.class);
			level = settings.variant;
		} else if (charGen instanceof PointBuyCharacterGenerator) {
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			level = settings.variant;
		} else {
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			level = settings.variant;
			if (level==PowerLevel.PRIME_RUNNER)
				model.setKarmaFree(100);
		}
		
		return unprocessed;
	}

}
