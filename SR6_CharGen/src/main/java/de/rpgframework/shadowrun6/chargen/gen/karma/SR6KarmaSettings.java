package de.rpgframework.shadowrun6.chargen.gen.karma;

import de.rpgframework.shadowrun.chargen.gen.IKarmaSettings;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;

/**
 * @author prelle
 *
 */
public class SR6KarmaSettings extends CommonSR6GeneratorSettings implements IKarmaSettings {

	public int startKarma;
	public int attrib;
	public int skills;
	public int spells;

	//-------------------------------------------------------------------
	/**
	 */
	public SR6KarmaSettings() {
	}

	//-------------------------------------------------------------------
	public String toAttributeString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\nKarma for attributes "+attrib);
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public String toSkillString() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

}
