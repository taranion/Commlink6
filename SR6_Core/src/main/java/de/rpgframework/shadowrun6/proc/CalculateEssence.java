package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateEssence implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateEssence.class.getPackageName()+".essence");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public CalculateEssence(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	private BigDecimal getOverriddenEssenceCost(CarriedItem<ItemTemplate> item) {
		double old = item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValueDouble();
		AugmentationQuality effectiveQuality = item.getAsObject(SR6ItemAttribute.QUALITY).getModifiedValue();
		// Check if this is a cyberadept
		if (model.hasRuleFlag(SR6RuleFlag.CYBERADEPT_NOVICE)) {
			switch (effectiveQuality) {
			case USED    : effectiveQuality = AugmentationQuality.STANDARD; break;
			case STANDARD: effectiveQuality = AugmentationQuality.ALPHA; break;
			default:
			}
			logger.log(Level.TRACE, "Raise effective quality of {0} from {1} due to CYBERADEPT_NOVICE", item.getNameWithoutRating(), effectiveQuality);
		}
		if (model.hasRuleFlag(SR6RuleFlag.CYBERADEPT_DISCIPLE)) {
			switch (effectiveQuality) {
			case STANDARD: effectiveQuality = AugmentationQuality.ALPHA; break;
			case ALPHA  : effectiveQuality = AugmentationQuality.BETA; break;
			default:
			}
			logger.log(Level.TRACE, "Raise effective quality of {0} from {1} due to CYBERADEPT_DISCIPLE", item.getNameWithoutRating(), effectiveQuality);
		}
		if (model.hasRuleFlag(SR6RuleFlag.CYBERADEPT_MASTER)) {
			switch (effectiveQuality) {
			case ALPHA  : effectiveQuality = AugmentationQuality.BETA; break;
			case BETA  : effectiveQuality = AugmentationQuality.DELTA; break;
			default:
			}
			logger.log(Level.TRACE, "Raise effective quality of {0} from {1} due to CYBERADEPT_DISCIPLE", item.getNameWithoutRating(), effectiveQuality);
		}

		AugmentationQuality regularQuality = item.getAsObject(SR6ItemAttribute.QUALITY).getModifiedValue();
		if (regularQuality!=effectiveQuality) {
			logger.log(Level.INFO, "Due to cyberadept quality of {0} changes from {1} to {2}", item.getNameWithoutRating(), regularQuality, effectiveQuality);
			CarriedItem<ItemTemplate> copy = new CarriedItem<ItemTemplate>(item);
			copy.removeDecision(ItemTemplate.UUID_AUGMENTATION_QUALITY);
			copy.addDecision(new Decision(ItemTemplate.UUID_AUGMENTATION_QUALITY, effectiveQuality.name()));
			SR6GearTool.recalculate("", null, model, copy);
			ItemAttributeFloatValue<SR6ItemAttribute> aVal = copy.getAsFloat(SR6ItemAttribute.ESSENCECOST);
			double ess = aVal.getModifiedValueDouble();
			logger.log(Level.INFO, "Essence changes from {0} to {1}", old, ess);
			item.getAsObject(SR6ItemAttribute.QUALITY).addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.QUALITY.name(), effectiveQuality.name(), SR6RuleFlag.CYBERADEPT_NOVICE));
			double diff = ess - old;
			item.getAsFloat(SR6ItemAttribute.ESSENCECOST).addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.ESSENCECOST.name(), (int)(diff*1000), SR6RuleFlag.CYBERADEPT_NOVICE));
//			System.out.println("A "+aVal);
//			logger.log(Level.INFO, "A {0}", aVal);
//			logger.log(Level.INFO, "B {0}", aVal.getModifiedValueDouble());
			System.err.println(item.dump());
