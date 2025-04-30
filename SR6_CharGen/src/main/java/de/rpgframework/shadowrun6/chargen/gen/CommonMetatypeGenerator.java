package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import de.rpgframework.classification.Gender;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public abstract class CommonMetatypeGenerator extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {

	protected final static Logger logger = System.getLogger(CommonMetatypeGenerator.class.getPackageName()+".meta");

	protected Map<SR6MetaType, MetaTypeOption> availableOptions;
	private static Random random = new Random();

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public CommonMetatypeGenerator(SR6CharacterController parent) {
		super(parent);
		availableOptions  = new HashMap<SR6MetaType,MetaTypeOption>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#getAvailable()
	 */
	@Override
	public List<MetaTypeOption> getAvailable() {
//		return new ArrayList<MetaTypeOption>(availableOptions.values().stream()
//				.filter(p -> parent.showDataItem(p))
//				.collect(Collectors.toMap(
//						MetaTypeOption::getId,          // Key-Mapper: ID as Key
//                        item -> item,             // Value-Mapper: The item as value
//                        (existing, replacement) -> // Merge-Function for duplicates:
//                            existing.getLanguage() != null ? existing : replacement  // prefer Item with Locale
//                )).values());
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
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#select(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean select(SR6MetaType value) {
		logger.log(Level.DEBUG, "ENTER select("+value+")");
		try {
			if (value==null && !canBeSelected(value)) {
				logger.log(Level.ERROR,"Trying to select {0} which is not allowed", value.getId());
				return false;
			}

			logger.log(Level.INFO, "Select "+value);
			if (getModel().getMetatype()==value) return true;
			getModel().setMetatype(value);
			randomizeSizeWeight();

			parent.runProcessors();
			return true;
		} catch (Exception e) {
			logger.log(Level.ERROR,"Error setting metatype",e);
			return false;
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+value+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl#roll()
	 */
	@Override
	public void roll() {
		// Meta
		float gauss = (float)random.nextGaussian();
		boolean useVariants = (gauss<-1 || gauss>1) && (availableOptions.size()>5);
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
		// Gender
		float gauss1 = (float)random.nextGaussian();
		boolean isDiverse = (gauss1<-1.2 || gauss1>1.2);
		if (isDiverse) {
			getModel().setGender(Gender.DIVERSE);
		} else {
			getModel().setGender( (gauss1>=0.0f)?Gender.MALE:Gender.FEMALE );
		}

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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#selectBodyType(de.rpgframework.shadowrun.BodyType)
	 */
	@Override
	public boolean selectBodyType(BodyType value) {
		logger.log(Level.DEBUG, "ENTER selectBodyType("+value+")");
		try {
			if (value!=getModel().getBodytype()) {
				logger.log(Level.INFO, "select body type {0}",value);
				getModel().setBodytype(value);
				QualityValue shifter = getModel().getQuality("shifter");
				if (shifter!=null)
					getModel().removeQuality(shifter);

				switch (value) {
				case SHAPESHIFTER:
					SR6Quality shifterDef = Shadowrun6Core.getItem(SR6Quality.class, "shifter");
					shifter = new QualityValue(shifterDef, 0);
					Decision fins = new Decision(shifterDef.getChoices().get(0), "fins");
					shifter.addDecision(fins);
					getModel().addQuality(shifter);
					break;
				}

				parent.runProcessors();
			}
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE selectBodyType("+value+")");
		}
	}

}
