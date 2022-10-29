package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
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
			for (CarriedItem<? extends PieceOfGear> item : model.getCarriedItems()) {
				if (item.isDirty()) {
					SR6GearTool.recalculate("", model, item);
				}
				
				logger.log(Level.DEBUG, "--item "+item.getKey());
				for (Modification mod : item.getCharacterModifications()) {
					logger.log(Level.INFO, "--item "+item.getKey()+": "+mod+"  apply="+mod.getApplyTo());
					// TODO:
					// Shadowrun6Tools.instantiateModification
					// um CHOICEs (z.B. von Reflex Recorder) mit Entscheidung zu verknüpfen
					Modification realMod = mod.getReferenceType().instantiateModification(mod, item, model);
					logger.log(Level.INFO, "--item "+item.getKey()+": realMod="+realMod);
					
//					if (mod instanceof ValueModification) {
//						ValueModification vMod = ((ValueModification)mod);
//						logger.log(Level.INFO, "--item "+item.getKey()+": 1-> "+vMod.hasFormula());
//						if (vMod.hasFormula()) {
//							logger.log(Level.INFO, "--item "+item.getKey()+": 2-> "+vMod.getFormula().isResolved());
//							if (!vMod.getFormula().isResolved()) {
//								logger.log(Level.INFO, "--item "+item.getKey()+": 3-> Todo: Resolve "+vMod.getFormula());
//								String resolved = FormulaTool.resolve(ShadowrunReference.ITEM_ATTRIBUTE, vMod.getFormula(), new VariableResolver(item, model));
//								logger.log(Level.INFO, "--item "+item.getKey()+": 4-> Resolved = "+resolved);
//								
//								System.exit(1);
//							}
//						}
//					}
					unprocessed.add(realMod);
				}
//				OperationResult<List<Modification>> modResult = SR6GearTool.recalculate("", model, item);
//				if (modResult.hasError()) {
//					logger.log(Level.WARNING, "Problem with {0}: {1}", item.getKey(), modResult.getError());
//					continue;
//				}
//				for (Modification mod : modResult.get()) {
//					if (mod.getApplyTo()==ApplyTo.CHARACTER) {
//						logger.log(Level.DEBUG, "Add modifications from gear {0}: {1}", item.getKey(), mod);
//						unprocessed.add(mod);
//					}
//				}
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