//			System.err.println(copy.dump());
//			System.exit(1);
			return aVal.getModifiedValueBigDecimal();
		}

		ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
		return aVal.getModifiedValueBigDecimal();
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {

		logger.log(Level.TRACE, "ENTER: process");
		try {
			// Base essence
			double essence = 6000;
			double holeTotal = 0;
			double hole    = 0;
			double essenceCostTotal = 0;
			for (ValueModification mod : model.getEssenceChanges()) {
				int change = mod.getValue();
				String name = mod.getKey();
				switch ( (ShadowrunReference)mod.getReferenceType() ) {
				case CARRIED:
					CarriedItem<ItemTemplate> carried = model.getCarriedItem(mod.getId());
					if (carried!=null) {
						name = carried.getNameWithRating();
					} else {
						logger.log(Level.INFO, "Removed item found: "+mod.getId()+" / "+mod.getSource());
						name = "BOOM";
					}
					break;
				case QUALITY:
					QualityValue quality = model.getQuality(mod.getKey());
					name = quality.getNameWithRating();
					break;
				}

				if (change==0) {
					logger.log(Level.WARNING, "Essence 0 for "+mod);
					switch ( (ShadowrunReference)mod.getReferenceType()) {
					case QUALITY:
						QualityValue quality = model.getQuality(mod.getKey());
						System.err.println(quality.getOutgoingModifications());
						Optional<ValueModification> valMod = quality.getOutgoingModifications()
								.stream()
								.filter(m -> m instanceof ValueModification && ((ValueModification)m).getKey().equals("ESSENCE_HOLE"))
								.map(m -> (ValueModification)m)
								.findFirst();
						if (valMod.isPresent()) {
							change = - valMod.get().getValue();
						} else
							logger.log(Level.ERROR, "Did not find ESSENCE_HOLE for quality {0}", quality);
					}
				}

				if (change<0) {
					hole -= change;
					holeTotal += change;
					logger.log(Level.INFO, "{0}: Increase essence hole by {1} to {2}", name, change, hole);
				} else {
					essenceCostTotal += change;
					if (hole>0) {
						if (change>hole) {
							logger.log(Level.INFO, "{0}: Partially pay essence with hole {1}", name, hole);
							change -= hole;
							hole=0;
						} else {
							// Change <= hole
							logger.log(Level.INFO, "{0}: Fully pay essence with hole {1}", name, hole);
							hole -= change;
							change=0;
						}
					}
					if (change>0) {
						logger.log(Level.INFO, "{0}: Pay essence {1}", name, change);
						essence -= change;
					}
				}
			}
			logger.log(Level.INFO, "Final essence: {0}", essence);
			AttributeValue<ShadowrunAttribute> holeVal = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
			if (holeVal==null) {
				holeVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0);
				model.setAttribute(holeVal);
			}
			holeVal.setDistributed((int) -holeTotal);
			holeVal.clearIncomingModifications();
			model.setEssenceHoleUnsed((int) hole);
			model.setEssenceCost((int) essenceCostTotal);

			// Ensure presence of attributes
			AttributeValue<ShadowrunAttribute> essVal = model.getAttribute(ShadowrunAttribute.ESSENCE);
			if (essVal==null) {
				essVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE, (int) essence);
				model.setAttribute(essVal);
			} else
				essVal.setDistributed((int) essence);
//			essVal.clearModifications();
			logger.log(Level.WARNING, "Essence is "+essVal.getModifiedValue());



