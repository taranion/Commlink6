package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ApplyStockModificationsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public ApplyStockModificationsStep() {
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac,
			CarriedItem<?> model, List<Modification> unprocessed) {

		logger.log(Level.DEBUG, "Decisions are {0}",model.getDecisions());

		@SuppressWarnings("unchecked")
		CarriedItem<ItemTemplate> model2 = (CarriedItem<ItemTemplate>) model;
		// Read all modifications that are meant for this item
		for (Modification tmp : model.getIncomingModifications()) {
			try {
				logger.log(Level.INFO, "Process {0}", tmp);
				if (tmp instanceof ValueModification) {
					applyModification(strict, charac, model2, (ValueModification) tmp);
				} else if (tmp instanceof EmbedModification) {
					embedModification(strict, charac, model2, (EmbedModification) tmp);
				} else if (tmp instanceof DataItemModification) {
					applyModification(strict, charac, model2, (DataItemModification) tmp);
				} else if (tmp instanceof ModificationChoice) {
					if (strict) {
						logger.log(Level.ERROR, "Unsupported modification: " + tmp);
					}
				} else {
					logger.log(Level.ERROR, "Unsupported modification: " + tmp);
				}
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error processing "+tmp+" from "+tmp.getSource(),e);
				System.err.println("ApplyStockModifications: Error processing "+tmp+" from "+tmp.getSource());
			}
		}

		for (Modification tmp : new ArrayList<>(unprocessed)) {
			try {
				logger.log(Level.WARNING, "Process {0}", tmp);
				boolean processed = false;
				if (tmp instanceof ValueModification) {
					processed = applyModification(strict, charac, model2, (ValueModification) tmp);
				} else if (tmp instanceof EmbedModification) {
					processed = embedModification(strict, charac, model2, (EmbedModification) tmp);
				} else if (tmp instanceof DataItemModification) {
					processed = applyModification(strict, charac, model2, (DataItemModification) tmp);
				} else {
					logger.log(Level.ERROR, "Unsupported modification: " + tmp);
				}
				if (processed) {
					unprocessed.remove(tmp);
				}
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error processing "+tmp+" from "+tmp.getSource(),e);
				System.err.println("ApplyStockModifications: Error processing "+tmp+" from "+tmp.getSource());
			}
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

	// -------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "incomplete-switch" })
	private boolean applyModification(boolean strict, Lifeform charac, CarriedItem<ItemTemplate> model, DataItemModification mod) {
		if (mod.getApplyTo() == ApplyTo.CHARACTER || mod.getApplyTo() == ApplyTo.UNARMED) {
			model.addCharacterModification(mod);
			logger.log(Level.WARNING, "Ignore for now " + mod);
			return false;
		}

		Decision[] decs = new Decision[mod.getDecisions().size()];
		decs = mod.getDecisions().toArray(decs);
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case GEARMOD:
			SR6ItemEnhancement itemenh = Shadowrun6Core.getItem(SR6ItemEnhancement.class, mod.getKey());
			if (itemenh!=null) {
				ItemEnhancementValue<SR6ItemEnhancement> enhVal = new ItemEnhancementValue<SR6ItemEnhancement>(itemenh);
				model.addAutoEnhancement(enhVal);
			} else {
				logger.log(Level.ERROR, "Unknown item enhancement ''{0}'' referenced in {0}", mod.getSource());
			}
			return true;
		case HOOK:
			ItemHook hook = mod.getResolvedKey();
			if (hook==ItemHook.SOFTWARE && model.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS)!=null) {
				logger.log(Level.INFO, "Add slot {0} and take capacity from CONCURRENT_PROGRAMS ", hook);
				AvailableSlot slot = new AvailableSlot(hook, model.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS).getDistributed());
				model.addSlot(slot);
			} else {
				CarriedItem<ItemTemplate> parent = model.getParent();
				// If possible, assume slot should be inserted into parent
				AvailableSlot slot = null;
				if (parent==null) {
					//logger.log(Level.WARNING, "HOOK {1} modification for item without a parent: {0}", model.getKey(), hook);
					slot = model.getSlot(hook);
				} else {
					slot = parent.getSlot(hook);
				}
				if (mod.isRemove()) {
					logger.log(Level.INFO, "Remove slot {0} from {1}", hook, mod.getSource());
					model.removeSlot(hook);
				} else {
					if (hook.hasCapacity) {
						ValueModification valMod = (ValueModification)mod;
						if (slot!=null) {
							logger.log(Level.INFO, "For slot {0} add capacity {1} from {2}", hook, valMod.getValueAsDouble(), mod.getSource());
							slot.addIncomingModification(valMod);
						} else {
							if (valMod.isDouble()) {
								logger.log(Level.INFO, "Add slot {0} with capacity {1} from {2}", hook, valMod.getValueAsDouble(), mod.getSource());
								slot = new AvailableSlot(hook, (float) valMod.getValueAsDouble());
							} else {
								logger.log(Level.INFO, "Add slot {0} with capacity {1} from {2}", hook, valMod.getValue(), mod.getSource());
								slot = new AvailableSlot(hook, valMod.getValue());
							}
							model.addSlot(slot);
						}
					} else {
						if (slot!=null) {
							logger.log(Level.INFO, "Slot {0} to add from {1} already exists", hook, mod.getSource());
						} else {
							logger.log(Level.INFO, "Add slot {0} without capacity from {1}", hook, mod.getSource());
							slot = new AvailableSlot(hook);
							model.addSlot(slot);
						}
					}
				}
			}
			return true;
		case GEAR:
			model.addCharacterModification(mod);
			return true;
		case ITEM_ATTRIBUTE:
			if (mod instanceof ValueModification) {
				ItemUtil.addOrSetItemAttribute(model, (ValueModification)mod);
				return true;
			} else {
				logger.log(Level.ERROR, "Not implemented: "+mod.getClass());
				return false;
			}
		case ITEMFLAG:
			model.setAutoFlag((Enum)mod.getResolvedKey(), true);
			return true;
		}
		logger.log(Level.WARNING, "ToDo: DataItemModification " + mod+" / "+mod.getReferenceType());
		System.err.println("ApplyStockModification: unsupported modification type: "+mod.getReferenceType());
