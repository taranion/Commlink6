package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class ResetModifications implements ProcessingStep {
	
	private final static Logger logger = System.getLogger(ResetModifications.class.getPackageName());
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public ResetModifications(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		System.err.println("ResetModifications");

		try {
			// Attributes
			for (AttributeValue<ShadowrunAttribute> val : model.getAttributes()) {
				val.clearModifications();
			}
			
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
			if (aVal==null) {
				aVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.PHYSICAL_MONITOR);
				model.setAttribute(aVal);
			}
			aVal.setDistributed(8);
			aVal.clearModifications();
			
			// Skills
			for (SR6SkillValue val : model.getSkillValues()) {
				val.clearModifications();
			}
			
			
			// Remove all auto-qualities or quality levels
			for (QualityValue val : new ArrayList<>(model.getQualities())) {
				boolean remove = val.isRemoveOnReset();
				val.clearModifications();
				if (remove) {
					logger.log(Level.DEBUG, "Remove quality "+val);
					model.removeQuality(val);
				}
			}
			
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