//			// The maximum essence is 6.0 + essence hole
//			AttributeValue<ShadowrunAttribute> holeVal = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
//			if (holeVal==null) {
//				holeVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0);
//				model.setAttribute(holeVal);
//			}
//			logger.log(Level.WARNING, "Essence hole: {0}   not spent: {1}",holeVal.getModifiedValue(), model.getEssenceHoleUnused());
//
//			// Max essence is 6.0 plus bonus essence hole minus unused essence hole
//			// This may be larger than 6000
//			int max = 6000 + holeVal.getModifiedValue() - model.getEssenceHoleUnused();
//			logger.log(Level.DEBUG, "Calculatory max essence = {0}",max);
//
//			// Now substract all items from it
//			BigDecimal essenceCostBD = new BigDecimal(0);
//			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
//				// Ignore pure virtual items
//				if (model.getVirtualCarriedItems().contains(item))
//					continue;
//				ItemType type = Shadowrun6Tools.getItemType(item);
//				if (Arrays.asList(ItemType.bodytechTypes()).contains(type) || (item.getVariant()!=null && item.getVariant().getEquipMode()==SR6VariantMode.BODYWARE)) {
////					logger.log(Level.INFO, "Test "+item.getKey()+" with "+type);
//					ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
//					logger.log(Level.DEBUG, "  essence = {0} for {1}",aVal, item.getKey());
//					if (aVal==null) continue;
//					essence = aVal.getModifiedValueDouble();
//
//					logger.log(Level.INFO,"* {0} = {1}  / {2}",item.getNameWithoutRating(),essence,getOverriddenEssenceCost(item).doubleValue());
//					essenceCostBD = essenceCostBD.add(getOverriddenEssenceCost(item));
//				}
//			}
//			int essenceCost = essenceCostBD.multiply(new BigDecimal(1000)).intValue();
//			model.setEssenceCost(essenceCost);
//			logger.log(Level.DEBUG, "Essence cost of all items {0}",essenceCost);
//			int essenceRemain = max - essenceCost;
//			// If there is more than 6000 essence left, fill up essence hole unused
//			if (essenceRemain-6000 >0) {
//				model.setEssenceHoleUnsed( model.getEssenceHoleUnused() + (essenceRemain-6000));
//				logger.log(Level.WARNING, "Remaining essence > 6,0 -> increase unused essence hole by difference");
//				essenceRemain = 6000;
//			}
//			logger.log(Level.DEBUG, "Remaining essence; {0}",essenceRemain);
//
//
//			// Ensure presence of attributes
//			AttributeValue<ShadowrunAttribute> essVal = model.getAttribute(ShadowrunAttribute.ESSENCE);
//			if (essVal==null) {
//				essVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE, essenceRemain);
//				model.setAttribute(essVal);
//			} else
//				essVal.setDistributed(essenceRemain);
////			essVal.clearModifications();
//			logger.log(Level.WARNING, "Essence is "+essVal.getModifiedValue());
//
//
//			float remain = essVal.getModifiedValue() / 1000f;
//			logger.log(Level.DEBUG, "Essence cost is {0}, hole is {1}, resulting remain essence is {2}", essenceCost, holeVal.getModifiedValue(), remain);
			int essenceCost = (int) (6000 - essence);

//			double min = 6.0f - (((double)essenceCost)/1000f); //Math.min(model.getEssence(), 6.0f-sum);
			int magicMalus = (int) Math.ceil( ((double)essenceCost)/1000d);
			if (magicMalus<0) magicMalus=0;
			logger.log(Level.INFO,"Magic/Resonance malus is "+magicMalus);
			if (magicMalus!=0) {
				model.getAttribute(ShadowrunAttribute.MAGIC).addIncomingModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus, ShadowrunAttribute.ESSENCE, ValueType.NATURAL));
				model.getAttribute(ShadowrunAttribute.RESONANCE).addIncomingModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), -magicMalus, ShadowrunAttribute.ESSENCE, ValueType.NATURAL));
				model.getAttribute(ShadowrunAttribute.POWER_POINTS).addIncomingModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.POWER_POINTS.name(), -magicMalus, ShadowrunAttribute.ESSENCE, ValueType.NATURAL));
			}
			// Also decrease maximum
			model.getAttribute(ShadowrunAttribute.MAGIC).addIncomingModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus, ShadowrunAttribute.ESSENCE, ValueType.MAX).setOrigin(Origin.OUTSIDE));
			// Eventually decrease social rating (Body Shop p.168)
			int socialMalus = (int) Math.floor( ((double)essenceCost)/2000d);
			QualityValue retention = model.getQuality("empathic_retention");
			if (retention!=null) {
				int buffer = Math.min(2,retention.getModifiedValue());
				logger.log(Level.WARNING,"APPLY Empathic Retention "+magicMalus);
				socialMalus -= buffer;
			}
			if (socialMalus<0) socialMalus=0;
			logger.log(Level.INFO,"Social malus is "+magicMalus);
			if (socialMalus!=0) {
				model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_SOCIAL).addIncomingModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.DEFENSE_RATING_SOCIAL.name(), -socialMalus, ShadowrunAttribute.ESSENCE, ValueType.NATURAL));
			}

		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
