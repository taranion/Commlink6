package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.AItemEnhancement;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.items.OperationMode;
import de.rpgframework.genericrpg.items.formula.FormulaImpl;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modification.Origin;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.RelevanceModification;
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
//			logger.log(Level.INFO, "Guess modification {0} is applied to {1}", realMod, apply);
		}

		switch (apply) {
		case CHARACTER:
		case UNARMED:
		case PERSONA:
			logger.log(Level.INFO, "Add outgoing {2} modification {0} to {1}", realMod, model, apply);
			model.addOutgoingModification(realMod);
			break;
		case ACTIVE_GEAR:
		case DATA_ITEM:
			logger.log(Level.INFO, "Add incoming {2} modification {0} to {1}", realMod, model, apply);
			model.addIncomingModification(realMod);
			break;
//		case PARENT:
//			logger.log(Level.INFO, "Add incoming {2} modification {0} to {1}", realMod, model.getParent(), apply);
//			if (model.getParent()==null) {
//				logger.log(Level.INFO, "No parent found for "+model);
//				return;
//			}
//			if (realMod.getReferenceType()==ShadowrunReference.HOOK)
//				realMod.setApplyTo(ApplyTo.DATA_ITEM);
//			model.addOutgoingModification(realMod);
//			model.getParent().addIncomingModification(realMod);
//			break;
		default:
//			logger.log(Level.WARNING, "Don't know how to decide for "+ apply);
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

		ValueModification mod = ((ValueModification)check).clone();

		if ("CHOICE".equals(mod.getKey())) {
			// Replace REF with decision from choice
			if (mod.getConnectedChoice()==null) {
				logger.log(Level.ERROR, "{0} has a CHOICE modification without UUID", model.getKey());
			} else {
				Decision dec = model.getDecision(mod.getConnectedChoice());
				if (dec==null) {
					logger.log(Level.WARNING, "{0}: Decision {1} for CHOICE mod not made", model.getKey(), mod.getConnectedChoice());
				} else {
					mod.setKey(dec.getValue());
					logger.log(Level.DEBUG, "{0}: Decision {1} for choice {2}", model.getKey(), dec.getValue(), mod.getConnectedChoice());
				}
			}
		}

		if (mod.getFormula()!=null && !mod.getFormula().isResolved()) {
			logger.log(Level.DEBUG, "  requires resolving: "+mod.getFormula());
			String result = FormulaTool.resolve(ShadowrunReference.ITEM_ATTRIBUTE, (FormulaImpl) mod.getFormula(), new VariableResolver(model, charac));
			logger.log(Level.DEBUG, "  resolved to "+result);
			// Follow table
			if (mod.getLookupTable()!=null) {
				int index = 1;
				try { index = Integer.parseInt(result); } catch (NumberFormatException nfe) {
					logger.log(Level.ERROR, "Found a table in {1}, but index is not a number: {0}/{2}",result, mod, mod.getFormula());
				}
				if (index>mod.getLookupTable().length) {
					logger.log(Level.ERROR, "Found a table in {1}, but index {0} is outside table",index, mod);
					index = mod.getLookupTable().length;
				}
				index--;
				result = String.valueOf(mod.getLookupTable()[index]);
				logger.log(Level.DEBUG, "  using table resolved to "+result);
			}
			mod.setValue(result);
			mod.getFormula().isResolved();
			mod.setInstantiated(true);
			mod.setOrigin(Origin.CHILDREN);
			logger.log(Level.DEBUG, "  Resolve "+mod.getFormula()+" to "+result+" and add "+mod);
		}
		logger.log(Level.TRACE, "instantiated modification {0} with direction {1}", mod, mod.getOrigin());
		return mod;
	}

	//-------------------------------------------------------------------
	private ApplyTo guessModificationTarget(Modification check, CarriedItem<?> model) {
		if (check instanceof DataItemModification) {
			DataItemModification mod = (DataItemModification) check;
			switch ((ShadowrunReference) mod.getReferenceType()) {
			case ATTRIBUTE:
			case ACTION:
			case SKILL:
			case SKILLSPECIALIZATION:
			case RULE:
				return ApplyTo.CHARACTER;
			case ITEM_ATTRIBUTE:
			case GEAR:
			case GEARMOD:
			case HOOK:
				return ApplyTo.DATA_ITEM;
			default:
				logger.log(Level.ERROR, "No processing for reference type: " + mod+" from "+mod.getSource());
			}
		} else if (check instanceof ModificationChoice ){
			return ApplyTo.DATA_ITEM;
		} else if (check instanceof RelevanceModification ){
			return ApplyTo.CHARACTER;
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
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		if (model.getResolved() != null) {
			model.getResolved().getOutgoingModifications().forEach(m ->  {
				if (m.getReferenceType()==ShadowrunReference.GEAR &&  m instanceof DataItemModification) {
					ItemTemplate pack = ((DataItemModification)m).getResolvedKey();
					if (pack!=null && pack.getItemType()==ItemType.PACK) {
						logger.log(Level.TRACE, "Gear {0} adds a {1} PACK", model.getKey(), pack.getId());
						for (Modification mod : pack.getOutgoingModifications()) {
							decideModification(mod, unprocessed, model, charac);
						}
						logger.log(Level.TRACE, "Done adding from PACK");
					} else {
						decideModification(m.setOrigin(Origin.CHILDREN), unprocessed, model, charac);
					}
				} else {
					decideModification(m.setOrigin(Origin.CHILDREN), unprocessed, model, charac);
				}
			});
		}
		if (model.getVariant() != null) {
			model.getVariant().getOutgoingModifications().forEach(m -> decideModification(m.setOrigin(Origin.CHILDREN), unprocessed, model, charac));
		}
		// Get modifications from enhancements
		for (ItemEnhancementValue<AItemEnhancement> enh : model.getEnhancements()) {
			SR6ItemEnhancement real = (SR6ItemEnhancement) enh.getResolved();
			if (real==null) {
				logger.log(Level.ERROR, "No resolution for item enhancement "+enh.getKey());
				continue;
			}
			real.getOutgoingModifications().forEach(m -> decideModification(m.setOrigin(Origin.CHILDREN), unprocessed, model, charac));
			model.getAsValue(SR6ItemAttribute.PRICE).addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), real.getPrice(), real));
		}

		for (OperationMode mode : model.getActiveOperationModes(true)) {
			mode.getModifications().forEach(m -> decideModification(m.setOrigin(Origin.CHILDREN), unprocessed, model, charac));
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
