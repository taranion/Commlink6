package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger.Level;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AnimalismController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6DrakeController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.RemainingKarmaNuyenController;
import de.rpgframework.shadowrun6.chargen.gen.ResetGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6ContactGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6DataStructureController;
import de.rpgframework.shadowrun6.chargen.gen.SR6LifestyleGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6SINGenerator;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CommonFocusController;

/**
 *
 */
@GeneratorId("free")
public class FreeCharacterGenerator extends CommonSR6CharacterGenerator {

	private boolean setupDone;

	//-------------------------------------------------------------------
	public FreeCharacterGenerator() {
	}

	//-------------------------------------------------------------------
	public FreeCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle, SR6FreeSettings.class);
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN, true);
	}

	//-------------------------------------------------------------------
	public SR6FreeSettings getSettings() {
		return getModel().getCharGenSettings(SR6FreeSettings.class);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "free";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] { WizardPageType.METATYPE, //WizardPageType.DRAKE,
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.SURGE, WizardPageType.SHIFTER, WizardPageType.INFECTED,
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
		return RES.getString("chargen.FreeCharacterGenerator");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return RES.getString("chargen.FreeCharacterGenerator.desc");
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

			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model, locale));
			processChain.add(new ResetGenerator(this));
			processChain.add(meta);
			processChain.add(drake);
			processChain.add(magicReso);
			processChain.add(qualities);
			processChain.add(qPaths);
			processChain.add(attributes);
			processChain.add(skills);
			processChain.add(spells);
			processChain.add(rituals);
			processChain.add(adeptPowers);
			processChain.add(martial);
			processChain.add(equipment);
			processChain.add(foci);
			processChain.add(animalism);
			processChain.add(dataStructures);
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
		logger.log(Level.ERROR, "ToDo");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#initializeModel()
	 */
	@Override
	protected void initializeModel() {
		if (model.getCharGenSettings(CommonSR6GeneratorSettings.class) == null  || !(model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6FreeSettings) ) {
			if (model.getChargenSettingsJSON() != null  && (model.getCharGenSettings(CommonSR6GeneratorSettings.class) instanceof SR6FreeSettings)) {
				logger.log(Level.INFO, "Restore generator config from {0}", model.getChargenSettingsJSON());
				SR6FreeSettings settings = model.getCharGenSettings(SR6FreeSettings.class);
				model.setCharGenSettings(settings);
			} else {
				logger.log(Level.INFO, "Create new generator config");
				SR6FreeSettings settings = new SR6FreeSettings();
//		settings.variant = PowerLevel.STANDARD;
				model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
				model.setCharGenUsed(getId());
				model.setCharGenSettings(settings);
				model.setKarmaFree(50);
			}
		}
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
		ruleCtrl.setRuleValue(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN, true);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl#createPartialController()
	 */
	@Override
	protected void createPartialController() {
		attributes = new SR6FreeAttributeGenerator(this);
		meta       = new SR6FreeMetatypeController(this);
		magicReso  = new FreeMagicOrResonanceController(this);
		skills     = new SR6FreeSkillGenerator(this);
		qualities  = new SR6FreeQualityGenerator(this);
		equipment  = new SR6FreeEquipmentGenerator(this);
		spells     = new SR6FreeSpellGenerator(this);
		rituals    = new SR6FreeRitualGenerator(this);
		adeptPowers= new SR6FreeAdeptPowerGenerator(this);
		complex    = new SR6FreeComplexFormGenerator(this);
		metaEcho   = new SR6FreeMetamagicOrEchoController(this, true);
		sins       = new SR6SINGenerator(this);
		lifestyles = new SR6LifestyleGenerator(this);
		contacts   = new SR6ContactGenerator(this);
		foci       = new SR6CommonFocusController(this);
		qPaths     = new CommonQualityPathController(this);
		martial    = new SR6MartialArtsController(this);
		drake     = new SR6DrakeController(this, true);
		animalism = new SR6FreeAnimalismController(this, true);
		dataStructures = new SR6DataStructureController(this);
	}
}
