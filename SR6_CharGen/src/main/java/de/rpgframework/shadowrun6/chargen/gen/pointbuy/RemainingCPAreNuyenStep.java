package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class RemainingCPAreNuyenStep implements ProcessingStep {

	protected static Logger logger = System.getLogger(RemainingCPAreNuyenStep.class.getPackageName()+".remain");

	protected SR6CharacterController parent;

	//-------------------------------------------------------------------
	/**
	 */
	public RemainingCPAreNuyenStep(SR6CharacterController parent) {
		this.parent = parent;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
		logger.log(Level.ERROR, "End with {0} character points", settings.characterPoints);

		settings.cpToResources = settings.characterPoints;
		int baseNuyen = 0;
		if (parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ERRATED_POINT_BUY)) {
			baseNuyen = Math.min(450000, settings.characterPoints*20000);
		} else {
			baseNuyen = 10000 + Math.min(440000, settings.characterPoints*20000);
		}
		logger.log(Level.INFO, "With {0} CP we start with {1} nuyen", settings.cpToResources, baseNuyen);
		unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), baseNuyen));

		return unprocessed;
	}

}
