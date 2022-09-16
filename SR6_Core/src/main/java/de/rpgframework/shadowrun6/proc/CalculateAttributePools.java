package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.PoolCalculation;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author stefa
 *
 */
public class CalculateAttributePools implements ProcessingStep {

	private final static Logger logger = System.getLogger("shadowrun6.proc");
	
	private Shadowrun6Character model;
	private Locale loc = Locale.getDefault();

	//-------------------------------------------------------------------
	public CalculateAttributePools(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	public CalculateAttributePools(Shadowrun6Character model, Locale loc) {
		this.model = model;
		this.loc   = loc;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		for (ShadowrunAttribute attr : ShadowrunAttribute.primaryAndSpecialValues()) {
			calculatePool(model.getAttribute(attr));
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void calculatePool(AttributeValue<ShadowrunAttribute> aVal) {
		logger.log(Level.WARNING, "convert: "+aVal.getModifications());
		Pool<Integer> pool = new Pool<Integer>();
		
		int augmentedMax = aVal.getDistributed() + 4;
		
		/*
		 * Natural attribute first
		 */
		pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(aVal.getDistributed(), aVal.getModifyable().getName(loc)));
		// Find all augmentations
		int sumAugmentations=0;
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()==ValueType.NATURAL) {
				logger.log(Level.WARNING, "Found NATURAL modifier in "+mod.getSource());
			}
			if (mod.getSet()!=ValueType.AUGMENTED)
				continue;
			int value = mod.getValue();
			String name = getNameFor(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			if (sumAugmentations+value > 4) {
				value = 4 - sumAugmentations;
				toAdd.value = value;
				toAdd.hitLimit = true;
			}
			sumAugmentations += value;
			pool.addStep(ValueType.NATURAL, toAdd);
		}
		
		/* Artificial */
		int sumArt = 0;
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.ARTIFICIAL)
				continue;
			int value = mod.getValue();
			String name = getNameFor(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			if (value + sumArt > augmentedMax) {
				value = augmentedMax - sumArt - value;
				toAdd.value = value;
				toAdd.hitLimit = true;
			}
			sumArt += value;
			pool.addStep(ValueType.ARTIFICIAL, toAdd);
		}
		logger.log(Level.WARNING, "ARTIFICIAL: "+pool.getCalculation(ValueType.ARTIFICIAL));
	}

	//-------------------------------------------------------------------
	private String getNameFor(Object source) {
		if (source instanceof ItemTemplate)
			return ((ItemTemplate)source).getName(loc);
		if (source instanceof CarriedItem)
			return ((CarriedItem<ItemTemplate>)source).getNameWithoutRating(loc);
		if (source instanceof SR6PieceOfGearVariant)
			return ((SR6PieceOfGearVariant)source).getName(loc);
		if (source!=null)
			System.err.println("CalculateAttributePools: To Do: "+source.getClass()+" = "+source);
		
		System.exit(1);
		return "?"+source+"?";
	}

}
