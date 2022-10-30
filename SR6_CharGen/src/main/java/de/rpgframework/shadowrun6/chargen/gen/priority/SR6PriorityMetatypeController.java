package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.classification.Gender;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonMetatypeGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6PriorityMetatypeController extends CommonMetatypeGenerator {

	private final static Logger logger = System.getLogger(SR6PriorityMetatypeController.class.getPackageName()+".meta");
	
	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);
	
	//-------------------------------------------------------------------
	public SR6PriorityMetatypeController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();
		Shadowrun6Character model = getModel();

		try {
			availableOptions.clear();
			todos.clear();
			
			for (Modification mod : previous) {
				if (mod.getReferenceType()==ShadowrunReference.METATYPE) {
					if (mod instanceof ValueModification) {
						ValueModification vMod = (ValueModification)mod;
						SR6MetaType meta = vMod.getResolvedKey();
						if (meta!=null) {
							MetaTypeOption opt = new MetaTypeOption(meta, meta.getKarma());
							opt.setSpecialAttributePoints(vMod.getValue());
							availableOptions.put(meta, opt);
							if (logger.isLoggable(Level.TRACE))
								logger.log(Level.TRACE, "Allow {0} for {1} cust. Karma and {2} AP", vMod.getKey(), opt.getAdditionalKarmaKost(), opt.getSpecialAttributePoints());
						}
					}
				} else {
					unprocessed.add(mod);
				}
			}
			logger.log(Level.INFO, "Found {0} allowed metatypes", availableOptions.size());

			// Add modifications from selection
			MetaType meta = getModel().getMetatype();
			if (meta == null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES, "generror.metatype_not_selected"));
			} else {
				MetaTypeOption opt = availableOptions.get(meta);
				if (opt==null) {
					List<MetaType> metas = new ArrayList<>(availableOptions.keySet());
					Collections.sort(metas, new Comparator<MetaType>() {
						public int compare(MetaType o1, MetaType o2) {
							if (o1.getVariantOf()!=null && o2.getVariantOf()!=null)
								return o1.getVariantOf().getName().compareTo(o2.getVariantOf().getName());
							if (o1.getVariantOf()!=null && o2.getVariantOf()==null)
								return o1.getVariantOf().getName().compareTo(o2.getName());
							if (o1.getVariantOf()==null && o2.getVariantOf()!=null)
								return o1.getName().compareTo(o2.getVariantOf().getName());
							return o1.getName().compareTo(o2.getName());
						}
					});
//					MetaType newMeta = metas.iterator().next();
//					logger.log(Level.ERROR, "Metatype ''{0}'' is not allowed - use ''{1}'' instead", meta.getId(), newMeta.getId());
//					getModel().setMetatype(newMeta);
//					opt = availableOptions.get(newMeta);
//					meta = newMeta;
					logger.log(Level.ERROR, "Metatype ''{0}'' is not allowed", meta.getId());
					todos.add(new ToDoElement(Severity.STOPPER, IRejectReasons.TODO_METATYPE_NOT_ALLOWED));
					getModel().setMetatype(null);
					meta = null;
					return unprocessed;
				}
				
				unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), opt.getSpecialAttributePoints()));				
				
				if (meta.getKarma() != 0) {
					logger.log(Level.INFO, "Pay {0} Karma for metatype ''{1}''", meta.getKarma(), meta.getId());
					model.setKarmaFree(model.getKarmaFree() - meta.getKarma());
				}
				// Add more modifications
//				unprocessed.addAll(meta.getModifications());
				// Already done in GetModificationsFromMetaType
			}
		return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
