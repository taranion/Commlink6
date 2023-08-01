package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
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
	@Override
	public List<Modification> process(List<Modification> previous) {

		logger.log(Level.TRACE, "ENTER: process");
		try {
			// The maximum essence is 6.0 + essence hole
			AttributeValue<ShadowrunAttribute> holeVal = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
			if (holeVal==null) {
				holeVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0);
				model.setAttribute(holeVal);
			}
			logger.log(Level.DEBUG, "Essence hole: {0}   not spent: {1}",holeVal.getModifiedValue(), model.getEssenceHoleUnused());

			// Max essence is 6.0 plus bonus essence hole minus unused essence hole
			// This may be larger than 6000
			int max = 6000 + holeVal.getModifiedValue() - model.getEssenceHoleUnused();
			logger.log(Level.DEBUG, "Calculatory max essence = {0}",max);

			// Now substract all items from it
			BigDecimal essenceCostBD = new BigDecimal(0);
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				ItemType type = Shadowrun6Tools.getItemType(item);
				if (Arrays.asList(ItemType.bodytechTypes()).contains(type) || (item.getVariant()!=null && item.getVariant().getEquipMode()==SR6VariantMode.BODYWARE)) {
//					logger.log(Level.INFO, "Test "+item.getKey()+" with "+type);
					ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
					logger.log(Level.DEBUG, "  essence = {0} for {1}",aVal, item.getKey());
					if (aVal==null) continue;
					double essence = aVal.getModifiedValueDouble();
					logger.log(Level.INFO,"* "+item.getNameWithoutRating()+" = "+essence);
					essenceCostBD = essenceCostBD.add(aVal.getModifiedValueBigDecimal());
				}
			}
			int essenceCost = essenceCostBD.multiply(new BigDecimal(1000)).intValue();
			model.setEssenceCost(essenceCost);
			logger.log(Level.DEBUG, "Essence cost of all items {0}",essenceCost);
			int essenceRemain = max - essenceCost;
			// If there is more than 6000 essence left, fill up essence hole unused
			if (essenceRemain-6000 >0) {
				model.setEssenceHoleUnsed( model.getEssenceHoleUnused() + (essenceRemain-6000));
				logger.log(Level.WARNING, "Remaining essence > 6,0 -> increase unused essence hole by difference");
				essenceRemain = 6000;
			}
			logger.log(Level.DEBUG, "Remaining essence; {0}",essenceRemain);


			// Ensure presence of attributes
			AttributeValue<ShadowrunAttribute> essVal = model.getAttribute(ShadowrunAttribute.ESSENCE);
			if (essVal==null) {
				essVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE, essenceRemain);
				model.setAttribute(essVal);
			} else
				essVal.setDistributed(essenceRemain);
			essVal.clearModifications();


			float remain = essVal.getModifiedValue() / 1000f;
			logger.log(Level.DEBUG, "Essence cost is {0}, hole is {1}, resulting remain essence is {2}", essenceCost, holeVal.getModifiedValue(), remain);

			double min = 6.0f - (((double)essenceCost)/1000f); //Math.min(model.getEssence(), 6.0f-sum);
			int magicMalus = (int) Math.ceil( ((double)essenceCost)/1000d);
			if (magicMalus<0) magicMalus=0;
			logger.log(Level.DEBUG,"Magic/Resonance malus is "+magicMalus);
			if (magicMalus!=0) {
				model.getAttribute(ShadowrunAttribute.MAGIC).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
				model.getAttribute(ShadowrunAttribute.RESONANCE).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
				model.getAttribute(ShadowrunAttribute.POWER_POINTS).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.POWER_POINTS.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
			}
			// Also decrease maximum
			model.getAttribute(ShadowrunAttribute.MAGIC).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus, ShadowrunAttribute.ESSENCE, ValueType.MAX));

		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
