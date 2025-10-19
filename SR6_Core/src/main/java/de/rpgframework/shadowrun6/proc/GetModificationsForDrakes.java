package de.rpgframework.shadowrun6.proc;

import static de.rpgframework.shadowrun.ShadowrunAttribute.AGILITY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.BODY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.CHARISMA;
import static de.rpgframework.shadowrun.ShadowrunAttribute.DRAKE_EDGE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.EDGE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.INTUITION;
import static de.rpgframework.shadowrun.ShadowrunAttribute.LOGIC;
import static de.rpgframework.shadowrun.ShadowrunAttribute.MAGIC;
import static de.rpgframework.shadowrun.ShadowrunAttribute.REACTION;
import static de.rpgframework.shadowrun.ShadowrunAttribute.RESONANCE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.STRENGTH;
import static de.rpgframework.shadowrun.ShadowrunAttribute.WILLPOWER;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.PoolCalculation;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Movement;
import de.rpgframework.shadowrun.Movement.MovementType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GetModificationsForDrakes implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsForDrakes.class.getPackageName()+".drake");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsForDrakes(Shadowrun6Character model) {
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
			DrakeTypeValue drake = model.getDrakeType();
			if (drake==null) {
				logger.log(Level.INFO, "not a drake");
				return previous;
			}

			// Add new body
			BodyForm body = new BodyForm(BodyType.DRAKE);
			body.addMovement( new Movement( model.getBodyForms().get(0).getMovement(MovementType.GROUND) ));
			body.addMovement( new Movement( model.getBodyForms().get(0).getMovement(MovementType.WATER) ));
			model.addBodyForm(body);
			logger.log(Level.INFO, "Added Drake body");

			// Copy attributes from regular body to drake body
			ShadowrunAttribute[] attributes = new ShadowrunAttribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE,DRAKE_EDGE};
			for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
				AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(key);
				AttributeValue<ShadowrunAttribute> copy = new AttributeValue<ShadowrunAttribute>(key, aVal.getDistributed());
				body.getAttributeValues().add(copy);
			}

			// Calculate DRAKE_EDGE
			int dEdge = model.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue();
			dEdge += model.getMetamagicOrEchoes().stream()
					.filter(me -> me.getModifyable().getType()==Type.DRACOGENESIS_POWER)
					.filter(me -> !me.isAutoAdded())
					.mapToInt(me -> (me.getDistributed()>0)?me.getDistributed():1)
					.sum();
			body.getAttributeValues().add(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.DRAKE_EDGE, dEdge));

			// Modify with drake adjustments
			for (AttributeValue<ShadowrunAttribute> aVal : drake.getAttributes()) {
				ShadowrunAttribute key = aVal.getModifyable();
				AttributeValue<ShadowrunAttribute> copy = body.getAttributeValue(key);
				ValueModification mod = new ValueModification(ShadowrunReference.ATTRIBUTE, key.name(), aVal.getModifiedValue());
				mod.setSource(BodyType.DRAKE);
				mod.setSet(ValueType.NATURAL);
				mod.setOrigin(Origin.OUTSIDE);
				copy.addIncomingModification(mod);
				logger.log(Level.INFO, "Change {0} fro {1} to {2}", key, model.getAttribute(key).getDistributed(), copy);
			}
			
			// Add natural weapon
//			body.clearWeapons();
			
			// Natural weapon
			ItemTemplate natural = new ItemTemplate();
			natural.setId("natweapon");
			Shadowrun6Core.getDataSet("LOFWYR").ifPresent(ds -> natural.assignToDataSet(ds));
			
			CarriedItem<ItemTemplate> natAttack = new CarriedItem<ItemTemplate>(natural, null, CarryMode.VIRTUAL);
			int strPlusRea = body.getAttributeValue(ShadowrunAttribute.STRENGTH).getModifiedValue()
					+ body.getAttributeValue(ShadowrunAttribute.REACTION).getModifiedValue();
			int dmg = (int) Math.round( (double)body.getAttributeValue(ShadowrunAttribute.STRENGTH).getModifiedValue() / 2.0);
			ItemAttributeObjectValue<SR6ItemAttribute> av = new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, new int[] {strPlusRea,0,0,0,0});
			natAttack.setAttribute(SR6ItemAttribute.ATTACK_RATING, av);
			natAttack.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.DAMAGE, new Damage(dmg, DamageType.PHYSICAL, DamageElement.REGULAR)));
			natAttack.setAttribute(SR6ItemAttribute.SKILL, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.SKILL, Shadowrun6Core.getSkill("close_combat")));
			natAttack.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed")));
			body.addNaturalWeapon(natAttack);
			
			// Elemental attack
			BiFunction<Shadowrun6Character,CarriedItem<ItemTemplate>,Pool<Integer>> supp = (mod,weap) -> {
				Pool<Integer> pool = new Pool<>();
				pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(body.getAttributeValue(ShadowrunAttribute.DRAKE_EDGE).getModifiedValue(), ShadowrunAttribute.EDGE.getName()));
				pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(body.getAttributeValue(ShadowrunAttribute.AGILITY).getModifiedValue(), ShadowrunAttribute.AGILITY.getName()));
				return pool;
			};
			ItemTemplate elemAt = new ItemTemplate();
			elemAt.setId("elemental_attack");
			Shadowrun6Core.getDataSet("LOFWYR").ifPresent(ds -> elemAt.assignToDataSet(ds));
			
			CarriedItem<ItemTemplate> elementalAttack = new CarriedItem<ItemTemplate>(elemAt, null, CarryMode.VIRTUAL);
			av = new ItemAttributeObjectValue<>(SR6ItemAttribute.ATTACK_RATING, new int[] {dEdge*2, dEdge*2-2, dEdge*2-8, dEdge*2-10,0});
			elementalAttack.setAttribute(SR6ItemAttribute.ATTACK_RATING, av);
			elementalAttack.setAttribute(SR6ItemAttribute.DAMAGE, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.DAMAGE, new Damage(dEdge, DamageType.PHYSICAL, DamageElement.FIRE)));
			elementalAttack.setAttribute(SR6ItemAttribute.POOL_SUPPLIER, new ItemAttributeObjectValue<SR6ItemAttribute>(SR6ItemAttribute.DAMAGE, supp));
			body.addNaturalWeapon(elementalAttack);


			// Iterate modifications
			if (drake.getResolved()==null) return unprocessed;
			for (Modification mod : drake.getResolved().getOutgoingModifications()) {
				switch ((ShadowrunReference)mod.getReferenceType()) {
				case ATTRIBUTE:
					logger.log(Level.INFO, "Drake {0} allows {1}", drake.getKey(), mod);
					unprocessed.add(mod);
					break;
				case CRITTER_POWER:
					DataItemModification diMod = (DataItemModification)mod;
					logger.log(Level.INFO, "Add critter power {0} to drake body", diMod.getKey());
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
					logger.log(Level.INFO, "Add quality {0} to drake body", diMod.getKey());
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
					logger.log(Level.INFO, "Add special power {0} to drake body", diMod.getKey());
					MetamagicOrEcho meta = mod.getReferenceType().resolve( diMod.getKey() );
					MetamagicOrEchoValue mVal = new MetamagicOrEchoValue(meta);
					if (mod instanceof ValueModification) {
						mVal.setDistributed( ((ValueModification)mod).getValue() );
					}
					diMod.getDecisions().forEach(d -> mVal.addDecision(d));
					mVal.addIncomingModification(diMod);
					model.addAutoMetamagicOrEchoe(mVal);
//					body.addMetaOrEcho(mVal);
					break;
				}
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
