package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.ResetGenerator;

public class SR6LifePathResetGenerator extends ResetGenerator {

	//-------------------------------------------------------------------
	public SR6LifePathResetGenerator(CommonSR6CharacterGenerator charGen) {
		super(charGen);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.ResetGenerator#process(List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		unprocessed = super.process(unprocessed);

		Shadowrun6Character model = charGen.getModel();
		model.setKarmaFree(50);
		model.setKarmaInvested(0);
		SR6LifePathSettings settings = model.getCharGenSettings(SR6LifePathSettings.class);
		for (QualityValue val : new ArrayList<QualityValue>( model.getQualities())) {
			model.removeQuality(val);
		}
		for (SR6SkillValue val : new ArrayList<SR6SkillValue>( model.getSkillValues())) {
			val.clearIncomingModifications();
		}
//		settings.characterPoints = 100;
//		settings.cpBoughtAttrib = 0;
//		settings.cpBoughtSpecial = 0;
//		settings.cpToResources = 0;
//		settings.cpToSkills = 0;
		logger.log(Level.DEBUG,"Reset CP");

		return unprocessed;
	}

}
