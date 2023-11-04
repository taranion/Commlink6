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
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author prelle
 *
 */
public class CalculateSkillPools implements ProcessingStep {

	private final static Logger logger = System.getLogger(CalculateSkillPools.class.getPackageName());

	private Shadowrun6Character model;
	private Locale loc = Locale.getDefault();

	//-------------------------------------------------------------------
	public CalculateSkillPools(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	public CalculateSkillPools(Shadowrun6Character model, Locale loc) {
		this.model = model;
		this.loc   = loc;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		for (SR6SkillValue sval : model.getSkillValues() ) {
			calculatePool(sval);
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void calculatePool(SR6SkillValue sVal) {
		Pool<Integer> pool = new Pool<Integer>();
		sVal.setPool(pool);

		// Clone pool for attribute, if possible
		ShadowrunAttribute attr = sVal.getSkill().getAttribute();
		if (attr!=null) {
//			pool = (Pool<Integer>) model.getAttribute(attr).getPool().clone();
			AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(attr);
			pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(
					Math.max(aVal.getModifiedValue(ValueType.ARTIFICIAL), aVal.getModifiedValue(ValueType.AUGMENTED)),
					attr.getName(loc)));
		}

		int augmentedMax = sVal.getDistributed() + 4;

		/*
		 * Natural attribute first
		 */
		pool.addStep(ValueType.NATURAL, new PoolCalculation<Integer>(sVal.getDistributed(), sVal.getModifyable().getName(loc)));
		// Add all natural modifiers
		for (Modification tmp : sVal.getIncomingModifications()) {
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
		for (Modification tmp : sVal.getIncomingModifications()) {
			if (!(tmp instanceof ValueModification))
				continue;
			ValueModification mod = (ValueModification)tmp;
			if (mod.getSet()!=ValueType.AUGMENTED)
				continue;
			if (mod instanceof CheckModification) {
				pool.addCheckModification((CheckModification) mod);
				continue;
			}
			int value = mod.getValue();
			if (mod.getSource()==null) {
				logger.log(Level.WARNING, "No source for modification "+mod);
			}
			String name = Shadowrun6Tools.getModificationSourceString(mod.getSource());
			PoolCalculation<Integer> toAdd = new PoolCalculation<Integer>(value, name, true);
			if (sumAugmentations+value > 4) {
				value = 4 - sumAugmentations;
				toAdd.value = value;
				toAdd.hitLimit = true;
			}
			sumAugmentations += value;
			pool.addStep(ValueType.NATURAL, toAdd);

			if (name.startsWith("?")) {
				System.err.println("Unknown modification source "+mod.getSource()+" for "+mod+" in attribute "+sVal);
				System.exit(1);
			}
		}
		logger.log(Level.DEBUG, "NATURAL: {0}",pool.getCalculation(ValueType.NATURAL));

		/* Artificial */
		int sumArt = 0;
		for (Modification tmp : sVal.getIncomingModifications()) {
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

		logger.log(Level.DEBUG, "{0}: converted {1} to {2}", sVal.getModifyable(), sVal.getModifiedValue(), sVal.getPool().toString());
	}

}
