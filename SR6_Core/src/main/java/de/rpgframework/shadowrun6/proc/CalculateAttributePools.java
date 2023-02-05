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
import de.rpgframework.genericrpg.modification.CheckModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author prelle
 *
 */
public class CalculateAttributePools implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateAttributePools.class.getPackageName()+".attr");

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
		if (aVal.getDistributed()!=0)
			pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(aVal.getDistributed(), aVal.getModifyable().getName(loc)));
		// Add all natural modifiers
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			if (tmp instanceof CheckModification)
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.NATURAL)
				continue;
			int value = mod.getValue();
			if (mod.getLookupTable() != null && mod.getLookupTable().length >= value) {
				String lookup = mod.getLookupTable()[value - 1];
				value = Integer.parseInt(lookup);
			}
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
			if (tmp instanceof CheckModification)
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.AUGMENTED)
				continue;
			int value = mod.getValue();
			if (mod.getLookupTable() != null && mod.getLookupTable().length >= value) {
				String lookup = mod.getLookupTable()[value - 1];
				value = Integer.parseInt(lookup);
			}
			if (mod.getSource() == null) {
				logger.log(Level.WARNING, "No source for modification "+mod);
			}
			String name = Shadowrun6Tools.getModificationSourceString(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name);
			toAdd.augment = true;
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
		logger.log(Level.DEBUG, "{0} NATURAL    : {1}",aVal.getModifyable(),pool.getCalculation(ValueType.NATURAL));

		/* Artificial */
		int sumArt = 0;
		for (Modification tmp : aVal.getModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			if (tmp instanceof CheckModification)
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.ARTIFICIAL)
				continue;
			int value = mod.getValue();
			if (mod.getLookupTable()!=null && mod.getLookupTable().length>=value) {
				String lookup = mod.getLookupTable()[value-1];
				value = Integer.parseInt(lookup);
			}
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
		logger.log(Level.DEBUG, "{0} ARTIFICIAL : {1}",aVal.getModifyable(), pool.getCalculation(ValueType.ARTIFICIAL));

		logger.log(Level.DEBUG, "{0}: converted {1} to {2}", aVal.getModifyable(), aVal.getModifiedValue(), aVal.getPool().toString());
	}

}
