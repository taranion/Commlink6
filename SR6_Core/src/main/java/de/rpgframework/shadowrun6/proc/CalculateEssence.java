package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CalculateEssence implements ProcessingStep {

	private final static Logger logger = System.getLogger("shadowrun6.proc");
	
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

			float sum = 0.0f;
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				if (Arrays.asList(ItemType.bodytechTypes()).contains(item.getResolved().getItemType())) {
					ItemAttributeObjectValue<SR6ItemAttribute> aVal = item.getAsObject(SR6ItemAttribute.ESSENCECOST);
					if (aVal==null) continue;
					float essence = item.getAsObject(SR6ItemAttribute.ESSENCECOST).getModifiedValue(); 
					logger.log(Level.INFO,"* "+item.getNameWithoutRating()+" = "+essence);
					sum += essence;
				}
			}
			
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
			if (aVal==null) {
				aVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0);
				model.setAttribute(aVal);
			}
			sum += (float)(aVal.getModifiedValue())/1000.0f;

			float normalLow = (6000 - (int)(sum*1000)) / 1000.0f;
//			if (model.getEssence()==0 || normalLow<model.getEssence()) {
//				logger.info("Unused essence decreased to "+normalLow);
//				model.setEssence(normalLow);
//			}
//			logger.info("sum="+sum+"  normalLow="+normalLow+"  unused="+model.getEssence());

			float min = 6.0f - sum; //Math.min(model.getEssence(), 6.0f-sum);
//			if (min!=model.getEssence()) {
//				logger.warn("Fix essence to "+min);
//				model.setEssence(min);
//			}
			int magicMalus = 6 - (int)min;
			logger.log(Level.INFO,"Magic malus is "+magicMalus);
			model.getAttribute(ShadowrunAttribute.MAGIC).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), -magicMalus));
			model.getAttribute(ShadowrunAttribute.RESONANCE).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), -magicMalus));
			model.getAttribute(ShadowrunAttribute.POWER_POINTS).addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.POWER_POINTS.name(), -magicMalus));
			// Also decrease maximum
//			model.getAttribute(Attribute.MAGIC).addModification(new AttributeModification(ModificationValueType.MAX, Attribute.MAGIC, -magicMalus, ModificationType.RELATIVE, Attribute.ESSENCE));
//			previous.add(new AttributeModification(ModificationValueType.MAX, Attribute.MAGIC, -magicMalus, ModificationType.RELATIVE, Attribute.ESSENCE));
			
		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
