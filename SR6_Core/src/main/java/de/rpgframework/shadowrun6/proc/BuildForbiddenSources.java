package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class BuildForbiddenSources implements ProcessingStep {

	private final static Logger logger = System.getLogger(BuildForbiddenSources.class.getPackageName()+".forbid");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public BuildForbiddenSources(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {

		logger.log(Level.TRACE, "ENTER: process");
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			for (Modification tmp : item.getOutgoingModifications()) {
				if (tmp instanceof DataItemModification) {
					DataItemModification mod = (DataItemModification) tmp;
					if (mod.isRemove()) {
						Object resolved = mod.getResolvedKey();
						logger.log(Level.WARNING, "Ignore "+resolved+" from "+mod.getSource());
						model.addForbiddenSource(resolved);
					}
				}
			}
		}
		return previous;
	}

}
