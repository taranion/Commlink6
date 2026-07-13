package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AnimalismController;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6DrakeController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MetamagicOrEchoController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonEquipmentGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.SR6DataStructureController;
import de.rpgframework.shadowrun6.chargen.gen.SR6EquipmentGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6FocusGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6LifestyleGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6SINGenerator;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;
import de.rpgframework.shadowrun6.proc.CalculateAttributePools;
import de.rpgframework.shadowrun6.proc.CalculateSkillPools;

/**
 * @Author prelle
 */
@GeneratorId("lifepath")
public class SR6LifepathCharacterGenerator extends CommonSR6CharacterGenerator implements CharacterGenerator<ShadowrunAttribute, Shadowrun6Character> {

	private boolean setupDone;

	private BornThisWayGenerator bornThisWay;
	private ChildhoodGenerator childhood;
	private EarlyAdultGenerator earlyAdult;
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
				WizardPageType.LP_BORN_THIS_WAY, WizardPageType.LP_CHILDHOOD, WizardPageType.LP_TEENAGE, WizardPageType.LP_ADULT,
				WizardPageType.POWERS, WizardPageType.SPELLS,
				WizardPageType.RITUALS, WizardPageType.COMPLEX_FORMS, WizardPageType.METAECHO,
				WizardPageType.GEAR, WizardPageType.SIN_LICENSE, WizardPageType.LIFESTYLE,
				WizardPageType.CONTACTS, WizardPageType.NAME,
				};
	}

	//-------------------------------------------------------------------
	public static String getStaticName() {
		return SR6CharacterGenerator.RES.getString("chargen.SR6LifepathCharacterGenerator");
	}
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return SR6CharacterGenerator.RES.getString("chargen.SR6LifepathCharacterGenerator");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return SR6CharacterGenerator.RES.getString("chargen.SR6LifepathCharacterGenerator.desc");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		SR6LifePathSettings settings = getSettings();
		if (settings==null)
			return false;
		if (settings.getBornQuality1()==null || settings.getNativeLanguage()==null)
			return false;
		if (settings.getChildhoodQuality1()==null || settings.getChildhoodArea()==null || settings.getChildhoodSkills().size()!=4)
			return false;
		if (settings.getEarlyAdultSkill()==null || settings.getEarlyAdultAttribute()==null)
			return false;
		int maximumModules = getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_MAX_MODULES);
		if (settings.getModules().size()>maximumModules)
			return false;

		Map<String, Long> moduleCounts = settings.getModules().stream()
				.map(LifepathModuleValue::getKey)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return moduleCounts.values().stream().noneMatch(count -> count>2);
	}

	//-------------------------------------------------------------------
	public void applyPowerLevelDefaultsIfNeeded() {
		SR6LifePathSettings settings = getSettings();
		PowerLevel level = settings.variant;
		if (level==null) {
			level = PowerLevel.STANDARD;
			settings.variant = level;
		}
		if (level==settings.getLifePathDefaultsAppliedFor())
			return;
		applyPowerLevelDefaults(level);
		settings.setLifePathDefaultsAppliedFor(level);
	}

	//-------------------------------------------------------------------
	public void applyPowerLevelDefaults(PowerLevel level) {
		if (level==null)
			level = PowerLevel.STANDARD;
		switch (level) {
		case STREET_LEVEL:
			setLifePathDefaults(25, 6, 4, 10, 5000);
			break;
		case ELITE:
			setLifePathDefaults(85, 10, 8, 30, 100000);
			break;
		default:
			setLifePathDefaults(50, 8, 6, 20, 25000);
			break;
		}
	}

	//-------------------------------------------------------------------
	private void setLifePathDefaults(int adjustmentKarma, int modules, int qualities, int negativeKarmaCap, int startNuyen) {
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_LIFEPATH_ADJUSTMENT_KARMA, adjustmentKarma);
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_LIFEPATH_MAX_MODULES, modules);
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_LIFEPATH_MAX_QUALITIES, qualities);
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_LIFEPATH_NEGATIVE_KARMA_CAP, negativeKarmaCap);
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_LIFEPATH_START_NUYEN, startNuyen);
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
			processChain.add(childhood);
			processChain.add(earlyAdult);
			processChain.add(modules);
			processChain.add(qualities);
			processChain.add(qPaths);
			processChain.add(attributes);
			processChain.add(new SR6LifePathSkillGenerator(model));
			processChain.add(new ApplyModificationsGeneric(model));
			processChain.add(new CalculateAttributePools(model, locale));
			processChain.add(new CalculateSkillPools(model, locale));
			processChain.add(spells);
			processChain.add(rituals);
			processChain.add(adeptPowers);
			processChain.add(martial);
			processChain.add(animalism);
			processChain.add(dataStructures);
			processChain.add(equipment);
			processChain.add(foci);
			processChain.add(complex);
			processChain.add(metaEcho);
			processChain.add(sins);
			processChain.add(lifestyles);
			processChain.add(contacts);
			processChain.add(new SR6LifePathRemainingKarmaNuyenController(this));

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
		if (model.getCharGenSettings(CommonSR6GeneratorSettings.class) == null  || !(model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6LifePathSettings) ) {
			if (model.getChargenSettingsJSON() != null  && (model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6LifePathSettings)) {
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
		childhood = new ChildhoodGenerator(this);
		earlyAdult= new EarlyAdultGenerator(this);
		modules   = new SR6LifePathModuleGenerator(this);
		attributes = new SR6LifePathAttributeGenerator(this);
		qualities = new CommonQualityGenerator(this);
		equipment = new SR6EquipmentGenerator(this);
		spells    = new SR6LifePathSpellGenerator(this);
		rituals   = new SR6LifePathRitualGenerator(this);
		adeptPowers = new SR6AdeptPowerController(this);
		complex   = new SR6LifePathComplexFormGenerator(this);
		metaEcho  = new SR6MetamagicOrEchoController(this, true);
		sins      = new SR6SINGenerator(this);
		lifestyles= new SR6LifestyleGenerator(this);
		contacts  = new SR6LifePathContactGenerator(this);
		foci      = new SR6FocusGenerator(this);
		qPaths    = new CommonQualityPathController(this);
		martial   = new SR6MartialArtsController(this);
		drake     = new SR6DrakeController(this, true);
		animalism = new SR6AnimalismController(this, true);
		dataStructures = new SR6DataStructureController(this);
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
		runProcessors();

		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
			int total = val.getModifiedValue();
			val.clearIncomingModifications();
			val.setDistributed(total);
			val.setStart(total);
		}

		for (SR6SkillValue val : getModel().getSkillValues()) {
			int total = val.getModifiedValue();
			if (total<=0)
				continue;
			val.clearIncomingModifications();
			val.setDistributed(total);
			val.setStart(total);
		}
		for (SR6SkillValue val : new ArrayList<>(getModel().getSkillValues())) {
			if (val.getModifiedValue()<=0)
				getModel().removeSkillValue(val);
		}

		getModel().setInCareerMode(true);
		getModel().setKarmaInvested(0);
		expandPACKs();
	}

	//-------------------------------------------------------------------
	private void expandPACKs() {
		for (CarriedItem<ItemTemplate> tmp : getModel().getCarriedItems()) {
			if (getItemType(tmp)==ItemType.PACK) {
				logger.log(Level.WARNING, "ToDo: handle PACK "+tmp);
				System.err.println("ToDo: Expand PACK "+tmp);

				for (SIN sin : new ArrayList<>(getModel().getSINs())) {
					if (sin.getInjectedBy()==tmp.getResolved()) {
						getModel().removeSIN(sin);
					}
				}

				for (LicenseValue lic : new ArrayList<>(getModel().getLicenses())) {
					if (lic.getInjectedBy()==tmp.getResolved()) {
						getModel().removeLicense(lic);
					}
				}
				((CommonEquipmentGenerator)getEquipmentController()).expandPACK(tmp);
				getModel().removeCarriedItem(tmp);
			}
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
	@Override
	public SR6LifePathModuleGenerator getLifePathModuleGenerator() {
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator#getChildhoodGenerator()
	 */
	@Override
	public ChildhoodGenerator getChildhoodGenerator() {
		return childhood;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator#getEarlyAdultGenerator()
	 */
	@Override
	public EarlyAdultGenerator getEarlyAdultGenerator() {
		return earlyAdult;
	}

}
