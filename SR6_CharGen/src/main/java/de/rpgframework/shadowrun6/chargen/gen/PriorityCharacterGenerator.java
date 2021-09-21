package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun.chargen.gen.Priority;
import de.rpgframework.shadowrun.chargen.gen.PriorityType;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class PriorityCharacterGenerator extends CommonSR6CharacterGenerator {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	protected SR6PriorityTableController prioCtrl;

	private boolean dontProcess;
	private boolean recalcuateHasEnoughData;
	private boolean hasEnoughData;
	private boolean setupDone;

	//-------------------------------------------------------------------
	public PriorityCharacterGenerator() {
		super();
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

	// --------------------------------------------------------------------
	@Override
	protected void setupProcessChain() {
		if (logger.isDebugEnabled())
			logger.debug("ENTER: setupProcessChain()");
		try {
			if (setupDone) {
				return;
			}

			prioCtrl = new SR6PriorityTableController(this);
			meta = new PriorityMetatypeController(this);
			skill = new PrioritySkillGenerator(this);
			logger.info("meta = " + getMetatypeController() + "  of " + this);

			processChain.add(prioCtrl);
			processChain.add(meta);
			//processChain.add(attr);
			processChain.add(skill);

			setupDone = true;
		} finally {
			if (logger.isDebugEnabled())
				logger.debug("LEAVE: setupProcessChain()");
		}
	}

	//-------------------------------------------------------------------
	public SR6PriorityTableController getPriorityController() {
		return prioCtrl;
	}

	// --------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.gen.CharacterGenerator#start(org.prelle.shadowrun6.ShadowrunCharacter)
	 */
	@Override
	public void start(Shadowrun6Character model) {
		this.model = model;
		this.setupDone = false;
		SR6PrioritySettings settings = new SR6PrioritySettings();
		settings.variant = PriorityVariant.STANDARD;
		settings.priorities.put(PriorityType.METATYPE, Priority.B);
		settings.priorities.put(PriorityType.ATTRIBUTE, Priority.A);
		settings.priorities.put(PriorityType.MAGIC, Priority.C);
		settings.priorities.put(PriorityType.SKILLS, Priority.D);
		settings.priorities.put(PriorityType.RESOURCES, Priority.E);
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
}
