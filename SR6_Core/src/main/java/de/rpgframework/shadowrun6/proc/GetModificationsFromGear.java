package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.SR6GearTool;

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
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE, "ENTER: process");
		try {
			for (CarriedItem<? extends PieceOfGear> item : model.getCarriedItems()) {
				logger.log(Level.DEBUG, "--item "+item.getKey());
				OperationResult<List<Modification>> modResult = SR6GearTool.recalculate("", model, item);
				if (modResult.hasError()) {
					logger.log(Level.WARNING, "Problem with {0}: {1}", item.getKey(), modResult.getError());
					continue;
				}
				for (Modification mod : modResult.get()) {
					if (mod.getApplyTo()==ApplyTo.CHARACTER) {
						logger.log(Level.DEBUG, "Add modifications from gear {0}: {1}", item.getKey(), mod);
						unprocessed.add(mod);
					}
				}
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
