package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
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
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GetModificationsForShifters implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsForShifters.class.getPackageName());

	private Shadowrun6Character model;

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
			
			// Copy basic shifter qualities over
			for (Modification mod : shifter.getResolved().getOutgoingModifications()) {
				logger.log(Level.INFO, mod);
				shifter.getOutgoingModifications().add(mod);
				ApplyModificationsGeneric.applyModification(model, mod);
			}

			List<QualityValue> shifterQualities = new ArrayList<>();
			shifterQualities.addAll(model.getShifterAuto());
			shifterQualities.addAll(model.getShifterAddOns());
			
			shifterQualities.forEach(qv -> body.addQuality(qv));
			
			for (QualityValue qv : model.getShifterAddOns()) {
			
			// Iterate modifications
			for (Modification mod : qv.getOutgoingModifications()) {
				switch ((ShadowrunReference)mod.getReferenceType()) {
//				case ATTRIBUTE:
//					logger.log(Level.INFO, "Drake {0} allows {1}", drake.getKey(), mod);
//					unprocessed.add(mod);
//					break;
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
//					body.addMetaOrEcho(mVal);
					break;
				}
			}
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
