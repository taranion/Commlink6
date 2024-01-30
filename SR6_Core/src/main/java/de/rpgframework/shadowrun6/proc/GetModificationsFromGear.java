package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;

/**
 * @author prelle
 *
 */
public class GetModificationsFromGear implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsFromGear.class.getPackageName()+".gear");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsFromGear(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE, "ENTER: process");
		try {
			Throwable lastException = null;
			for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
				try {
					logger.log(Level.DEBUG, "isDirty({0}) = {1}", item.getKey(), item.isDirty());
					if (item.isDirty()) {
						SR6GearTool.recalculate("", model, item);
					}

					for (Modification mod : item.getOutgoingModifications()) {
						logger.log(Level.TRACE, "--item "+item.getKey()+": "+mod+"  apply="+mod.getApplyTo());
						// Make specific instances of the modification (if necessary)
						int multiplier = ItemUtil.getRating((CarriedItem<ItemTemplate>) item);
						if (mod instanceof ValueModification && ((ValueModification)mod).isInstantiated())
							multiplier=1;
						// Calls ShadowrunTools.instantiateModification
						logger.log(Level.TRACE, "--item {0}: preMod={1} ", item.getKey(), mod);
						Modification realMod = mod.getReferenceType().instantiateModification(mod, item, multiplier, model);
						logger.log(Level.TRACE, "--item {0}: realMod={1} ", item.getKey(), realMod);

						unprocessed.add(realMod);
					}
				} catch (Exception e) {
					logger.log(Level.ERROR, "Error processing item {0} of {1}: {2}", item.getKey(), model.getName(), e.toString());
					logger.log(Level.ERROR, "Error was",e);
					lastException = e;
				}

				// Check for alternate usages
				checkAlternateUsages(item);

				// Check accessories for alternate usages
				for (CarriedItem<ItemTemplate> alt : item.getAccessories()) {
					checkAlternateUsages(alt);
				}
			}
			// If there was an error, inform user
			if (lastException!=null) {
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error processing gear of "+model.getName(), lastException);
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		logger.log(Level.DEBUG,"return "+ unprocessed);
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void checkAlternateUsages(CarriedItem<ItemTemplate> item) {
		for (CarriedItem<?> alt : item.getAlternates()) {
			logger.log(Level.WARNING, "Found alternate item {0} of {1}", alt, item);
			alt.setInjectedBy(item);
			logger.log(Level.TRACE, "{0}",alt.dump());
			model.addVirtualCarriedItem((CarriedItem<ItemTemplate>) alt);
		}
	}

}
