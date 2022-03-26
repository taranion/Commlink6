package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.Shadowrun6Rules.PRIORITY_MAGIC;

/**
 * @author prelle
 *
 */
public class SR6AdeptPowerGenerator extends SR6AdeptPowerController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6AdeptPowerGenerator(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	protected int determineMaxFreePoints() {
		Shadowrun6Character model = getModel();
		logger.log(Level.INFO, "MOR = "+model.getMagicOrResonanceType());
		if (model.getMagicOrResonanceType()==null) 
			return 0;
		if (!model.getMagicOrResonanceType().usesPowers()) 
			return 0;
		
		int ret = 0;
		// TODO: Check essence
		logger.log(Level.WARNING, "TODO: Handle lowered essence");
		//if (model.getAttribute(ShadowrunAttribute.ESSENCE))
		
		PRIORITY_MAGIC chosen = model.getRuleValueAsEnum(Shadowrun6Rules.CHARGEN_PRIO_ADEPT_PP);
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);

		// Regular adepts get free power points matching their magic attribute
		if (!model.getMagicOrResonanceType().paysPowers()) {
			switch (chosen) {
			case MAGIC_PLUS_KARMA:
				ret += settings.perAttrib.get(ShadowrunAttribute.MAGIC).points3;
				// Now add priority with next case
			case PRIO_MAGIC:
				switch (settings.priorities.get(PriorityType.MAGIC)) {
				case A: ret+=4; break;
				case B: ret+=3; break;
				case C: ret+=2; break;
				case D: ret+=1; break;
				case E: ret+=0; break;
				}
				break;
			case FINAL_MAGIC:
				ret = model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
			}
			
			logger.log(Level.INFO, "Regular adept - get {0,number,integer} power points from MAGIC", ret);
			return ret;
		}
		if (model.getMagicOrResonanceType().paysPowers()) {
			ret += settings.mysticAdeptPowerPoints;
			logger.log(Level.INFO, "Mystic adept - get {0,number,integer} power points from MAGIC", ret);

			return ret;
		}
		
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			todos.clear();
			allocatePP();
			
			Shadowrun6Character model = getModel();
			
			for (AdeptPowerValue val : model.getAdeptPowers()) {
				// Apply modifications
				unprocessed.addAll(val.getModifications());
			}
			
			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} remaining power points", freePoints);
			if (freePoints>0) {
				todos.add(new ToDoElement(Severity.WARNING, "Unused power points"));
			} else if (freePoints<0) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many power points used"));
			}
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
