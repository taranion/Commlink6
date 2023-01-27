package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonMetatypeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.PriorityCharacterGenerator;

/**
 * @author prelle
 *
 */
public class PointBuyMetatypeController extends CommonMetatypeGenerator {

	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);

//	private List<SR6MetaType> availableOptions;

	//-------------------------------------------------------------------
	public PointBuyMetatypeController(SR6CharacterController parent) {
		super(parent);
//		availableOptions  = new ArrayList<>();
//		availableOptions.addAll(Shadowrun6Core.getItemList(SR6MetaType.class));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		availableOptions.clear();
		for (SR6MetaType meta : Shadowrun6Core.getItemList(SR6MetaType.class)) {
			MetaTypeOption opt = new MetaTypeOption(meta, meta.getKarma());
			opt.setSpecialAttributePoints(0);
			availableOptions.put(meta, opt);
		}

		for (Modification mod : previous) {
			unprocessed.add(mod);
		}

		Shadowrun6Character model = parent.getModel();
		logger.log(Level.DEBUG, "Available metatype options: "+availableOptions);
		SR6MetaType selected = model.getMetatype();
		logger.log(Level.DEBUG, "  selected: "+selected);

		/*
		 * If a metatype is selected, apply it
		 */
		if (selected==null) {
			todos.add(new ToDoElement(Severity.STOPPER, RES.getString("pointbuy.todo.metatype")));
		} else {
			// A selection has been made
			if (!availableOptions.containsKey(selected)) {
				logger.log(Level.WARNING, "Deselected metatype since it is not available anymore");
				model.setMetatype(null);
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("pointbuy.todo.metatype")));
			} else {
				int karma = selected.getKarma();
				if (karma>0) {
					logger.log(Level.INFO, "Pay "+karma+" for metatype "+selected.getId());
					model.setKarmaFree(model.getKarmaFree()-karma);
				}
				// Applying modification is a special CharacterProcessor from Core
			}
		}

		return unprocessed;
	}

}
