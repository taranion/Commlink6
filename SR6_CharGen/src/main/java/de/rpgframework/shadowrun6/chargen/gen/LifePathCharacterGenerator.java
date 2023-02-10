package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathMagicOrResonanceController;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathMetatypeController;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathResetGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathSettings;

/**
 * @author stefa
 *
 */
@GeneratorId("life")
public class LifePathCharacterGenerator extends CommonSR6CharacterGenerator {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(LifePathCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	private boolean setupDone;

	//-------------------------------------------------------------------
	/**
	 */
	public LifePathCharacterGenerator() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "life";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] {
				WizardPageType.METATYPE,
				WizardPageType.SR6_LIFEPATH1,
				WizardPageType.MAGIC_OR_RESONANCE,
				WizardPageType.SURGE,
				WizardPageType.INFECTED};
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("generator.name");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return RES.getString("generator.desc");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#setModel(Shadowrun6Character)
	 */
	@Override
	public void setModel(Shadowrun6Character model, CharacterHandle handle) {
		this.model = model;
		this.handle= handle;
		this.setupDone = false;
		SR6LifePathSettings settings = new SR6LifePathSettings();
		settings.variant = PowerLevel.STANDARD;
//		model.addRule(Shadowrun6Rules.CHARGEN_ALLOW_INITIATION, "false");
		model.setCharGenUsed(getId());
		model.setCharGenSettings(settings);
		model.setKarmaFree(50);
		logger.log(Level.INFO, "----------------Start generator-----------------------" + toString() + "\n\n\n");

		try {
			setupProcessChain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Failed on process chain", e);
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#initializeModel()
	 */
	@Override
	protected void initializeModel() {
//		if (model.getCharGenSettings(CommonSR6GeneratorSettings.class) == null  || !(model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6PointBuySettings) ) {
//			if (model.getChargenSettingsJSON() != null  && (model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6PointBuySettings)) {
//				logger.log(Level.INFO, "Restore generator config from {0}", model.getChargenSettingsJSON());
//				SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
//				model.setCharGenSettings(settings);
//			} else {
//				logger.log(Level.INFO, "Create new generator config");
//				SR6PointBuySettings settings = new SR6PointBuySettings();
////		settings.variant = PowerLevel.STANDARD;
//				model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
//				model.setCharGenUsed(getId());
//				model.setCharGenSettings(settings);
//				model.setKarmaFree(50);
//			}
//		}
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl#createPartialController()
	 */
	@Override
	protected void createPartialController() {
//		attributes = new PointBuySR6AttributeGenerator(this);
		meta = new SR6LifePathMetatypeController(this);
		magicReso = new SR6LifePathMagicOrResonanceController(this);
//		skill = new PointBuySR6SkillGenerator(this);
		martial   = new SR6MartialArtsController(this);
		logger.log(Level.INFO, "meta = " + getMetatypeController() + "  of " + this);

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#setupProcessChain()
	 */
	@Override
	protected void setupProcessChain() {
		if (logger.isLoggable(Level.DEBUG))
			logger.log(Level.DEBUG, "ENTER: setupProcessChain()");
		try {
			if (setupDone) {
				return;
			}

			createPartialController();
			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model, locale));
			processChain.add(new SR6LifePathResetGenerator(this));
			processChain.add(meta);
			processChain.add(magicReso);
//			processChain.add(attributes);
//			processChain.add(skill);

			setupDone = true;
		} finally {
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, "LEAVE: setupProcessChain()");
		}
		logger.log(Level.ERROR, "ToDo");
	}

	//-------------------------------------------------------------------
	public void setNativeLanguage(String n) {
		logger.log(Level.WARNING, "ToDo: setLanguage");
		//skill.setLanguage(n);
	}

}
