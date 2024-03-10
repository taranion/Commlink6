package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
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
public class FixEssenceChanges implements ProcessingStep {

	private final static Logger logger = System.getLogger(FixEssenceChanges.class.getPackageName()+".essence");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public FixEssenceChanges(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	private ValueRequirement getEssenceRequirement(ComplexDataItemValue<?> value) {
		for (Requirement req : value.getResolved().getRequirements()) {
			if (!(req instanceof ValueRequirement))
				continue;
			ValueRequirement vr = (ValueRequirement)req;
			if (vr.getType()==ShadowrunReference.ATTRIBUTE && vr.getKey().equals(ShadowrunAttribute.ESSENCE.name())) {
				return vr;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	private void tryToInsertStashed(List<ComplexDataItemValue<?>> stashed, int essence) {
		logger.log(Level.DEBUG, "Check essence {0} with {1}", essence, stashed);
		if (stashed.isEmpty()) return;

		for (ComplexDataItemValue<?> value : stashed) {
			ValueRequirement req = getEssenceRequirement(value);
			logger.log(Level.INFO, "Check essence {0} with {1}", essence, stashed);
			if (req.getMaxValue()>0 && essence<=req.getMaxValue()) {
				logger.log(Level.DEBUG, "Reached requirement {0} with {1} - insert {2}", req, essence, value);
				Shadowrun6Tools.recordEssenceChange(model, value);
				stashed.remove(value);
				return;
			}
		}
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.TRACE, "ENTER: process {0}", model.getEssenceChanges().size());
		try {
			if (!model.getEssenceChanges().isEmpty())
				return previous;

			List<ComplexDataItemValue<?>> toCheck = new ArrayList<>();
			toCheck.addAll(model.getMetamagicOrEchoes());
			toCheck.addAll(model.getQualities());
			List<ComplexDataItemValue<?>> checkLater = new ArrayList<>();

			for (ComplexDataItemValue<?> value : toCheck) {
				ValueRequirement req = getEssenceRequirement(value);
				if (req!=null) {
					checkLater.add(value);
					break;
				}
			}

			int essence = 6000;
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				// Ignore pure virtual items
				if (model.getVirtualCarriedItems().contains(item))
					continue;
				ItemType type = Shadowrun6Tools.getItemType(item);
				if (Arrays.asList(ItemType.bodytechTypes()).contains(type) || (item.getVariant()!=null && item.getVariant().getEquipMode()==SR6VariantMode.BODYWARE)) {
					logger.log(Level.INFO, "Test "+item.getKey()+" with "+type);
					int essenceChange = Shadowrun6Tools.recordEssenceChange(model, item);
					logger.log(Level.INFO, " essence change of {0} is {1}", item.getKey(), essenceChange);
					essence -= essenceChange;

					tryToInsertStashed(checkLater, essence);
				}
			}

			// Add everything not yet added before
			for (ComplexDataItemValue<?> value : checkLater) {
				Shadowrun6Tools.recordEssenceChange(model, value);
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE: process");
		}
		return previous;
	}

}
