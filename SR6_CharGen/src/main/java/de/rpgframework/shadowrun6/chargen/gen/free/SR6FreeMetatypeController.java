package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
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
public class SR6FreeMetatypeController extends CommonMetatypeGenerator {

	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);

//	private List<SR6MetaType> availableOptions;

	//-------------------------------------------------------------------
	public SR6FreeMetatypeController(SR6CharacterController parent) {
		super(parent);
//		availableOptions  = new ArrayList<>();
//		availableOptions.addAll(Shadowrun6Core.getItemList(SR6MetaType.class));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#canBeSelected(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean canBeSelected(SR6MetaType type) {
		return true;
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


		return unprocessed;
	}

}