//		model.addModification(mod);
		return false;
	}

	// -------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "incomplete-switch" })
	private boolean embedModification(boolean strict, Lifeform charac, CarriedItem<?> model, EmbedModification mod) {
		logger.log(Level.DEBUG, "Before processing "+mod+" Decisions are "+model.getDecisions());
		if (mod.getApplyTo() == ApplyTo.CHARACTER || mod.getApplyTo() == ApplyTo.UNARMED) {
			model.addCharacterModification(mod);
			logger.log(Level.WARNING, "Ignore for now " + mod);
			return false;
		}

		// Merge the decisions of the modifications and the item itself
		logger.log(Level.DEBUG, "Embed "+mod+" with decisions="+mod.getDecisions());
		Decision[] decs = new Decision[mod.getDecisions().size()];
		decs = mod.getDecisions().toArray(decs);
		switch ((ShadowrunReference) mod.getReferenceType()) {
		case GEAR:
			ItemTemplate templ = mod.getReferenceType().resolve(mod.getKey());
			if ("CHOICE".equals(mod.getKey())) {
				Shadowrun6Character sr6char = (charac instanceof Shadowrun6Character)?(Shadowrun6Character) charac:null;
				Choice choice = model.getChoiceMapRecursivly(sr6char).get( mod.getConnectedChoice() );
				logger.log(Level.DEBUG, "Search choice "+choice+" in decisions "+mod.getDecisions());
				// Use the decision instead of the key
				for (Decision d : model.getDecisions()) {
//					logger.log(Level.WARNING, "Compare {0} with {1}", d.getChoiceUUID(), mod.getConnectedChoice());
					if (d.getChoiceUUID().equals(mod.getConnectedChoice())) {
						templ = mod.getReferenceType().resolve(d.getValue());
//						logger.log(Level.WARNING, "CHOICE was "+templ);
						break;
					}
				}
				if (templ==null) {
					logger.log(Level.DEBUG, "Could not find a (valid) decision for CHOICE "+mod.getConnectedChoice());
					return true;
				}
			}
			ItemHook hook = mod.getHook();
			logger.log(Level.INFO, "Add instanceof {0} into hook {1} of {2} - with {3} decisions", templ.getId(), hook, model.getKey(), decs.length);
			OperationResult<CarriedItem<ItemTemplate>> carriedR = SR6GearTool.buildItem(templ, CarryMode.EMBEDDED, null, charac, false, model, decs);
			if (carriedR.hasError()) {
//				logger.log(Level.ERROR, "Error embedding {0} into hook {1} of {2}: {3}", mod.getKey(), hook, model.getKey(),carriedR.getError());
				return true;
			}
			CarriedItem accessory = carriedR.get();
			accessory.setInjectedBy(mod.getSource());
			//if (mod.isIncludedInStats())
			// Check if AvailableSlot already exists - if not, create one
			AvailableSlot slot = (AvailableSlot) model.getSlot(hook);
			if (slot==null) {
				if (hook.hasCapacity && strict) {
					logger.log(Level.WARNING, "Item {0} (from {1}) has an <embed> for a not existing slot {2}, but cannot auto-create slot since no capacity is given",
							mod.getKey(), mod.getSource(), hook);
					return false;
				}
				slot = new AvailableSlot(hook);
				model.addSlot(slot);
			}
			slot.addEmbeddedItem(accessory);

			// Now apply modifications that the new accessory provides
			for (Modification mod2 : accessory.getIncomingModifications()) {
				if (mod2 instanceof DataItemModification) {
					applyModification(false, charac, (CarriedItem<ItemTemplate>) model, (DataItemModification)mod2);
				}
			}
			return true;
		}
		logger.log(Level.WARNING, "ToDo: EmbedModification " + mod);
//		model.addModification(mod);
		return false;
	}

}
