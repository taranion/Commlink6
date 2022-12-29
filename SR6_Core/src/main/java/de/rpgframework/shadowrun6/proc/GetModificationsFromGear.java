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
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GetModificationsFromGear implements ProcessingStep {
	
	protected static final Logger logger = System.getLogger(GetModificationsFromGear.class.getPackageName());
	
	private Shadowrun6Character model;
	
	//-------------------------------------------------------------------
	public GetModificationsFromGear(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	private List<Modification> calculateModifications(CarriedItem<ItemTemplate> item) {
		OperationResult<List<Modification>> modResult = SR6GearTool.recalculate("", model, item);
		if (modResult.hasError()) {
			logger.log(Level.WARNING, "Problem with {0}: {1}", item.getKey(), modResult.getError());
			return new ArrayList<>();
		}
		return modResult.get();
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
			for (CarriedItem<? extends PieceOfGear> item : model.getCarriedItems()) {
				try {
					if (item.isDirty()) {
						SR6GearTool.recalculate("", model, item);
					}
					
					for (Modification mod : item.getCharacterModifications()) {
						logger.log(Level.INFO, "--item "+item.getKey()+": "+mod+"  apply="+mod.getApplyTo());
						// Make specific instances of the modification (if necessary)
						// Calls ShadowrunTools.instantiateModification 
						logger.log(Level.TRACE, "--item {0}: preMod={1} ", item.getKey(), mod);
						Modification realMod = mod.getReferenceType().instantiateModification(mod, item, model);
						logger.log(Level.TRACE, "--item {0}: realMod={1} ", item.getKey(), realMod);

						unprocessed.add(realMod);
					}
				} catch (Exception e) {
					logger.log(Level.ERROR, "Error processing item {0} of {1}: {2}", item.getKey(), model.getName(), e.toString());
					logger.log(Level.ERROR, "Error was",e);
					lastException = e;
				}				
			}
			// If there was an error, inform user
			if (lastException!=null) {
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error processing gear of "+model.getName(), lastException);
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
