package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;

/**
 * @author stefa
 *
 */
public class LifeModulesCharacterGenerator extends CommonSR6CharacterGenerator {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(LifeModulesCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	//-------------------------------------------------------------------
	/**
	 */
	public LifeModulesCharacterGenerator() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "life";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return new WizardPageType[] {};
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
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator#setupProcessChain()
	 */
	@Override
	protected void setupProcessChain() {
		logger.error("ToDo");
	}

}
