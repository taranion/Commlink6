package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.persist.IntegerArrayConverter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.persist.AttackRatingArrayConverter;

/**
 * @author prelle
 *
 */
public class ApplyAmmunitionTypeStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	//-------------------------------------------------------------------
	/**
	 */
	public ApplyAmmunitionTypeStep() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String, de.rpgframework.genericrpg.data.Lifeform, de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(boolean strict, ModifiedObjectType ref, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		ItemAttributeNumericalValue<SR6ItemAttribute> attrib = model.getAsValue(SR6ItemAttribute.PRICE);

		// Apply ammunition type modifier
		// Search choice for item type first
		ItemTemplate item = (ItemTemplate) model.getResolved();
		Optional<Choice> ammoTypeChoice = item.getChoices()
			.stream()
			.filter(c -> c.getChooseFrom()==ShadowrunReference.AMMUNITION_TYPE)
			.findFirst();
		if (ammoTypeChoice.isPresent()) {
			Decision dec = model.getDecision(ammoTypeChoice.get().getUUID());
			if (dec==null) {
				logger.log(Level.ERROR, "Item {0} should have an ammunition type decision {1}, but there isn't one", model.getKey(), ammoTypeChoice.get().getUUID());
			} else {
				AmmunitionType type = Shadowrun6Core.getItem(AmmunitionType.class, dec.getValue());
				if (type==null) {
					logger.log(Level.ERROR, "Choice for unknown ammunition type ''{0}''", dec.getValue());
				} else {
					int target = (int) (attrib.getDistributed() * type.getCostMultiplier());
					int diff = target - attrib.getDistributed();
					logger.log(Level.DEBUG, "Ammo type ''{0}'' multiplies with {1}, therefore add {2} Nuyen", dec.getValue(), type.getCostMultiplier(), diff);
					attrib.addIncomingModification(new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, SR6ItemAttribute.PRICE.name(), diff) );

					List<Modification> passUp = new ArrayList<>();
					for (Modification mod : type.getOutgoingModifications()) {
						logger.log(Level.INFO, "Add modification {0} -> {1}",mod.getApplyTo(),mod);
						if (mod.getApplyTo()==null || mod.getApplyTo()==ApplyTo.DATA_ITEM) {
							if (mod.getReferenceType()==ShadowrunReference.ITEM_ATTRIBUTE) {
								ValueModification valMod = (ValueModification)mod;
								SR6ItemAttribute key = valMod.getResolvedKey();
								ItemAttributeValue<SR6ItemAttribute> attrib2 = (ItemAttributeValue<SR6ItemAttribute>) model.getAttributeRaw(key);
								if (attrib2==null) {
									// Item Attribute value does not yet exist
									attrib2 = new ItemAttributeNumericalValue<SR6ItemAttribute>(key, valMod.getValue());
									try {
										if (key==SR6ItemAttribute.ATTACK_RATING)
											attrib2 = new ItemAttributeObjectValue<>(key, (new AttackRatingArrayConverter()).read(valMod.getRawValue()));
									} catch (Exception e) {
										logger.log(Level.ERROR, "Failed converting attack rating: "+valMod.getRawValue(),e);
									}
									model.setAttribute(key, attrib2);
								} else {
									logger.log(Level.DEBUG, "Add modification {0} to {1}", mod, attrib2);
									attrib2.addIncomingModification(mod);
								}
							}
						} else
							passUp.add(mod);
					}
					unprocessed.addAll(passUp);
				}

			}
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
