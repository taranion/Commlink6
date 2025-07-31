package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.function.BiFunction;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AnimalismController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6DrakeController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MetamagicOrEchoController;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.RemainingKarmaNuyenController;
import de.rpgframework.shadowrun6.chargen.gen.ResetGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6ContactGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6DataStructureController;
import de.rpgframework.shadowrun6.chargen.gen.SR6EquipmentGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6FocusGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6LifestyleGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6SINGenerator;
import de.rpgframework.shadowrun6.proc.CalculateAttributePools;
import de.rpgframework.shadowrun6.proc.CalculateSkillPools;

/**
 * @author prelle
 *
 */
@GeneratorId("prio")
public class PriorityCharacterGenerator extends CommonSR6CharacterGenerator
	implements IPriorityGenerator<Shadowrun6Character, SR6PrioritySettings> {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	protected PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prioCtrl;

	protected BiFunction<PriorityType, Priority, PriorityTableEntry> resolver;

	private boolean dontProcess;
	private boolean recalcuateHasEnoughData;
	private boolean hasEnoughData;
	private boolean setupDone;

	//-------------------------------------------------------------------
	public PriorityCharacterGenerator() {
		super();
		resolver = new BiFunction<PriorityType, Priority, PriorityTableEntry>() {
			public PriorityTableEntry apply(PriorityType type, Priority prio) {
				return Shadowrun6Core.getPriorityTableEntry(type, prio);
			}};
	}

	//-------------------------------------------------------------------
	public PriorityCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle, SR6PrioritySettings.class);
		resolver = new BiFunction<PriorityType, Priority, PriorityTableEntry>() {
			public PriorityTableEntry apply(PriorityType type, Priority prio) {
				return Shadowrun6Core.getPriorityTableEntry(type, prio);
			}};
		initializeModel();
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "prio";
	}

	//-------------------------------------------------------------------
	public static String getStaticName() {
		return RES.getString("generator.name");
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("generator.name");
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return RES.getString("generator.desc");
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] { WizardPageType.PRIORITIES, WizardPageType.METATYPE, WizardPageType.SHIFTER, //WizardPageType.DRAKE,
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.SURGE, WizardPageType.INFECTED,
				WizardPageType.QUALITIES, WizardPageType.ATTRIBUTES,
				WizardPageType.SKILLS, WizardPageType.SPELLS, WizardPageType.RITUALS,
				WizardPageType.POWERS, WizardPageType.COMPLEX_FORMS, WizardPageType.METAECHO,
				WizardPageType.GEAR, WizardPageType.SIN_LICENSE, WizardPageType.LIFESTYLE,
				WizardPageType.CONTACTS, WizardPageType.NAME, };
	}

	//--------------------------------------------------------------------
	protected PriorityTableController<Shadowrun6Character,SR6PrioritySettings> createPriorityTableController() {
		return new SR6PriorityTableController(this, resolver);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#initializeModel()
	 */
	@Override
	protected void initializeModel() {
		if (model.getCharGenSettings(Object.class) == null  || !(model.getCharGenSettings(Object.class) instanceof SR6PrioritySettings) ) {
			if (model.getChargenSettingsJSON() != null  && (model.getCharGenSettings(Object.class) instanceof SR6PrioritySettings)) {
				logger.log(Level.INFO, "Restore generator config from {0}", model.getChargenSettingsJSON());
				SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
				model.setCharGenSettings(settings);
			} else {
				logger.log(Level.INFO, "Create new generator config");
				SR6PrioritySettings settings = new SR6PrioritySettings();
				if (settings.variant==PowerLevel.ELITE) {
					settings.priorities.put(PriorityType.METATYPE, Priority.B);
					settings.priorities.put(PriorityType.ATTRIBUTE, Priority.A);
					settings.priorities.put(PriorityType.MAGIC, Priority.D);
					settings.priorities.put(PriorityType.SKILLS, Priority.B);
					settings.priorities.put(PriorityType.RESOURCES, Priority.C);
				} else if (settings.variant==PowerLevel.STREET_LEVEL) {
					settings.priorities.put(PriorityType.METATYPE, Priority.C);
					settings.priorities.put(PriorityType.ATTRIBUTE, Priority.B);
					settings.priorities.put(PriorityType.MAGIC, Priority.E);
					settings.priorities.put(PriorityType.SKILLS, Priority.C);
					settings.priorities.put(PriorityType.RESOURCES, Priority.D);
				} else {
					settings.priorities.put(PriorityType.METATYPE, Priority.C);
					settings.priorities.put(PriorityType.ATTRIBUTE, Priority.A);
					settings.priorities.put(PriorityType.MAGIC, Priority.E);
					settings.priorities.put(PriorityType.SKILLS, Priority.B);
					settings.priorities.put(PriorityType.RESOURCES, Priority.D);
				}
//		model.addRule(Shadowrun6Rules.CHARGEN_ALLOW_INITIATION, "false");
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
		prioCtrl  = createPriorityTableController();
		attributes= new SR6PriorityAttributeGenerator(this);
		meta      = new SR6PriorityMetatypeController(this);
		magicReso = new PriorityMagicOrResonanceController(this);
		skills    = new SR6PrioritySkillGenerator(this);
		qualities = new CommonQualityGenerator(this);
		equipment = new SR6EquipmentGenerator(this);
		spells    = new SR6PrioritySpellGenerator(this);
		rituals   = new SR6PriorityRitualGenerator(this);
		adeptPowers = new SR6PriorityAdeptPowerGenerator(this);
		complex   = new SR6PriorityComplexFormGenerator(this);
		metaEcho  = new SR6MetamagicOrEchoController(this, true);
		sins      = new SR6SINGenerator(this);
		lifestyles= new SR6LifestyleGenerator(this);
		contacts  = new SR6ContactGenerator(this);
		foci      = new SR6FocusGenerator(this);
		qPaths    = new CommonQualityPathController(this);
		martial   = new SR6MartialArtsController(this);
		drake     = new SR6DrakeController(this, true);
		animalism = new SR6AnimalismController(this, true);
		dataStructures = new SR6DataStructureController(this);
	}

	// --------------------------------------------------------------------
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
			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model, locale));
			processChain.add(new ResetGenerator(this));
			// Now add generator specifics on top
			processChain.add(prioCtrl);
			processChain.add(meta);
			processChain.add(shifter);
			processChain.add(drake);
			processChain.add(magicReso);
			processChain.add(qualities);
			processChain.add(qPaths);
			processChain.add(attributes);
			processChain.add(skills);
			// Re-run those steps to recalculate after skills and attributes have been copied over from priority settings
			processChain.add(new CalculateAttributePools(model, locale));
			processChain.add(new CalculateSkillPools(model, locale));
			processChain.add(spells);
			processChain.add(rituals);
			processChain.add(new SR6PriorityCalculateMaxPowerPointsStep(this));
			processChain.add(adeptPowers);
//			processChain.add(new GetModificationsFromGear(model));
//			processChain.add(new ApplyModificationsGeneric(model));
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
			processChain.add(new RemainingKarmaNuyenController(this));

			setupDone = true;
		} finally {
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, "LEAVE: setupProcessChain()");
		}
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		logger.log(Level.WARNING, "TODO: finish###############################################################");

		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> attr = model.getAttribute(key);
			//attr.setDistributed(attr.)
			logger.log(Level.WARNING, "Finalize {0} to {1}", key, attr.getModifiedValue(ValueType.NATURAL));
			attr.setStart(attr.getModifiedValue(ValueType.NATURAL));
			attr.setDistributed(attr.getModifiedValue(ValueType.NATURAL));
		}

		// Power points
		AttributeValue<ShadowrunAttribute> attr = model.getAttribute(ShadowrunAttribute.POWER_POINTS);
		//attr.setDistributed(attr.)
		logger.log(Level.WARNING, "Finalize {0} to {1}", attr.getModifyable(), attr.getModifiedValue(ValueType.NATURAL));
		attr.setStart(attr.getModifiedValue(ValueType.NATURAL));
		attr.setDistributed(attr.getModifiedValue(ValueType.NATURAL));


		super.finish();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator#getPriorityController()
	 */
	@Override
	public PriorityTableController<Shadowrun6Character,SR6PrioritySettings> getPriorityController() {
		return prioCtrl;
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
	 * @see de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator#getSettings()
	 */
	@Override
	public SR6PrioritySettings getSettings() {
		return model.getCharGenSettings(SR6PrioritySettings.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator#getPriorityAttributeController()
	 */
	@Override
	public PriorityAttributeGenerator getPriorityAttributeController() {
		return (PriorityAttributeGenerator) super.attributes;
	}

}
