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
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author stefa
 *
 */
public class CalculateAttributePools implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateAttributePools.class.getPackageName());
	
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
		for (ShadowrunAttribute attr : ShadowrunAttribute.values()) {
			if (attr==ShadowrunAttribute.INITIATIVE_DICE_ASTRAL) {
				System.err.println("CalculateAttribtePools: "+attr);
			}
			calculatePool(model.getAttribute(attr));
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void calculatePool(AttributeValue<ShadowrunAttribute> aVal) {
		Pool<Integer> pool = new Pool<Integer>();
		aVal.setPool(pool);
		
		int augmentedMax = aVal.getDistributed() + 4;
		
		/*
		 * Natural attribute first
		 */
		pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(aVal.getDistributed(), aVal.getModifyable().getName(loc)));
		// Add all natural modifiers
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.NATURAL)
				continue;
			int value = mod.getValue();
			if (mod.getSource()==null) {
				logger.log(Level.WARNING, "No source for modification "+mod);
			}
			String name = Shadowrun6Tools.getModificationSourceString(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			pool.addStep(ValueType.NATURAL, toAdd);
		}

		// Find all augmentations
		int sumAugmentations=0;
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.AUGMENTED)
				continue;
			int value = mod.getValue();
			if (mod.getSource()==null) {
				logger.log(Level.WARNING, "No source for modification "+mod);
			}
			String name = Shadowrun6Tools.getModificationSourceString(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			if (sumAugmentations+value > 4) {
				value = 4 - sumAugmentations;
				toAdd.value = value;
				toAdd.hitLimit = true;
			}
			sumAugmentations += value;
			pool.addStep(ValueType.NATURAL, toAdd);
			
			if (name.startsWith("?")) {
				System.err.println("Unknown modification source "+mod.getSource()+" for "+mod+" in attribute "+aVal);
				System.exit(1);
			}
		}
		logger.log(Level.DEBUG, "NATURAL: {0}",pool.getCalculation(ValueType.NATURAL));
		
		/* Artificial */
		int sumArt = 0;
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.ARTIFICIAL)
				continue;
			int value = mod.getValue();
			if (mod.getSource()==null) {
				logger.log(Level.WARNING, "No source for modification "+mod);
			}
			String name = Shadowrun6Tools.getModificationSourceString(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			if (value + sumArt > augmentedMax) {
				value = augmentedMax - sumArt - value;
				toAdd.value = value;
				toAdd.hitLimit = true;
			}
			sumArt += value;
			pool.addStep(ValueType.ARTIFICIAL, toAdd);
		}
		logger.log(Level.DEBUG, "ARTIFICIAL: {0}",pool.getCalculation(ValueType.ARTIFICIAL));
		
		//logger.log(Level.INFO, "{0}: converted {1} to {2}", aVal.getModifyable(), aVal.getModifiedValue(), aVal.getPool().toString());
	}

}
