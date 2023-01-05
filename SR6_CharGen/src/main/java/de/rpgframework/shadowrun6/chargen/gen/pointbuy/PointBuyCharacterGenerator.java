package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MetamagicOrEchoController;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.RemainingKarmaNuyenController;
import de.rpgframework.shadowrun6.chargen.gen.ResetGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6ContactGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6EquipmentGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6LifestyleGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6SINGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.RemainingCPAreNuyenStep;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityRitualGenerator;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CommonFocusController;
import de.rpgframework.shadowrun6.proc.CalculateAttributePools;
import de.rpgframework.shadowrun6.proc.CalculateSkillPools;

/**
 * @author stefa
 *
 */
@GeneratorId("pointbuy")
public class PointBuyCharacterGenerator extends CommonSR6CharacterGenerator  implements ISR6PointBuyGenerator {

	static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PointBuyCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	public static String TODO_SKILLS_TOO_MANY_CP_CONVERTED = "pointbuy.todo.skills.too_many_converted";
	public static String TODO_ATTRIB_TOO_MANY_CP_CONV_REG  = "pointbuy.todo.attrib.too_many_regular";
	public static String TODO_ATTRIB_TOO_MANY_CP_CONV_SPEC = "pointbuy.todo.attrib.too_many_special";

	private RemainingCPAreNuyenStep cpToNuyenStep;


	private boolean setupDone;

	//-------------------------------------------------------------------
	public PointBuyCharacterGenerator() {
//		try {throw new RuntimeException("Trace");} catch (Exception e) {e.printStackTrace();}
	}

	//-------------------------------------------------------------------
	public PointBuyCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle, SR6PointBuySettings.class);
//		try {throw new RuntimeException("Trace");} catch (Exception e) {e.printStackTrace();}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "pointbuy";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] { WizardPageType.METATYPE,
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.SURGE, WizardPageType.INFECTED,
				WizardPageType.QUALITIES,
				WizardPageType.ATTRIBUTES,
				WizardPageType.SKILLS, WizardPageType.POWERS, WizardPageType.SPELLS,
				WizardPageType.RITUALS, WizardPageType.COMPLEX_FORMS, WizardPageType.METAECHO,
				WizardPageType.GEAR, WizardPageType.SIN_LICENSE, WizardPageType.LIFESTYLE,
				WizardPageType.CONTACTS, WizardPageType.NAME, };
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
			for (Class<? extends ProcessingStep> cls : Shadowrun6Tools.RECALCULATE_STEPS) {
				try {
					Constructor<? extends ProcessingStep> cons = null;
					try {
						cons = cls.getConstructor(Shadowrun6Character.class);
					} catch (NoSuchMethodException nsm) {
						cons = cls.getConstructor(ShadowrunCharacter.class);
					}
					processChain.add(cons.newInstance(model));
				} catch (NoSuchMethodException e) {
					logger.log(Level.ERROR, "Missing constructor <init>(Shadowrun6Character) in "+cls);
					System.exit(1);
				} catch (Exception e) {
					logger.log(Level.ERROR, "Failed instantiating "+cls,e);
				}

			}
			processChain.add(new ResetGenerator(this));
//			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model));
			processChain.add(meta);
			processChain.add(magicReso);
			processChain.add(qualities);
			processChain.add(qPaths);
			processChain.add(attributes);
			processChain.add(skills);
			processChain.add(spells);
			processChain.add(rituals);
			processChain.add(adeptPowers);
			processChain.add(cpToNuyenStep);
			processChain.add(equipment);
			processChain.add(foci);
			processChain.add(complex);
			processChain.add(metaEcho);
			processChain.add(sins);
			processChain.add(lifestyles);
			processChain.add(contacts);
			processChain.add(new CalculateAttributePools(model, Locale.getDefault()));
			processChain.add(new RemainingKarmaNuyenController(this));
			processChain.add(new CalculateSkillPools(model, Locale.getDefault()));

			setupDone = true;
		} finally {
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, "LEAVE: setupProcessChain()");
		}
		logger.log(Level.ERROR, "ToDo");
	}

	//-------------------------------------------------------------------
	@Override
	public void runProcessors() {
		SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
		settings.characterPoints = 100;
		super.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator#getSettings()
	 */
	@Override
	public SR6PointBuySettings getSettings() {
		return getModel().getCharGenSettings(SR6PointBuySettings.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator#getPointBuyAttributeController()
	 */
	@Override
	public PointBuyAttributeGenerator getPointBuyAttributeController() {
		return (PointBuyAttributeGenerator) super.attributes;
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
				SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
				model.setCharGenSettings(settings);
			} else {
				logger.log(Level.INFO, "Create new generator config");
				SR6PointBuySettings settings = new SR6PointBuySettings();
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
		attributes = new SR6PointBuyAttributeGenerator(this);
		meta       = new PointBuyMetatypeController(this);
		magicReso = new PointBuyMagicOrResonanceController(this);
		skills = new PointBuySR6SkillGenerator(this);
		qualities = new CommonQualityGenerator(this);
		cpToNuyenStep = new RemainingCPAreNuyenStep(this);
		equipment = new SR6EquipmentGenerator(this);
		spells    = new SR6PointBuySpellGenerator(this);
		rituals   = new SR6PointBuyRitualGenerator(this);
		adeptPowers = new SR6PointBuyAdeptPowerGenerator(this);
		complex   = new SR6PointBuyComplexFormGenerator(this);
		metaEcho  = new SR6MetamagicOrEchoController(this, true);
		sins      = new SR6SINGenerator(this);
		lifestyles= new SR6LifestyleGenerator(this);
		contacts  = new SR6ContactGenerator(this);
		foci      = new SR6CommonFocusController(this);
		qPaths    = new CommonQualityPathController(this);
		martial   = new SR6MartialArtsController(this);
	}
}
