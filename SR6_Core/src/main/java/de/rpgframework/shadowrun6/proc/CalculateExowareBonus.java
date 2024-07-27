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
public class CalculateExowareBonus implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateExowareBonus.class.getPackageName()+".essence");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public CalculateExowareBonus(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {

		logger.log(Level.TRACE, "ENTER: process");
		try {
			double exowareEssence = 0.0d;
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				// Ignore pure virtual items
				if (model.getVirtualCarriedItems().contains(item))
					continue;
				ItemType type = Shadowrun6Tools.getItemType(item);
				if (Arrays.asList(ItemType.bodytechTypes()).contains(type) || (item.getVariant()!=null && item.getVariant().getEquipMode()==SR6VariantMode.BODYWARE)) {
					ItemAttributeFloatValue<SR6ItemAttribute> aVal = item.getAsFloat(SR6ItemAttribute.ESSENCECOST);
					Decision decision = item.getDecision(ItemTemplate.UUID_AUGMENTATION_QUALITY);
					if (decision==null) continue;
					if (!AugmentationQuality.EXO.name().equals(decision.getValue())) continue;
					if (aVal==null) continue;
					exowareEssence += aVal.getModifiedValueDouble();
				}
			}
			int physicalBodyMod = (int)exowareEssence;
			if (physicalBodyMod>0) {
				logger.log(Level.INFO, "Essence spent in exoware: {0}", exowareEssence);
				AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
				CalculateDerivedAttributes.addNaturalModifier(val, physicalBodyMod, AugmentationQuality.EXO.getName());
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
