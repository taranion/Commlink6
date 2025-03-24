package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Movement;
import de.rpgframework.shadowrun.Movement.MovementType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GetModificationsForShifters implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsForShifters.class.getPackageName());

	private final static ItemTemplate NATURAL_WEAPON = new ItemTemplate();
	
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	static {
		NATURAL_WEAPON.setId("natural_weapon");
		NATURAL_WEAPON.setAttribute(SR6ItemAttribute.DAMAGE, 4);
		NATURAL_WEAPON.setAttribute(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT);
		NATURAL_WEAPON.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.UNARMED);
	}

	//-------------------------------------------------------------------
	public GetModificationsForShifters(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE, "ENTER: process");
		try {
			model.clearShifterAuto();
			QualityValue shifter = model.getQuality("shifter");
			if (shifter==null) {
				return previous;
			}

			// Add new body
			BodyForm body = new BodyForm(BodyType.SHAPESHIFTER);
			body.addMovement( new Movement( model.getBodyForms().get(0).getMovement(MovementType.GROUND) ));
			body.addMovement( new Movement( model.getBodyForms().get(0).getMovement(MovementType.WATER) ));
			model.addBodyForm(body);
			logger.log(Level.INFO, "Added Shifter body");
			
			// Copy unmodified attributes
			for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
				AttributeValue<ShadowrunAttribute> humanAttrib = model.getAttribute(key);
				AttributeValue<ShadowrunAttribute> shiftAttrib = new AttributeValue<ShadowrunAttribute>(key);
				shiftAttrib.setDistributed(humanAttrib.getDistributed());
				body.getAttributeValues().add(shiftAttrib);
				
				Pool<Integer> pool = new Pool<Integer>(humanAttrib.getDistributed());
				shiftAttrib.setPool(pool);
			}
			
			// Copy basic shifter qualities over
			for (Modification mod : shifter.getResolved().getOutgoingModifications()) {
				logger.log(Level.INFO, mod);
				shifter.getOutgoingModifications().add(mod);
				ApplyModificationsGeneric.applyModification(model, mod);
			}

			// Apply a natural weapon
			body.getNaturalWeapons().clear();
			CarriedItem<ItemTemplate> naturalWeapon = new CarriedItem<ItemTemplate>();
			naturalWeapon.setResolved(NATURAL_WEAPON);
			naturalWeapon.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DAMAGE, 4));
			naturalWeapon.setAttribute(SR6ItemAttribute.ITEMTYPE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.ITEMTYPE, ItemType.WEAPON_CLOSE_COMBAT));
			naturalWeapon.setAttribute(SR6ItemAttribute.ITEMSUBTYPE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.ITEMSUBTYPE, ItemSubType.UNARMED));
			naturalWeapon.setAttribute(SR6ItemAttribute.ATTACK_RATING, 
					new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.ATTACK_RATING, 
							0));
			naturalWeapon.setInjectedBy("Shifter");
			model.addVirtualCarriedItem(naturalWeapon);
			
			List<QualityValue> shifterQualities = new ArrayList<>();
			shifterQualities.addAll(model.getShifterAuto());
			shifterQualities.addAll(model.getShifterAddOns());
			
			shifterQualities.forEach(qv -> body.addQuality(qv));
			
			for (QualityValue qv : model.getShifterAddOns()) {
				// Iterate modifications
				for (Modification rmod : qv.getResolved().getOutgoingModifications()) {
					Modification mod = Shadowrun6Tools.instantiateModification(rmod, qv, qv.getDistributed(), model);
					if (mod.getApplyTo()!=ApplyTo.ANIMAL) {
						unprocessed.add(mod);
						continue;
					}
						
					logger.log(Level.INFO, "Apply {0}",mod);
					switch ((ShadowrunReference)mod.getReferenceType()) {
					case ATTRIBUTE:
						ValueModification valMod = (ValueModification)mod;
						logger.log(Level.INFO, "Shifter modifies {0} to shifter body", valMod.getKey());
						body.getAttributeValue(valMod.getResolvedKey()).addIncomingModification(valMod);
						break;
					case CRITTER_POWER:
						DataItemModification diMod = (DataItemModification)mod;
						logger.log(Level.INFO, "Add critter power {0} to shifter body", diMod.getKey());
						CritterPower power = mod.getReferenceType().resolve( diMod.getKey() );
						CritterPowerValue cpVal = new CritterPowerValue(power);
						if (mod instanceof ValueModification) {
							cpVal.setDistributed( ((ValueModification)mod).getValue() );
						}
						diMod.getDecisions().forEach(d -> cpVal.addDecision(d));
						body.addCritterPower(cpVal);
						break;
					case QUALITY:
						diMod = (DataItemModification)mod;
						logger.log(Level.INFO, "Add quality {0} to shifter body", diMod.getKey());
						Quality qual = mod.getReferenceType().resolve( diMod.getKey() );
						QualityValue qVal = new QualityValue(qual,0);
						if (mod instanceof ValueModification) {
							qVal.setDistributed( ((ValueModification)mod).getValue() );
						}
						diMod.getDecisions().forEach(d -> qVal.addDecision(d));
						body.addQuality(qVal);
						break;
					case METAECHO:
						diMod = (DataItemModification)mod;
						logger.log(Level.INFO, "TODO Add special power {0} to drake body", diMod.getKey());
						MetamagicOrEcho meta = mod.getReferenceType().resolve( diMod.getKey() );
						MetamagicOrEchoValue mVal = new MetamagicOrEchoValue(meta);
						if (mod instanceof ValueModification) {
							mVal.setDistributed( ((ValueModification)mod).getValue() );
						}
						diMod.getDecisions().forEach(d -> mVal.addDecision(d));
						mVal.addIncomingModification(diMod);
						model.addMetamagicOrEcho(mVal);
//						body.addMetaOrEcho(mVal);
						break;
					case ITEM_ATTRIBUTE:
						valMod = (ValueModification)mod;
						naturalWeapon.getAttributeRaw(valMod.getResolvedKey()).addIncomingModification(valMod);
						break;
					default:
						logger.log(Level.WARNING, "Don't know how to apply "+mod);
					}
				}
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
