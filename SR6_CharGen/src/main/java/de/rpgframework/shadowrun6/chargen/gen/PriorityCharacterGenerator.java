package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class PriorityCharacterGenerator extends CommonSR6CharacterGenerator {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.GERMAN);

	private boolean dontProcess;
	private boolean recalcuateHasEnoughData;
	private boolean hasEnoughData;
	private boolean setupDone;

	//-------------------------------------------------------------------
	/**
	 */
	public PriorityCharacterGenerator() {
		super();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "prio";
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[]{
				WizardPageType.PRIORITIES,
				WizardPageType.METATYPE,
				WizardPageType.MAGIC_OR_RESONANCE,
				WizardPageType.QUALITIES,
				WizardPageType.ATTRIBUTES,
				WizardPageType.SKILLS,
				WizardPageType.SPELLS,
				WizardPageType.ALCHEMY,
				WizardPageType.RITUALS,
				WizardPageType.POWERS,
				WizardPageType.COMPLEX_FORMS,
				WizardPageType.NAME,
		};
	}

	//--------------------------------------------------------------------
	@Override
	protected void setupProcessChain() {
		if (setupDone) {
			System.err.println("NewPriorityCharacterGenerator.setupProcessChain: already set up");
			return;
		}
		
		meta     = new PriorityMetatypeController(this);
		skill    = new PrioritySkillGenerator(this);
		logger.info("meta = "+getMetatypeController()+"  of "+this);
		
		processChain.add(meta);
//		processChain.add(attr);
		processChain.add(skill);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.gen.CharacterGenerator#start(org.prelle.shadowrun6.ShadowrunCharacter)
	 */
	@Override
	public void start(Shadowrun6Character model) {
		this.model = model;
		this.setupDone = false;
//		PrioritySettings settings = new PrioritySettings();
//		settings.variant = PriorityVariant.STANDARD;
//		settings.priorities = new HashMap<PriorityType,Priority>();
//		settings.priorities.put(PriorityType.METATYPE , Priority.B);
//		settings.priorities.put(PriorityType.ATTRIBUTE, Priority.A);
//		settings.priorities.put(PriorityType.MAGIC    , Priority.C);
//		settings.priorities.put(PriorityType.SKILLS   , Priority.D);
//		settings.priorities.put(PriorityType.RESOURCES, Priority.E);
//		model.setChargenUsed(getID());
//		model.setTemporaryChargenSettings(settings);
//		model.setKarmaFree(50);
		logger.info("----------------Start generator-----------------------"+toString()+"\n\n\n");
		
		try {
			setupProcessChain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Failed on process chain",e);
		}
	}
}
