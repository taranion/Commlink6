package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPower.Activation;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author prelle
 *
 */
public class GetModificationsFromPowers implements ProcessingStep {
	
	protected static final Logger logger = System.getLogger(GetModificationsFromPowers.class.getPackageName()+".adeptpower");
	
	private Shadowrun6Character model;
	
	//-------------------------------------------------------------------
	public GetModificationsFromPowers(Shadowrun6Character model) {
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
			// Calculate effective modifications from available powers
			for (AdeptPowerValue ref :model.getAdeptPowers()) {
				AdeptPower power = ref.getModifyable();
				logger.log(Level.DEBUG, "add from power "+power.getId()+" / "+ref+" / dec="+ref.getDecisions());
				// Calculate modifications
				ref.clearModifications();
				
				if (power.getChoices().isEmpty()) {
					logger.log(Level.INFO, " - "+ref.getModifyable().getId()+" has modifications: "+ref.getModifications());
					for (Modification mod : ref.getModifications()) {
						mod.setSource(ref.getModifyable());
					}
//					logger.log(Level.DEBUG, " - add modifications: "+ref.getModifications());
					unprocessed.addAll(ref.getModifications());
					continue;
				}
				
//				if (power.needsChoice() && ref.getChoice()==null) {
//					ref.setChoice(ShadowrunTools.resolveChoiceType(power.getSelectFrom(), ref.getChoiceReference(), model));
//					logger.log(Level.DEBUG, "resolve "+power.getSelectFrom()+" '"+ref.getChoiceReference()+"' to "+ref.getChoice());
//				}
				int multiplier = (ref.getModifyable().getMaxLevel()>1)?ref.getModifiedValue():1;
				if (multiplier==0) {
					logger.log(Level.WARNING,"Strange! Found AdeptPower with value 0 that should have at least 1 - ignore it: "+ref);
//					continue;
				}
				for (Modification mod : ref.getModifyable().getModifications()) {
					logger.log(Level.WARNING, "ToDo: "+mod);
					try {
//						Modification realMod = Shadowrun6Tools.instantiateModification(mod, ref.getChoice(), multiplier);
//						logger.log(Level.DEBUG, "  instantiated mod "+realMod+" with multiplier "+multiplier);
//						if (ref.getModifyable().getActivation()!=Activation.PASSIVE) {
//							if (realMod instanceof AttributeModification)
//								((AttributeModification)realMod).setConditional(true);
//							else if (realMod instanceof SkillModification)
//								((SkillModification)realMod).setConditional(true);
//						}
//						ref.addModification(realMod);
					} catch (Exception e) {
						logger.log(Level.ERROR, "Problem calculating modifications for adept power: "+ref,e);
					}
				}
				
				
				
				if (ref.getModifications()!=null && !ref.getModifications().isEmpty()) {
					logger.log(Level.DEBUG, " - "+ref.getModifyable().getId()+" has modifications: "+ref.getModifications());
					for (Modification mod : ref.getModifications()) {
						mod.setSource(ref.getModifyable());
					}
//					logger.log(Level.DEBUG, " - add modifications: "+ref.getModifications());
					unprocessed.addAll(ref.getModifications());
				}
			}
		} finally {
			logger.log(Level.TRACE,  "STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
