package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6PriorityCalculateMaxPowerPointsStep implements ProcessingStep {

	protected static Logger logger = System.getLogger(SR6PriorityCalculateMaxPowerPointsStep.class.getPackageName()+".adept");

	private SR6CharacterController parent;

	//-------------------------------------------------------------------
	public SR6PriorityCalculateMaxPowerPointsStep(SR6CharacterController parent) {
		this.parent = parent;
	}

	//-------------------------------------------------------------------
	protected int determineMaxFreePoints() {
		Shadowrun6Character model = parent.getModel();
		logger.log(Level.INFO, "MOR = "+model.getMagicOrResonanceType());
		if (model.getMagicOrResonanceType()==null)
			return 0;
		if (!model.getMagicOrResonanceType().usesPowers())
			return 0;

		int ret = 0;
		// TODO: Check essence
		logger.log(Level.WARNING, "TODO: Handle lowered essence");
		//if (model.getAttribute(ShadowrunAttribute.ESSENCE))

		boolean adjustedMagic =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_PRIO_ADJUSTED_MAGIC_RESO);
		boolean autoPPKarma =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP);
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		PerAttributePoints mag = settings.perAttrib.get(ShadowrunAttribute.MAGIC);
		logger.log(Level.WARNING, "MAGIC "+mag);

		// Regular adepts get free power points matching their magic attribute
		if (!model.getMagicOrResonanceType().paysPowers()) {
			ret = mag.getSum();
			logger.log(Level.INFO, "Regular adept - get {0,number,integer} power points from MAGIC", ret);
			logger.log(Level.INFO, "per={0}  useAdjustedMagic={1}  autoPPForKarma={2}", mag, adjustedMagic, autoPPKarma);
			return ret;
		}
		if (model.getMagicOrResonanceType().paysPowers()) {
			logger.log(Level.INFO, "per={0}  useAdjustedMagic={1}  autoPPForKarma={2}", mag, adjustedMagic, autoPPKarma);
			// Mystic adepts
			switch (settings.priorities.get(PriorityType.MAGIC)) {
			case A: ret=4; break;
			case B: ret=3; break;
			case C: ret=2; break;
			case D: ret=1; break;
			case E: ret=0; break;
			}
			logger.log(Level.INFO, "Mystic adept - may split {0} MAG from priority", ret);
			if (adjustedMagic) {
				logger.log(Level.INFO, "Mystic adept - add {0} to MAG from adjustment points (House Rule)", mag.points1);
				ret += mag.points1;
			}
			// Set the theoretical maximum
			settings.mysticAdeptMaxPoints = ret;

			if (settings.getMagicForPP()<ret) {
				logger.log(Level.INFO, "Mystic adept - could have up to {0} points, but chose only to have {1} (in favor of spells)", ret, settings.getMagicForPP());
				ret = settings.getMagicForPP();
			} else {
				logger.log(Level.INFO, "Mystic adept - get {0,number,integer} power points from MAGIC", ret);
			}
			settings.setMagicForPP(ret);

			// (DE) Rule to grant +1 PP when MAG is raised with Karma
			if (autoPPKarma) {
				logger.log(Level.INFO, "Mystic adept - add {0} to MAG from Karma adjustments (House Rule)", mag.points3);
				ret += mag.points3;
			}

			return ret;
		}

		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		int max = determineMaxFreePoints();
		logger.log(Level.INFO, "Can spend {0} power points", max);
		AttributeValue<ShadowrunAttribute> val = parent.getModel().getAttribute(ShadowrunAttribute.POWER_POINTS);
		val.setDistributed(max);

		return unprocessed;
	}

}
