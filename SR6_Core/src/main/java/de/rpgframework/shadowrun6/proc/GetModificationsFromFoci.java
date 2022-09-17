package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class GetModificationsFromFoci implements ProcessingStep {
	
	protected static final Logger logger = System.getLogger(GetModificationsFromFoci.class.getPackageName());
	
	private Shadowrun6Character model;
	
	//-------------------------------------------------------------------
	public GetModificationsFromFoci(Shadowrun6Character model) {
		this.model = model;
	}
	
//	//-------------------------------------------------------------------
//	private Object getChoiceFor(FocusValue ref) {
//		Focus focus = ref.getModifyable();
//		if (ref.getChoice()!=null)
//			return ref.getChoice();
//		switch (focus.getChoice()) {
//		case WEAPON:
//			ItemTemplate weapon = (ItemTemplate)ShadowrunTools.resolveChoiceType(focus.getChoice(), ref.getChoiceReference());
//			ref.setChoice(weapon);
//			break;
//		
//		default:
//			logger.error("Not implemented for "+focus.getChoice());
//		}
//		return ref.getChoice();
//	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE,  "ENTER: process");
		try {
			// Apply modifications by qualities
//			logger.log(Level.DEBUG, "2. Apply modifications from qualities");
			for (FocusValue ref :model.getFoci()) {
				logger.log(Level.ERROR, "Not implemented yet: "+ref);
//				if (ref.getUniqueId()==null) {
//					ref.setUniqueId(UUID.randomUUID());
//				}
//				logger.log(Level.DEBUG, "FOCUS "+ref+" / "+ref.getModifications());
//				Focus focus = ref.getModifyable();
//				logger.log(Level.DEBUG, "  direct modifications: "+focus.getModifications());
//				if (focus.needsChoice()) {
//					Object choice = ShadowrunTools.resolveChoiceType(focus.getChoice(), ref.getChoiceReference(), model);
//					if (choice==null) {
//						logger.warn("No choice for "+focus);
//					} else {
//						ref.setChoice(choice);
//						switch (focus.getChoice()) {
//						case MELEE_WEAPON:
//							CarriedItem item = new CarriedItem((ItemTemplate) choice);
//							item.setUsedFocus(ref);
//							item.setUniqueId(ref.getUniqueId());
//							model.addAutoItem(item);
//							item.setName(item.getName()+" ("+focus.getName()+")");
//							logger.log(Level.INFO,  "Added auto-gear: "+item.getName());
//							break;
//						case SPELL_CATEGORY:
//							focus.getModifications().forEach(mod -> {
//								Modification realMod = ShadowrunTools.instantiateModification(mod, choice, ref.getLevel());
//								realMod.setSource(ref);
//								logger.log(Level.INFO,  "Add "+realMod+" from "+ref);
//								unprocessed.add(realMod);
//							});
//							break;
//						case ADEPT_POWER:
//							focus.getModifications().forEach(mod -> {
//								Modification realMod = null;
//								if (mod instanceof AdeptPowerModification) {
//									AdeptPower power = (AdeptPower)ref.getChoice();
//									realMod = new AdeptPowerModification(power,0);
//									if (power.needsChoice()) {
//										if (ref.getChoicesChoiceReference()==null) {
//											logger.error("Missing choices choice reference for focus "+ref);
//										} else {
//											logger.log(Level.INFO,  "Set choice of auto-added adept power to "+ref.getChoicesChoiceReference());
//											((AdeptPowerModification)realMod).setChoice(ref.getChoicesChoiceReference());
//										}
//									}
//									if (power.hasLevels()) {
//										logger.log(Level.INFO,  "Rating of "+ref+" is "+ref.getLevel());
//										logger.log(Level.INFO,  "Force of "+ref+" is "+((float)ref.getLevel())/power.getCost());
//										((AdeptPowerModification)realMod).setValue( (int)( ref.getLevel()/power.getCost()/4 ) );
//										logger.log(Level.INFO,  "Power  of "+ref+" is "+((AdeptPowerModification)realMod).getValue());
//									}
//								} else { 
//									realMod = ShadowrunTools.instantiateModification(mod, choice, ref.getLevel());
//								}
//								realMod.setSource(ref);
//								logger.log(Level.INFO,  "Add "+realMod+" from "+ref);
//								unprocessed.add(realMod);
//							});
//							break;
//						case SPIRIT:	
//							unprocessed.addAll(focus.getModifications());
//							break;
//						default:
//							logger.warn("\n\nTODO: Implement effects of focus type: "+focus.getId()+" and choice "+focus.getChoice()+"\n");
////							System.exit(1);
//							unprocessed.addAll(focus.getModifications());
//						}
//					}
//				} else {
//					// No choices necessary
//					for (Modification mod : focus.getModifications()) {
//						Modification realMod = Shadowrun6Tools.instantiateModification(mod, null, ref.getLevel());
//						realMod.setSource(ref);
//						logger.log(Level.INFO,  "Add "+realMod+" from "+ref);
//						unprocessed.add(realMod);
//					}
//				}
			}
		} finally {
			logger.log(Level.TRACE,  "STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
//		logger.fatal("STOP HERE: "+unprocessed);
//		System.exit(1);
		return unprocessed;
	}

}
