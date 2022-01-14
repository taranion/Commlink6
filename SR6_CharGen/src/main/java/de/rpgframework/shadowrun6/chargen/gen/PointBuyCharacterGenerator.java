package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.shadowrun.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator;

/**
 * @author stefa
 *
 */
@GeneratorId("karma")
public class PointBuyCharacterGenerator extends CommonSR6CharacterGenerator  implements ISR6PointBuyGenerator {

	static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PointBuyCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	private boolean setupDone;

	//-------------------------------------------------------------------
	public PointBuyCharacterGenerator() {
	}

	//-------------------------------------------------------------------
	public PointBuyCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle);
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
				WizardPageType.MAGIC_OR_RESONANCE, WizardPageType.QUALITIES, WizardPageType.ATTRIBUTES,
				WizardPageType.SKILLS, WizardPageType.SPELLS, WizardPageType.ALCHEMY, WizardPageType.RITUALS,
				WizardPageType.POWERS, WizardPageType.COMPLEX_FORMS, WizardPageType.NAME, };
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
		SR6PointBuySettings settings = new SR6PointBuySettings();
		settings.variant = PowerLevel.STANDARD;
		settings.characterPoints = 100;
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
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#setupProcessChain()
	 */
	@Override
	protected void setupProcessChain() {
		if (logger.isDebugEnabled())
			logger.debug("ENTER: setupProcessChain()");
		try {
			if (setupDone) {
				return;
			}

			attributes = new PointBuySR6AttributeGenerator(this);
			meta = new PointBuyMetatypeController(this);
			magicReso = new PointBuyMagicOrResonanceController(this);
			skill = new PointBuySR6SkillGenerator(this);
			logger.info("meta = " + getMetatypeController() + "  of " + this);

			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model));
			processChain.add(new PointBuyResetGenerator(this));
			processChain.add(meta);
			processChain.add(magicReso);
			processChain.add(attributes);
			processChain.add(skill);

			setupDone = true;
		} finally {
			if (logger.isDebugEnabled())
				logger.debug("LEAVE: setupProcessChain()");
		}
		logger.error("ToDo");
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
}
