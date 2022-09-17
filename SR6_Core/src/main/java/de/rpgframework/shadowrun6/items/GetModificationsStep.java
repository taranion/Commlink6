package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.formula.FormulaImpl;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GetModificationsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public GetModificationsStep() {
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void decideModification(Modification check, List<Modification> unprocessed, CarriedItem<?> model, Lifeform charac) {
		Modification realMod = instantiateModification(check, model, charac);
		
		ApplyTo apply = realMod.getApplyTo();
		if (apply==null) {
			apply = guessModificationTarget(realMod, model);
			logger.log(Level.INFO, "Guess modification {0} is applied to {1}", realMod, apply);
		}
		
		switch (apply) {
		case CHARACTER:
		case UNARMED:
			model.addCharacterModification(realMod);
			break;
		case ACTIVE_GEAR:
		case DATA_ITEM:
			model.addModification(realMod);
			break;
		default:
			logger.log(Level.WARNING, "Don't know how to decide for "+ apply);
			unprocessed.add(realMod);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * For unresolved ValueModifications, resolve the formula and create a
	 * new modification instance
	 */
	@SuppressWarnings("rawtypes")
	private Modification instantiateModification(Modification check, CarriedItem<?> model, Lifeform charac) {
		if (!(check instanceof ValueModification))
			return check;
		
		ValueModification mod = (ValueModification)check;
		if (mod.getFormula()!=null && !mod.getFormula().isResolved()) {
			logger.log(Level.INFO, "  requires resolving: "+mod.getFormula());
			String result = FormulaTool.resolve(ShadowrunReference.ITEM_ATTRIBUTE, (FormulaImpl) mod.getFormula(), new VariableResolver(model, charac));
			logger.log(Level.INFO, "  resolved to "+result);
			ValueModification newMod = new ValueModification(mod.getReferenceType(), mod.getKey(), result);
			newMod.setApplyTo(mod.getApplyTo());
			newMod.setWhen(mod.getWhen());
			newMod.setSource(mod.getSource());
			logger.log(Level.INFO, "Resolve "+mod.getFormula()+" to "+result+" and add "+newMod);
			return newMod;
		}
		return check;
	}

	//-------------------------------------------------------------------
	private ApplyTo guessModificationTarget(Modification check, CarriedItem<?> model) {
		if (check instanceof DataItemModification) {
			DataItemModification mod = (DataItemModification) check;
			switch ((ShadowrunReference) mod.getReferenceType()) {
			case ATTRIBUTE:
			case ACTION:
			case SKILL:
				return ApplyTo.CHARACTER;
			case ITEM_ATTRIBUTE:
			case HOOK:
				return ApplyTo.DATA_ITEM;
			default:
				logger.log(Level.ERROR, "No processing for reference type: " + mod);
			}
		} else {
			logger.log(Level.ERROR, "Don't know how to handle "+ check.getClass());
		}
		return ApplyTo.DATA_ITEM;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(String indent, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		if (model.getResolved() != null) {
			model.getResolved().getModifications().forEach(m -> decideModification(m, unprocessed, model, charac));
		}
		if (model.getVariant() != null) {
			model.getVariant().getModifications().forEach(m -> decideModification(m, unprocessed, model, charac));
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
