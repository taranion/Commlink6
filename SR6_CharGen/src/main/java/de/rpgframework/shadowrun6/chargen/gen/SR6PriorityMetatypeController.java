package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
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
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6PriorityMetatypeController extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {

	private final static Logger logger = System.getLogger(SR6PriorityMetatypeController.class.getPackageName()+".meta");
	
	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.ENGLISH);

	private Map<SR6MetaType, MetaTypeOption> availableOptions;
	private static Random random = new Random();
	
	//-------------------------------------------------------------------
	public SR6PriorityMetatypeController(SR6CharacterController parent) {
		super(parent);
		availableOptions  = new HashMap<SR6MetaType,MetaTypeOption>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#getAvailable()
	 */
	@Override
	public List<MetaTypeOption> getAvailable() {
		List<MetaTypeOption> ret = new ArrayList<>(availableOptions.values());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#getKarmaCost(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public int getKarmaCost(SR6MetaType type) {
		if (availableOptions.containsKey(type))
			return availableOptions.get(type).getAdditionalKarmaKost();
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#canBeSelected(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean canBeSelected(SR6MetaType type) {
		if (!availableOptions.containsKey(type))
			return false;
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public void roll() {
		// Gender
		float gauss = (float)random.nextGaussian();
		boolean isDiverse = (gauss<-1.2 || gauss>1.2);
		if (isDiverse) {
			getModel().setGender(Gender.DIVERSE);
		} else {
			getModel().setGender( (gauss>=0.0f)?Gender.MALE:Gender.FEMALE ); 
		}

		// Meta
		gauss = (float)random.nextGaussian();
		boolean useVariants = (gauss<-1 || gauss>1);
		logger.log(Level.WARNING, "Roll {0} means useVariants={1}", gauss, useVariants);
		logger.log(Level.WARNING, "PRE: "+ availableOptions.keySet());
		List<SR6MetaType> pick = availableOptions.keySet().stream().filter(m -> (useVariants?(m.getVariantOf()!=null || m.isMetahuman()==false):(m.getVariantOf()==null && m.isMetahuman()))).collect(Collectors.toList());
		logger.log(Level.WARNING, "POST: "+ pick);
		SR6MetaType toSelect = pick.get(random.nextInt(pick.size()));
		logger.log(Level.WARNING, "Selected "+toSelect);
		
		select(toSelect);
		
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#randomizeSizeWeight()
	 */
	@Override
	public void randomizeSizeWeight() {
		MetaType value = getModel().getMetatype();
		if (value==null)
			return;
		// Roll until you get a sensible distribution result
		for (int i=0; i<10; i++) {
			float gauss = (float)random.nextGaussian();
			float diff  = 0.15f*gauss;
			float diff2 = 0.10f*gauss;
			getModel().setSize(Math.round(value.getSize()+value.getSize()*diff));
			getModel().setWeight(Math.round(value.getWeight()+value.getWeight()*diff2));
			if (gauss>1.0f || gauss<-1.0f)
				continue;
			break;
		}	
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#select(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean select(SR6MetaType value) {
		logger.log(Level.DEBUG, "ENTER select("+value+")");
		try {
			if (!canBeSelected(value))
				return false;

			logger.log(Level.INFO, "Select "+value);
			getModel().setMetatype(value);
			randomizeSizeWeight();

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+value+")");
		}
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
					MetaType newMeta = availableOptions.keySet().iterator().next();
					logger.log(Level.ERROR, "Metatype ''{0}'' is not allowed - use ''{1}'' instead", meta.getId(), newMeta.getId());
					getModel().setMetatype(newMeta);
					opt = availableOptions.get(newMeta);
					meta = newMeta;
				}
				
				unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), opt.getSpecialAttributePoints()));				
				
				if (meta.getKarma() != 0) {
					logger.log(Level.INFO, "Pay {0} Karma for metatype ''{1}''", meta.getKarma(), meta.getId());
					model.setKarmaFree(model.getKarmaFree() - meta.getKarma());
				}
				// Add more modifications
				unprocessed.addAll(meta.getModifications());
			}
		return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#selectBodyType(de.rpgframework.shadowrun.BodyType)
	 */
	@Override
	public boolean selectBodyType(BodyType value) {
		logger.log(Level.DEBUG, "ENTER selectBodyType("+value+")");
		try {
			getModel().setBodytype(value);

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE selectBodyType("+value+")");
		}
	}

}
