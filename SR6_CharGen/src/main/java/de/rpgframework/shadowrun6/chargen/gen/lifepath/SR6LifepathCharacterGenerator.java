package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6DrakeController;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.RemainingKarmaNuyenController;
import de.rpgframework.shadowrun6.chargen.gen.SR6EquipmentGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;

/**
 * @Author prelle
 */
@GeneratorId("lifepath")
public class SR6LifepathCharacterGenerator extends CommonSR6CharacterGenerator implements CharacterGenerator<ShadowrunAttribute, Shadowrun6Character> {

	static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PointBuyCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	private boolean setupDone;

	private BornThisWayGenerator bornThisWay;
	private SR6LifePathModuleGenerator modules;

	//-------------------------------------------------------------------
	public SR6LifepathCharacterGenerator() {
	}

	//-------------------------------------------------------------------
	public SR6LifepathCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle, SR6LifePathSettings.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "lifepath";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] { WizardPageType.METATYPE, //WizardPageType.DRAKE,
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.SURGE, WizardPageType.INFECTED,
				WizardPageType.LP_BORN_THIS_WAY,
				WizardPageType.QUALITIES,
				WizardPageType.ATTRIBUTES,
//				WizardPageType.SKILLS, WizardPageType.POWERS, WizardPageType.SPELLS,
//				WizardPageType.RITUALS, WizardPageType.COMPLEX_FORMS, WizardPageType.METAECHO,
//				WizardPageType.GEAR, WizardPageType.SIN_LICENSE, WizardPageType.LIFESTYLE,
//				WizardPageType.CONTACTS, WizardPageType.NAME,
				};
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("generator.lifepath.name");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return RES.getString("generator.lifepath.desc");
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

		initializeModel();
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

			// First the regular processing steps
			processChain.clear();
			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model, locale));
			processChain.add(new SR6LifePathResetGenerator(this));
			processChain.add(meta);
			processChain.add(drake);
			processChain.add(magicReso);
			processChain.add(bornThisWay);
//			processChain.add(qualities);
			processChain.add(modules);
//			processChain.add(qPaths);
			processChain.add(attributes);
			processChain.add(new ApplyModificationsGeneric(model));
//			processChain.add(skills);
//			processChain.add(spells);
//			processChain.add(rituals);
//			processChain.add(adeptPowers);
//			processChain.add(martial);
//			processChain.add(dataStructures);
////			processChain.add(cpToNuyenStep);
//			processChain.add(equipment);
//			processChain.add(foci);
//			processChain.add(complex);
//			processChain.add(metaEcho);
//			processChain.add(sins);
//			processChain.add(lifestyles);
//			processChain.add(contacts);
			processChain.add(new RemainingKarmaNuyenController(this));

			setupDone = true;
		} finally {
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, "LEAVE: setupProcessChain()");
		}
		logger.log(Level.ERROR, "ToDo");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#initializeModel()
	 */
	@Override
	protected void initializeModel() {
		if (model.getCharGenSettings(CommonSR6GeneratorSettings.class) == null  || !(model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6PointBuySettings) ) {
			if (model.getChargenSettingsJSON() != null  && (model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6PointBuySettings)) {
				logger.log(Level.INFO, "Restore generator config from {0}", model.getChargenSettingsJSON());
				SR6LifePathSettings settings = model.getCharGenSettings(SR6LifePathSettings.class);
				model.setCharGenSettings(settings);
			} else {
				logger.log(Level.INFO, "Create new generator config");
				SR6LifePathSettings settings = new SR6LifePathSettings();
//		settings.variant = PowerLevel.STANDARD;
				model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
				model.setCharGenUsed(getId());
				model.setCharGenSettings(settings);
				model.setKarmaFree(50);
			}
		}
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl#createPartialController()
	 */
	@Override
	protected void createPartialController() {
		meta = new SR6LifePathMetatypeController(this);
		magicReso = new SR6LifePathMagicOrResonanceController(this);
		bornThisWay = new BornThisWayGenerator(this);
		modules   = new SR6LifePathModuleGenerator(this);
		attributes = new SR6LifePathAttributeGenerator(this);
//		skills = new SR6PointBuySkillGenerator(this);
		qualities = new CommonQualityGenerator(this);
////		cpToNuyenStep = new RemainingCPAreNuyenStep(this);
		equipment = new SR6EquipmentGenerator(this);
////		spells    = new SR6PointBuySpellGenerator(this);
//		rituals   = new SR6PointBuyRitualGenerator(this);
//		adeptPowers = new SR6PointBuyAdeptPowerGenerator(this);
////		complex   = new SR6PointBuyComplexFormGenerator(this);
//		metaEcho  = new SR6MetamagicOrEchoController(this, true);
//		sins      = new SR6SINGenerator(this);
//		lifestyles= new SR6LifestyleGenerator(this);
//		contacts  = new SR6ContactGenerator(this);
//		foci      = new SR6CommonFocusController(this);
//		qPaths    = new CommonQualityPathController(this);
//		martial   = new SR6MartialArtsController(this);
		drake     = new SR6DrakeController(this, true);
//		dataStructures = new SR6DataStructureController(this);
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator#getSettings()
//	 */
//	@Override
//	public SR6LifePathSettings getSettings() {
//		return getModel().getCharGenSettings(SR6LifePathSettings.class);
//	}

	//-------------------------------------------------------------------
	public void setNativeLanguage(String n) {
		logger.log(Level.WARNING, "ToDo: setLanguage");
		//skill.setLanguage(n);
	}

	public void finish() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
			int total = val.getModifiedValue();
			val.clearIncomingModifications();
			val.setDistributed(total);
			val.setStart(total);
		}

		for (SR6SkillValue val : getModel().getSkillValues()) {
			int total = val.getModifiedValue();
			val.clearIncomingModifications();
			val.setDistributed(total);
			val.setStart(total);
		}
	}

	//-------------------------------------------------------------------
	public SR6LifePathSettings getSettings() {
		return model.getCharGenSettings(SR6LifePathSettings.class);
	}

	//-------------------------------------------------------------------
	public SR6LifePathModuleGenerator getModuleGenerator() {
		return modules;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator#getBornThisWayGenerator()
	 */
	@Override
	public BornThisWayGenerator getBornThisWayGenerator() {
		return bornThisWay;
	}

}
