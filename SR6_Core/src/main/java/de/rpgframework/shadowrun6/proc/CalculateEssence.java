package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
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
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateEssence implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateEssence.class.getPackageName());
	
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

			float essenceCost = 0.0f;
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				logger.log(Level.DEBUG, "Item type of {0} is {1}", item, item.getAttributeRaw(SR6ItemAttribute.ITEMTYPE));
				ItemType type = Shadowrun6Tools.getItemType(item);
				if (Arrays.asList(ItemType.bodytechTypes()).contains(type)) {
					logger.log(Level.INFO, "Test "+item.getKey()+" with "+type);
					ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
					logger.log(Level.INFO, "  essence = "+aVal);
					if (aVal==null) continue;
					float essence = aVal.getModifiedValue(); 
					logger.log(Level.INFO,"* "+item.getNameWithoutRating()+" = "+essence);
					essenceCost += essence;
				}
			}
			
			// Ensure presence of attributes
			AttributeValue<ShadowrunAttribute> essVal = model.getAttribute(ShadowrunAttribute.ESSENCE);
			if (essVal==null) {
				essVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE, 6000);
				model.setAttribute(essVal);
			}
			AttributeValue<ShadowrunAttribute> holeVal = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
			if (holeVal==null) {
				holeVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0);
				model.setAttribute(holeVal);
			}
			int essenceHole = holeVal.getModifiedValue();
			
			if (model.isInCareerMode()) {
				// Determine the max essence
//				float max = 6000 - model.getEssenceHole();
				// Reduce the cost by variable
				logger.log(Level.WARNING, "ToDo: Calculate essence");
			} else {
				// Max essence to substract from is 6 plus additional essence hole
				float max = 6000 + holeVal.getModifiedValue();
				
				int remain = Math.min(6000, Math.round(max - (int)(essenceCost*1000)));
				essVal.setDistributed(remain);
				holeVal.setDistributed(0);
				model.setEssenceCost( (int)(essenceCost*1000));
				logger.log(Level.INFO, "Essence cost is {0}, hole is {1}, resulting remain essence is {2}", essenceCost, holeVal.getModifiedValue(), remain);
				essVal.setDistributed(remain);
				model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE).setDistributed(essenceHole);
			}

			float min = 6.0f - essenceCost; //Math.min(model.getEssence(), 6.0f-sum);
//			if (min!=model.getEssence()) {
//				logger.warn("Fix essence to "+min);
//				model.setEssence(min);
//			}
			int magicMalus = 5 - (int)min;
			if (magicMalus<0) magicMalus=0;
			logger.log(Level.INFO,"Magic malus is "+magicMalus);
			if (magicMalus!=0) {
				model.getAttribute(ShadowrunAttribute.MAGIC).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
				model.getAttribute(ShadowrunAttribute.RESONANCE).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
				model.getAttribute(ShadowrunAttribute.POWER_POINTS).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.POWER_POINTS.name(), -magicMalus, ShadowrunAttribute.ESSENCE));
			}
			// Also decrease maximum
//			model.getAttribute(Attribute.MAGIC).addModification(new AttributeModification(ModificationValueType.MAX, Attribute.MAGIC, -magicMalus, ModificationType.RELATIVE, Attribute.ESSENCE));
//			previous.add(new AttributeModification(ModificationValueType.MAX, Attribute.MAGIC, -magicMalus, ModificationType.RELATIVE, Attribute.ESSENCE));
			
		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
