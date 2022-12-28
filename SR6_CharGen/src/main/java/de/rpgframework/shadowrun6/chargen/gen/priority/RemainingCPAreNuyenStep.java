package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class RemainingCPAreNuyenStep implements ProcessingStep {
	
	protected static Logger logger = System.getLogger(RemainingCPAreNuyenStep.class.getPackageName());
	
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
		logger.log(Level.INFO, "End with {0} character points", settings.characterPoints);
		
		settings.cpToResources = settings.characterPoints;
		int baseNuyen = 10000 + settings.characterPoints*20000;
		logger.log(Level.INFO, "With {0} CP we start with {1} nuyen", settings.cpToResources, baseNuyen);
		unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), baseNuyen));

		return unprocessed;
	}

}
