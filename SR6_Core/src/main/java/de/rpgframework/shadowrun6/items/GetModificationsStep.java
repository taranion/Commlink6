package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class GetModificationsStep implements CarriedItemProcessor {

	final static Logger logger = SR6GearTool.logger;

	// -------------------------------------------------------------------
	public GetModificationsStep() {
	}

	// -------------------------------------------------------------------
	private void decideModification(Modification check, List<Modification> unprocessed, CarriedItem<?> model) {
		if (check.getApplyTo() != null) {
			switch (check.getApplyTo()) {
			case CHARACTER:
			case POINTS:
			case RULES:
			case UNARMED:
				logger.log(Level.INFO, "Add character modification: " + check);
				model.addCharacterModification(check);
				break;
			case ACTIVE_GEAR:
				unprocessed.add(check);
				break;
			default:
				logger.log(Level.ERROR, "Don't know how to handle {0}", check.getApplyTo());
				unprocessed.add(check);
			}
		}
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.CarriedItemProcessor#process(java.lang.String,
	 *      de.rpgframework.genericrpg.data.Lifeform,
	 *      de.rpgframework.genericrpg.items.CarriedItem, java.util.List)
	 */
	@Override
	public OperationResult<List<Modification>> process(String indent, Lifeform charac, CarriedItem<?> model,
			List<Modification> unprocessed) {

		if (model.getResolved() != null) {
			model.getResolved().getModifications().forEach(m -> decideModification(m, unprocessed, model));
		}
		if (model.getVariant() != null) {
			model.getVariant().getModifications().forEach(m -> decideModification(m, unprocessed, model));
		}

		return new OperationResult<List<Modification>>(unprocessed);
	}

}
