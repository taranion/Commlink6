package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;
import java.util.function.BiFunction;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;

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

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "prio";
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] { WizardPageType.PRIORITIES, WizardPageType.METATYPE,
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.QUALITIES, WizardPageType.ATTRIBUTES,
				WizardPageType.SKILLS, WizardPageType.SPELLS, WizardPageType.ALCHEMY, WizardPageType.RITUALS,
				WizardPageType.POWERS, WizardPageType.COMPLEX_FORMS, WizardPageType.NAME, };
	}

	//--------------------------------------------------------------------
	protected PriorityTableController<Shadowrun6Character,SR6PrioritySettings> createPriorityTableController() {
		return new PriorityTableController<Shadowrun6Character,SR6PrioritySettings>(this, SR6PrioritySettings.class, resolver);
	}

	// --------------------------------------------------------------------
	@Override
	protected void setupProcessChain() {
		if (logger.isDebugEnabled())
			logger.debug("ENTER: setupProcessChain()");
		try {
			if (setupDone) {
				return;
			}

			prioCtrl = createPriorityTableController();
			attributes = new PriorityAttributeGenerator(this);
			meta = new PriorityMetatypeController(this);
			skill = new PrioritySkillGenerator(this);
			logger.info("meta = " + getMetatypeController() + "  of " + this);

			processChain.add(prioCtrl);
			processChain.add(meta);
			processChain.add(attributes);
			processChain.add(skill);

			setupDone = true;
		} finally {
			if (logger.isDebugEnabled())
				logger.debug("LEAVE: setupProcessChain()");
		}
	}

	//-------------------------------------------------------------------
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
		SR6PrioritySettings settings = new SR6PrioritySettings();
		settings.variant = PriorityVariant.STANDARD;
		settings.priorities.put(PriorityType.METATYPE, Priority.B);
		settings.priorities.put(PriorityType.ATTRIBUTE, Priority.A);
		settings.priorities.put(PriorityType.MAGIC, Priority.E);
		settings.priorities.put(PriorityType.SKILLS, Priority.C);
		settings.priorities.put(PriorityType.RESOURCES, Priority.D);
//		model.addRule(Shadowrun6Rules.CHARGEN_ALLOW_INITIATION, "false");
		model.setCharGenUsed(getId());
		model.setCharGenSettings(settings);
		model.setKarmaFree(50);
		logger.info("----------------Start generator-----------------------" + toString() + "\n\n\n");
		
		try {
			setupProcessChain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Failed on process chain", e);
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
	public PriorityAttributeController getPriorityAttributeController() {
		return (PriorityAttributeController) super.attributes;
	}
}
